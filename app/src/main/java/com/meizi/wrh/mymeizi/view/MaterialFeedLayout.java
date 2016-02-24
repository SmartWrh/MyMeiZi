package com.meizi.wrh.mymeizi.view;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding.view.RxView;
import com.meizi.wrh.mymeizi.R;
import com.meizi.wrh.mymeizi.activity.WebActivity;
import com.meizi.wrh.mymeizi.constans.BaseEnum;
import com.meizi.wrh.mymeizi.constans.BaseImageUrl;
import com.meizi.wrh.mymeizi.databinding.ViewHomeFeedBinding;
import com.meizi.wrh.mymeizi.driver.PhotoActivity;
import com.meizi.wrh.mymeizi.model.GankIoModel;
import com.meizi.wrh.mymeizi.util.StrUtil;

import java.io.ByteArrayOutputStream;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

/**
 * Created by wrh on 16/2/2.
 */
public class MaterialFeedLayout extends RelativeLayout {

    private ViewHomeFeedBinding mBinding;
    private GankIoModel.ResultsEntity mData;

    public MaterialFeedLayout(Context context) {
        super(context);
        initView(context);
    }

    public MaterialFeedLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MaterialFeedLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        mBinding = ViewHomeFeedBinding.inflate(inflater, this, true);
        RxView.clicks(mBinding.viewLinearFeed).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if (mData.getType().equals(BaseEnum.fuli.getValue())) {
                    startPhotoActivity();
                } else {
                    startWebActivity();
                }
            }
        });
    }

    public void setData(GankIoModel.ResultsEntity data) {
        mData = data;
        data.setSubType(StrUtil.formatType(data.getType()));
        mBinding.setGankio(data);
        Glide.with(getContext()).load(data.getImageUrl()).asBitmap().centerCrop().into(mBinding.viewImgFeed);
    }

    private void startWebActivity() {
        Intent intent = new Intent(getContext(), WebActivity.class);
        intent.putExtra(WebActivity.WEB_TITLE, mData.getDesc());
        intent.putExtra(WebActivity.WEB_URL, mData.getUrl());
        getContext().startActivity(intent);
    }

    private void startPhotoActivity() {
        Intent intent = new Intent(getContext(), PhotoActivity.class);
        intent.putExtra(PhotoActivity.IMAGE_URL, mData.getUrl());
            getContext().startActivity(intent);
    }
}
