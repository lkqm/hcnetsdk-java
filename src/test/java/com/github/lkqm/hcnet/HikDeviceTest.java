package com.github.lkqm.hcnet;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.lkqm.hcnet.callback.PrintDeviceExceptionCallback;
import com.github.lkqm.hcnet.callback.PrintFaceSnapHandler;
import com.github.lkqm.hcnet.handler.DispatchMessageCallback;
import com.github.lkqm.hcnet.test.DeviceConstants;
import com.github.lkqm.hcnet.test.DeviceConstants.DeviceInfo;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class HikDeviceTest {

    private static HikDevice device;
    private static DeviceInfo dvrDeviceInfo;

    @BeforeAll
    static void beforeAll() {
        JnaPathUtils.initJnaLibraryPathDev();
        HCNetSDK hcnetsdk = HCNetSDK.INSTANCE;

        dvrDeviceInfo = DeviceConstants.dvrDevice;
        device = new HikDevice(hcnetsdk, dvrDeviceInfo.ip, dvrDeviceInfo.port, dvrDeviceInfo.user,
                dvrDeviceInfo.password);

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
        HikResult actionResult = device.modifyPassword(dvrDeviceInfo.user, "hik123456");
        assertTrue(actionResult.isSuccess(), "密码修改失败: " + actionResult.getError());

        // 还原
        actionResult = device.modifyPassword(dvrDeviceInfo.user, dvrDeviceInfo.password);
        assertTrue(actionResult.isSuccess(), "密码修改失败: " + actionResult.getError());
    }

}
