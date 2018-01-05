package com.chezi008.videosurveillance.viedeoview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.TextureView;

/**
 * 描述：
 * 作者：chezi008 on 2017/6/1 16:11
 * 邮箱：chezi008@163.com
 */

public class VideoTextureView extends TextureView {
    public String TAG = getClass().getSimpleName();

    public VideoTextureView(Context context) {
        this(context, null);
    }

    public VideoTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        int height = 0;
//        Log.d(TAG, "onMeasure: widthMode-->" + widthMode + "heightMode-->" + heightMode);
//        if (widthMode == MeasureSpec.EXACTLY) {
//            // Parent has told us how big to be. So be it.
//            width = widthSize;
//        } else if (widthMode == MeasureSpec.AT_MOST) {
//            width = 200;
//        } else {
//            width = widthSize;
//            Log.d(TAG, "onMeasure: width else");
//        }
        width = widthSize;
        height = width * 3 / 4;
        if (height > heightSize) {
            height = heightSize;
        }

//        if (heightMode == MeasureSpec.EXACTLY) {
//            // Parent has told us how big to be. So be it.
//            height = heightSize;
//        } else if (widthMode == MeasureSpec.AT_MOST) {
//            height = width * 3 / 4;
//        } else {
//            height = heightSize;
//        }
//        Log.d(TAG, "onMeasure: width-->" + width + "height-->" + height);
        setMeasuredDimension(width, height);
    }

}
