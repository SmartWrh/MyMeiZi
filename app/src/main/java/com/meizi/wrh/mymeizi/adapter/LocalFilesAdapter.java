package com.meizi.wrh.mymeizi.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.meizi.wrh.mymeizi.R;
import com.meizi.wrh.mymeizi.listener.OnItemClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wrh on 16/2/22.
 */
public class LocalFilesAdapter extends RecyclerView.Adapter<LocalFilesAdapter.ItemHolder> {
    private Context mContext;
    private List<File> mData;
    private OnItemClickListener mOnItemClickListener;

    public LocalFilesAdapter(Context context, List<File> files) {
        mContext = context;
        mData = files;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_local_file, null);
        ItemHolder holder = new ItemHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ItemHolder holder, int position) {
        Glide.with(mContext).load(mData.get(position)).into(holder.imgThumbnail);
        holder.txtName.setText(mData.get(position).getName());
        holder.relaGroup.setOnClickListener(new ItemClick(mData.get(position)));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }




    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    static class ItemHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        ImageView imgThumbnail;
        RelativeLayout relaGroup;

        public ItemHolder(View itemView) {
            super(itemView);
            relaGroup = (RelativeLayout) itemView.findViewById(R.id.item_file_group);
            txtName = (TextView) itemView.findViewById(R.id.item_file_name);
            imgThumbnail = (ImageView) itemView.findViewById(R.id.item_file_thumbnail);
        }
    }

    private class ItemClick implements View.OnClickListener {
        private File mFile;

        public ItemClick(File file) {
            mFile = file;
        }

        @Override
        public void onClick(View v) {
            if (mData.contains(mFile) && mOnItemClickListener != null) {
                mOnItemClickListener.onItemClick(mData.indexOf(mFile));
            }
        }
    }
}
