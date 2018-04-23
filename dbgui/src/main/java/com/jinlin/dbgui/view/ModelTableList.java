package com.jinlin.dbgui.view;

import com.jinlin.dbgui.anno.processor.AnnotationForView;
import com.jinlin.dbgui.gui.AbsolutelyPanel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SunYuLin
 * @date Created on 2018/4/20
 */
public abstract class ModelTableList<HOLDER extends AbsolutelyPanel, MODEL>
        extends AdapterTableList<HOLDER> {
    protected List<MODEL> items = new ArrayList<>();

    public void updateItems(List<? extends MODEL> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyChanged();
        scrollToTop();//无奈之举，否则界面可能不会刷新。将来有时间可以考虑优化。
    }

    @Override
    protected void bindViewHolder(HOLDER holder, int position) {
        MODEL model = items.get(position);
        AnnotationForView.processViewBinderAnnotation(holder, model);
        bindModelHolder(model, holder, position);
    }

    @Override
    public int getDataCount() {
        return items.size();
    }

    protected abstract void bindModelHolder(MODEL model, HOLDER holder, int position);

}
