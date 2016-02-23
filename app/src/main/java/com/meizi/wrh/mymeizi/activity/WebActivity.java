package com.meizi.wrh.mymeizi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.meizi.wrh.mymeizi.R;
import com.meizi.wrh.mymeizi.util.StrUtil;

public class WebActivity extends BaseActivity {

    public static final String WEB_TITLE = WebActivity.class.getSimpleName() + "_web_title";
    public static final String WEB_URL = WebActivity.class.getSimpleName() + "_web_url";
    private String mTitle, mUrl;
    private Toolbar toolbar;
    private WebView webView;

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
        }
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.web_toolbar);
        toolbar.setTitle(mTitle);
        setSupportActionBar(toolbar);
        webView = (WebView) findViewById(R.id.web_content);
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
}
