package com.chezi008.videosurveillance.viedeoview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chezi008.videosurveillance.R;
import com.chezi008.videosurveillance.utils.DensityUtils;

/**
 * @author ：chezi008 on 2017/12/28 16:30
 * @description ：
 * @email ：chezi008@163.com
 */

public class PqVideoPlayerZoomIn extends FrameLayout implements View.OnClickListener {
    private String TAG = getClass().getSimpleName();

    private RelativeLayout mTextureParent, mRlBottomBar;
    private TextureView mTextureView;

    private ImageView ivResAdd;

    private TextView mTvPath, mTvLoss;

    private PqZoomOutVideoListener mPqZoomOutVideoListener;
    private SurfaceTexture mSurfaceTexture;

    public PqVideoPlayerZoomIn(@NonNull Context context) {
        this(context, null);
    }


    public PqVideoPlayerZoomIn(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PqVideoPlayerZoomIn(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        //设置屏幕常亮
        setKeepScreenOn(true);
        setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        addTextureParent();
//        addTextureView();
        addResIconView();
        addBottomView();
    }

    private void addBottomView() {
        //底部白色背景
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.BOTTOM;
        mRlBottomBar = new RelativeLayout(getContext());
        mRlBottomBar.setBackgroundColor(Color.WHITE);
        mRlBottomBar.getBackground().setAlpha(128);
        mRlBottomBar.setGravity(RelativeLayout.CENTER_VERTICAL);
        addView(mRlBottomBar, params);

        mRlBottomBar.setVisibility(VISIBLE);
        //全屏
        int btnWidth = DensityUtils.sp2px(getContext(), 25);
        int btnPadding = DensityUtils.sp2px(getContext(), 3);
        RelativeLayout.LayoutParams layoutParamsFullScreen = new RelativeLayout.LayoutParams(btnWidth, btnWidth);
        layoutParamsFullScreen.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        ImageView ivFull = new ImageView(getContext());
        ivFull.setId(R.id.btnFullScreen);

        ivFull.setPadding(btnPadding, btnPadding, btnPadding, btnPadding);
        ivFull.setImageResource(R.mipmap.vp_ic_full_screen);
        ivFull.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ivFull.setOnClickListener(this);
        mRlBottomBar.addView(ivFull, layoutParamsFullScreen);
        //停止
        RelativeLayout.LayoutParams layoutParamsStopVideo = new RelativeLayout.LayoutParams(btnWidth, btnWidth);
        layoutParamsStopVideo.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        ImageView ivStop = new ImageView(getContext());
        ivStop.setId(R.id.btnStop);
        ivStop.setPadding(btnPadding, btnPadding, btnPadding, btnPadding);
        ivStop.setImageResource(R.mipmap.vp_ic_stop_play);
        ivStop.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ivStop.setOnClickListener(this);
        mRlBottomBar.addView(ivStop, layoutParamsStopVideo);

        //设备路径信息
        LinearLayout llPath = new LinearLayout(getContext());
        llPath.setId(R.id.tvDeviceInfo);
        mTvPath = new TextView(getContext());
        mTvPath.setTextColor(Color.WHITE);
        mTvPath.setTextSize(8);
        mTvPath.setSingleLine(true);
        mTvPath.setText("轮播字");
        mTvPath.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        mTvPath.setMarqueeRepeatLimit(-1);
        mTvPath.setSelected(true);
        llPath.addView(mTvPath);

        RelativeLayout.LayoutParams layoutParamsDevicePathTextView = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsDevicePathTextView.setMargins(btnPadding, btnPadding, btnPadding, btnPadding);
        layoutParamsDevicePathTextView.addRule(RelativeLayout.RIGHT_OF, R.id.btnStop);
        layoutParamsDevicePathTextView.addRule(RelativeLayout.LEFT_OF, R.id.tvLossFrame);
        layoutParamsDevicePathTextView.addRule(RelativeLayout.CENTER_VERTICAL);
        mRlBottomBar.addView(llPath, layoutParamsDevicePathTextView);

        //丢包率
        mTvLoss = new TextView(getContext());
        mTvLoss.setId(R.id.tvLossFrame);
        mTvLoss.setTextColor(Color.WHITE);
        mTvLoss.setTextSize(8);
        mTvLoss.setLines(1);
        mTvLoss.setText("0.00%");
        RelativeLayout.LayoutParams rlLoss = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rlLoss.setMargins(btnPadding, btnPadding, btnPadding, btnPadding);
        rlLoss.addRule(RelativeLayout.LEFT_OF, R.id.btnFullScreen);
        rlLoss.addRule(RelativeLayout.CENTER_VERTICAL);
        mRlBottomBar.addView(mTvLoss, rlLoss);

//        mRlBorderBg.addView(mRlVideoBg, layoutParams);
    }

    private void addTextureParent() {
        //先添加其父容器
        LayoutParams parentParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        parentParams.gravity = Gravity.CENTER;

        mTextureParent = new RelativeLayout(getContext());
        mTextureParent.setBackgroundColor(Color.BLACK);
        mTextureParent.setVisibility(VISIBLE);
        addView(mTextureParent, parentParams);
    }

    private void addResIconView() {
        //添加添加资源图标
        LayoutParams layoutParams = new LayoutParams(DensityUtils.sp2px(getContext(), 35),
                DensityUtils.sp2px(getContext(), 35));
        layoutParams.gravity = Gravity.CENTER;
        ivResAdd = new ImageView(getContext());
        ivResAdd.setImageResource(R.mipmap.vp_ic_res_add);
        ivResAdd.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ivResAdd.setId(R.id.iv_res_add);
        ivResAdd.setOnClickListener(this);
        addView(ivResAdd, layoutParams);
    }

    private void addTextureView() {
        //添加渲染视图
        if (mTextureView == null) {
            mTextureView = new VideoTextureView(getContext());
            mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    Log.d(TAG, "onSurfaceTextureAvailable: ");
                    if(mSurfaceTexture!=null){
                        mTextureView.setSurfaceTexture(mSurfaceTexture);
                    }
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    Log.d(TAG, "onSurfaceTextureDestroyed: ");
                    mSurfaceTexture = surface;
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {

                }
            });
        }

        if (mTextureParent.getChildCount() > 0) {
            throw new IllegalStateException("已经添加了一个子控件了！");
        }
        RelativeLayout.LayoutParams LinlayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        LinlayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mTextureParent.addView(mTextureView, LinlayoutParams);
    }

    public void setSurfaceView(SurfaceTexture surfaceView) {
        Log.d(TAG, "setSurfaceView: ");
        addTextureView();
        mTextureView.setSurfaceTexture(surfaceView);
    }

    public void setPqZoomOutVideoListener(PqZoomOutVideoListener mPqZoomOutVideoListener) {
        this.mPqZoomOutVideoListener = mPqZoomOutVideoListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                doubleClick_2();
                return true;
            case MotionEvent.ACTION_UP:
                return true;
        }
        return super.onTouchEvent(event);
    }

    private long[] mHits = new long[2];

    private void doubleClick_2() {
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        //获取手机开机时间
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[mHits.length - 1] - mHits[0] < 500) {
            /**双击的业务逻辑*/
            Log.d(TAG, "doubleClick_2: 双击");
            onZoomViewExchange();
        }
    }

    private void onZoomViewExchange() {
        mTextureParent.removeView(mTextureView);
        mPqZoomOutVideoListener.onZoomIn();
        setVisibility(GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_res_add:
                break;
        }
    }

    public interface PqZoomOutVideoListener {
        void onZoomIn();
    }

}
