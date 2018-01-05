package com.chezi008.videosurveillance.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * @author ：chezi008 on 2017/12/27 18:05
 * @description ：
 * @email ：chezi008@163.com
 */

public abstract class BaseActivity extends AppCompatActivity implements BaseUi {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBaseView();
        initVariable();
        initView();
        initData();
    }

    protected  void initBaseView(){
        setContentView(getLayoutResId());
    }
}
