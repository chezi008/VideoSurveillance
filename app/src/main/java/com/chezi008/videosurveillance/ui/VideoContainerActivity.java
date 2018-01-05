package com.chezi008.videosurveillance.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.chezi008.videosurveillance.VideoGridLayoutManager;
import com.chezi008.videosurveillance.R;
import com.chezi008.videosurveillance.adapter.ContainerAdapter;
import com.chezi008.videosurveillance.base.BaseActivity;
import com.chezi008.videosurveillance.bean.VideoBean;
import com.chezi008.videosurveillance.viedeoview.PqVideoPlayer;
import com.chezi008.videosurveillance.viedeoview.PqVideoPlayerZoomIn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ：chezi008 on 2017/12/27 17:59
 * @description ：视频播放器容器界面
 * @email ：chezi008@qq.com
 */
public class VideoContainerActivity extends BaseActivity {
    private String TAG = getClass().getSimpleName();

    private RecyclerView rv_list;
    private PqVideoPlayerZoomIn pqZoomOutVideoPlayer;
    private PqVideoPlayer mCurPqVideoPlayer;
    private GridLayoutManager mGridLayoutManager;

    private ContainerAdapter containerAdapter;
    private List<VideoBean> mData;


    public static void start(Context context) {
        Intent starter = new Intent(context, VideoContainerActivity.class);
        context.startActivity(starter);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_video_container;
    }

    @Override
    public void initVariable() {
        mData = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            VideoBean videoBean = new VideoBean();
            mData.add(videoBean);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void initView() {
        rv_list = findViewById(R.id.rv_list);
        pqZoomOutVideoPlayer = findViewById(R.id.pq_video);

//        containerAdapter = new ContainerAdapter(mData);
        containerAdapter.setVideoPlayerListener(new PqVideoPlayer.VideoPlayerListener() {
            @Override
            public void onZoomInView(PqVideoPlayer videoPlayer) {
                mCurPqVideoPlayer = videoPlayer;

                videoPlayer.setVisibility(View.GONE);
                pqZoomOutVideoPlayer.setVisibility(View.VISIBLE);
                pqZoomOutVideoPlayer.setPqZoomOutVideoListener(videoPlayer);

//                mGridLayoutManager.setSpanCount(1);
//                mGridLayoutManager.requestLayout();
            }

            @Override
            public void onChangeSurface(SurfaceTexture surfaceTexture) {
                pqZoomOutVideoPlayer.setSurfaceView(surfaceTexture);
            }

        });
        mGridLayoutManager = new VideoGridLayoutManager(this, 2);
        rv_list.setLayoutManager(mGridLayoutManager);
        rv_list.setAdapter(containerAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
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
                        Collections.swap(mData, i, i + 1);
                    }
                } else {
                    for (int i = fromPosition; i < toPosition; i--) {
                        Collections.swap(mData, i, i - 1);
                    }
                }
                containerAdapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Log.d(TAG, "onSwiped: ");
            }
        });
        itemTouchHelper.attachToRecyclerView(rv_list);
    }

    @Override
    public void initData() {

    }

}
