package com.meizi.wrh.mymeizi.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by wrh on 16/2/2.
 */
public class StrUtil {

    public static final String LINK_CHAR = "·";
    public static final String LINK_DASH = "────";


    public static boolean isEmpty(CharSequence s) {
        return s == null || "".equals(s.toString().trim()) || "null".equals(s.toString().trim());
    }

    public static String formatType(String type) {
        if (!isEmpty(type)) {
            return LINK_DASH + " " + LINK_CHAR + " " + type + " " + LINK_CHAR + " " + LINK_DASH;
        }
        return "";
    }

    public static String getMD5(String val)  {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md5.update(val.getBytes());
        byte[] m = md5.digest();//加密
        return getString(m);
    }

    private static String getString(byte[] b) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            sb.append(b[i]);
        }
        return sb.toString();
    }
}
