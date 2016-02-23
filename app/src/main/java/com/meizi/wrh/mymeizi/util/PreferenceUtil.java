package com.meizi.wrh.mymeizi.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wrh on 15/12/23.
 */
public class PreferenceUtil {

    private static final String MEI_ZI = "meizi";

    private static PreferenceUtil preferenceUtil;
    private SharedPreferences sharedPreferences;

    private PreferenceUtil(Context context) {
        sharedPreferences = context.getSharedPreferences(MEI_ZI, Context.MODE_PRIVATE);
    }

    public static PreferenceUtil getInstance(Context context) {
        if (preferenceUtil == null) {
            preferenceUtil = new PreferenceUtil(context);
        }
        return preferenceUtil;
    }

    public void setString(String key, String s) {
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, s);
            editor.commit();
        }
    }

    public String getString(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getString(key, null);
        }
        return null;
    }

    public void setInt(String key,int value){
        if (sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(key, value);
            editor.commit();
        }
    }

    public int getInt(String key) {
        if (sharedPreferences != null) {
            return sharedPreferences.getInt(key, 0);
        }
        return 0;
    }

}
