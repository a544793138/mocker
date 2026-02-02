package com.tjwoods.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonUtil {
    private static final Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
    private static final Gson compactGson = new Gson();

    /**
     * 格式化 JSON 字符串（美观显示）
     */
    public static String prettify(String json) {
        if (json == null || json.trim().isEmpty()) {
            return json;
        }
        try {
            Object obj = compactGson.fromJson(json, Object.class);
            return prettyGson.toJson(obj);
        } catch (Exception e) {
            return json; // 如果不是有效的 JSON，返回原样
        }
    }

    /**
     * 紧凑化 JSON 字符串（用于发送响应）
     */
    public static String compact(String json) {
        if (json == null || json.trim().isEmpty()) {
            return json;
        }
        try {
            Object obj = compactGson.fromJson(json, Object.class);
            return compactGson.toJson(obj);
        } catch (Exception e) {
            return json; // 如果不是有效的 JSON，返回原样
        }
    }

    /**
     * 检查字符串是否是有效的 JSON
     */
    public static boolean isValidJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return false;
        }
        try {
            compactGson.fromJson(json, Object.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
