package com.jinlin.dbgui.view;

import com.jinlin.dbgui.anno.processor.AnnotationForUtil;
import com.jinlin.dbgui.anno.processor.AnnotationForView;
import com.jinlin.dbgui.gui.AbsolutelyPanel;
import com.jinlin.dbgui.util.AdapterTableParam;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * 网格列表
 *
 * @author SunYuLin
 * @date Created on 2018/4/18
 */
public abstract class AdapterTableList<HOLDER extends AbsolutelyPanel>
        extends AdapterVerticalList {
    private static final int SCROLL_BAR_WIDTH = 18;//滚动条宽度大致就是此值
    private List<HOLDER> holderList = new ArrayList<>();
    private AdapterTableParam param;
    private int column;
    @Setter
    private boolean notFillEmpty;
    @Setter
    private boolean notAddEmptyNote;

    public AdapterTableList() {
        param = new AdapterTableParam();
        AnnotationForView.processListAnnotation(this, param);
        column = param.column;
    }

    @Override
    public void notifyChanged() {
        holderList.clear();
        super.notifyChanged();
        //空表，添加空表说明组件
        JPanel mainPanel = getMainPanel();
        if (!notAddEmptyNote && getDataCount() == 0) {
            JLabel label = new JLabel("暂无数据", JLabel.CENTER);
            mainPanel.add(buildHolderPanel(label));
            mainPanel.invalidate();
        }
        //数据量较少时，添加空位补齐组件
        if (!notFillEmpty) {
            int viewHeight = getHeight();
            int nowHeight = getRowCount() * param.itemHeight;
            if (getDataCount() == 0) nowHeight += param.itemHeight;//暂无数据那行的高度
            int subHeight = viewHeight - nowHeight;
            if (subHeight > 0) {
                addEmptyView(subHeight);
            }
        }
    }

    public final HOLDER getRowViewHolder(int position) {//禁止重写
        return holderList.get(position);//List自带越界检查
    }

    @Override
    protected JComponent getRowView(int position) {
        checkItemWidth(); //确保列宽不为空
        if (param.listTitle != null) position--;//标题行索引-1，有无标题索引一致
        AbsolutelyPanel holder;
        if (position == -1) {
            holder = getTitleRow();
        } else {
            HOLDER holderNew = getNewViewHolder(position);
            bindViewHolder(holderNew, position);
            AnnotationForView.processHolderAnnotation(holderNew, param);
            holderList.add(holderNew);
            holder = holderNew;
        }
        //边界
        boolean isTop = position == -1 || (param.listTitle == null && position == 0);
        drawBorder(holder, isTop);
        //在父容器添加支撑组件确保高度
        return buildHolderPanel(holder);
    }

    @Override
    public int getRowCount() {
        int count = getDataCount();
        return (param.listTitle == null) ? (count) : (count + 1);
    }

    private void checkItemWidth() {
        if (param.itemWidth != null && param.itemWidth.length != column) {//列宽设定与列数不一致时
            int[] newWidth = new int[column];
            int sumWidth = 0;
            int i = 0;
            for (; i < param.itemWidth.length && i < column; i++) {
                newWidth[i] = param.itemWidth[i];
                sumWidth += newWidth[i];
            }
            if (column > i) {//存在剩余列为设定，均分剩余空间
                int restWidth = (getWidth() - SCROLL_BAR_WIDTH - sumWidth) / (column - i);
                for (; i < column; i++) {
                    newWidth[i] = restWidth;
                }
            }
            param.itemWidth = newWidth;
        } else if (param.itemWidth == null) {//这里才能获取到宽度
            param.itemWidth = new int[column];
            int width = (getWidth() - SCROLL_BAR_WIDTH) / column;
            for (int i = 0; i < column; i++) {
                param.itemWidth[i] = width;
            }
        }
    }

    private AbsolutelyPanel getTitleRow() {
        AbsolutelyPanel panel = new AbsolutelyPanel();
        Font font = new Font(panel.getFont().getName(), Font.BOLD,
                panel.getFont().getSize() + 2);//标题加粗并且文字大一点
        int x = 0;
        for (int i = 0; i < column; i++) {
            if (i > 0) x += param.itemWidth[i - 1];//最后一个的宽是不用加进来的
            JLabel label = new JLabel(param.listTitle[i], JLabel.CENTER);
            label.setBounds(x, 0, param.itemWidth[i], param.itemHeight);
            label.setFont(font);
            label.setForeground(Color.BLACK);
            panel.add(label);
        }
        return panel;
    }

    private void drawBorder(AbsolutelyPanel holder, boolean needTop) {
        int width = getWidth() - SCROLL_BAR_WIDTH;
        int borderWeight = param.borderWeight;
        int columnHeight = (param.itemHeight > holder.getAbsolutelyHeight())
                ? param.itemHeight
                : holder.getAbsolutelyHeight();
        int xx = 0;
        for (int i = 0; i < column; i++) {
            holder.add(buildBorderDivider(xx, 0, borderWeight, columnHeight));//纵向
            xx += param.itemWidth[i];
        }
        holder.add(buildBorderDivider(xx - borderWeight, 0,
                borderWeight, columnHeight));//右侧最后一个
        if (needTop) {
            holder.add(buildBorderDivider(0, 0,
                    width, borderWeight));//首个元素的顶部绘制
        }
        holder.add(buildBorderDivider(0, columnHeight - borderWeight,
                width, borderWeight)); //底部
    }

    private JComponent buildBorderDivider(int x, int y, int w, int h) {
        JPanel divider = new JPanel();
        divider.setBounds(x, y, w, h);
        divider.setBackground(param.borderColor);
        return divider;
    }

    private JComponent buildHolderPanel(JComponent holder) {
        int columnHeight = param.itemHeight;
        if (holder instanceof AbsolutelyPanel) {
            int panelHeight = ((AbsolutelyPanel) holder).getAbsolutelyHeight();
            if (panelHeight > columnHeight) columnHeight = panelHeight;
        }
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(holder, BorderLayout.CENTER);
        panel.add(Box.createVerticalStrut(columnHeight),//添加支撑组件，负责会无视组件高度
                BorderLayout.EAST);
        return panel;
    }

    protected HOLDER getNewViewHolder(int position) {
        try {
            return AnnotationForUtil.getFirstGenericInstance(this);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("ViewHolder with non empty constructor" +
                    " must override method 'getNewViewHolder(int)'.And not call 'super(int)'.");
        }
    }

    protected abstract void bindViewHolder(HOLDER holder, int position);

    public abstract int getDataCount();
}
