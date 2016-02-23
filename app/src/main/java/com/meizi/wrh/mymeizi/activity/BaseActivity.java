package com.meizi.wrh.mymeizi.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.meizi.wrh.mymeizi.R;
import com.meizi.wrh.mymeizi.util.PreferenceUtil;

/**
 * Created by wrh on 16/2/22.
 */
public class BaseActivity extends AppCompatActivity {
    public static final String STYLES_INDEX = BaseActivity.class.getSimpleName() + "_styles_index";

    int styles[] = {R.style.AppTheme, R.style.AppTheme_night, R.style.AppTheme_pink, R.style.AppTheme_yellow};
    int mStyleIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mStyleIndex = PreferenceUtil.getInstance(this).getInt(STYLES_INDEX);
        setTheme(styles[mStyleIndex]);
        super.onCreate(savedInstanceState);
    }
}
