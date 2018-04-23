package com.jinlin.dbgui.util;

/**
 * 接口空实现
 *
 * @author SunYuLin
 * @date Created on 2018/4/19
 */
public class EmptyImplForDialog<MODEL>
        implements DialogClosedListener<MODEL>,
        DialogSuccessListener<MODEL> {

    @Override
    public void onDialogClosed(MODEL info, boolean clickCloseButton) {
        //empty
    }

    @Override
    public void onDialogClosed(MODEL info) {
        //empty
    }
}
