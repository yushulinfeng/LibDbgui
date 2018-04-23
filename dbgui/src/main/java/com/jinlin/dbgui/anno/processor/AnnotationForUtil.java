package com.jinlin.dbgui.anno.processor;

import com.jinlin.dbgui.GuiUtil;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author SunYuLin
 * @date Created on 2018/4/21
 */
public class AnnotationForUtil {

    public static boolean processHasEmptyEditableView(Container holder) {
        Class<?> cls = holder.getClass();
        for (Field field : cls.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object obj = field.get(holder);
                if (obj == null) continue;
                if (obj instanceof JTextComponent) {
                    String text = ((JTextComponent) obj).getText();
                    if (GuiUtil.isEmpty(text)) return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean processHasEmptyStringField(Object obj) {
        Class<?> cls = obj.getClass();
        for (Field field : cls.getDeclaredFields()) {
            if (field.getType() != String.class) continue;
            field.setAccessible(true);
            try {
                Object object = field.get(obj);
                if (object == null || "".equals(object)) {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static void processCopyValue(Object src, Object tar) {
        Field[] fields = src.getClass().getDeclaredFields();
        for (Field field : tar.getClass().getDeclaredFields()) {
            for (Field srcField : fields) {
                if (srcField.getName().equals(field.getName())) {
                    srcField.setAccessible(true);
                    field.setAccessible(true);
                    try {
                        Object srcValue = srcField.get(src);
                        field.set(tar, srcValue);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    //不对外配置。获取泛型对象首个泛型的实例，抛出异常便于提醒用户。
    public static <T> T getFirstGenericInstance(Object obj) throws Exception {
        ParameterizedType pt = null;
        Class<?> cls = obj.getClass();
        for (int i = 0; i < 3; i++) {//最多允许3次继承
            Type type = cls.getGenericSuperclass();//获取父类的泛型
            if (type instanceof ParameterizedType) {
                pt = (ParameterizedType) type;
                break;
            }
            cls = cls.getSuperclass();
        }
        if (pt == null) {
            throw new RuntimeException(obj.getClass()
                    + ": Generic type can be extended only 3 times.");
        }
        Class<?> clazz = (Class<?>) pt.getActualTypeArguments()[0];
        Constructor<?> conn = clazz.getDeclaredConstructor();
        conn.setAccessible(true);
        return (T) conn.newInstance();
    }
}
