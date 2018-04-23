package com.jinlin.dbgui.util;

/**
 * 对话框关闭监听。
 * 需要返回的数据，是否是因为点击了右上角关闭按钮
 *
 * @author SunYuLin
 * @date Created on 2018/4/19
 */
public interface DialogClosedListener<MODEL> {
    void onDialogClosed(MODEL info, boolean clickCloseButton);
}
