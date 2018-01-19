package com.chezi008.videosurveillance.constant;

import android.os.Environment;

import java.io.File;

/**
 * @author ：chezi008 on 2017/12/27 20:15
 * @description ：
 * @email ：chezi008@163.com
 */

public class FileConstant {
    public static final String baseFile = Environment.getExternalStorageDirectory().getPath();
    public static final String h264FileName = "mtv.h264";
    public static final String mp4FileName = "mtv.mp4";
    public static final String aacFileName = "test.aac";

    public static final String mp4FilePath = baseFile + File.separator + mp4FileName;
    public static final String h264FilePath = baseFile + File.separator + h264FileName;
    public static final String aacFilePath = baseFile + File.separator + aacFileName;
}
