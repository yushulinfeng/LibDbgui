package com.jinlin.dbgui.gui;

import com.jinlin.dbgui.Dbgui;
import com.jinlin.dbgui.GuiUtil;

import javax.swing.*;

/**
 * @author SunYuLin
 * @date Created on 2018/4/18
 */
public class AbsolutelyFrame extends JFrame {

    public AbsolutelyFrame() {
        Dbgui.processView(this);
        Dbgui.processAutoCreate(this);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    //常用功能
    public void disposeForNext(JFrame frame) {
        dispose();
        if (frame != null) {
            frame.setVisible(true);
        }
    }

    public void showErrorDialog(String title, String text) {
        GuiUtil.showErrorDialog(this, title, text);
    }
}
