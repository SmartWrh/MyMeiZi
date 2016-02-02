package com.meizi.wrh.mymeizi.util;

/**
 * Created by wrh on 16/2/2.
 */
public class StrUtil {

    public static final String LINK_CHAR = "·";
    public static final String LINK_DASH = "────";


    public static boolean isEmpty(CharSequence s) {
        return s == null || "".equals(s.toString().trim()) || "null".equals(s.toString().trim());
    }

    public static String formatType(String type){
        if (!isEmpty(type)) {
            return LINK_DASH + " " + LINK_CHAR + " " + type + " " + LINK_CHAR + " " + LINK_DASH;
        }
        return "";
    }
}
