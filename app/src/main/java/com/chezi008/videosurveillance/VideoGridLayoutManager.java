package com.chezi008.videosurveillance;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author ：chezi008 on 2017/12/28 11:44
 * @description ：
 * @email ：chezi008@163.com
 */

public class VideoGridLayoutManager extends GridLayoutManager{
    public VideoGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public VideoGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public VideoGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public boolean canScrollVertically() {
//        super.canScrollVertically()
        return false;
    }

}
