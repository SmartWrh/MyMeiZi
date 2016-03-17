package com.meizi.wrh.mymeizi.driver;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.katelee.widget.RecyclerViewLayout;
import com.github.katelee.widget.recyclerviewlayout.AdvanceAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.meizi.wrh.mymeizi.R;
import com.meizi.wrh.mymeizi.activity.BaseActivity;
import com.meizi.wrh.mymeizi.adapter.DriverFeedAdapter;
import com.meizi.wrh.mymeizi.adapter.HomeFeedAdapter;
import com.meizi.wrh.mymeizi.constans.BaseEnum;
import com.meizi.wrh.mymeizi.constans.BaseService;
import com.meizi.wrh.mymeizi.constans.GankIoService;
import com.meizi.wrh.mymeizi.model.GankIoModel;
import com.meizi.wrh.mymeizi.model.SizeModel;
import com.meizi.wrh.mymeizi.view.DriverView;

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
import rx.schedulers.Schedulers;
import uk.co.senab.photoview.PhotoView;

public class DriverActivity extends BaseActivity implements AdvanceAdapter.OnLoadMoreListener,SwipeRefreshLayout.OnRefreshListener {

    private int mCount = 1;
    private Retrofit mRetrofit;
    private Subscription mLoadNetScription;
    private GankIoService service;

    private DriverFeedAdapter feedAdapter;
    private RecyclerViewLayout recyclerView;

    private List<GankIoModel.ResultsEntity> mData = new ArrayList<>();
    private List<SizeModel> mSizeData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentWithToolbarView(R.layout.activity_driver);
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
        Observable<GankIoModel> observable = service.homeResult(BaseEnum.fuli.getValue(), mCount);
        if (mLoadNetScription != null && mLoadNetScription.isUnsubscribed()) {
            mLoadNetScription.unsubscribe();
        }
        mLoadNetScription = observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).filter(new Func1<GankIoModel, Boolean>() {
            @Override
            public Boolean call(GankIoModel gankIoModel) {
                return !gankIoModel.isError() && gankIoModel != null;
            }
        }).flatMap(new FilterMap()).subscribe(new LoadNetSubscriber());
        mCount++;
    }

    private void initView() {
        setTitle(getResources().getString(R.string.driver_know_world));
        recyclerView = (RecyclerViewLayout) findViewById(R.id.driver_recycler);
        recyclerView.getRecyclerView().setHasFixedSize(true);
        recyclerView.post(refreshRunnable);
        recyclerView.setOnRefreshListener(this);
        recyclerView.getRecyclerView().setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
    }

    private void initData() {
        feedAdapter = new DriverFeedAdapter(this, mData);
        recyclerView.getRecyclerView().setAdapter(feedAdapter);
        feedAdapter.setOnLoadMoreListener(this);
        feedAdapter.disableLoadMore();
    }

    private Runnable refreshRunnable = new Runnable() {
        @Override
        public void run() {
            recyclerView.setRefreshing(true);
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if (!mLoadNetScription.isUnsubscribed()) {
            mLoadNetScription.unsubscribe();
        }
    }



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

    /**
     * 网络请求
     */
    class LoadNetSubscriber extends Subscriber<GankIoModel.ResultsEntity> {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            feedAdapter.setLoadingMore(false);
            recyclerView.setRefreshing(false);
            Toast.makeText(DriverActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNext(GankIoModel.ResultsEntity resultsEntity) {
            mData.add(resultsEntity);
            SizeModel sizeModel = new SizeModel();
            sizeModel.setUrl(resultsEntity.getUrl());
            mSizeData.add(sizeModel);
            feedAdapter.setSizeModel(mSizeData);
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
}
