package com.ooo.mq.utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * 浮点型数据格式转换类
 * dongdd 2016/12/27 0012 08:42
 */
public class DataFormatUtils {

    /**
     * 定义Gson静态对象
     */
    private static Gson gson = null;

    static {
        if (gson == null) {
            gson = new Gson();
        }
    }

    /**
     * obj转成json
     *
     * @param obj
     * @return
     */
    public static String GsonString(Object obj) {
        String gsonString = null;
        if (gson != null) {
            gsonString = gson.toJson(obj);
        }
        return gsonString;
    }

    /**
     * 将json字符串转换为对象实体
     *
     * @param json
     * @param typeOfT
     * @param <T>
     * @return
     */
    public static <T> T jsonToObj(String json, Type typeOfT) {
        if (json.length() > 0) {
            T classOfT = gson.fromJson(json, typeOfT);
            return classOfT;
        } else {
            return null;
        }
    }

}
