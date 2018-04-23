package com.jinlin.dbgui.view;

import com.jinlin.dbgui.GuiUtil;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Delegate;

import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 一组TextField
 *
 * @author SunYuLin
 * @date Created on 2018/4/20
 */
@ToString
@NoArgsConstructor
public class EditableGroup {
    @Delegate
    private List<JTextComponent> list = new ArrayList<>();
    private List<String> textList = new ArrayList<>();

    public EditableGroup(List<Object> newList) {
        list.clear();
        list.addAll(newList.stream().filter(obj -> obj instanceof JTextComponent)
                .map(obj -> (JTextComponent) obj).collect(Collectors.toList()));
    }

    public boolean isFirstEditable() {
        return list.size() != 0 && list.get(0).isEditable();
    }

    public boolean isFirstEnabled() {
        return list.size() != 0 && list.get(0).isEnabled();
    }

    public Color getFirstBackground() {
        return (list.size() == 0) ? null : list.get(0).getBackground();
    }

    public boolean hasEmptyEditableView() {
        for (JTextComponent view : list) {
            if (GuiUtil.isEmpty(view.getText())) return true;
        }
        return false;
    }

    public void saveCurrentViewText() {
        textList.clear();
        textList.addAll(list.stream().map(JTextComponent::getText)
                .collect(Collectors.toList()));
    }

    public boolean restoreLastViewText() {
        if (textList.size() == 0 || textList.size() != list.size()) {
            return false;
        }
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setText(textList.get(i));
        }
        return true;
    }

    public void setEditable(boolean editable) {
        for (JTextComponent view : list) {
            view.setEditable(editable);
        }
    }

    public void setEnabled(boolean enabled) {
        for (JTextComponent view : list) {
            view.setEnabled(enabled);
        }
    }

    public void setText(String text) {
        for (JTextComponent view : list) {
            view.setText(text);
        }
    }

    public void setName(String text) {
        for (JTextComponent view : list) {
            view.setName(text);
        }
    }

    public void setBackground(Color bg) {
        for (JTextComponent view : list) {
            view.setBackground(bg);
        }
    }
}
