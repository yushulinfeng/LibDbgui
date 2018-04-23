package com.jinlin.dbgui.anno.processor;

import com.jinlin.dbgui.anno.Click;
import com.jinlin.dbgui.anno.CollectView;
import com.jinlin.dbgui.anno.DialogSuccess;
import com.jinlin.dbgui.anno.View;
import com.jinlin.dbgui.gui.AbsolutelyModelDialog;
import com.jinlin.dbgui.view.ClickableGroup;
import com.jinlin.dbgui.view.EditableGroup;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.lang.reflect.*;
import java.util.ArrayList;

/**
 * 图形界面注解解析
 *
 * @author SunYuLin
 * @date Created on 2018/4/17
 */
public class AnnotationForUI {

    public static void processView(Object obj) {
        if (obj instanceof Window) {
            processViewForClass((Window) obj);
        }
        if (obj instanceof Container) {
            processViewForField((Container) obj);
            processCollectView((Container) obj);
        }
        if (obj instanceof AbsolutelyModelDialog) {
            processDialogSuccess((AbsolutelyModelDialog) obj);
        }
        processClick(obj);
    }

    //不必对外配置
    public static void processCollectView(Container holder) {
        Class<?> cls = holder.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            CollectView g = field.getAnnotation(CollectView.class);
            if (g == null) continue;
            Class<?> fieldType = field.getType();
            if (fieldType != EditableGroup.class && fieldType != ClickableGroup.class
                    && fieldType != List.class) {
                throw new RuntimeException(field + ":Annotation 'CollectView' can only " +
                        "decorate type 'java.util.List' or 'EditableGroup/ClickableGroup'.");
            }
            Class<?> collectType = g.type();
            boolean editable = g.editable();
            field.setAccessible(true);
            if (fieldType == java.util.List.class) {
                Type genericType = null;
                try {
                    genericType = ((ParameterizedType) field.getGenericType())
                            .getActualTypeArguments()[0];
                } catch (Exception ignore) {
                    //genericType will be null, so will throw the follow exception
                }
                if (genericType != collectType) {
                    throw new RuntimeException(field + ":Annotation CollectView's param 'type' " +
                            "must match java.util.List's generic type.");
                }
            }
            java.util.List<Object> list = new ArrayList<>();
            try {
                for (Field f : fields) {
                    if (f.getType() != collectType) continue;
                    f.setAccessible(true);
                    Object object = f.get(holder);
                    list.add(object);
                    if (object instanceof JTextComponent && !editable) {
                        ((JTextComponent) object).setEditable(false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (fieldType == EditableGroup.class) {
                    field.set(holder, new EditableGroup(list));
                } else if (fieldType == ClickableGroup.class) {
                    field.set(holder, new ClickableGroup(list));
                } else {//List.class
                    field.set(holder, list);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //解析类上的 View 注解
    private static void processViewForClass(Window frame) {
        View v = frame.getClass().getAnnotation(View.class);
        if (v == null) return;
        NameTagPos info = getNameTagPosInfo(v.value());
        if (info.text != null) {
            if (frame instanceof JFrame) {
                ((JFrame) frame).setTitle(info.text);
            } else if (frame instanceof JDialog) {
                ((JDialog) frame).setTitle(info.text);
            }
        }
        if (info.pos.length == 2) {
            frame.setSize(info.w, info.h);
            frame.setLocationRelativeTo(null);
        } else if (info.pos.length == 4) {
            frame.setBounds(info.x, info.y,
                    info.w, info.h);
        }
    }

    //解析变量上的 View 注解
    private static void processViewForField(Container obj) {//use obj
        obj.setLayout(null); //需要在这里设定
        Class<?> cls = obj.getClass();
        for (Field field : cls.getDeclaredFields()) {
            View v = field.getAnnotation(View.class);
            if (v == null) continue;
            field.setAccessible(true);
            NameTagPos info = getNameTagPosInfo(v.value());
            Component view;
            try {
                view = (Component) field.getType().newInstance();
                field.set(obj, view);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            if (view instanceof JLabel) {//JLable居中文本
                ((JLabel) view).setHorizontalAlignment(JLabel.CENTER);
            }
            if (view instanceof JTextArea) {//JTextArea自动换行和换行不切分单词
                ((JTextArea) view).setLineWrap(true);
                ((JTextArea) view).setWrapStyleWord(true);
            }
            if (info.text != null) {
                try {
                    Method method = field.getType().getMethod("setText", String.class);
                    method.invoke(view, info.text);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (info.tag != null) {
                view.setName(info.tag);
            }
            if (v.scroll()) {//需要滚动的话，进行一次包装
                view = new JScrollPane(view);
            }
            if (info.pos.length == 4) {//only enable 4 param
                view.setBounds(info.x, info.y, info.w, info.h);
            }
            obj.add(view);
        }
    }

    private static void processDialogSuccess(final AbsolutelyModelDialog obj) {
        Class<?> cls = obj.getClass();
        for (Field field : cls.getDeclaredFields()) {
            DialogSuccess succ = field.getAnnotation(DialogSuccess.class);
            if (succ == null) continue;
            if (field.getType() != JButton.class) continue;
            JButton button;
            try {
                field.setAccessible(true);
                button = (JButton) field.get(obj);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            button.addActionListener(e -> obj.successAndClose());
        }
    }

    //解析 Click 注解
    private static void processClick(Object obj) {
        Class<?> cls = obj.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Method method : cls.getDeclaredMethods()) {
            Click c = method.getAnnotation(Click.class);
            if (c == null) continue;
            boolean needParam = (method.getParameterCount() == 1)
                    && (method.getParameterTypes()[0] == JButton.class);
            if ("".equals(c.value())) {//全部
                for (Field field : fields) {
                    if (field.getType() == JButton.class) {
                        addListener(method, field, obj, needParam);
                    }
                }
            } else {
                Field field;
                try {
                    field = cls.getDeclaredField(c.value());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(method + ": Click annotation has error value.");
                }
                addListener(method, field, obj, needParam);
            }
        }
    }

    private static void addListener(final Method method, Field field, Object obj, boolean needParam) {
        method.setAccessible(true);
        field.setAccessible(true);
        try {
            Object fieldObj = field.get(obj);
            final Object[] param = needParam ? new Object[1] : null;
            if (param != null) param[0] = fieldObj;
            Object listener = Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(),
                    new Class[]{ActionListener.class}, (proxy, methodNew, args) -> {
                        if (param == null) method.invoke(obj);
                        else method.invoke(obj, param);
                        return null;
                    });
            Method addClickMethod = field.getType()
                    .getMethod("addActionListener", ActionListener.class);
            addClickMethod.invoke(fieldObj, listener);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static NameTagPos getNameTagPosInfo(String[] args) {
        return new NameTagPos(args);
    }

    private static class NameTagPos {
        private String text;
        private String tag;
        private String[] pos;// not null
        private int x, y, w, h;

        private NameTagPos(String[] args) {
            String position = null;
            //split string
            if (args.length == 1) {
                position = args[0];
            } else if (args.length == 2) {
                text = args[0];
                position = args[1];
            } else if (args.length == 3) {
                tag = args[0];
                text = args[1];
                position = args[2];
            }
            //split int
            if (position == null) {
                pos = new String[0];
                return;
            }
            pos = position.split(",");
            switch (pos.length) {
                case 4:
                    x = Integer.parseInt(pos[0]);
                    y = Integer.parseInt(pos[1]);
                    pos[0] = pos[2];
                    pos[1] = pos[3];
                case 2:
                    w = Integer.parseInt(pos[0]);
                    h = Integer.parseInt(pos[1]);
                    break;
            }
        }
    }
}
