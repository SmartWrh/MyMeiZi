package com.meizi.wrh.mymeizi.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.github.katelee.widget.RecyclerViewLayout;
import com.github.katelee.widget.recyclerviewlayout.AdvanceAdapter;
import com.meizi.wrh.mymeizi.R;
import com.meizi.wrh.mymeizi.adapter.HomeFeedAdapter;
import com.meizi.wrh.mymeizi.constans.BaseEnum;
import com.meizi.wrh.mymeizi.constans.BaseService;
import com.meizi.wrh.mymeizi.constans.GankIoService;
import com.meizi.wrh.mymeizi.driver.DriverActivity;
import com.meizi.wrh.mymeizi.fragment.LeftMenuFragment;
import com.meizi.wrh.mymeizi.model.GankIoModel;
import com.meizi.wrh.mymeizi.util.HidingScrollListener;

import java.util.ArrayList;
import java.util.List;

import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity implements AdvanceAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private int mCount = 1;
    private String mType = "All";
    private String mLoadType = BaseEnum.all.getValue();
    private Retrofit mRetrofit;
    private Subscription mLoadNetScription;
    private GankIoService service;
    private MyScrollListener scrollListener;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private FloatingActionButton actionButton;
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
        service = mRetrofit.create(GankIoService.class);
        loadData();
    }

    private void loadData() {
        Observable<GankIoModel> observable = service.homeResult(mLoadType, mCount);
        Observable<GankIoModel> observableFuli = service.homeResult(BaseEnum.fuli.getValue(), mCount);
        if (mLoadNetScription != null && mLoadNetScription.isUnsubscribed()) {
            mLoadNetScription.unsubscribe();
        }
        mLoadNetScription = Observable.combineLatest(observable, observableFuli, new Func2<GankIoModel, GankIoModel, GankIoModel>() {
            @Override
            public GankIoModel call(GankIoModel gankIoModel, GankIoModel gankIoModel2) {
                List<GankIoModel.ResultsEntity> resultsEntities = gankIoModel.getResults();
                for (int i = 0; i < resultsEntities.size(); i++) {
                    if (!resultsEntities.get(i).getType().equals(BaseEnum.fuli.getValue())) {
                        resultsEntities.get(i).setImageUrl(gankIoModel2.getResults().get(i).getUrl());
                    } else {
                        resultsEntities.get(i).setImageUrl(gankIoModel.getResults().get(i).getUrl());
                    }
                }
                return gankIoModel;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).filter(new Func1<GankIoModel, Boolean>() {
            @Override
            public Boolean call(GankIoModel gankIoModel) {
                return !gankIoModel.isError() && gankIoModel != null;
            }
        }).flatMap(new FilterMap()).subscribe(new LoadNetSubscriber());

        mCount++;
    }

    private void initData() {
        feedAdapter = new HomeFeedAdapter(this, mData);
        recyclerView.getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        recyclerView.getRecyclerView().setAdapter(feedAdapter);
        feedAdapter.setOnLoadMoreListener(this);
        feedAdapter.disableLoadMore();
    }

    private void initView() {
        actionButton = (FloatingActionButton) findViewById(R.id.home_fab);
        recyclerView = (RecyclerViewLayout) findViewById(R.id.home_recycler);
        recyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        recyclerView.post(refreshRunnable);
        recyclerView.setOnRefreshListener(this);
        scrollListener = new MyScrollListener();
        recyclerView.getRecyclerView().setOnScrollListener(scrollListener);
        toolbar = (Toolbar) findViewById(R.id.home_toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = (DrawerLayout) findViewById(R.id.home_drawer_group);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.home_navigation_open,
                R.string.home_navigation_close);
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);
        actionButton.setOnClickListener(this);
    }

    private void loadRefresh() {
        recyclerView.setRefreshing(true);
        onRefresh();
        toolbar.setTitle(mType);
        scrollListener.reset();
        actionButton.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!mLoadNetScription.isUnsubscribed()) {
            mLoadNetScription.unsubscribe();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_all_menu:
                if (mType.equals("All")) {
                    return true;
                }
                mType = "All";
                mLoadType = BaseEnum.all.getValue();
                loadRefresh();
                return true;
            case R.id.main_android_menu:
                if (mType.equals("Android")) {
                    return true;
                }
                mType = "Android";
                mLoadType = BaseEnum.Android.getValue();
                loadRefresh();
                return true;
            case R.id.main_ios_menu:
                if (mType.equals("IOS")) {
                    return true;
                }
                mType = "IOS";
                mLoadType = BaseEnum.iOS.getValue();
                loadRefresh();
                return true;
            case R.id.main_video_menu:
                if (mType.equals("休息视频")) {
                    return true;
                }
                mType = "休息视频";
                mLoadType = BaseEnum.video.getValue();
                loadRefresh();
                return true;
            case R.id.main_expand_menu:
                if (mType.equals("拓展资源")) {
                    return true;
                }
                mType = "拓展资源";
                mLoadType = BaseEnum.expand.getValue();
                loadRefresh();
                return true;
            case R.id.main_qianduan_menu:
                if (mType.equals("前端")) {
                    return true;
                }
                mType = "前端";
                mLoadType = BaseEnum.qianduan.getValue();
                loadRefresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
            return;
        }
        super.onBackPressed();
    }

    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            recyclerView.setRefreshing(true);
        }
    };


    @Override
    public void loadMore() {
        loadData();
    }

    @Override
    public void onRefresh() {
        mCount = 1;
        feedAdapter.disableLoadMore();
        feedAdapter.notifyAdapterItemRangeRemoved(0, mData.size());
        mData.clear();
        loadData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_fab:
//                Intent intent = new Intent(MainActivity.this, DriverActivity.class);
//                startActivity(intent);
                startActivity(new Intent(MainActivity.this, TestActivity.class));

                break;
        }
    }

    /**
     * 网络请求
     */
    class LoadNetSubscriber extends Subscriber<GankIoModel.ResultsEntity> {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNext(GankIoModel.ResultsEntity resultsEntity) {
            mData.add(resultsEntity);
            feedAdapter.notifyAdapterItemInserted(mData.size());
        }
    }

    class FilterMap implements Func1<GankIoModel, Observable<GankIoModel.ResultsEntity>> {

        @Override
        public Observable<GankIoModel.ResultsEntity> call(GankIoModel gankIoModel) {
            if (gankIoModel.getResults().size() >= 10) {
                feedAdapter.enableLoadMore();
            } else {
                feedAdapter.disableLoadMore();
            }
            feedAdapter.setLoadingMore(false);
            recyclerView.setRefreshing(false);
            return Observable.from(gankIoModel.getResults());
        }
    }

    class MyScrollListener extends HidingScrollListener {

        @Override
        public void onHide() {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) actionButton.getLayoutParams();
            int fabBottomMargin = lp.bottomMargin;
            actionButton.animate().translationY(actionButton.getHeight() + fabBottomMargin).setInterpolator(new AccelerateInterpolator(2)).start();
        }

        @Override
        public void onShow() {
            actionButton.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        }
    }
}
