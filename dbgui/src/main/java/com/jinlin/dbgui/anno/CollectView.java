package com.jinlin.dbgui.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author SunYuLin
 * @date Created on 2018/4/20
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CollectView {

    //要收集的类型
    Class<?> type();

    //可编辑类型，是否允许编辑
    boolean editable() default true;
}
