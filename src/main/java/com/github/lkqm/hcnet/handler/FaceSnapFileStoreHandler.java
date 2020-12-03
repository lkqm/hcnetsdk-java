package com.github.lkqm.hcnet.handler;

import com.github.lkqm.hcnet.model.DeviceInfo;
import com.github.lkqm.hcnet.model.FaceSnapEvent;
import com.github.lkqm.hcnet.model.FaceSnapInfo;
import com.github.lkqm.hcnet.util.InnerUtils;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 人脸抓拍事件处理.
 */
@AllArgsConstructor
@Getter
public class FaceSnapFileStoreHandler extends AbstractFaceSnapHandler {

    /**
     * 文件存储基本目录.
     */
    protected final String baseDir;

    // <time>_<ip>_<serialNo>_<type>.jpg
    private static final String FILE_NAME_TPL = "%s_%s_%s_%s.jpg";


    @Override
    public void handle(FaceSnapEvent event) {
        DeviceInfo deviceInfo = event.getDeviceInfo();
        FaceSnapInfo snapInfo = event.getFaceSnapInfo();

        byte[] faceBytes = snapInfo.getFaceImageBytes();
        if (faceBytes != null) {
            String facePath = baseDir + File.separator + getFacePictureFileName(event);
            InnerUtils.writeFile(faceBytes, facePath);
        }
        byte[] backgroundBytes = snapInfo.getBackgroundImageBytes();
        if (backgroundBytes != null) {
            String backgroundPath = baseDir + File.separator + getBackgroundPictureFileName(event);
            InnerUtils.writeFile(backgroundBytes, backgroundPath);
        }
    }

    protected String getFacePictureFileName(FaceSnapEvent event) {
        return doPictureName(event, "face");
    }

    private String getBackgroundPictureFileName(FaceSnapEvent event) {
        return doPictureName(event, "bk");
    }

    private String doPictureName(FaceSnapEvent event, String type) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");
        DeviceInfo deviceInfo = event.getDeviceInfo();
        FaceSnapInfo snapInfo = event.getFaceSnapInfo();
        Date snapDate = new Date(snapInfo.getSnapTimestamp());
        String snapTime = sdf.format(snapDate);
        String day = sdf2.format(snapDate);
        return day + File.separator + String
                .format(FILE_NAME_TPL, snapTime, deviceInfo.getDeviceIp(), deviceInfo.getSerialNumber(), type);
    }
}
