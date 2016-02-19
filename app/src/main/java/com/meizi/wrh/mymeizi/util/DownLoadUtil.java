package com.meizi.wrh.mymeizi.util;

import android.app.Activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wrh on 16/2/19.
 */
public class DownLoadUtil {

    public boolean download(String url, File file) {
        try {
            URL u = new URL(url);
            HttpURLConnection c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setDoOutput(true);
            c.connect();
            FileOutputStream f = new FileOutputStream(file);
            InputStream in = c.getInputStream();
            byte[] buffer = new byte[1024];
            int len1 = 0;
            while ((len1 = in.read(buffer)) > 0) {
                f.write(buffer, 0, len1);
            }
            f.close();
            in.close();

            return true;
        } catch (Exception e) {
        }

        return false;
    }
}
