package com.github.lkqm.hcnet.options;

import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_SDKABL;
import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_SDKSTATE;
import com.github.lkqm.hcnet.HikDeviceTemplate;
import com.github.lkqm.hcnet.HikResult;

/**
 * sdk本地功能.
 */
public class SdkOptionsImpl extends BaseOptions implements SdkOptions {

    public SdkOptionsImpl(HikDeviceTemplate deviceTemplate) {
        super(deviceTemplate);
    }


    @Override
    public String getVersion() {
        int buildVersion = getHcnetsdk().NET_DVR_GetSDKBuildVersion();
        return (buildVersion >> 24) + "." + (buildVersion << 8 >> 24) + "." + (buildVersion << 16 >> 16);
    }

    @Override
    public HikResult<NET_DVR_SDKSTATE> getState() {
        NET_DVR_SDKSTATE sdkState = new NET_DVR_SDKSTATE();
        boolean result = getHcnetsdk().NET_DVR_GetSDKState(sdkState);
        if (!result) {
            return lastError();
        }
        sdkState.read();
        return HikResult.ok(sdkState);
    }

    @Override
    public HikResult<NET_DVR_SDKABL> getAbility() {
        NET_DVR_SDKABL ability = new NET_DVR_SDKABL();
        boolean result = getHcnetsdk().NET_DVR_GetSDKAbility(ability);
        if (!result) {
            return lastError();
        }
        return HikResult.ok(ability);
    }

    @Override
    public HikResult<?> setLogFile(int logLevel, String logDir, boolean autoDel) {
        boolean result = getHcnetsdk().NET_DVR_SetLogToFile(logLevel, logDir, autoDel);
        if (!result) {
            return lastError();
        }
        return HikResult.ok();
    }

    @Override
    public HikResult<?> setTimeout(int connectTimeoutMs, int recvTimeoutMs, int reconnectIntervalMs) {
        boolean result = getHcnetsdk().NET_DVR_SetConnectTime(connectTimeoutMs, 3);
        if (!result) {
            return lastError();
        }

        result = getHcnetsdk().NET_DVR_SetRecvTimeOut(recvTimeoutMs);
        if (!result) {
            return lastError();
        }

        result = getHcnetsdk().NET_DVR_SetReconnect(reconnectIntervalMs, reconnectIntervalMs > 0);
        if (!result) {
            return lastError();
        }
        return HikResult.ok();
    }
}
