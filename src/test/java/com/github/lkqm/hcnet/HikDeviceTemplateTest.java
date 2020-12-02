package com.github.lkqm.hcnet;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_TIME;
import com.github.lkqm.hcnet.callback.PrintDeviceExceptionCallback;
import com.github.lkqm.hcnet.handler.AbstractFaceSnapHandler;
import com.github.lkqm.hcnet.handler.DispatchMessageCallback;
import com.github.lkqm.hcnet.model.DeviceUpgradeResponse;
import com.github.lkqm.hcnet.model.FaceSnapEvent;
import com.github.lkqm.hcnet.model.Token;
import com.sun.jna.NativeLong;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("rawtypes")
public class HikDeviceTemplateTest {

    private final String ip = "192.168.0.123";
    private final int port = HikDeviceTemplate.DEFAULT_PORT;
    private final String user = "admin";
    private final String password = "hik123456";
    private Token token;

    static HikDeviceTemplate deviceTemplate;

    @BeforeAll
    static void beforeAll() {
        JnaPathUtils.initJnaLibraryPathDev(HikDeviceTemplateTest.class);
        deviceTemplate = new HikDeviceTemplate(HCNetSDK.INSTANCE);
    }

    @BeforeEach
    void beforeEach() {
        HikResult<Token> actionResult = deviceTemplate.login(ip, port, user, password);
        assertTrue(actionResult.isSuccess(), "登录失败: " + actionResult.getError());
        this.token = actionResult.getData();
    }

    @AfterEach
    void destroy() {
        if (this.token != null) {
            deviceTemplate.logout(this.token.getUserId());
        }
    }

    @Test
    void test() {
        HCNetSDK hcnetsdk = deviceTemplate.getHcnetsdk();
        boolean result = hcnetsdk.NET_DVR_PTZControl_Other(new NativeLong(token.getUserId()), new NativeLong(1), 2, 0);
        if (!result) {
            HikResult error = deviceTemplate.lastError();
            System.out.println(error);
        }
    }

    @Test
    void login() {
        HikResult<Token> tokenResult = deviceTemplate.login(ip, port, user, password);
        assertNotNull(tokenResult, "登录响应数据不能为空");
        if (tokenResult.isSuccess()) {
            Token token = tokenResult.getData();
            assertNotEquals(token.getUserId().longValue(), -1, "登录成功userId!=-1");
            assertTrue(token.getDeviceSerialNumber() != null && token.getDeviceSerialNumber().length() > 0,
                    "登录返回设备序列号不能为空");
        } else {
            assertNotNull(tokenResult.getErrorCode(), "错误码不能为空");
            assertNotNull(tokenResult.getErrorMsg(), "错误消息不能为空");
        }
        assertTrue(tokenResult.isSuccess(), "登录应该成功: " + tokenResult.getErrorMsg());
    }

    @Test
    void modifyPassword() {
        HikResult actionResult = deviceTemplate.modifyPassword(token.getUserId(), user, "hik123456");
        assertTrue(actionResult.isSuccess(), "密码修改失败: " + actionResult.getError());
        // 还原
        actionResult = deviceTemplate.modifyPassword(token.getUserId(), user, password);
        assertTrue(actionResult.isSuccess(), "密码修改失败: " + actionResult.getError());
    }

    @SneakyThrows
    @Test
    void setupDeploy() {
        DispatchMessageCallback.INSTANCE.addHandler(new AbstractFaceSnapHandler() {
            @Override
            public void handle(FaceSnapEvent event) {
                System.out.println(event.getDeviceInfo() + ": " + event.getFaceSnapInfo().getFaceScore());
            }
        });

        CountDownLatch sign = new CountDownLatch(1);
        HikResult<Long> callbackResult = deviceTemplate.setupDeploy(token.getUserId(), DispatchMessageCallback.INSTANCE,
                PrintDeviceExceptionCallback.INSTANCE);
        assertTrue(callbackResult.isSuccess(), "布防失败: " + callbackResult.getError());

        callbackResult = deviceTemplate.setupDeploy(token.getUserId(), DispatchMessageCallback.INSTANCE,
                PrintDeviceExceptionCallback.INSTANCE);
        assertTrue(callbackResult.isSuccess(), "布防失败: " + callbackResult.getError());

        sign.await(120, TimeUnit.SECONDS);
    }

    @Test
    void adjustTime() {
        HikResult adjustTimeResult = deviceTemplate.adjustTime(token.getUserId(), new Date());
        assertTrue(adjustTimeResult.isSuccess(), "校时失败: " + adjustTimeResult.getErrorMsg());
    }

    @Test
    void reboot() {
        HikResult actionResult = deviceTemplate.reboot(token.getUserId());
        assertTrue(actionResult.isSuccess(), "重启失败: " + actionResult.getErrorMsg());
    }

    @Test
    void getDvrConfig() {
        HikResult<NET_DVR_TIME> result = deviceTemplate
                .getDvrConfig(token.getUserId(), 0, HCNetSDK.NET_DVR_GET_TIMECFG, NET_DVR_TIME.class);
        assertTrue(result.isSuccess(), "获取配置: " + result.getErrorMsg());
        result.getData().clear();
    }

    @Test
    void upgradeSync() {
        HikResult<DeviceUpgradeResponse> result = deviceTemplate.upgradeSync(token.getUserId(),
                "C:\\appfile\\downlods\\digicap.dav");
        assertTrue(result.isSuccess(), "请求升级: " + result.getErrorMsg());
        DeviceUpgradeResponse upgradeResponse = result.getData();
        assertEquals(1, upgradeResponse.getState(), "升级结果: " + upgradeResponse.getState());
    }

    @Test
    void ptzControl() {
        HikResult result = deviceTemplate.ptzControlStart(token.getUserId(), 21, 7);
        assertTrue(result.isSuccess(), "云台控制开始失败: " + result.getError());

        result = deviceTemplate.ptzControlStop(token.getUserId(), 21, 7);
        assertTrue(result.isSuccess(), "云台控制停止失败: " + result.getError());
    }

    @Test
    void ptzPreset() {
        int presetIndex = 1;
        HikResult result = deviceTemplate.ptzPresetSet(token.getUserId(), presetIndex);
        assertTrue(result.isSuccess(), "云台点位设置失败: " + result.getError());

        result = deviceTemplate.ptzPresetGoto(token.getUserId(), presetIndex);
        assertTrue(result.isSuccess(), "云台点位跳转失败: " + result.getError());

        result = deviceTemplate.ptzPresetClean(token.getUserId(), presetIndex);
        assertTrue(result.isSuccess(), "云台点位跳转失败: " + result.getError());
    }

    @Test
    void ptzCruise() {
        int route = 1;
        HikResult result = deviceTemplate.ptzCruiseFillPreset(token.getUserId(), route, 1, 18);
        assertTrue(result.isSuccess(), "云台巡航添加点位失败: " + result.getError());

        result = deviceTemplate.ptzCruiseRun(token.getUserId(), route);
        assertTrue(result.isSuccess(), "云台巡航运行失败: " + result.getError());

        result = deviceTemplate.ptzCruiseStop(token.getUserId(), route);
        assertTrue(result.isSuccess(), "云台巡航停止失败: " + result.getError());
    }

    @Test
    void ptzTrack() {
        HikResult result = deviceTemplate.ptzTrackStartRecord(token.getUserId());
        assertTrue(result.isSuccess(), "云台轨迹开始记录失败: " + result.getError());

        result = deviceTemplate.ptzTrackStopRecord(token.getUserId());
        assertTrue(result.isSuccess(), "云台轨迹停止记录失败: " + result.getError());

        result = deviceTemplate.ptzTrackRun(token.getUserId());
        assertTrue(result.isSuccess(), "云台轨迹运行失败: " + result.getError());

    }

    @Test
    void pztZoom() {
        HikResult result = deviceTemplate.ptzZoom(token.getUserId(), 0, 0, 1000, 1000);
        assertTrue(result.isSuccess(), "云台图像区域缩放失败: " + result.getError());
    }

}