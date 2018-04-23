package com.jinlin.dbgui.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 视图组件类
 *
 * @author SunYuLin
 * @date Created on 2018/4/18
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface View {
    //FIELD:tag(3),text(2),location(1),only init(0)
    //TYPE:title(2),location/size(1),don't use(0)
    String[] value() default {};

    //for outer scroll view
    boolean scroll() default false;

    //作为ViewHolder的索引
    int index() default 0;

    //作为ViewHolder的间距(4padding)(width,height)(top, left, bottom, right)
    int[] padding() default {};
}
