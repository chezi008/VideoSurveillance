package com.chezi008.videosurveillance.ui;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chezi008.videosurveillance.R;
import com.chezi008.videosurveillance.base.BaseActivity;
import com.chezi008.videosurveillance.utils.PermissionTools;

/**
 * @author ：chezi008 on 2017/12/27 17:56
 * @description ：视频监控测试主界面
 * @email ：chezi008@qq.com
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private Button btn_video_container,btn_auto_update;
    private PermissionTools mPermissionTools;

    private String[] permissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    public int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Override
    public void initVariable() {
        mPermissionTools = new PermissionTools(this, permissions);
        mPermissionTools.requestApplicationPermission();
    }

    @Override
    public void initView() {
        btn_video_container = findViewById(R.id.btn_video_container);
        btn_auto_update = findViewById(R.id.btn_auto_update);

        btn_video_container.setOnClickListener(this);
        btn_auto_update.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_video_container:
                PqVideoContainerActivity.start(this);
                break;
            case R.id.btn_auto_update:
                PqVideoContainerActivity.start(this);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mPermissionTools.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
