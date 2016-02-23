package com.meizi.wrh.mymeizi.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meizi.wrh.mymeizi.R;
import com.meizi.wrh.mymeizi.listener.OnItemClickListener;
import com.meizi.wrh.mymeizi.view.ColorView;

/**
 * Created by wrh on 16/2/20.
 */
public class SelectDialog extends Dialog implements AdapterView.OnItemClickListener {

    private String colorNames[] = {"日间模式", "夜间模式", "少女模式", "菊花模式"};

    private int colors[] = {R.color.colorPrimary, R.color.colorPrimaryDark_night, R.color.colorPrimary_pink, R.color.colorPrimary_yellow};

    private ListView listView;
    private OnItemClickListener mOnItemClickListener;

    public SelectDialog(Context context) {
        super(context, R.style.SelectDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_color);
        listView = (ListView) findViewById(R.id.dialog_list_select);
        listView.setAdapter(new SelectAdapter(getContext(), R.layout.adapter_select_color, colorNames));
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(position);
        }
        SelectDialog.this.dismiss();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }


    private class SelectAdapter extends ArrayAdapter<String> {
        private int mResourceId;
        private String[] mData;

        public SelectAdapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
            mResourceId = resource;
            mData = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(mResourceId, null);
                holder = new ItemHolder();
                holder.colorView = (ColorView) convertView.findViewById(R.id.item_color_view);
                holder.txtName = (TextView) convertView.findViewById(R.id.item_color_name);
                convertView.setTag(holder);
            } else {
                holder = (ItemHolder) convertView.getTag();
            }
            holder.colorView.setColor(colors[position]);
            holder.txtName.setText(mData[position]);
            holder.txtName.setTextColor(ContextCompat.getColor(getContext(), colors[position]));
            return convertView;
        }

        class ItemHolder {
            private ColorView colorView;
            private TextView txtName;
        }
    }
}
