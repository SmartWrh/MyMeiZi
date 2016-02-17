package com.meizi.wrh.mymeizi.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.meizi.wrh.mymeizi.R;
import com.meizi.wrh.mymeizi.databinding.ViewDriverFeedBinding;
import com.meizi.wrh.mymeizi.model.GankIoModel;
import com.meizi.wrh.mymeizi.model.SizeModel;

import java.lang.annotation.Target;

/**
 * Created by wrh on 16/2/15.
 */
public class DriverView extends RelativeLayout {
    private boolean isGlobal;
    private ViewDriverFeedBinding mBinding;
    private SizeModel mSizeModel;
    private DriverViewTarget mViewTarget;

    public DriverView(Context context) {
        super(context);
        initView(context);
    }

    public DriverView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public DriverView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        mBinding = ViewDriverFeedBinding.inflate(inflater, this, true);
    }

    public void setData(final GankIoModel.ResultsEntity data, SizeModel sizeModel) {
        mBinding.setGankio(data);
        mSizeModel = sizeModel;
        mViewTarget = new DriverViewTarget(mBinding.viewImgFeed);
        if (!mSizeModel.isNull()) {
            setCardViewLayoutParams(mSizeModel.getWidth(), mSizeModel.getHeight());
        }
        
        this.post(new Runnable() {
            @Override
            public void run() {
                Glide.with(getContext()).load(data.getUrl()).asBitmap().fitCenter().override(mBinding.viewImgFeed.getWidth(), BitmapImageViewTarget.SIZE_ORIGINAL).into(mViewTarget);
            }
        });
    }

    private void setCardViewLayoutParams(int width, int height) {
        ViewGroup.LayoutParams layoutParams = mBinding.viewImgFeed.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        mBinding.viewImgFeed.setLayoutParams(layoutParams);
    }

    private class DriverViewTarget extends BitmapImageViewTarget {

        public DriverViewTarget(ImageView view) {
            super(view);
        }

        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            if (mSizeModel.isNull()) {
                int viewWidth = mBinding.viewImgFeed.getWidth();
                float scale = resource.getWidth() / (viewWidth * 1.0f);
                int viewHeight = (int) (resource.getHeight() * scale);
                setCardViewLayoutParams(viewWidth, viewHeight);
                mSizeModel.setSize(viewWidth, viewHeight);
            }
            super.onResourceReady(resource, glideAnimation);
        }
    }

}
