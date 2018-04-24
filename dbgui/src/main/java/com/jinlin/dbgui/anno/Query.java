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

    int ALL_PAGE = -1; //for onePage.

    int onePage() default 0;//指定分页，需要最后有一个页码参数(页码-1全部(自己算页数)，0报错，1首页)
}
