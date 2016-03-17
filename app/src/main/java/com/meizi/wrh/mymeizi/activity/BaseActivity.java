package com.meizi.wrh.mymeizi.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.meizi.wrh.mymeizi.R;
import com.meizi.wrh.mymeizi.util.PreferenceUtil;

/**
 * Created by wrh on 16/2/22.
 */
public class BaseActivity extends AppCompatActivity {
    public static final String STYLES_INDEX = BaseActivity.class.getSimpleName() + "_styles_index";

    int styles[] = {R.style.AppTheme, R.style.AppTheme_night, R.style.AppTheme_pink, R.style.AppTheme_yellow};
    int mStyleIndex = 0;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mStyleIndex = PreferenceUtil.getInstance(this).getInt(STYLES_INDEX);
        setTheme(styles[mStyleIndex]);
        super.onCreate(savedInstanceState);
    }

    public void setContentWithToolbarView(int layoutResID) {
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        View totalView = LayoutInflater.from(this).inflate(R.layout.activity_base, null);
        mToolbar = (Toolbar) totalView.findViewById(R.id.base_toolbar);
        FrameLayout contentLayout = (FrameLayout) totalView.findViewById(R.id.base_frame);
        contentLayout.addView(view);
        super.setContentView(totalView);
        setToolBar();
    }

    /**
     * 设置toolBar代替ActionBar
     */
    private void setToolBar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        super.setTitle(title);
        titleRunnable.run();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private Runnable titleRunnable = new Runnable() {
        @Override
        public void run() {
            if (mToolbar != null) {
                mToolbar.setTitle(getTitle());
            }
        }
    };
}
