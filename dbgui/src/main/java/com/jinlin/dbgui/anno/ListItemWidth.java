package com.jinlin.dbgui.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 列表项宽度，允许少于列数
 *
 * @author SunYuLin
 * @date Created on 2018/4/18
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ListItemWidth {
    int[] value();
}
