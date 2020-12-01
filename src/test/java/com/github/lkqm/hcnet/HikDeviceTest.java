package com.github.lkqm.hcnet;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.lkqm.hcnet.callback.PrintDeviceExceptionCallback;
import com.github.lkqm.hcnet.callback.PrintFaceSnapHandler;
import com.github.lkqm.hcnet.handler.DispatchMessageCallback;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@SuppressWarnings("rawtypes")
class HikDeviceTest {

    private static HikDevice device;
    private static final String password = "hik12345+";

    @BeforeAll
    static void beforeAll() {
        JnaPathUtils.initJnaLibraryPathDev(HikDeviceTest.class);
        HCNetSDK hcnetsdk = HCNetSDK.INSTANCE;
        device = new HikDevice(hcnetsdk, "192.168.0.123", HikDeviceTemplate.DEFAULT_PORT, "admin", password);

        /*
        HikResult initResult = device.init();
        assertTrue(initResult.isSuccess(), "设备初始化失败: " + initResult.getError());
         */
    }

    @AfterAll
    static void after() {
        device.destroy();
    }

    @Test
    void setupDeploy() throws InterruptedException {
        DispatchMessageCallback.INSTANCE.addHandler(PrintFaceSnapHandler.INSTANCE);
        HikResult<Long> result = device
                .setupDeploy(DispatchMessageCallback.INSTANCE, PrintDeviceExceptionCallback.INSTANCE);
        assertTrue(result.isSuccess(), "布防失败: " + result.getError());
        Thread.sleep(TimeUnit.SECONDS.toMillis(120));
    }

    @Test
    void testModifyPassword() {
        HikResult actionResult = device.modifyPassword("admin", "hik123456");
        assertTrue(actionResult.isSuccess(), "密码修改失败: " + actionResult.getError());

        // 还原
        actionResult = device.modifyPassword("admin", password);
        assertTrue(actionResult.isSuccess(), "密码修改失败: " + actionResult.getError());
    }

    @Test
    void testAdjustTime() {
        HikResult adjustTimeResult = device.adjustTime(new Date());
        assertTrue(adjustTimeResult.isSuccess(), "校时失败: " + adjustTimeResult.getErrorMsg());
    }

    @Test
    void testReboot() {
        HikResult actionResult = device.reboot();
        assertTrue(actionResult.isSuccess(), "重启失败: " + actionResult.getErrorMsg());
    }
}
