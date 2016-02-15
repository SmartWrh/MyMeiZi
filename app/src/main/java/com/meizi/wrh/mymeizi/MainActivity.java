package com.meizi.wrh.mymeizi;

import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.github.katelee.widget.RecyclerViewLayout;
import com.github.katelee.widget.recyclerviewlayout.AdvanceAdapter;
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

public class MainActivity extends AppCompatActivity implements AdvanceAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    private int count = 1;
    private Retrofit mRetrofit;
    private Subscription mLoadNetScription;
    private GankIoService service;

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
        service = mRetrofit.create(GankIoService.class);
        loadData();
    }

    private void loadData() {
        Observable<GankIoModel> observable = service.homeResult(BaseEnum.all.getValue(), count);
        if (mLoadNetScription != null && mLoadNetScription.isUnsubscribed()) {
            mLoadNetScription.unsubscribe();
        }
        mLoadNetScription = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).filter(new Func1<GankIoModel, Boolean>() {
            @Override
            public Boolean call(GankIoModel gankIoModel) {
                return !gankIoModel.isError();
            }
        }).subscribe(new LoadNetSubscriber());
        count++;
    }

    private void initData() {
        feedAdapter = new HomeFeedAdapter(this, mData);
        recyclerView.getRecyclerView().setAdapter(feedAdapter);
        feedAdapter.setOnLoadMoreListener(this);
        feedAdapter.disableLoadMore();
    }

    private void initView() {
        recyclerView = (RecyclerViewLayout) findViewById(R.id.home_recycler);
        recyclerView.getRecyclerView().setLayoutManager(new LinearLayoutManager(this));
        recyclerView.post(refreshRunnable);
        recyclerView.setOnRefreshListener(this);
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

    @Override
    protected void onStop() {
        super.onStop();
        if (!mLoadNetScription.isUnsubscribed()) {
            mLoadNetScription.unsubscribe();
        }
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
        count = 1;
        mData.clear();
        loadData();
    }

    /**
     * 网络请求
     */
    class LoadNetSubscriber extends Subscriber<GankIoModel> {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNext(GankIoModel gankIoModel) {
            if (gankIoModel.getResults().size() >= 10) {
                feedAdapter.enableLoadMore();
            } else {
                feedAdapter.disableLoadMore();
            }
            List<GankIoModel.ResultsEntity> tempData = mData;
            mData.addAll(gankIoModel.getResults());
            if (recyclerView.isRefreshing()) {
                feedAdapter.notifyDataSetChanged();
                recyclerView.setRefreshing(false);
            } else {
                feedAdapter.notifyAdapterItemRangeInserted(tempData.size(), mData.size());
                feedAdapter.setLoadingMore(false);
            }
        }
    }
}
