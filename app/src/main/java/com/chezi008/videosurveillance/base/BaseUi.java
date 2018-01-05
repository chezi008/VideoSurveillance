package com.chezi008.videosurveillance.base;

import android.support.annotation.LayoutRes;

/**
 * @author ：chezi008 on 2017/12/27 18:07
 * @description ：
 * @email ：chezi008@163.com
 */

public interface BaseUi {
    @LayoutRes
    int getLayoutResId();

    void initVariable();

    void initView();

    void initData();
}
