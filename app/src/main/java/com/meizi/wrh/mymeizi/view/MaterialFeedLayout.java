package com.meizi.wrh.mymeizi.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.jakewharton.rxbinding.view.RxView;
import com.meizi.wrh.mymeizi.R;
import com.meizi.wrh.mymeizi.constans.BaseEnum;
import com.meizi.wrh.mymeizi.constans.BaseImageUrl;
import com.meizi.wrh.mymeizi.databinding.ViewHomeFeedBinding;
import com.meizi.wrh.mymeizi.model.GankIoModel;
import com.meizi.wrh.mymeizi.util.StrUtil;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rx.functions.Action1;

/**
 * Created by wrh on 16/2/2.
 */
public class MaterialFeedLayout extends RelativeLayout {

    private ViewHomeFeedBinding mBinding;

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
        LayoutInflater inflater = LayoutInflater.from(context);
        mBinding = ViewHomeFeedBinding.inflate(inflater, this, true);
        RxView.clicks(mBinding.viewLinearFeed).throttleFirst(500, TimeUnit.MILLISECONDS).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {

            }
        });
    }


    public void setData(GankIoModel.ResultsEntity data) {
        data.setSubType(StrUtil.formatType(data.getType()));
        mBinding.setGankio(data);
        if (!data.getType().equals(BaseEnum.fuli.getValue())) {
            Glide.with(getContext()).load(getUrl(data.getType())).centerCrop().into(mBinding.viewImgFeed);
        } else {
            Glide.with(getContext()).load(data.getUrl()).centerCrop().into(mBinding.viewImgFeed);
        }
    }


    private String getUrl(String type) {
        switch (type) {
            case "App":
                return BaseImageUrl.APP;
            case "Android":
                return BaseImageUrl.ANDROID;
            case "iOS":
                return BaseImageUrl.IOS;
            default:
                return BaseImageUrl.OTHER;
        }
    }
}
