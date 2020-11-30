package com.github.lkqm.hcnet;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_TIME;
import com.github.lkqm.hcnet.handler.AbstractFaceSnapHandler;
import com.github.lkqm.hcnet.handler.DispatchMessageCallback;
import com.github.lkqm.hcnet.model.DeviceUpgradeResponse;
import com.github.lkqm.hcnet.model.FaceSnapEvent;
import com.github.lkqm.hcnet.model.Token;
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
    public static void initStatic() {
        JnaPathUtils.initJnaLibraryPath();
        deviceTemplate = new HikDeviceTemplate(HCNetSDK.INSTANCE);
    }

    @BeforeEach
    public void init() {
        HikResult<Token> actionResult = deviceTemplate.login(ip, port, user, password);
        assertTrue(actionResult.isSuccess(), "登录失败: " + actionResult.getErrorMsg());
        this.token = actionResult.getData();
    }

    @AfterEach
    public void destroy() {
        if (this.token != null) {
            deviceTemplate.logout(this.token.getUserId());
        }
    }

    @Test
    public void login() {
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
    public void modifyPassword() {
        HikResult actionResult = deviceTemplate.modifyPassword(token.getUserId(), user, "123456");
        assertTrue(actionResult.isSuccess(), "密码修改失败: " + actionResult.getError());
        // 还原
        actionResult = deviceTemplate.modifyPassword(token.getUserId(), user, password);
        assertTrue(actionResult.isSuccess(), "密码修改失败: " + actionResult.getError());
    }

    @SneakyThrows
    @Test
    public void registerMessageCallback() {
        CountDownLatch sign = new CountDownLatch(1);
        DispatchMessageCallback dispatcher = DispatchMessageCallback.INSTANCE;
        dispatcher.addHandler(new AbstractFaceSnapHandler() {
            @Override
            public void handle(FaceSnapEvent event) {
                System.out.println(event.getDeviceInfo());
                sign.countDown();
            }
        });

        HikResult<Long> callbackResult = deviceTemplate.registerMessageCallback(token.getUserId(), dispatcher);
        assertTrue(callbackResult.isSuccess(), "布防失败: " + callbackResult.getError());
        sign.await(10, TimeUnit.SECONDS);
    }

    @Test
    public void adjustTime() {
        HikResult adjustTimeResult = deviceTemplate.adjustTime(token.getUserId(), new Date());
        assertTrue(adjustTimeResult.isSuccess(), "校时失败: " + adjustTimeResult.getErrorMsg());
    }

    @Test
    public void reboot() {
        HikResult actionResult = deviceTemplate.reboot(token.getUserId());
        assertTrue(actionResult.isSuccess(), "重启失败: " + actionResult.getErrorMsg());
    }

    @Test
    public void getDvrConfig() {
        HikResult<NET_DVR_TIME> result = deviceTemplate
                .getDvrConfig(token.getUserId(), 0, HCNetSDK.NET_DVR_GET_TIMECFG, NET_DVR_TIME.class);
        assertTrue(result.isSuccess(), "获取配置: " + result.getErrorMsg());
        result.getData().clear();
    }

    @Test
    public void upgradeSync() {
        HikResult<DeviceUpgradeResponse> result = deviceTemplate.upgradeSync(token.getUserId(),
                "C:\\appfile\\downlods\\digicap.dav");
        assertTrue(result.isSuccess(), "请求升级: " + result.getErrorMsg());
        DeviceUpgradeResponse upgradeResponse = result.getData();
        assertEquals(1, upgradeResponse.getState(), "升级结果: " + upgradeResponse.getState());
    }

}