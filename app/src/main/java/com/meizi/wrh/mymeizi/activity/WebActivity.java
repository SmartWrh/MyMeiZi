package com.meizi.wrh.mymeizi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.meizi.wrh.mymeizi.R;
import com.meizi.wrh.mymeizi.util.PreferenceUtil;
import com.meizi.wrh.mymeizi.util.StrUtil;

public class WebActivity extends BaseActivity {

    public static final String WEB_TITLE = WebActivity.class.getSimpleName() + "_web_title";
    public static final String WEB_URL = WebActivity.class.getSimpleName() + "_web_url";
    private String mTitle, mUrl;
    private Toolbar toolbar;
    private WebView webView;
    private ProgressBar progressBar;
    private RelativeLayout relaGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        Intent intent = getIntent();

        mTitle = intent.getExtras().getString(WEB_TITLE);
        mUrl = intent.getExtras().getString(WEB_URL);
        initView();
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        if (!StrUtil.isEmpty(mUrl)) {
            webView.loadUrl(mUrl);
            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    if (newProgress == 100) {
                        progressBar.setVisibility(View.INVISIBLE);
                    } else {
                        progressBar.setVisibility(View.VISIBLE);
                        progressBar.setProgress(newProgress);
                    }
                    super.onProgressChanged(view, newProgress);
                }
            });
        }
    }

    private void initView() {
        relaGroup = (RelativeLayout) findViewById(R.id.web_group);
        toolbar = (Toolbar) findViewById(R.id.web_toolbar);
        toolbar.setTitle(mTitle);
        setSupportActionBar(toolbar);
        initAddView();
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                view.loadUrl(url);
                return true;
            }
        });
    }

    private void initAddView() {
        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setProgressDrawable(ContextCompat.getDrawable(this, R.drawable.web_progress));
        webView = new WebView(getApplicationContext());
        relaGroup.addView(webView);
        relaGroup.addView(progressBar);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) progressBar.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = 5;
        progressBar.setLayoutParams(layoutParams);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                webView.removeAllViews();
                webView.destroy();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.removeAllViews();
        webView.destroy();
    }
}
