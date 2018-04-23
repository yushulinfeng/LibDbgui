package com.jinlin.dbgui.anno.processor;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jinlin.dbgui.anno.AutoCreate;
import com.jinlin.dbgui.anno.AutoIncrement;
import com.jinlin.dbgui.anno.Modify;
import com.jinlin.dbgui.anno.Query;
import com.jinlin.dbgui.db.DatabaseUtil;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库注解解析
 *
 * @date Created on 2018/4/17
 */
public class AnnotationForDB {

    //解析AutoCreate注解，自动创建接口对象
    public static void processAutoCreate(Object obj) {
        Class<?> cls = obj.getClass();
        for (Field field : cls.getDeclaredFields()) {
            if (field.getAnnotation(AutoCreate.class) == null) {
                continue;
            }
            field.setAccessible(true);
            try {
                field.set(obj, processInterface(field.getType()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //解析Query和Modify注解，转换到真正的数据库操作
    private static Object processInterface(Class<?> cls) throws Exception {
        final List<MethodNote> list = new ArrayList<>();
        for (Method method : cls.getDeclaredMethods()) {
            Query q = method.getAnnotation(Query.class);
            Modify m = method.getAnnotation(Modify.class);
            String value;
            if (q != null) {//query
                value = q.value().equals("")
                        ? q.table().getSimpleName().toLowerCase()
                        : q.value();
                if (value.equals(Void.class.getSimpleName().toLowerCase())) {
                    throw new RuntimeException(method + ":Annotation can not be empty.");
                }
                Type type = ((ParameterizedType) method.getGenericReturnType())
                        .getActualTypeArguments()[0];
                if (!q.groupMerge().equals("")) {//返回为两层列表，故需要再次取泛型
                    try {
                        type = ((ParameterizedType) type).getActualTypeArguments()[0];
                    } catch (Exception e) {//转换为易懂的Exception提示
                        throw new Exception(method + ":Error return type. " +
                                "GroupMerge need 'List<List<T>>'.");
                    }
                }
                list.add(new MethodNote(method, value, type)
                        .setQueryInfo(q.increaseSort(), q.decreaseSort(), q.groupMerge()));
            } else if (m != null) {//modify
                value = m.value().equals("")
                        ? m.table().getSimpleName().toLowerCase()
                        : m.value();
                if (value.equals(Void.class.getSimpleName().toLowerCase())) {
                    throw new RuntimeException(method + ":Annotation can not be empty.");
                }
                list.add(new MethodNote(method, value));
            }
        }
        InvocationHandler handler = (proxy, method, args) -> {
            for (MethodNote note : list) {
                if (note.method.equals(method)) {
                    if (note.isQuery) {
                        List<?> result = dealQuery(note, args);
                        if (note.groupMerge.equals("")) {//无需合并
                            return result;
                        } else {
                            return dealQueryGroup(result, note.groupMerge);
                        }
                    } else {
                        return dealModify(note, args);
                    }
                }
            }
            return null;
        };
        return Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(),
                new Class[]{cls}, handler);
    }

    private static <T> List<List<T>> dealQueryGroup(List<T> result, String groupMerge) {
        List<List<T>> list = new ArrayList<>();
        if (result == null) {
            return list;//non null
        }
        if (result.size() == 0) {
            return list;
        }
        if (result.size() == 1) {
            list.add(result);
            return list;
        }
        String[] merge = groupMerge.split("#");
        //聚类
        Gson gson = new Gson();
        int len = result.size();
        String[] cls = new String[len];
        int[] index = new int[len];
        for (int i = 0; i < len; i++) {
            JsonObject json = gson.toJsonTree(result.get(i)).getAsJsonObject();
            String value = null;//enable null
            for (String column : merge) {
                JsonElement element = json.get(column);
                String oneValue = null;
                if (element != null) {
                    oneValue = element.getAsString();
                }
                if (value == null && oneValue != null) {//初值
                    value = oneValue;
                } else if (value != null && oneValue != null) {//后续值，保证全空则为null.
                    value += "#" + oneValue;
                }
            }
            cls[i] = value;
            index[i] = -1;
        }
        int id = -1;
        for (int i = 0; i < len; i++) {
            if (index[i] != -1) continue;
            id++;
            String now = cls[i];
            index[i] = id;
            for (int j = i + 1; j < len; j++) {
                if ((now == null && cls[j] == null)
                        || (now != null && now.equals(cls[j]))) {//相同类别
                    index[j] = id;
                }
            }
        }
        for (int i = 0; i < id + 1; i++) {
            list.add(new ArrayList<>());
        }
        for (int i = 0; i < len; i++) {
            list.get(index[i]).add(result.get(i));
        }
        return list;
    }

    //执行查询操作
    private static List<?> dealQuery(MethodNote note, Object[] args) {
        String sql = getRealSql(note, args, true);
        //deal sort
        if (!note.incSort.equals("")) {
            sql += " order by `" + note.incSort + "`";
        } else if (!note.desSort.equals("")) {
            sql += " order by `" + note.desSort + "` desc";
        }
        return DatabaseUtil.getInstance().query(sql, note.type);
    }

    //执行增删改操作
    private static boolean dealModify(MethodNote note, Object[] args) throws Exception {
        String sql = getRealSql(note, args, false);
        return DatabaseUtil.getInstance().modify(sql);
    }

    private static String getRealSql(MethodNote note, Object[] args,
                                     boolean trueQueryFalseModify) {
        String sql;
        if (!note.sql.contains(" ")) {//add db
            if (trueQueryFalseModify) {
                sql = "select * from `" + note.sql + "`";
            } else {
                try {
                    sql = buildInsertSql(note, args);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        } else {//with param
            sql = buildSql(note, args);
        }
        return sql;
    }

    private static String buildInsertSql(MethodNote note, Object[] args) throws Exception {
        if (args.length != 1) {
            throw new RuntimeException(note.method + ":Insert only enable one param.");
        }
        String tableName = note.sql;
        Object model = args[0];
        String sql = "insert into `" + tableName + "` set";
        for (Field field : model.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            if (field.getAnnotation(AutoIncrement.class) != null) {
                continue;//自增字段，不需要手动设置值
            }
            String key = field.getName();
            Object value = field.get(model);
            key = "`" + key + "`";
            if (isStringType(field.getType())) {
                if (value != null) {//null不加引号
                    String tempValue = String.valueOf(value);
                    if (tempValue.contains("'")) {
                        tempValue = tempValue.replace("'", "\\'");//for sql safe
                    }
                    value = tempValue;
                    value = "'" + value + "'";
                }
            }
            sql += " " + key + " = " + value + " ,";
        }
        sql = sql.substring(0, sql.length() - 1);
        return sql;
    }

    private static String buildSql(MethodNote note, Object[] args) {
        if (args == null || args.length == 0) {//SQL有空格却无参数，认为是完整SQL.
            return note.sql;
        }
        if (args.length > 9) {//最多9个参数，更多请使用Model作为参数
            throw new RuntimeException(note.method + ":Too many params.");
        }
        String sql = note.sql;//不要修改参数中的信息
        for (int i = 1; i <= args.length; i++) {
            String rawSql = "?" + i;
            //format check
            if (!sql.contains(rawSql)) {
                throw new RuntimeException(note.method + ":Error sql format.");
            }
            String rawValue = String.valueOf(args[i - 1]);
            if (isStringType(args[i - 1].getClass()) || !"null".equals(rawValue)) {
                if (rawValue.contains("'")) {
                    rawValue = rawValue.replace("'", "\\'");//for sql safe
                }
                rawValue = "'" + rawValue + "'";
            }
            sql = sql.replace(rawSql, rawValue);
        }
        return sql;
    }

    private static boolean isStringType(Class<?> cls) {
        return cls == String.class;//jvm为每种类管理者创建独一的Class对象
    }

    //用于保存方法和其注解信息
    private static class MethodNote {
        private Method method;
        private String sql;
        private Type type;
        private boolean isQuery;
        //for query
        private String incSort;
        private String desSort;
        private String groupMerge;

        private MethodNote(Method method, String sql, Type type) {
            this.method = method;
            this.sql = sql;
            this.type = type;
            this.isQuery = true;
        }

        private MethodNote(Method method, String sql) {
            this.method = method;
            this.sql = sql;
            this.type = null;
            this.isQuery = false;
        }

        public MethodNote setQueryInfo(String incSort, String desSort, String groupMerge) {
            this.incSort = incSort;
            this.desSort = desSort;
            this.groupMerge = groupMerge;
            return this;
        }

    }
}
