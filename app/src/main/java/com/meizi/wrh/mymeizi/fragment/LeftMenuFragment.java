package com.meizi.wrh.mymeizi.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.meizi.wrh.mymeizi.activity.MainActivity;
import com.meizi.wrh.mymeizi.R;
import com.meizi.wrh.mymeizi.activity.BaseActivity;
import com.meizi.wrh.mymeizi.constans.BaseService;
import com.meizi.wrh.mymeizi.constans.GankIoService;
import com.meizi.wrh.mymeizi.dialog.SelectDialog;
import com.meizi.wrh.mymeizi.listener.OnItemClickListener;
import com.meizi.wrh.mymeizi.localfile.LocalFilesActivity;
import com.meizi.wrh.mymeizi.model.GankIoModel;
import com.meizi.wrh.mymeizi.util.PreferenceUtil;
import com.meizi.wrh.mymeizi.view.StrokeImageView;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class LeftMenuFragment extends Fragment implements OnClickListener, OnItemClickListener {

    private Retrofit mRetrofit;
    private Subscription mLoadNetScription;
    private GankIoService service;

    private View mView;
    private ImageView imgBackground;
    private LinearLayout linColorSelect;
    private LinearLayout linFileManager;
    private StrokeImageView imgAvatar;

    public LeftMenuFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRetrofit = new Retrofit.Builder()
                .baseUrl(BaseService.BASE_API).addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        service = mRetrofit.create(GankIoService.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_left_menu, container, false);
        imgBackground = (ImageView) mView.findViewById(R.id.left_img_background);
        imgAvatar = (StrokeImageView) mView.findViewById(R.id.left_img_avatar);
        linColorSelect = (LinearLayout) mView.findViewById(R.id.left_lin_select);
        linColorSelect.setOnClickListener(this);
        linFileManager = (LinearLayout) mView.findViewById(R.id.left_lin_download);
        linFileManager.setOnClickListener(this);
        loadData();
        return mView;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mLoadNetScription != null && !mLoadNetScription.isUnsubscribed()) {
            mLoadNetScription.unsubscribe();
        }
    }

    private void loadData() {
        Observable<GankIoModel> observable = service.randomResult();

        mLoadNetScription = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).filter(new Func1<GankIoModel, Boolean>() {
            @Override
            public Boolean call(GankIoModel gankIoModel) {
                return !gankIoModel.isError();
            }
        }).subscribe(new Subscriber<GankIoModel>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(GankIoModel gankIoModel) {
                Glide.with(LeftMenuFragment.this).load(gankIoModel.getResults().get(0).getUrl()).bitmapTransform(new BlurTransformation(getActivity(), 20, 6)).into(imgBackground);
                Glide.with(LeftMenuFragment.this).load(gankIoModel.getResults().get(0).getUrl()).bitmapTransform(new CropCircleTransformation(getActivity())).into(imgAvatar);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.left_lin_select:
                SelectDialog dialog = new SelectDialog(getActivity());
                dialog.setOnItemClickListener(this);
                dialog.show();
                break;
            case R.id.left_lin_download:
                startActivity(new Intent(getActivity(), LocalFilesActivity.class));
                break;
        }

    }

    @Override
    public void onItemClick(int position) {
        PreferenceUtil.getInstance(getActivity()).setInt(BaseActivity.STYLES_INDEX, position);
        MainActivity activity = (MainActivity) getActivity();
        activity.recreate();
    }
}
