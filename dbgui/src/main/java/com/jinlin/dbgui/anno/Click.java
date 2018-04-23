package com.jinlin.dbgui.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 单击事件绑定
 * 方法返回viod，参数可空或者AbstractButton相关子类
 *
 * @author SunYuLin
 * @date Created on 2018/4/17
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Click {
    String value() default "";
}
