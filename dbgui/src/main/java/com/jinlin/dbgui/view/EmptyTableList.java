package com.jinlin.dbgui.view;

import com.jinlin.dbgui.gui.AbsolutelyPanel;

/**
 * @author SunYuLin
 * @date Created on 2018/4/20
 */
public class EmptyTableList extends AdapterTableList<AbsolutelyPanel> {

    //non scroll auto
    public EmptyTableList() {
        setNotFillEmpty(true);
        setNotAddEmptyNote(true);
    }

    @Override
    protected void bindViewHolder(AbsolutelyPanel absolutelyPanel, int position) {
        //empty
    }

    @Override
    public int getDataCount() {
        return 0;//zero for only title.
    }
}
