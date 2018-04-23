package com.jinlin.dbgui.gui;

import com.jinlin.dbgui.Dbgui;
import com.jinlin.dbgui.anno.ViewBinder;
import com.jinlin.dbgui.anno.processor.AnnotationForUtil;
import com.jinlin.dbgui.anno.processor.AnnotationForView;
import com.jinlin.dbgui.util.DialogClosedListener;
import com.jinlin.dbgui.util.DialogSuccessListener;
import com.jinlin.dbgui.GuiUtil;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Field;

/**
 * @author SunYuLin
 * @date Created on 2018/4/19
 */
public abstract class AbsolutelyModelDialog<MODEL> extends JDialog {
    private boolean clickCloseButton;
    private DialogClosedListener<MODEL> closedListener;
    private DialogSuccessListener<MODEL> successListener;

    public AbsolutelyModelDialog() {
        Dbgui.processView(this);
        Dbgui.processAutoCreate(this);
        setResizable(false);
        setModal(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        clickCloseButton = false;
        addWindowListener(new DialogCloseAdapter());
    }

    //for override
    protected MODEL dialogModelInfo() {
        try {
            MODEL model = AnnotationForUtil.getFirstGenericInstance(this);
            AnnotationForView.processDataBinderAnnotation(this, model);
            return model;
        } catch (Exception e) {
            throw new RuntimeException("Model with non empty constructor" +
                    " must override method 'dialogModelInfo()'.And not call 'super()'.");
        }
    }

    public AbsolutelyModelDialog setClosedListener(DialogClosedListener<MODEL> closedListener) {
        this.closedListener = closedListener;
        return this;
    }

    public AbsolutelyModelDialog setSuccessListener(DialogSuccessListener<MODEL> successListener) {
        this.successListener = successListener;
        return this;
    }

    public void showSelf() {
        setVisible(true);
    }

    public void successAndClose() {
        dispose();//请不要复用
    }

    public void showErrorDialog(String title, String text) {
        GuiUtil.showErrorDialog(this, title, text);
    }

    private class DialogCloseAdapter extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            clickCloseButton = true;
        }

        @Override
        public void windowClosed(WindowEvent e) {
            //这里需要减少不必要调用，只success监听时，右上角关闭不能调用。
            if (closedListener == null && successListener == null) {
                return;
            }
            MODEL model = dialogModelInfo();
            if (closedListener != null) {
                closedListener.onDialogClosed(model, clickCloseButton);
            }
            if (successListener != null && !clickCloseButton) {
                successListener.onDialogClosed(model);
            }
        }
    }
}
