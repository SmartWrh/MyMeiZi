package com.meizi.wrh.mymeizi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.katelee.widget.RecyclerViewLayout;
import com.meizi.wrh.mymeizi.R;
import com.meizi.wrh.mymeizi.model.GankIoModel;
import com.meizi.wrh.mymeizi.view.MaterialFeedLayout;

import java.util.List;
import java.util.logging.Logger;

/**
 * Created by wrh on 16/2/2.
 */
public class HomeFeedAdapter extends RecyclerViewLayout.Adapter<HomeFeedAdapter.ItemHolder> {

    private Context mContext;
    private List<GankIoModel.ResultsEntity> mData;

    public HomeFeedAdapter(Context context, List<GankIoModel.ResultsEntity> data) {
        this.mContext = context;
        this.mData = data;
    }


    @Override
    protected ItemHolder onAdapterCreateViewHolder(ViewGroup viewGroup, int viewType) {
        MaterialFeedLayout feedLayout = new MaterialFeedLayout(mContext);
        ItemHolder itemHolder = new ItemHolder(feedLayout);
        return itemHolder;
    }

    @Override
    protected void onAdapterBindViewHolder(ItemHolder viewHolder, int position) {
        viewHolder.feedLayout.setData(mData.get(position));
    }

    @Override
    protected View onLoadMoreCreateView(ViewGroup viewGroup) {
        return LayoutInflater.from(mContext).inflate(R.layout.view_common_loadmore, viewGroup, false);
    }

    @Override
    public int getAdapterItemCount() {
        return mData.size();
    }

    @Override
    public boolean hasHeader() {
        return false;
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        MaterialFeedLayout feedLayout;

        public ItemHolder(View itemView) {
            super(itemView);
            feedLayout = (MaterialFeedLayout) itemView;
        }
    }
}
