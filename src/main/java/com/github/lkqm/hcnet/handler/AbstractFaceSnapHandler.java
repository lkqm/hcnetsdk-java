package com.github.lkqm.hcnet.handler;

import com.github.lkqm.hcnet.HCNetSDK;
import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_ALARMER;
import com.github.lkqm.hcnet.HCNetSDK.NET_VCA_HUMAN_FEATURE;
import com.github.lkqm.hcnet.HCNetSDK.RECV_ALARM;
import com.github.lkqm.hcnet.JnaUtils;
import com.github.lkqm.hcnet.model.FaceSnapEvent;
import com.github.lkqm.hcnet.model.FaceSnapInfo;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

/**
 * 人脸抓拍事件处理.
 */
public abstract class AbstractFaceSnapHandler extends AbstractHandler {

    public abstract void handle(FaceSnapEvent event);

    @Override
    public boolean accept(long command) {
        return command == HCNetSDK.COMM_UPLOAD_FACESNAP_RESULT;
    }

    @Override
    public void invoke(NativeLong lCommand, NET_DVR_ALARMER pAlarmer, RECV_ALARM pAlarmInfo, int dwBufLen,
            Pointer pUser) {
        FaceSnapEvent event = new FaceSnapEvent();
        event.setDeviceInfo(resolveDeviceInfo(pAlarmer));
        event.setFaceSnapInfo(resolveFaceSnapInfo(pAlarmInfo));

        this.handle(event);
    }

    // 解析身份证信息
    private FaceSnapInfo resolveFaceSnapInfo(RECV_ALARM pAlarmInfo) {
        HCNetSDK.NET_VCA_FACESNAP_RESULT strFaceSnapInfo = new HCNetSDK.NET_VCA_FACESNAP_RESULT();
        JnaUtils.writePointer(pAlarmInfo.getPointer(), strFaceSnapInfo);
        NET_VCA_HUMAN_FEATURE faceFeature = strFaceSnapInfo.struFeature;

        byte[] faceBytes = JnaUtils.pointerToBytes(strFaceSnapInfo.pBuffer1, strFaceSnapInfo.dwFacePicLen);
        byte[] backgroundBytes = JnaUtils.pointerToBytes(strFaceSnapInfo.pBuffer2, strFaceSnapInfo.dwBackgroundPicLen);

        FaceSnapInfo faceInfo = new FaceSnapInfo();
        faceInfo.setFaceScore(strFaceSnapInfo.dwFaceScore);
        faceInfo.setFaceImageBytes(faceBytes);
        faceInfo.setBackgroundImageBytes(backgroundBytes);
        faceInfo.setAge(faceFeature.byAge);
        faceInfo.setEyeGlass(faceFeature.byEyeGlass);
        faceInfo.setAgeGroup(faceFeature.byAgeGroup);
        faceInfo.setAgeDeviation(faceFeature.byAgeDeviation);
        return faceInfo;
    }

}
