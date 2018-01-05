package com.chezi008.videosurveillance.ui;

import android.content.Context;
import android.content.Intent;

import com.chezi008.videosurveillance.R;
import com.chezi008.videosurveillance.base.BaseActivity;

public class PqVideoContainerActivity extends BaseActivity {


    @Override
    public int getLayoutResId() {
        return R.layout.activity_pq_video_container;
    }

    @Override
    public void initVariable() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

    }

    public static void start(Context context) {
        Intent starter = new Intent(context, PqVideoContainerActivity.class);
        context.startActivity(starter);
    }
}
