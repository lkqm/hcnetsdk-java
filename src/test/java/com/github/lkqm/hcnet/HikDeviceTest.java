package com.github.lkqm.hcnet;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.lkqm.hcnet.handler.AbstractFaceSnapHandler;
import com.github.lkqm.hcnet.handler.DispatchMessageCallback;
import com.github.lkqm.hcnet.model.FaceSnapEvent;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@SuppressWarnings("rawtypes")
class HikDeviceTest {

    private static HikDevice hikDevice;
    private static final String password = "hik123456";

    @BeforeAll
    static void setUp() {
        JnaPathUtils.initJnaLibraryPath();
        HCNetSDK hcnetsdk = HCNetSDK.INSTANCE;
        hikDevice = new HikDevice(hcnetsdk, "192.168.0.123", HikDeviceTemplate.DEFAULT_PORT, "admin", password);

        HikResult initResult = hikDevice.init();
        assertTrue(initResult.isSuccess(), "设备初始化失败: " + initResult.getError());
    }

    @AfterAll
    static void after() {
        hikDevice.destroy();
    }


    @SneakyThrows
    @Test
    void testRegisterMessageCallback() {
        CountDownLatch sign = new CountDownLatch(1);
        DispatchMessageCallback.INSTANCE.addHandler(new AbstractFaceSnapHandler() {
            @Override
            public void handle(FaceSnapEvent event) {
                System.out.println(event);
                sign.countDown();
            }
        });
        HikResult<Long> result = hikDevice.registerMessageCallback(new DispatchMessageCallback());
        assertTrue(result.isSuccess(), "设置回调函数失败: " + result.getError());
        sign.await();
    }

    @Test
    void testModifyPassword() {
        HikResult actionResult = hikDevice.modifyPassword("admin", "hik123456");
        assertTrue(actionResult.isSuccess(), "密码修改失败: " + actionResult.getError());

        // 还原
        actionResult = hikDevice.modifyPassword("admin", password);
        assertTrue(actionResult.isSuccess(), "密码修改失败: " + actionResult.getError());
    }

    @Test
    void testAdjustTime() {
        HikResult adjustTimeResult = hikDevice.adjustTime(new Date());
        assertTrue(adjustTimeResult.isSuccess(), "校时失败: " + adjustTimeResult.getErrorMsg());
    }

    @Test
    void testReboot() {
        HikResult actionResult = hikDevice.reboot();
        assertTrue(actionResult.isSuccess(), "重启失败: " + actionResult.getErrorMsg());
    }
}
