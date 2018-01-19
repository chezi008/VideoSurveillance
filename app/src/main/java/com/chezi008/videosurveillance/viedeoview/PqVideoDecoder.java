package com.chezi008.videosurveillance.viedeoview;

import android.annotation.TargetApi;
import android.graphics.SurfaceTexture;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Surface;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * @author ：chezi008 on 2017/11/29 12:34
 * @description ：视频解码器
 * @email ：chezi008@qq.com
 */

public class PqVideoDecoder {

    public static final int NALU_TYPE_IDR = 5;
    public static final int NALU_TYPE_SPS = 7;
    public static final int NALU_TYPE_PPS = 8;

    private String TAG = getClass().getSimpleName();
    private MediaFormat mVideoFormat;
    private MediaCodec mVideoCodec;
    private Surface mSurface;
    private ByteBuffer[] mVideoInputBuffers;

    private long timeUs=10000;
    private long presentationTimeUs;

    public static byte[] header_sps;
    public static byte[] header_pps;

    /**
     * 初始化视频编码器
     *
     * @param surfaceTexture
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public boolean init(SurfaceTexture surfaceTexture, int width, int height) {
        if (mVideoCodec == null) {
            try {
                mVideoFormat = MediaFormat.createVideoFormat("video/avc", width, height);
//                if (true) {
//                    Log.d(TAG, "init: header_sps"+ Arrays.toString(header_sps));
//                    byte[] sps = { 0, 0, 0, 1, 103, 100, 0, 40, -84, 52, -59, 1, -32, 17, 31, 120, 11, 80, 16, 16, 31, 0, 0, 3, 3, -23, 0, 0, -22, 96, -108 };
//                    byte[] pps = { 0, 0, 0, 1, 104, -18, 60, -128 };
//                    mVideoFormat.setByteBuffer("csd-0", ByteBuffer.wrap(sps));
//                    mVideoFormat.setByteBuffer("csd-1", ByteBuffer.wrap(pps));
//                }
//                mVideoFormat.setInteger(MediaFormat.KEY_FRAME_RATE,5);
//                mVideoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL,1);
                String mime = mVideoFormat.getString(MediaFormat.KEY_MIME);
                mVideoCodec = MediaCodec.createDecoderByType(mime);
                mSurface = new Surface(surfaceTexture);
                mVideoCodec.configure(mVideoFormat, mSurface, null, 0);
                mVideoCodec.start();
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }
        return true;
    }

    private boolean isSetSpecificData;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void feedData(byte[] buf, int offset, int length) {
        int naluType = 0;
        if (length < 4) {
            return;
        }
        //判断帧的类型
//        Log.d(TAG, "feedData: type:"+(buf[4] & 0x1f));
        switch (buf[4] & 0x1f) {
            case NALU_TYPE_IDR:
                naluType = MediaCodec.BUFFER_FLAG_KEY_FRAME;
                break;
            case NALU_TYPE_SPS:
                isSetSpecificData = true;
                naluType = MediaCodec.BUFFER_FLAG_CODEC_CONFIG;
                header_sps = buf;
                break;
            case NALU_TYPE_PPS:
                naluType = MediaCodec.BUFFER_FLAG_CODEC_CONFIG;
                header_pps = buf;
                break;
            default:
                naluType = 0;
                break;
        }
        if (!isSetSpecificData) {
            Log.d(TAG, "feedData: return");
            return;
        }
        try {
            mVideoInputBuffers = mVideoCodec.getInputBuffers();
            // 这里解释一下  传0是不等待 传-1是一直等待 但是传-1会在很多机器上挂掉，所以还是用0吧 丢帧总比挂掉强
            int inputBufferIndex = mVideoCodec.dequeueInputBuffer(timeUs);
            if (inputBufferIndex >= 0) {
                // 从输入队列里去空闲buffer
                ByteBuffer inputBuffer = mVideoCodec.getInputBuffer(inputBufferIndex);
                inputBuffer.clear();
                inputBuffer.put(buf, offset, length);
                mVideoCodec.queueInputBuffer(inputBufferIndex, 0, length, presentationTimeUs * 1000000 / 25, naluType);
                presentationTimeUs ++;
            }

            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
            //出列输出缓冲区，阻塞最多timeoutUs微妙
            int outputBufferIndex = mVideoCodec.dequeueOutputBuffer(bufferInfo, timeUs);
//            if (outputBufferIndex >= 0) {
//                // 将解码后数据渲染到surface上
//                boolean doRender = (bufferInfo.size != 0);
//                // doRender为true的时候意味着将数据显示在之前设置好的surfaceview上面
//                mVideoCodec.releaseOutputBuffer(outputBufferIndex, doRender);
//            } else if (outputBufferIndex == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
//                Log.d(TAG, "onDecodeVideoFrame: ----->outputBufferIndex:" + outputBufferIndex);
//            }
            while (outputBufferIndex>0){
                mVideoCodec.releaseOutputBuffer(outputBufferIndex, true);
                outputBufferIndex = mVideoCodec.dequeueOutputBuffer(bufferInfo, 0);
            }
//            switch (outputBufferIndex) {
//                case MediaCodec.INFO_OUTPUT_BUFFERS_CHANGED:
//                    return ;
//                case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
//                    return ;
//                case MediaCodec.INFO_TRY_AGAIN_LATER:
//                    return ;
//                case MediaCodec.BUFFER_FLAG_KEY_FRAME:
//                    return ;
//                default:
//                    //show image right now
//                    mVideoCodec.releaseOutputBuffer(outputBufferIndex, true);
//                    return ;
//            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void stop() {
        if (mVideoCodec != null) {
            try {
                mVideoCodec.stop();
                mVideoCodec.release();
                mVideoCodec = null;
            } catch (IllegalStateException e) {
                e.printStackTrace();
                mVideoCodec = null;
            }
        }

    }
}
