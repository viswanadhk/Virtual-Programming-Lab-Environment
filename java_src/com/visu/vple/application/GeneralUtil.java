package com.visu.vple.application;

import org.json.JSONObject;

public class GeneralUtil {
    public static String replaceChar(JSONObject obj, String searchText) {
        try {
            return obj.get(searchText).toString().replaceAll("%", "%25").replaceAll("\\+", "%2B");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
