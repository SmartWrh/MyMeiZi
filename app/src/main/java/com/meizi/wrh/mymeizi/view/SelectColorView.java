package com.meizi.wrh.mymeizi.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import com.meizi.wrh.mymeizi.R;

/**
 * Created by wrh on 16/2/20.
 */
public class SelectColorView extends RelativeLayout {
    public SelectColorView(Context context) {
        super(context);
        initView(context);
    }


    public SelectColorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SelectColorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.adapter_select_color, this, true);
    }
}
