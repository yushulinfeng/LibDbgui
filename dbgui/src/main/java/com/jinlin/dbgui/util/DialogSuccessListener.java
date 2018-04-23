package com.jinlin.dbgui.util;

/**
 * 对话框关闭监听。用户手动调用dispose而不是右上角关闭按钮。
 *
 * @author SunYuLin
 * @date Created on 2018/4/19
 */
public interface DialogSuccessListener<MODEL> {
    void onDialogClosed(MODEL info);
}
