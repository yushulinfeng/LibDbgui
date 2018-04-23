package com.jinlin.dbgui;

import com.jinlin.dbgui.anno.processor.AnnotationForDB;
import com.jinlin.dbgui.db.DatabaseUtil;
import com.jinlin.dbgui.anno.processor.AnnotationForUI;

/**
 * 综合类
 *
 * @author SunYuLin
 * @date Created on 2018/4/18
 */
public class Dbgui {

    public static void initDatabase(String dbName, String userName, String passWord) {
        DatabaseUtil.getInstance().init(dbName, userName, passWord);
    }

    public static void releaseDatabase() {
        DatabaseUtil.getInstance().release();
    }

    public static void processAutoCreate(Object obj) {
        AnnotationForDB.processAutoCreate(obj);
    }

    public static void processView(Object obj) {
        AnnotationForUI.processView(obj);
    }

}
