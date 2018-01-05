package com.chezi008.videosurveillance.viedeoview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.SurfaceTexture;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.chezi008.videosurveillance.R;
import com.chezi008.videosurveillance.VideoGridLayoutManager;
import com.chezi008.videosurveillance.adapter.ContainerAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ：chezi008 on 2017/12/29 10:35
 * @description ：
 * @email ：chezi008@163.com
 */

public class VideoContainer extends FrameLayout {

    private String TAG = getClass().getSimpleName();
    private static final int DEFALT_COLUMNS_NUM = 2;

    private VideoRecycler mVideoRecycler;
    private ItemTouchHelper mItemTouchHelper;
    private VideoGridLayoutManager mGridLayoutManager;
    private ContainerAdapter mAdapter;
    /**
     * 放大后的播放器
     */
    private PqVideoPlayerZoomIn mPqVideoPlayerZoomIn;

    private List<PqVideoPlayer> videoIdList;

    /**
     * 控件属性
     */
    private int mColumns;

    public VideoContainer(@NonNull Context context) {
        this(context, null);
    }

    public VideoContainer(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public VideoContainer(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.VideoRecycler);
        mColumns = ta.getInt(R.styleable.VideoRecycler_vc_columns_num, DEFALT_COLUMNS_NUM);
        initVariable();
        initView();
    }

    private void initVariable() {
        videoIdList = new ArrayList<>();

        for (int i = 0; i < mColumns * mColumns; i++) {
            PqVideoPlayer pqVideoPlayer = new PqVideoPlayer(getContext());
            pqVideoPlayer.setId(1000 + i);
            videoIdList.add(pqVideoPlayer);
        }

        mPqVideoPlayerZoomIn = new PqVideoPlayerZoomIn(getContext());
        mGridLayoutManager = new VideoGridLayoutManager(getContext(), mColumns);
        mAdapter = new ContainerAdapter(videoIdList);
        mVideoRecycler = new VideoRecycler(getContext());

        mAdapter.setVideoPlayerListener(new PqVideoPlayer.VideoPlayerListener() {
            @Override
            public void onZoomInView(PqVideoPlayer videoPlayer) {
                videoPlayer.setVisibility(GONE);
                mPqVideoPlayerZoomIn.setVisibility(View.VISIBLE);
                mPqVideoPlayerZoomIn.setPqZoomOutVideoListener(videoPlayer);
            }

            @Override
            public void onChangeSurface(SurfaceTexture surfaceTexture) {
                mPqVideoPlayerZoomIn.setSurfaceView(surfaceTexture);
            }
        });

        mItemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                Log.d(TAG, "getMovementFlags: ");
                int dragFlags;
                int swipeFlags;
                if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                    dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN |
                            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                    swipeFlags = 0;
                } else {
                    dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                    swipeFlags = 0;
                }
                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                Log.d(TAG, "onMove: ");
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(videoIdList, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i < toPosition; i--) {
                        Collections.swap(videoIdList, i, i - 1);
                    }
                }
                mAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Log.d(TAG, "onSwiped: ");
            }
        });
    }

    private void initView() {

        mVideoRecycler.setLayoutManager(mGridLayoutManager);
        mVideoRecycler.setAdapter(mAdapter);
        //容器可拖拽
        mItemTouchHelper.attachToRecyclerView(mVideoRecycler);

        //添加放大的播放器
        FrameLayout.LayoutParams zoomInParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                , ViewGroup.LayoutParams.MATCH_PARENT);
        zoomInParams.gravity = Gravity.CENTER;

        //添加视频列表
        addView(mVideoRecycler, zoomInParams);
        //添加放大的播放器
        mPqVideoPlayerZoomIn.setVisibility(GONE);
        addView(mPqVideoPlayerZoomIn, zoomInParams);
    }
}
