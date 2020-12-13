package com.github.lkqm.hcnet.options;

import com.github.lkqm.hcnet.HCNetSDK;
import com.github.lkqm.hcnet.HikDeviceTemplate;
import com.github.lkqm.hcnet.HikResult;

public abstract class BaseOptions {

    protected final HikDeviceTemplate deviceTemplate;

    public BaseOptions(HikDeviceTemplate deviceTemplate) {
        this.deviceTemplate = deviceTemplate;
    }

    protected HCNetSDK getHcnetsdk() {
        return deviceTemplate.getHcnetsdk();
    }

    protected <T> HikResult<T> lastError() {
        return deviceTemplate.lastError();
    }
}
