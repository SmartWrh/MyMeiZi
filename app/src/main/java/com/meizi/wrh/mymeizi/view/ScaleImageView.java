package com.meizi.wrh.mymeizi.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by wrh on 16/2/19.
 */
public class ScaleImageView extends ImageView {
    public ScaleImageView(Context context) {
        super(context);
    }

    public ScaleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScaleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ObjectAnimator.ofFloat(this, "ScaleX", 1.0f, 0.8f).setDuration(300).start();
                ObjectAnimator.ofFloat(this, "ScaleY", 1.0f, 0.8f).setDuration(300).start();
                break;
            case MotionEvent.ACTION_UP:
                ObjectAnimator.ofFloat(this, "ScaleX", 0.8f, 1.0f).setDuration(300).start();
                ObjectAnimator.ofFloat(this, "ScaleY", 0.8f, 1.0f).setDuration(300).start();
                break;
        }

        return super.onTouchEvent(event);
    }
}
