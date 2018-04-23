package com.jinlin.dbgui.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库-写
 *
 * @author SunYuLin
 * @date Created on 2018/4/17
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Modify {
    String value() default "";

    Class<?> table() default Void.class;
}
