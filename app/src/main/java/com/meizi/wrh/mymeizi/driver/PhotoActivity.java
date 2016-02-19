package com.meizi.wrh.mymeizi.driver;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.jakewharton.rxbinding.view.RxView;
import com.meizi.wrh.mymeizi.R;
import com.meizi.wrh.mymeizi.util.DownLoadUtil;
import com.meizi.wrh.mymeizi.util.FileUtil;
import com.meizi.wrh.mymeizi.util.StrUtil;
import com.meizi.wrh.mymeizi.view.ScaleImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.annotation.Target;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PhotoActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String BITMAP = PhotoActivity.class.getSimpleName() + "_bitmap";
    public static final String IMAGE_URL = PhotoActivity.class.getSimpleName() + "_image_url";
    private String mImageUrl;
    private FileUtil fileUtil;
    private MediaScannerConnection connection;
    private PhotoViewAttacher attacher;
    private ImageView imgContent;
    private ScaleImageView imgDownLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.PhotoTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        imgContent = (ImageView) findViewById(R.id.photo_img_content);
        imgDownLoad = (ScaleImageView) findViewById(R.id.photo_img_download);
        imgDownLoad.setOnClickListener(this);
        mImageUrl = getIntent().getExtras().getString(IMAGE_URL);
        fileUtil = new FileUtil(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            byte[] byteArray = getIntent().getByteArrayExtra(BITMAP);
            Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            imgContent.setImageBitmap(bitmap);
            ViewCompat.setTransitionName(imgContent, "driverView");
            attacher = new PhotoViewAttacher(imgContent);
            attacher.setOnPhotoTapListener(onPhotoTapListener);
        } else {
            Glide.with(this).load(mImageUrl).asBitmap().into(new BitmapImageViewTarget(imgContent) {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    super.onResourceReady(resource, glideAnimation);
                    attacher = new PhotoViewAttacher(imgContent);
                    attacher.setOnPhotoTapListener(onPhotoTapListener);
                }
            });
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!subscriber.isUnsubscribed()) {
            subscriber.unsubscribe();
        }
    }

    @Override
    public void onClick(View v) {
        Observable.just(mImageUrl).flatMap(new Func1<String, Observable<Boolean>>() {
            @Override
            public Observable<Boolean> call(String s) {
                File file = fileUtil.getDiskCacheDir(StrUtil.getMD5(s) + ".jpg");
                boolean isSuccess = new DownLoadUtil().download(s, file);
                if (isSuccess) {
                    try {
                        MediaStore.Images.Media.insertImage(getContentResolver(),
                                file.getPath(), null, null);
                        connection = new MediaScannerConnection(PhotoActivity.this, new MediaScannerUtil(file.getPath()));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                return Observable.just(isSuccess);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(subscriber);
    }

    private Subscriber<Boolean> subscriber = new Subscriber<Boolean>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Toast.makeText(PhotoActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNext(Boolean isSuccess) {
            if (isSuccess) {
                Toast.makeText(PhotoActivity.this, "下载成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(PhotoActivity.this, "下载失败", Toast.LENGTH_SHORT).show();
            }
        }
    };


    private class MediaScannerUtil implements MediaScannerConnection.MediaScannerConnectionClient {
        private String mPath;

        public MediaScannerUtil(String path) {
            mPath = path;
        }

        @Override
        public void onMediaScannerConnected() {
            connection.scanFile(mPath, "image/jpg");
        }

        @Override
        public void onScanCompleted(String path, Uri uri) {
            connection.disconnect();
        }
    }

    private PhotoViewAttacher.OnPhotoTapListener onPhotoTapListener = new PhotoViewAttacher.OnPhotoTapListener() {
        @Override
        public void onPhotoTap(View view, float x, float y) {
            onBackPressed();
        }
    };

}
