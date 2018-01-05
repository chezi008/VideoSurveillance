package com.chezi008.videosurveillance.viedeoview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

import com.chezi008.videosurveillance.R;

/**
 * @author ：chezi008 on 2017/12/29 10:37
 * @description ：
 * @email ：chezi008@163.com
 */

public class VideoRecycler extends RecyclerView {

    public VideoRecycler(Context context) {
        this(context, null);
    }

    public VideoRecycler(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoRecycler(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private void initView() {
        setBackgroundColor(getResources().getColor(R.color.colorAccent));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = 0;
        int height = 0;

        width = widthSize;
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            default:
                height = widthSize;
                break;
        }
        if (height > heightSize) {
            height = heightSize;
        }
        setMeasuredDimension(width, height);
    }
}
