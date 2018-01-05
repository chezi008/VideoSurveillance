package com.chezi008.videosurveillance.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

/**
 * 描述：
 * 作者：chezi008 on 2017/7/6 9:51
 * 邮箱：chezi008@163.com
 */

public class PermissionTools {
    private static final int MY_PERMISSION_REQUEST_CODE = 10000;
    private Context mContext;
    private String[] requestPermissions;
    private RequestPermissionListenner mRequestPermissionListenner;

    public PermissionTools(Context context, String[] permissions) {
        this.mContext = context;
        this.requestPermissions = permissions;
    }

    public void setmRequestPermissionListenner(RequestPermissionListenner mRequestPermissionListenner) {
        this.mRequestPermissionListenner = mRequestPermissionListenner;
    }

    /**
     * 点击按钮，将通讯录备份保存到外部存储器备。
     * <p>
     * 需要3个权限(都是危险权限):
     * 1. 读取通讯录权限;
     * 2. 读取外部存储器权限;
     * 3. 写入外部存储器权限.
     */
    public void requestApplicationPermission() {
        /**
         * 第 1 步: 检查是否有相应的权限
         */
        boolean isAllGranted = checkPermissionAllGranted(requestPermissions);
        // 如果这3个权限全都拥有, 则直接执行备份代码
        if (isAllGranted) {
            if (mRequestPermissionListenner != null)
                mRequestPermissionListenner.requestSuccess();
            return;
        }

        /**
         * 第 2 步: 请求权限
         */
        // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
        ActivityCompat.requestPermissions(
                (Activity) mContext,
                requestPermissions,
                MY_PERMISSION_REQUEST_CODE
        );
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;

            // 判断是否所有的权限都已经授予了
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                // 如果所有的权限都授予了, 则执行备份代码
                if (mRequestPermissionListenner != null){
                    mRequestPermissionListenner.requestSuccess();
                }
            } else {
                // 弹出对话框告诉用户需要权限的原因, 并引导用户去应用权限管理中手动打开权限按钮
                openAppDetails();
            }
        }
    }

    /**
     * 打开 APP 的详情设置
     */
    private void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("缺少必要权限，请到 “应用信息 -> 权限” 中授予！");
        builder.setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                mContext.startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }

    public interface RequestPermissionListenner {
        void requestSuccess();
    }
}
