package com.jinlin.dbgui.view;

import com.jinlin.dbgui.Dbgui;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;

/**
 * 适配器列表类，类似于安卓的自定义列表。（Nx1）
 * 另外，请优先考虑JList和JTable，最后考虑本组件。
 *
 * @author SunYuLin
 * @date Created on 2018/4/18
 */
public abstract class AdapterVerticalList extends JScrollPane {
    private static final int DEFAULT_SCROLL_SPEED = 5;//滚轮滚动量
    private MouseWheelAdapter wheelAdapter;
    private JPanel mainPanel;

    public AdapterVerticalList() {
        Dbgui.processAutoCreate(this);
        mainPanel = new JPanel();
        setViewportView(mainPanel);
        getVerticalScrollBar().setUnitIncrement(DEFAULT_SCROLL_SPEED);
    }

    public void notifyChanged() {
        int row = getRowCount();
        mainPanel.removeAll();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        for (int i = 0; i < row; i++) {
            mainPanel.add(getRowView(i));
        }
        mainPanel.invalidate();
    }

    //此处无法获取内容组件高度，故无法自动填补空白。可后期手动调用此方法。
    public void addEmptyView(int height) {
        if (height < 0) return;
        mainPanel.add(Box.createVerticalStrut(height));
        mainPanel.invalidate();
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public void interceptMouseWheelEvent(boolean intercept) {
        //这样防止重复添加和重复移除
        if (!intercept && wheelAdapter == null) {//禁用拦截
            addMouseWheelListener(wheelAdapter = new MouseWheelAdapter());
        } else if (intercept && wheelAdapter != null) {//还原(默认原本是拦截的)
            removeMouseWheelListener(wheelAdapter);
        }
    }

    //for data update
    public void scrollToTop() {
        scrollTo(1);
        scrollTo(0);
    }

    public void scrollTo(final int value) {
        SwingUtilities.invokeLater(() -> getVerticalScrollBar().setValue(value));
    }

    private class MouseWheelAdapter extends MouseAdapter {
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            getParent().dispatchEvent(e);
        }
    }

    protected abstract JComponent getRowView(int position);

    public abstract int getRowCount();//对外是有用的
}
