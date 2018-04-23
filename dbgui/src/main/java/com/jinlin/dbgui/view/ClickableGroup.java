package com.jinlin.dbgui.view;

import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Delegate;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 一组AbstractButton
 *
 * @author SunYuLin
 * @date Created on 2018/4/20
 */
@ToString
@NoArgsConstructor
public class ClickableGroup {
    @Delegate
    private List<AbstractButton> list = new ArrayList<>();

    public ClickableGroup(List<Object> newList) {
        list.clear();
        list.addAll(newList.stream().filter(obj -> obj instanceof AbstractButton)
                .map(obj -> (AbstractButton) obj).collect(Collectors.toList()));
    }

    public boolean isFirstEnabled() {
        return list.size() != 0 && list.get(0).isEnabled();
    }

    public Color getFirstBackground() {
        return (list.size() == 0) ? null : list.get(0).getBackground();
    }

    public void setEnabled(boolean enabled) {
        for (AbstractButton view : list) {
            view.setEnabled(enabled);
        }
    }

    public void setText(String text) {
        for (AbstractButton view : list) {
            view.setText(text);
        }
    }

    public void setName(String text) {
        for (AbstractButton view : list) {
            view.setName(text);
        }
    }

    public void addActionListener(ActionListener listener) {
        for (AbstractButton view : list) {
            view.addActionListener(listener);
        }
    }

    public void setBackground(Color bg) {
        for (AbstractButton view : list) {
            view.setBackground(bg);
        }
    }

}
