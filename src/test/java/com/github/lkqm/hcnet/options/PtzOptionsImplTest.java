package com.github.lkqm.hcnet.options;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.lkqm.hcnet.HCNetSDK;
import com.github.lkqm.hcnet.HikDeviceTemplate;
import com.github.lkqm.hcnet.HikResult;
import com.github.lkqm.hcnet.JnaPathUtils;
import com.github.lkqm.hcnet.Token;
import com.github.lkqm.hcnet.test.DeviceConstants;
import com.github.lkqm.hcnet.test.DeviceConstants.DeviceInfo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class PtzOptionsImplTest {

    static HikDeviceTemplate deviceTemplate;
    static Token dvrToken;
    static PtzOptions ptzOptions;

    @BeforeAll
    static void beforeAll() {
        JnaPathUtils.initJnaLibraryPathDev();
        deviceTemplate = new HikDeviceTemplate(HCNetSDK.INSTANCE);

        DeviceInfo dvr = DeviceConstants.dvrDevice;
        HikResult<Token> loginResult = deviceTemplate.login(dvr.ip, dvr.port, dvr.user, dvr.password);
        Assertions.assertTrue(loginResult.isSuccess(), "登录失败: " + loginResult.getError());

        dvrToken = loginResult.getData();
        ptzOptions = new PtzOptionsImpl(deviceTemplate, dvrToken.getUserId());
    }

    @AfterAll
    static void afterAll() {
        if (dvrToken != null && dvrToken.getUserId() != null) {
            deviceTemplate.logout(dvrToken.getUserId());
        }
    }

    @Test
    void ptzControl() {
        HikResult result = ptzOptions.controlStart(21, 7);
        assertTrue(result.isSuccess(), "云台控制开始失败: " + result.getError());

        result = ptzOptions.controlStop(21, 7);
        assertTrue(result.isSuccess(), "云台控制停止失败: " + result.getError());
    }

    @Test
    void ptzPreset() {
        int presetIndex = 1;
        HikResult result = ptzOptions.presetSet(presetIndex);
        assertTrue(result.isSuccess(), "云台点位设置失败: " + result.getError());

        result = ptzOptions.presetGoto(presetIndex);
        assertTrue(result.isSuccess(), "云台点位跳转失败: " + result.getError());

        result = ptzOptions.presetClean(presetIndex);
        assertTrue(result.isSuccess(), "云台点位跳转失败: " + result.getError());
    }

    @Test
    void ptzCruise() {
        int route = 1;
        HikResult result = ptzOptions.cruiseFillPreset(route, 1, 18);
        assertTrue(result.isSuccess(), "云台巡航添加点位失败: " + result.getError());

        result = ptzOptions.cruiseRun(route);
        assertTrue(result.isSuccess(), "云台巡航运行失败: " + result.getError());

        result = ptzOptions.cruiseStop(route);
        assertTrue(result.isSuccess(), "云台巡航停止失败: " + result.getError());
    }

    @Test
    void ptzTrack() {
        HikResult result = ptzOptions.trackStartRecord();
        assertTrue(result.isSuccess(), "云台轨迹开始记录失败: " + result.getError());

        result = ptzOptions.trackStopRecord();
        assertTrue(result.isSuccess(), "云台轨迹停止记录失败: " + result.getError());

        result = ptzOptions.trackRun();
        assertTrue(result.isSuccess(), "云台轨迹运行失败: " + result.getError());

    }

    @Test
    void pztZoom() {
        HikResult result = ptzOptions.zoom(0, 0, 1000, 1000);
        assertTrue(result.isSuccess(), "云台图像区域缩放失败: " + result.getError());
    }

}