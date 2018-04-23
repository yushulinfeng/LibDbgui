package com.jinlin.dbgui.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 列表项目中的组件索引，从0开始
 *
 * @author SunYuLin
 * @date Created on 2018/4/18
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ListItemIndex {
    int value();
}
