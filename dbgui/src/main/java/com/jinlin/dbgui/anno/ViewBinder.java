package com.jinlin.dbgui.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 将Model数据与（列表/对话框）View绑定
 *
 * @author SunYuLin
 * @date Created on 2018/4/20
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewBinder {

    //与View对应的索引
    int index() default 0;

    //与View外部对应的binderId，便于多层列表嵌套
    int binderId() default 0;
}
