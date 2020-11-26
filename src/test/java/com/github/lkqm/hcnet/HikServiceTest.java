package com.github.lkqm.hcnet;


import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.lkqm.hcnet.handler.AbstractFaceSnapHandler;
import com.github.lkqm.hcnet.handler.DispatchMessageCallback;
import com.github.lkqm.hcnet.model.FaceSnapEvent;
import com.github.lkqm.hcnet.model.Token;
import java.util.concurrent.CountDownLatch;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class HikServiceTest {

    static HikService hikService;

    @BeforeAll
    public static void init() {
        JnaPathUtils.initJnaLibraryPath();
        hikService = new HikService(HCNetSDK.INSTANCE);
    }

    @Test
    public void login() {
        HikResult<Token> tokenResult = hikService.login("192.168.0.12", HikService.DEFAULT_PORT, "admin", "123456");
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
        HikResult<Token> tokenResult = hikService.login("192.168.0.12", HikService.DEFAULT_PORT, "admin", "123456");
        assertTrue(tokenResult.isSuccess(), "登录应该成功: " + tokenResult.getErrorMsg());

        Token token = tokenResult.getData();
        HikResult modifyResult = hikService.modifyPassword(token.getUserId(), "admin", "wxb123456");
        assertTrue(modifyResult.isSuccess(), "密码修改应该成功");

        modifyResult = hikService.modifyPassword(token.getUserId(), "admin", "wxb888888");
        assertTrue(modifyResult.isSuccess(), "密码修改应该成功");

    }


    @SneakyThrows
    @Test
    public void registerMessageCallback() {
        HikResult<Token> tokenResult = hikService.login("192.168.0.239", HikService.DEFAULT_PORT, "admin", "123456");
        assertTrue(tokenResult.isSuccess(), "登录应该成功: " + tokenResult.getErrorMsg());
        Token token = tokenResult.getData();

        CountDownLatch sign = new CountDownLatch(1);
        DispatchMessageCallback dispatcher = DispatchMessageCallback.INSTANCE;
        dispatcher.addHandler(new AbstractFaceSnapHandler() {
            @Override
            public void handle(FaceSnapEvent event) {
                System.out.println(event.getDeviceInfo());
                sign.countDown();
            }
        });

        HikResult<Long> callbackResult = hikService.registerMessageCallback(token.getUserId(), dispatcher);
        assertTrue(callbackResult.isSuccess(), "设置回调应该成功: " + callbackResult.getError());
        sign.await();
    }

}