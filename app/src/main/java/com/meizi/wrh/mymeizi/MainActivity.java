package com.meizi.wrh.mymeizi;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;

import com.github.katelee.widget.RecyclerViewLayout;
import com.meizi.wrh.mymeizi.adapter.HomeFeedAdapter;
import com.meizi.wrh.mymeizi.constans.BaseEnum;
import com.meizi.wrh.mymeizi.constans.BaseService;
import com.meizi.wrh.mymeizi.constans.GankIoService;
import com.meizi.wrh.mymeizi.model.GankIoModel;

import java.util.ArrayList;
import java.util.List;

import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private Retrofit mRetrofit;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private HomeFeedAdapter feedAdapter;
    private RecyclerViewLayout recyclerView;
    private List<GankIoModel.ResultsEntity> mData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();

        mRetrofit = new Retrofit.Builder()
                .baseUrl(BaseService.BASE_API).addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        GankIoService service = mRetrofit.create(GankIoService.class);
        Observable<GankIoModel> observable = service.homeResult(BaseEnum.all.getValue(), 1);
        Subscription subscribe = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).filter(new Func1<GankIoModel, Boolean>() {
            @Override
            public Boolean call(GankIoModel gankIoModel) {
                return !gankIoModel.isError();
            }
        }).subscribe(new Action1<GankIoModel>() {
            @Override
            public void call(GankIoModel gankIoService) {
                mData.addAll(gankIoService.getResults());
                feedAdapter.notifyDataSetChanged();
            }
        });

    }

    private void initData() {
        feedAdapter = new HomeFeedAdapter(this, mData);
        recyclerView.getRecyclerView().setAdapter(feedAdapter);
    }

    private void initView() {
        recyclerView = (RecyclerViewLayout) findViewById(R.id.home_recycler);
        recyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = (DrawerLayout) findViewById(R.id.home_drawer_group);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.home_navigation_open,
                R.string.home_navigation_close);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);
    }
}
