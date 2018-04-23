package com.jinlin.dbgui.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author SunYuLin
 * @date Created on 2018/4/19
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ListBorder {

    int value() default 1;//weight

    int color() default 1;//argb(认为1是默认颜色)
}
