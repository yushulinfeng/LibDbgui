package com.jinlin.dbgui;

import com.google.gson.Gson;
import com.jinlin.dbgui.anno.processor.AnnotationForUtil;
import com.jinlin.dbgui.util.ButtonLabelUI;
import com.jinlin.dbgui.util.EmptyImplForDialog;
import com.jinlin.dbgui.view.Toast;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * @author SunYuLin
 * @date Created on 2018/4/18
 */
public class GuiUtil {
    private static final String HTML_TEXT_FORMAT = "<html>%s</html>";
    private static final String DOUBLE_ALIGN_FORMAT = "<html><table width='%dpx'>" +
            "<tr><td>%s</td><td align='right'>%s</td></tr></table></html>";
    private static final Gson gson = new Gson();

    public static void showErrorDialog(Component component, String title, String text) {
        JOptionPane.showMessageDialog(component, text, title, JOptionPane.ERROR_MESSAGE);
    }

    public static int showConfirmDialog(Component component, String title, String text) {
        return JOptionPane.showConfirmDialog(component, text, title,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE);
    }

    public static <MODEL> EmptyImplForDialog<MODEL> getListenerEmptyImpl() {
        return new EmptyImplForDialog<>();
    }

    public static Border newBorder(int weight, Color color) {
        return BorderFactory.createMatteBorder(weight, weight, weight, weight, color);
    }

    public static String formatDoubleAlignText(int width, String left, String right) {
        return String.format(DOUBLE_ALIGN_FORMAT, width, left, right);
    }

    public static String formatVerticalText(String text) {
        if (text == null || text.length() <= 1) return text;
        String[] args = new String[text.length()];
        for (int i = 0; i < args.length; i++) {
            args[i] = String.valueOf(text.charAt(i));
        }
        return formatVerticalText(args);
    }

    public static String formatVerticalText(String[] args) {
        if (args == null || args.length == 0) return null;
        String text = args[0];
        for (int i = 1; i < args.length; i++) {
            text += "<p/>" + args[i];
        }
        return String.format(HTML_TEXT_FORMAT, text);
    }

    public static <T> T cloneObject(Object raw) {
        String json = gson.toJson(raw);
        //noinspection unchecked
        return gson.fromJson(json, (Class<T>) raw.getClass());
    }

    public static void copyValue(Object src, Object tar) {
        if (src == null || tar == null) return;
        AnnotationForUtil.processCopyValue(src, tar);
    }

    public static boolean isEmpty(String text) {
        return text == null || text.equals("");
    }

    public static boolean isEmpty(JTextComponent view) {
        return view == null || isEmpty(view.getText());
    }

    public static boolean hasEmptyStringField(Object obj) {
        //null is self empty,so true.
        return obj == null || AnnotationForUtil.processHasEmptyStringField(obj);
    }

    public static boolean hasEmptyEditableView(Container obj) {
        //null is no view,so false.
        return obj != null && AnnotationForUtil.processHasEmptyEditableView(obj);
    }

    public static void disableButtonAsLabel(JButton button) {
        button.setEnabled(false);
        button.setUI(new ButtonLabelUI());
    }

    public static void enableButtonFromLabel(JButton button) {
        button.setEnabled(true);
        button.setUI(new JButton().getUI());
    }

    public static void showToast(Component parent, String message, int period) {
        Toast.showToast(parent, message, period);
    }

    public static void showToast(Component parent, String message) {
        Toast.showToast(parent, message);
    }

    public static <T> T[] mergeArray(T[] a, T[] b) {
        if (a == null) return b;
        if (b == null) return a;
        //noinspection unchecked //泛型数组不能直接初始化，此处借助系统的工具类
        T[] c = (T[]) Array.newInstance(a[0].getClass(), a.length + b.length);
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }
}
