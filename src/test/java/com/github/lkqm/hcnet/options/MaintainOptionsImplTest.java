package com.github.lkqm.hcnet.options;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.lkqm.hcnet.HCNetSDK;
import com.github.lkqm.hcnet.HikDeviceTemplate;
import com.github.lkqm.hcnet.HikResult;
import com.github.lkqm.hcnet.JnaPathUtils;
import com.github.lkqm.hcnet.Token;
import com.github.lkqm.hcnet.model.UpgradeResponse;
import com.github.lkqm.hcnet.test.DeviceConstants;
import com.github.lkqm.hcnet.test.DeviceConstants.DeviceInfo;
import java.util.Date;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class MaintainOptionsImplTest {

    static HikDeviceTemplate deviceTemplate;
    static Token dvrToken;
    static MaintainOptions dvrMaintainOptions;

    @BeforeAll
    static void beforeAll() {
        JnaPathUtils.initJnaLibraryPathDev();
        deviceTemplate = new HikDeviceTemplate(HCNetSDK.INSTANCE);

        DeviceInfo dvr = DeviceConstants.dvrDevice;
        HikResult<Token> loginResult = deviceTemplate.login(dvr.ip, dvr.port, dvr.user, dvr.password);
        Assertions.assertTrue(loginResult.isSuccess(), "登录失败: " + loginResult.getError());

        dvrToken = loginResult.getData();
        dvrMaintainOptions = new MaintainOptionsImpl(deviceTemplate, dvrToken.getUserId());
    }

    @AfterAll
    static void afterAll() {
        if (dvrToken != null && dvrToken.getUserId() != null) {
            deviceTemplate.logout(dvrToken.getUserId());
        }
    }

    @Test
    void isOnline() {
        boolean isOnline = dvrMaintainOptions.isOnline();
        assertTrue(isOnline, "设备离线");
    }

    @Test
    void reboot() {
        HikResult<?> result = dvrMaintainOptions.reboot();
        assertTrue(result.isSuccess(), result.getError());
    }

    @Test
    void getDeviceTime() {
        HikResult<Date> result = dvrMaintainOptions.getDeviceTime();
        assertTrue(result.isSuccess(), result.getError());
        assertNotNull(result.getData());
    }

    @Test
    void setDeviceTime() {
        HikResult<?> result = dvrMaintainOptions.setDeviceTime(new Date());
        assertTrue(result.isSuccess(), result.getError());

        HikResult<Date> deviceTimeResult = dvrMaintainOptions.getDeviceTime();
        assertTrue(deviceTimeResult.isSuccess(), deviceTimeResult.getError());
        assertNotNull(deviceTimeResult.getData());

        Date deviceTime = deviceTimeResult.getData();
        assertTrue(Math.abs(System.currentTimeMillis() - deviceTime.getTime()) < 5000, "设置时间成功后获取时差不超过5秒");
    }

    @Test
    void upgradeSync() {
        HikResult<UpgradeResponse> result = dvrMaintainOptions.upgradeSyncForDVR("C:\\appfile\\downloads\\zpj.dav");
        assertTrue(result.isSuccess(), "请求升级: " + result.getErrorMsg());
        UpgradeResponse upgradeResponse = result.getData();
        assertEquals(1, upgradeResponse.getState(), "升级结果: " + upgradeResponse.getState());
    }

    @Test
    void getConfig() {
        HikResult<String> result = dvrMaintainOptions.getConfig();
        if (result.isSuccess()) {
            System.out.println(result.getData());
        }
    }
}