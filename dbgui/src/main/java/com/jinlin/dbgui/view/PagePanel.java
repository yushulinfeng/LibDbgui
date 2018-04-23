package com.jinlin.dbgui.view;

import com.jinlin.dbgui.GuiUtil;
import com.jinlin.dbgui.anno.Click;
import com.jinlin.dbgui.anno.DialogSuccess;
import com.jinlin.dbgui.anno.View;
import com.jinlin.dbgui.anno.ViewBinder;
import com.jinlin.dbgui.gui.AbsolutelyModelDialog;
import com.jinlin.dbgui.gui.AbsolutelyPanel;
import lombok.Data;
import lombok.Setter;

import javax.swing.*;

public class PagePanel extends AbsolutelyPanel {
    private static final int DEFAULT_SPACE = 20;//按钮间距
    private static final int DEFAULT_WIDTH = 385;
    private static final int DEFAULT_HEIGHT = 40;
    //使用protected便于继承和修改
    @View({"上一页", "75,30"})
    protected JButton btnLast;
    @View({"第1页 / 共1页", "135,30"})
    protected JButton btnNumber;
    @View({"下一页", "75,30"})
    protected JButton btnNext;

    @Setter
    private PageListener pageListener;
    private int nowPage = 1, allPage = 1; //default

    public PagePanel() {
        GuiUtil.disableButtonAsLabel(btnNumber);
        btnNumber.setToolTipText("点击进行页面跳转");
        btnNumber.setEnabled(true);
        //此时宽高为0，需要通过invokeLater来设定宽高
        SwingUtilities.invokeLater(this::updateButtonPosition);
    }

    protected void updateButtonPosition() {
        int width = getWidth();
        int height = getHeight();
        if (width == 0) width = DEFAULT_WIDTH;
        if (height == 0) height = DEFAULT_HEIGHT;
        int y = (height - 30) / 2;
        int xNum = (width - 135) / 2;
        btnLast.setLocation(xNum - DEFAULT_SPACE - 75, y);
        btnNumber.setLocation(xNum, y);
        btnNext.setLocation(xNum + 135 + DEFAULT_SPACE, y);
    }

    //从1开始
    public void setPage(int now, int all) {
        nowPage = now;
        allPage = all;
        btnNumber.setText(String.format("第%d页 / 共%d页", now, all));
        btnLast.setEnabled(now != 1);
        btnNumber.setEnabled(all != 1);// 超过1页允许选择跳转
        btnNext.setEnabled(all != 1 && now != all);
        onPageUpdate();
    }

    @Click("btnLast")
    private void gotoLastPage() {
        setPage(nowPage - 1, allPage);
    }

    @Click("btnNext")
    private void gotoNextPage() {
        setPage(nowPage + 1, allPage);
    }

    @Click("btnNumber")
    protected void gotoSelectPage() {
        new PageDialog().setSuccessListener(this::onSelectSuccess).showSelf();
    }

    private void onSelectSuccess(PageInfo pageInfo) {
        int page = -1;
        try {
            page = Integer.parseInt(pageInfo.getPage());
        } catch (Exception ignore) {
        }
        if (page <= 0 || page > allPage) {
            showErrorDialog("跳转错误", "无效的页码！");
            return;
        }
        setPage(page, allPage);
    }

    private void onPageUpdate() {
        if (pageListener != null) {
            pageListener.onPageUpdate(nowPage, allPage);
        }
    }

    public interface PageListener {
        void onPageUpdate(int nowPage, int allPage);
    }

    @View({"页码跳转", "255,90"})
    private class PageDialog extends AbsolutelyModelDialog<PageInfo> {
        @View({"页码：", "0,10,60,40"})
        private JLabel tvPage;
        @View(value = {"60,15,100,30"}, index = 1)
        private JTextField etPage;
        @View({"跳转", "175,19,60,22"})
        @DialogSuccess
        private JButton btnSure;
    }

    @Data
    private static class PageInfo {
        @ViewBinder(index = 1)
        private String page;
    }
}
