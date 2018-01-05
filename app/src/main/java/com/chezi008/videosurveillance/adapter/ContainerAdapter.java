package com.chezi008.videosurveillance.adapter;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chezi008.videosurveillance.R;
import com.chezi008.videosurveillance.bean.VideoBean;
import com.chezi008.videosurveillance.viedeoview.PqVideoPlayer;

import java.util.List;

/**
 * @author ：chezi008 on 2017/12/27 18:10
 * @description ：
 * @email ：chezi008@163.com
 */

public class ContainerAdapter extends RecyclerView.Adapter<ContainerAdapter.ContainerHoler> {
    private String TAG = getClass().getSimpleName();
    private PqVideoPlayer.VideoPlayerListener videoPlayerListener;
    private List<PqVideoPlayer> mData;

    private ConstraintLayout.LayoutParams layoutParams;

    public ContainerAdapter(List<PqVideoPlayer> datas) {
        this.mData = datas;
    }

    @Override
    public ContainerHoler onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container, parent, false);
        ContainerHoler containerHoler = new ContainerHoler(rootView);
        layoutParams = new ConstraintLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        return containerHoler;
    }

    @Override
    public void onBindViewHolder(final ContainerHoler holder, int position) {
//        final VideoBean videoBean = mData.get(position);
//        holder.pqVideoPlayer.setId(videoBean.getId());
//        holder.pqVideoPlayer.setFocus(videoBean.isSelect());
//        holder.pqVideoPlayer.setVideoPlayerListener(videoPlayerListener);
//        holder.pqVideoPlayer.setPqVideoOnclickListener(new PqVideoPlayer.PqVideoOnclickListener() {
//            @Override
//            public void onClick() {
//                refreshSelect(videoBean);
//            }
//        });
        final PqVideoPlayer pqVideoPlayer = mData.get(position);
        pqVideoPlayer.setVideoPlayerListener(videoPlayerListener);
        pqVideoPlayer.setPqVideoOnclickListener(new PqVideoPlayer.PqVideoOnclickListener() {
            @Override
            public void onClick() {
                refreshSelect(pqVideoPlayer);
            }
        });
        holder.constraintLayout.addView(pqVideoPlayer,0,layoutParams);

    }

    private void refreshSelect(PqVideoPlayer videoBean) {
        Log.d(TAG, "refreshSelect: ");
        for (PqVideoPlayer vb :
                mData) {
            vb.setFocus(vb == videoBean);
        }
//        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ContainerHoler extends RecyclerView.ViewHolder {
//        private PqVideoPlayer pqVideoPlayer;
        private ConstraintLayout constraintLayout;

        public ContainerHoler(View itemView) {
            super(itemView);
//            pqVideoPlayer = itemView.findViewById(R.id.pq_video_player);
            constraintLayout = itemView.findViewById(R.id.container);
        }
    }

    public void setVideoPlayerListener(PqVideoPlayer.VideoPlayerListener videoPlayerListener) {
        this.videoPlayerListener = videoPlayerListener;
    }
}
