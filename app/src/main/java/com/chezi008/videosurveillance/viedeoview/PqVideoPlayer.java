package com.chezi008.videosurveillance.viedeoview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chezi008.videosurveillance.H264ReadRunable;
import com.chezi008.videosurveillance.R;
import com.chezi008.videosurveillance.ui.FullScreenActivity;
import com.chezi008.videosurveillance.utils.DensityUtils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 描述：自定义播放器
 *
 * @author ：chezi008 on 2017/11/21 15:41
 *         邮箱：chezi008@163.com
 */

public class PqVideoPlayer extends RelativeLayout implements View.OnClickListener, PqVideoPlayerZoomIn.PqZoomOutVideoListener {

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAXIMUM_POOL_SIZE = 10;
    private static final int KEEP_ALIVE_TIME = 60000;

    public static final int HANDLER_SET_STATUS_DEAULT = 0;
    public static final int HANDLER_SET_STATUS_PLAYING = 1;
    public static final int HANDLER_UPDATE_BPS = 2;
    public static final int HANDLER_UPDATE_LOSS_FRAME = 3;

    private ArrayBlockingQueue mArrayBlockingQueue;
    private String TAG = getClass().getSimpleName();
    //自定义属性  1、选中时候边框大小 2、选中边框颜色
    //3、默认背景色 4、选中背景色
    // 5、资源添加icon
    /**
     * 默认边框大小
     */
    private final int DEFAULT_BORDER_SIZE = 1;
    private final int DEFALUT_BORDER_COLOR = Color.parseColor("#00000000");
    /**
     * 默认背景
     */
    private final int DEFAULT_BG_COLOR = Color.parseColor("#6b7e9e");
    private final int DEFAULT_BG_SELECT_COLOR = Color.parseColor("#FFFFFF");
    /**
     * 默认添加资源图片
     */
    private final int DEFAULT_RES_ICON = R.mipmap.vp_ic_res_add;
    private final int DEFAULT_FULL_SCREEN_ICON = R.mipmap.vp_ic_full_screen;
    private final int DEFAULT_STOP_ICON = R.mipmap.vp_ic_stop_play;
    /**
     * 选中边框
     */
    private int mBorderSize, mBorderColor;
    private int mBgColor, mBgSelectColor;
    private int mResIcon, mFullIcon, mStopIcon;

    private RelativeLayout mRlBorderBg, mRlVideoBg, mTextureParent, mRlBottomBar;
    private TextureView mTextureView;
    private SurfaceTexture mSurfaceTexture;
    private TextureView.SurfaceTextureListener mSurfaceTextureListener;

    private ProgressBar mProcessBar;
    private TextView mTvPath, mTvLoss;
    private ImageView ivResAdd;

    /**
     * 解码器
     */
    private PqVideoDecoder mVideoDecoder;
    /**
     * 线程池
     */
    private ThreadPoolExecutor mExecutorService;
    private ScheduledExecutorService mScheduledExecutorService;
    private Runnable mScheduleRunable;
    private H264ReadRunable mH264ReadRunable;

    private boolean isFullScreen, isFoucsed, isZoomIn;
    /**
     * 码流统计
     */
    private Handler mMainHandler;

    private VideoPlayerListener videoPlayerListener;
    private PqVideoOnclickListener mPqVideoOnclickListener;

    private PlayerViewState curState;

    public PqVideoPlayer(Context context) {
        this(context, null);
    }

    public PqVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PqVideoPlayer);
        mBorderSize = ta.getInt(R.styleable.PqVideoPlayer_vp_border_size, DEFAULT_BORDER_SIZE);
        mBorderColor = ta.getColor(R.styleable.PqVideoPlayer_vp_border_color, DEFALUT_BORDER_COLOR);
        mBgColor = ta.getColor(R.styleable.PqVideoPlayer_vp_bg_default_color, DEFAULT_BG_COLOR);
        mBgSelectColor = ta.getColor(R.styleable.PqVideoPlayer_vp_bg_select_color, DEFAULT_BG_SELECT_COLOR);
        mResIcon = ta.getResourceId(R.styleable.PqVideoPlayer_vp_res_icon, DEFAULT_RES_ICON);
        mFullIcon = ta.getResourceId(R.styleable.PqVideoPlayer_vp_fullscreen_icon, DEFAULT_FULL_SCREEN_ICON);
        mStopIcon = ta.getResourceId(R.styleable.PqVideoPlayer_vp_stop_icon, DEFAULT_STOP_ICON);

        initVirable();
        initView();
    }


    public void setVideoPlayerListener(VideoPlayerListener videoPlayerListener) {
        this.videoPlayerListener = videoPlayerListener;
    }

    public void setPqVideoOnclickListener(PqVideoOnclickListener mPqVideoOnclickListener) {
        this.mPqVideoOnclickListener = mPqVideoOnclickListener;
    }

    private void initVirable() {
        mArrayBlockingQueue = new ArrayBlockingQueue(MAXIMUM_POOL_SIZE);
        mExecutorService = new ThreadPoolExecutor(CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.MILLISECONDS, mArrayBlockingQueue);
        mScheduledExecutorService = new ScheduledThreadPoolExecutor(1);

        if (mMainHandler == null) {
            mMainHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        case HANDLER_SET_STATUS_DEAULT:
                            setDefaultUI();
                            break;
                        case HANDLER_SET_STATUS_PLAYING:
                            setPlayerViewState(PlayerViewState.Playying);
                            break;
                        case HANDLER_UPDATE_BPS:
//                            if (videoPlayerFullScreenListener != null) {
//                                videoPlayerFullScreenListener.onGetBps((String) msg.obj);
//                            }
                            break;
                        //播放器丢包率更新
                        case HANDLER_UPDATE_LOSS_FRAME:
                            mTvLoss.setText((String) msg.obj);
//                            if (videoPlayerFullScreenListener != null) {
//                                videoPlayerFullScreenListener.onGetLossFrame((String) msg.obj);
//                            }
                            break;
                        default:
                            break;
                    }
                }
            };
        }
    }

    /**
     * 初始化视图
     */
    private void initView() {
        //设置屏幕常亮
        setKeepScreenOn(true);
        //添加选中的边框
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mRlBorderBg = new RelativeLayout(getContext());
        mRlBorderBg.setBackgroundColor(mBorderColor);
        mRlBorderBg.setPadding(mBorderSize, mBorderSize, mBorderSize, mBorderSize);
        addView(mRlBorderBg, layoutParams);
        //添加播放器背景
        layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mRlVideoBg = new RelativeLayout(getContext());
        mRlVideoBg.setBackgroundColor(mBgColor);
        mRlBorderBg.addView(mRlVideoBg, layoutParams);

        //添加播放器显示界面
        layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mTextureParent = new RelativeLayout(getContext());
        mTextureParent.setBackgroundColor(Color.BLACK);
        mTextureParent.setVisibility(GONE);
        mRlVideoBg.addView(mTextureParent, layoutParams);

        //添加添加资源图标
        layoutParams = new LayoutParams(DensityUtils.sp2px(getContext(), 35),
                DensityUtils.sp2px(getContext(), 35));
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        ivResAdd = new ImageView(getContext());
        ivResAdd.setImageResource(mResIcon);
        ivResAdd.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ivResAdd.setId(R.id.iv_res_add);
        ivResAdd.setOnClickListener(this);
        mRlVideoBg.addView(ivResAdd, layoutParams);
        //添加progres
        mProcessBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyle);
        mRlVideoBg.addView(mProcessBar, layoutParams);
        mProcessBar.setVisibility(GONE);

//        addTextureView();

        //添加播放器控制栏
        //底部白色背景
        LayoutParams layoutParamsInner = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mRlBottomBar = new RelativeLayout(getContext());
        mRlBottomBar.setBackgroundColor(Color.WHITE);
        mRlBottomBar.getBackground().setAlpha(128);
        mRlBottomBar.setGravity(CENTER_VERTICAL);
        layoutParamsInner.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mRlVideoBg.addView(mRlBottomBar, layoutParamsInner);
        mRlBottomBar.setVisibility(GONE);
        //全屏
        int btnWidth = DensityUtils.sp2px(getContext(), 25);
        int btnPadding = DensityUtils.sp2px(getContext(), 3);
        LayoutParams layoutParamsFullScreen = new LayoutParams(btnWidth, btnWidth);
        layoutParamsFullScreen.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        ImageView ivFull = new ImageView(getContext());
        ivFull.setId(R.id.btnFullScreen);

        ivFull.setPadding(btnPadding, btnPadding, btnPadding, btnPadding);
        ivFull.setImageResource(mFullIcon);
        ivFull.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        ivFull.setOnClickListener(this);
        mRlBottomBar.addView(ivFull, layoutParamsFullScreen);
        //停止
        LayoutParams layoutParamsStopVideo = new LayoutParams(btnWidth, btnWidth);
        layoutParamsStopVideo.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        ImageView ivStop = new ImageView(getContext());
        ivStop.setId(R.id.btnStop);
        ivStop.setPadding(btnPadding, btnPadding, btnPadding, btnPadding);
        ivStop.setImageResource(mStopIcon);
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

        LayoutParams layoutParamsDevicePathTextView = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParamsDevicePathTextView.setMargins(btnPadding, btnPadding, btnPadding, btnPadding);
        layoutParamsDevicePathTextView.addRule(RIGHT_OF, R.id.btnStop);
        layoutParamsDevicePathTextView.addRule(RelativeLayout.LEFT_OF, R.id.tvLossFrame);
        layoutParamsDevicePathTextView.addRule(CENTER_VERTICAL);
        mRlBottomBar.addView(llPath, layoutParamsDevicePathTextView);

        //丢包率
        mTvLoss = new TextView(getContext());
        mTvLoss.setId(R.id.tvLossFrame);
        mTvLoss.setTextColor(Color.WHITE);
        mTvLoss.setTextSize(8);
        mTvLoss.setLines(1);
        mTvLoss.setText("0.00%");
        LayoutParams rlLoss = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rlLoss.setMargins(btnPadding, btnPadding, btnPadding, btnPadding);
        rlLoss.addRule(RelativeLayout.LEFT_OF, R.id.btnFullScreen);
        rlLoss.addRule(CENTER_VERTICAL);
        mRlBottomBar.addView(mTvLoss, rlLoss);

        setPlayerViewState(PlayerViewState.Default);
//        setPlayerViewState(PlayerViewState.Playying);
    }

    private void initTextureView() {
        if (mTextureView == null) {
            mTextureView = new VideoTextureView(getContext());
            mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                @Override
                public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                    Log.d(TAG, "onSurfaceTextureAvailable: ");
                    //检查解码器是否初始化
                    if (!initDecoder(surface)) {
                        Log.d(TAG, "onSurfaceTextureAvailable: init decoder failed!");
                        return;
                    }
                    if (mH264ReadRunable != null) {
                        Log.d(TAG, "onSurfaceTextureAvailable: 读取视频流");
                        mExecutorService.execute(mH264ReadRunable);
                    }
                }

                @Override
                public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

                }

                @Override
                public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                    Log.d(TAG, "onSurfaceTextureDestroyed: ");
//                    stopDecoder();
                    mSurfaceTexture = surface;
                    if (isZoomIn) {
                        videoPlayerListener.onChangeSurface(surface);
                    }
                    return false;
                }

                @Override
                public void onSurfaceTextureUpdated(SurfaceTexture surface) {

                }
            });

        }
    }

    /**
     * 添加显示画面
     */
    private void addTextureView() {
        Log.d(TAG, "addTextureView: ----------->");
        LayoutParams textureParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        textureParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        if (mTextureParent.getChildCount() > 0) {
            throw new IllegalStateException("已经添加了显示控件！");
        }
        //不能添加空控件  播放器setPlaying的时候会添加
        mTextureParent.addView(mTextureView, textureParams);
        mTextureParent.setVisibility(VISIBLE);
    }


    /**
     * 停止解码器
     */
    private void stopDecoder() {
        if (mVideoDecoder != null) {
            mVideoDecoder.stop();
        }
    }

    /**
     * 移除显示画面
     */
    private void removeTextureView() {
        mTextureParent.setVisibility(GONE);
        if (mTextureParent.getChildCount() > 0) {
            mTextureParent.removeView(mTextureView);
//            mTextureView = null;
        }
    }

    private void resetTextureView() {
        mTextureParent.setVisibility(GONE);
        if (mTextureParent.getChildCount() > 0) {
            mTextureParent.removeView(mTextureView);
            mTextureView = null;
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_res_add) {
            //播放
            if (mH264ReadRunable == null) {
                mH264ReadRunable = new H264ReadRunable();
                mH264ReadRunable.setH264ReadListener(new H264ReadRunable.H264ReadListener() {
                    @Override
                    public void onFrameData(byte[] datas) {
                        mVideoDecoder.feedData(datas, 0, datas.length);
                    }

                    @Override
                    public void onStopRead() {
                        stopVideoPlayer(true);
                    }
                });
            }
            setPlayerViewState(PlayerViewState.Playying);
        } else if (v.getId() == R.id.btnFullScreen) {
            //全屏
            startFullScreen();
        } else if (v.getId() == R.id.btnStop) {
            //停止
            Log.d(TAG, "onClick:btnStop ");
            stopVideoPlayer(true);
        }
        setSelectStyle();
    }

    /**
     * 开启全屏
     */
    public void startFullScreen() {
        isFullScreen = true;
        FullScreenActivity.start(getContext());
    }

    /**
     * 退出全屏模式
     */
    public void exitFullScreen() {
        isFullScreen = false;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
//        gestureDetector.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                doubleClick_2();
                mPqVideoOnclickListener.onClick();
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
        if (mTextureView != null) {
            mSurfaceTextureListener = mTextureView.getSurfaceTextureListener();
        }
        isZoomIn = true;
        removeTextureView();
        videoPlayerListener.onZoomInView(PqVideoPlayer.this);
    }

    /**
     * 设置选中的状态
     */
    private void setSelectStyle() {
        isFoucsed = true;
        //背景为黑色，且出现选中边框
        mRlVideoBg.setBackgroundColor(Color.BLACK);
        mRlBorderBg.setBackgroundColor(mBgSelectColor);
    }

    private void setDefultStyle() {
        isFoucsed = false;
        mRlVideoBg.setBackgroundColor(mBgColor);
        mRlBorderBg.setBackgroundColor(mBorderColor);
    }

    /**
     * 设置时候获取焦点
     *
     * @param focus
     */
    public void setFocus(boolean focus) {
        if (focus) {
            setSelectStyle();
        } else {
            setDefultStyle();
        }
    }

    /**
     * 开始播放视频
     */
    public void startVideoPlayer() {
        setPlayerViewState(PlayerViewState.Playying);
        //设置轮播字幕
//        mTvPath.setText(mParams.getVideoName());
    }

    public void stopVideoPlayer(boolean isClear) {
        //停止视频解码器
        stopDecoder();
        //移除显示画面
        mMainHandler.sendEmptyMessage(HANDLER_SET_STATUS_DEAULT);
//        setPlayerViewState(PlayerViewState.Default);
        if (isClear) {
//            mParams.getSessionParams().requestId = null;
        }
    }

    /**
     * 显示加载动画
     */
    public void showProgressbar() {
        setPlayerViewState(PlayerViewState.Waiting);
        //定时器 15s还没来自动关闭
        if (mScheduleRunable == null) {
            mScheduleRunable = new Runnable() {
                @Override
                public void run() {
                    if (mProcessBar.getVisibility() == VISIBLE) {
                        mMainHandler.sendEmptyMessage(HANDLER_SET_STATUS_DEAULT);
                    }
                }
            };
        }
        mScheduledExecutorService.schedule(mScheduleRunable, 15000, TimeUnit.MILLISECONDS);
    }


    private void setPlayerViewState(PlayerViewState state) {
        curState = state;
        Log.d(TAG, "setPlayerViewState: " + curState);
        switch (state) {
            case Default:
                //默认背景
                mRlVideoBg.setBackgroundColor(mBgColor);
                ivResAdd.setVisibility(VISIBLE);
                //隐藏textureView
                resetTextureView();
                //隐藏dialog
                mProcessBar.setVisibility(GONE);
                //隐藏底部控制栏
                mRlBottomBar.setVisibility(GONE);
                break;
            case Waiting:
                //背景设为黑色
                mRlVideoBg.setBackgroundColor(Color.BLACK);
                //隐藏资源图标
                ivResAdd.setVisibility(GONE);
                //显示dialog
                mProcessBar.setVisibility(VISIBLE);
                break;
            case Playying:
                //隐藏dialog
                mProcessBar.setVisibility(GONE);
                //隐藏资源
                ivResAdd.setVisibility(GONE);
                initTextureView();
                addTextureView();
                //显示底部控制栏
                mRlBottomBar.setVisibility(VISIBLE);
                break;
            case Control:
                break;
        }
    }


    private boolean initDecoder(SurfaceTexture surface) {
        Log.d(TAG, "initDecoder: ");
        if (mVideoDecoder == null) {
            mVideoDecoder = new PqVideoDecoder();
        }
        boolean initSuccess = mVideoDecoder.init(surface,
                1280,
                720);

        return initSuccess;
    }


    private void setDefaultUI() {
        //判断当前是否是播放状态
        if (mRlBottomBar.getVisibility() == VISIBLE || mProcessBar.getVisibility() == VISIBLE) {
            //停止视频解码器
            stopDecoder();
            //移除显示画面
            setPlayerViewState(PlayerViewState.Default);
            setSelectStyle();
        }
    }

    @Override
    public void onZoomIn() {
        onResume();
        setVisibility(VISIBLE);
        isZoomIn = false;
    }


    public interface VideoPlayerListener {
        /**
         * 双击放大
         *
         * @param videoPlayer
         */
        void onZoomInView(PqVideoPlayer videoPlayer);

        /**
         * 渲染画面被交换
         *
         * @param surfaceTexture
         */
        void onChangeSurface(SurfaceTexture surfaceTexture);

    }

    public void onResume() {
        if (curState == PlayerViewState.Playying) {
            setSurfaceTexture();
        }
    }

    private void setSurfaceTexture() {
        if (null == mTextureView.getSurfaceTexture()) {
            Log.d(TAG, "setSurfaceTexture: ");
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
            mTextureView.setSurfaceTexture(mSurfaceTexture);
        }
        addTextureView();
    }

    public SurfaceTexture getSurfaceTexture() {
        return mTextureView.getSurfaceTexture();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
                getDefaultSize(0, heightMeasureSpec));

        int childWidthSize = getMeasuredWidth();
        // 高度和宽度一样
        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                childWidthSize, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public interface PqVideoOnclickListener {
        void onClick();
    }
}
