package com.jinlin.dbgui.view;

import javax.swing.*;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 类似于Android的Toast提示。
 * 参考于网络资源：http://abian.iteye.com/blog/2025298
 *
 * @author SunYuLin
 * @date Created on 2018/4/21
 */
public class Toast extends JWindow {
    private final Insets insets = new Insets(12, 24, 12, 24);
    private Color background = new Color(0x515151);
    private Color foreground = Color.WHITE;
    private Font font;
    private String message;
    private int period;

    private Toast(Component parent, String message, int period) {
        this.message = message;
        this.period = period;
        font = new Font("宋体", Font.PLAIN, 16);
        setSize(getStringSize(font, message));
        setLocationRelativeTo(parent);//居中
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Composite oldComposite = g2.getComposite(); // old
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();
        g2.setColor(background);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        g2.setColor(foreground);
        g2.drawString(message, insets.left, fm.getAscent() + insets.top);
        g2.setComposite(oldComposite); // restore
    }

    public void start() {
        this.setVisible(true);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                dispose();//释放资源，不再复用
            }
        }, period);
    }

    private Dimension getStringSize(Font font, String text) {
        FontRenderContext renderContext = new FontRenderContext(null, true, false);
        Rectangle2D bounds = font.getStringBounds(text, renderContext);
        int width = (int) bounds.getWidth() + 2 * insets.left;
        int height = (int) bounds.getHeight() + insets.top * 2;
        return new Dimension(width, height);
    }

    public static void showToast(Component parent, String message, int period) {
        new Toast(parent, message, period).start();
    }

    public static void showToast(Component parent, String message) {
        showToast(parent, message, 1200);
    }
}
