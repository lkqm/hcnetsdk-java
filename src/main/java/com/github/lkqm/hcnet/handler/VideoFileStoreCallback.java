package com.github.lkqm.hcnet.handler;

import com.github.lkqm.hcnet.HCNetSDK;
import com.github.lkqm.hcnet.HCNetSDK.FRealDataCallBack_V30;
import com.github.lkqm.hcnet.util.InnerUtils;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import java.io.File;
import java.util.Date;

/**
 * 视频存储消息回调
 */
public class VideoFileStoreCallback implements FRealDataCallBack_V30 {

    /**
     * 基本目录.
     */
    private final String baseDir;

    /**
     * 音频头数据.
     */
    private byte[] header;

    public VideoFileStoreCallback(String baseDir) {
        this.baseDir = baseDir;
    }


    @Override
    public void invoke(NativeLong lRealHandle, int dwDataType, ByteByReference pBuffer, int dwBufSize, Pointer pUser) {
        byte[] bytes = pBuffer.getPointer().getByteArray(0, dwBufSize);
        if (dwDataType == HCNetSDK.NET_DVR_SYSHEAD) {
            // 头数据
            header = bytes;
        } else if (dwDataType == HCNetSDK.NET_DVR_STREAMDATA && dwBufSize > 0) {
            // 视频流
            String videoPath = getVideoFilePath();
            File videoFile = new File(videoPath);
            if (!videoFile.exists()) {
                InnerUtils.writeFile(header, videoPath);
            }
            InnerUtils.writeFile(bytes, videoPath);
        }
    }

    /**
     * 获取视频路径.
     */
    protected String getVideoFilePath() {
        String filename = InnerUtils.formatDate(new Date(), "yyyyMMdd") + ".mp4";
        return baseDir + File.separator + filename;
    }
}
