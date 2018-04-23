package com.jinlin.dbgui.util;

import sun.swing.SwingUtilities2;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

/**
 * 按钮像Label一样的外观，设置为不可用时，只去掉鼠标效果，文字不会变灰。
 *
 * @author SunYuLin
 * @date Created on 2018/4/21
 */
public class ButtonLabelUI extends BasicButtonUI {
    @Override
    protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
        AbstractButton b = ((AbstractButton) c);
        ButtonModel model = b.getModel();
        if (!model.isEnabled()) {
            FontMetrics fm = SwingUtilities2.getFontMetrics(c, g);
            int mnemonicIndex = b.getDisplayedMnemonicIndex();
            g.setColor(Color.DARK_GRAY);
            SwingUtilities2.drawStringUnderlineCharAt(c, g, text, mnemonicIndex,
                    textRect.x + getTextShiftOffset(),
                    textRect.y + fm.getAscent() + getTextShiftOffset());
        } else super.paintText(g, c, textRect, text);
    }
}
