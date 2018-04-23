package com.jinlin.dbgui.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库-读
 *
 * @author SunYuLin
 * @date Created on 2018/4/17
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {
    String value() default "";

    Class<?> table() default Void.class;

    String groupMerge() default "";//多列的话，#分隔

    String increaseSort() default "";//排序冲突则此项优先

    String decreaseSort() default "";
}
