package com.meizi.wrh.mymeizi.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.meizi.wrh.mymeizi.R;

/**
 * Created by wrh on 16/2/20.
 */
public class StrokeImageView extends ImageView {

    private final static int STROKE_WIDTH = 2;

    private int mStrokeWidth;


    public StrokeImageView(Context context) {
        super(context);
    }

    public StrokeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.StrokeImageView);
        mStrokeWidth = a.getDimensionPixelOffset(R.styleable.StrokeImageView_strokeWidth, STROKE_WIDTH);
        a.recycle();
    }

    public StrokeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Rect rect = canvas.getClipBounds();
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(mStrokeWidth);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        int x = rect.width() / 2;
        int y = rect.height() / 2;
        int radius = x - mStrokeWidth / 2;
        canvas.drawCircle(x, y, radius, paint);
    }
}
