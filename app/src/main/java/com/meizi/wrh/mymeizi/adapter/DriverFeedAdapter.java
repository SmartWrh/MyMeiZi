package com.meizi.wrh.mymeizi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.katelee.widget.RecyclerViewLayout;
import com.meizi.wrh.mymeizi.R;
import com.meizi.wrh.mymeizi.model.GankIoModel;
import com.meizi.wrh.mymeizi.model.SizeModel;
import com.meizi.wrh.mymeizi.view.DriverView;
import com.meizi.wrh.mymeizi.view.MaterialFeedLayout;

import java.util.List;

/**
 * Created by wrh on 16/2/15.
 */
public class DriverFeedAdapter extends RecyclerViewLayout.Adapter<DriverFeedAdapter.ItemHolder> {

    private Context mContext;
    private List<GankIoModel.ResultsEntity> mData;
    private List<SizeModel> mSizeModel;

    public DriverFeedAdapter(Context context, List<GankIoModel.ResultsEntity> data) {
        this.mContext = context;
        this.mData = data;
    }

    public void setSizeModel(List<SizeModel> sizeModel) {
        this.mSizeModel = sizeModel;
    }

    @Override
    protected ItemHolder onAdapterCreateViewHolder(ViewGroup viewGroup, int viewType) {
        DriverView driverView = new DriverView(mContext);
        ItemHolder itemHolder = new ItemHolder(driverView);
        return itemHolder;
    }

    @Override
    protected void onAdapterBindViewHolder(ItemHolder viewHolder, int position) {
        viewHolder.driverView.setData(mData.get(position), mSizeModel.get(position));
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

    class ItemHolder extends RecyclerView.ViewHolder {
        DriverView driverView;

        public ItemHolder(View itemView) {
            super(itemView);
            driverView = (DriverView) itemView;
        }
    }
}
