package com.github.lkqm.hcnet.options;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.lkqm.hcnet.HCNetSDK;
import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_SDKABL;
import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_SDKSTATE;
import com.github.lkqm.hcnet.HikDeviceTemplate;
import com.github.lkqm.hcnet.HikResult;
import com.github.lkqm.hcnet.JnaPathUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class SdkOptionsImplTest {

    static SdkOptions sdkOptions;

    @BeforeAll
    static void beforeEach() {
        JnaPathUtils.initJnaLibraryPathDev();
        HikDeviceTemplate deviceTemplate = new HikDeviceTemplate(HCNetSDK.INSTANCE);
        sdkOptions = new SdkOptionsImpl(deviceTemplate);
    }

    @Test
    void getVersion() {
        String version = sdkOptions.getVersion();
        assertNotNull(version);
        String[] versionParts = version.split("\\.");
        assertEquals(3, versionParts.length);
    }

    @Test
    void getState() {
        HikResult<NET_DVR_SDKSTATE> result = sdkOptions.getState();
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        System.out.println(result.getData());
    }

    @Test
    void getAbility() {
        HikResult<NET_DVR_SDKABL> result = sdkOptions.getAbility();
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        System.out.println(result.getData());
    }

    @Test
    void setLogFile() {
        HikResult<?> result = sdkOptions.setLogFile(1, "/tmp/", true);
        assertTrue(result.isSuccess());
    }

    @Test
    void setTimeout() {
        HikResult<?> result = sdkOptions.setTimeout(3000, 5000, 5000);
        assertTrue(result.isSuccess());
    }
}