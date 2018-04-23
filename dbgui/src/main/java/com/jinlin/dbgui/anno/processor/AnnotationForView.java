package com.jinlin.dbgui.anno.processor;

import com.jinlin.dbgui.GuiUtil;
import com.jinlin.dbgui.anno.*;
import com.jinlin.dbgui.gui.AbsolutelyPanel;
import com.jinlin.dbgui.util.AdapterTableParam;
import com.jinlin.dbgui.view.AdapterTableList;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static com.jinlin.dbgui.util.AdapterTableParam.*;

/**
 * @author SunYuLin
 * @date Created on 2018/4/19
 */
public class AnnotationForView {

    //不必配置到综合方法里面
    public static void processListAnnotation(AdapterTableList obj, AdapterTableParam param) {
        Class<?> cls = obj.getClass();
        ListTitle annoTitle = cls.getAnnotation(ListTitle.class);
        ListItemWidth annoWidth = cls.getAnnotation(ListItemWidth.class);
        ListItemHeight annoHeight = cls.getAnnotation(ListItemHeight.class);
        ListColumnCount annoColumn = cls.getAnnotation(ListColumnCount.class);
        ListBorder annoBorder = cls.getAnnotation(ListBorder.class);
        if (annoTitle != null) param.listTitle = annoTitle.value();
        if (annoWidth != null) param.itemWidth = annoWidth.value();
        if (annoHeight != null) param.itemHeight = annoHeight.value();
        if (annoColumn != null) param.column = annoColumn.value();
        //前三个优先，标题允许为空，宽默认为空均分，高默认高度，列数默认列数
        if (param.itemHeight <= 0) param.itemHeight = DEFAULT_ITEM_HEIGHT;
        if (param.itemWidth != null) param.column = param.itemWidth.length;
        if (param.listTitle != null) param.column = param.listTitle.length;//更权重
        if (param.column <= 0) param.column = DEFAULT_COLUMN;
        //边框
        param.borderWeight = DEFAULT_BORDER_WEIGHT;
        param.borderColor = new Color(DEFAULT_BORDER_COLOR, true);
        if (annoBorder != null) {
            param.borderWeight = annoBorder.value();
            if (annoBorder.color() != 1) {
                param.borderColor = new Color(annoBorder.color(), true);
            }
        }
    }

    //同上
    public static void processHolderAnnotation(AbsolutelyPanel holder, AdapterTableParam param) {
        processHolderViewAnnotation(holder, param);
        AnnotationForUI.processCollectView(holder);
    }

    //不对外配置。将model的值，复制到对应index的组件上显示。
    public static void processViewBinderAnnotation(AbsolutelyPanel holder, Object model) {
        Map<Field, Field> map = processViewDataMatcher(holder, model);
        for (Field viewField : map.keySet()) {
            Field dataField = map.get(viewField);
            try {
                Object dataObj = dataField.get(model);
                String data = (dataObj == null) ? "" : String.valueOf(dataObj);
                Object view = viewField.get(holder);
                Method method = viewField.getType().getMethod("setText", String.class);
                method.invoke(view, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //不对外配置。将holder中对应index的组件，其内容复制到model对应的键。只支持model的String类型。
    public static void processDataBinderAnnotation(Container holder, Object model) {
        Map<Field, Field> map = processViewDataMatcher(holder, model);
        for (Field viewField : map.keySet()) {
            Field dataField = map.get(viewField);
            if (dataField.getType() != String.class) continue;
            try {
                Object view = viewField.get(holder);
                if (!(view instanceof JTextComponent)) continue;
                String text = ((JTextComponent) view).getText();
                dataField.set(model, text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static int processHolderViewBinderId(Object holder) {
        Class<?> viewCls = holder.getClass();
        ViewBinder viewBinder = viewCls.getAnnotation(ViewBinder.class);
        int viewBinderId = 0;
        if (viewBinder != null) {
            viewBinderId = viewBinder.binderId();
        }
        return viewBinderId;
    }

    private static Map<Field, Field> processViewDataMatcher(Object holder, Object model) {
        Map<Field, Field> map = new HashMap<>();
        if (model instanceof String) return map;//String作为保留类型，不处理。
        Field[] viewFields = holder.getClass().getDeclaredFields();
        int viewBinderId = processHolderViewBinderId(holder);
        Class<?> cls = model.getClass();
        Field[] dataFields = cls.getDeclaredFields();
        while (cls.getSuperclass() != Object.class) {//允许多次继承，继承注解共享
            cls = cls.getSuperclass();
            //子类的在后面，这样子类属性就可以覆盖父类的同索引属性了。
            dataFields = GuiUtil.mergeArray(cls.getDeclaredFields(), dataFields);
        }
        for (Field field : dataFields) {
            ViewBinder binder = field.getAnnotation(ViewBinder.class);
            if (binder == null) continue;
            int binderId = binder.binderId();
            int index = binder.index();
            if (viewBinderId != binderId) continue;
            for (Field f : viewFields) {
                View v = f.getAnnotation(View.class);
                if (v == null) continue;
                int viewIndex = v.index();
                if (index != viewIndex) continue;
                f.setAccessible(true);
                field.setAccessible(true);
                map.put(f, field);
            }
        }
        return map;
    }

    private static void processHolderViewAnnotation(AbsolutelyPanel holder,
                                                    AdapterTableParam param) {
        Class<?> cls = holder.getClass();
        for (Field field : cls.getDeclaredFields()) {
            View v = field.getAnnotation(View.class);
            if (v == null || v.index() < 0 || v.index() >= param.column) continue;
            field.setAccessible(true);
            int index = v.index();
            Component view;
            try {
                view = (Component) field.get(holder);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            int x = 0;
            for (int i = 0; i < index; i++) {
                x += param.itemWidth[i];
            }
            //计算padding
            int columnHeight = (param.itemHeight > holder.getAbsolutelyHeight())
                    ? param.itemHeight
                    : holder.getAbsolutelyHeight();
            int rowWidth = param.itemWidth[index];
            int padTop = 0, padLeft = 0, padBottom = 0, padRight = 0;
            if (v.padding().length == 1) {//4padding
                padLeft = padTop = padBottom = padRight = v.padding()[0];
            } else if (v.padding().length == 2) {//width,height
                padLeft = padRight = ((rowWidth - v.padding()[0]) / 2);
                padTop = padBottom = ((columnHeight - v.padding()[1]) / 2);
            } else if (v.padding().length == 4) {//top, left, bottom, right
                padTop = v.padding()[0];
                padLeft = v.padding()[1];
                padBottom = v.padding()[2];
                padRight = v.padding()[3];
            }
            view.setBounds(x + padLeft, padTop,
                    rowWidth - padLeft - padRight,
                    columnHeight - padTop - padBottom);
        }
    }

}
