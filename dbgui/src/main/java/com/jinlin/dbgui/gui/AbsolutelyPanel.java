package com.jinlin.dbgui.gui;

import com.jinlin.dbgui.Dbgui;
import com.jinlin.dbgui.GuiUtil;

import javax.swing.*;

/**
 * @author SunYuLin
 * @date Created on 2018/4/18
 */
public class AbsolutelyPanel extends JPanel {

    public AbsolutelyPanel() {
        Dbgui.processView(this);
        Dbgui.processAutoCreate(this);
    }

    //for override,for table list.
    public int getAbsolutelyHeight() {
        return 0;//for speed,so not getHeight.
    }

    public void showErrorDialog(String title, String text) {
        GuiUtil.showErrorDialog(this, title, text);
    }
}
