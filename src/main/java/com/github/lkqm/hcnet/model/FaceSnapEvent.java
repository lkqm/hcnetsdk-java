package com.github.lkqm.hcnet.model;

import lombok.Data;

/**
 * 人脸抓拍事件.
 */
@Data
public class FaceSnapEvent {

    private FaceSnapInfo faceSnapInfo;
    private DeviceInfo deviceInfo;

}
