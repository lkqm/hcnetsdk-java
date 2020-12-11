package com.github.lkqm.hcnet;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_TIME;
import com.github.lkqm.hcnet.callback.PrintDeviceExceptionCallback;
import com.github.lkqm.hcnet.handler.DispatchMessageCallback;
import com.github.lkqm.hcnet.handler.FaceSnapFileStoreHandler;
import com.github.lkqm.hcnet.handler.VideoFileStoreCallback;
import com.github.lkqm.hcnet.model.PassThroughResponse;
import com.github.lkqm.hcnet.model.Token;
import com.github.lkqm.hcnet.model.UpgradeResponse;
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

    private final String ip = "192.168.0.239";
    private final int port = HikDeviceTemplate.DEFAULT_PORT;
    private final String user = "admin";
    private final String password = "hik12345+";
    private Token token;

    static HikDeviceTemplate deviceTemplate;

    @BeforeAll
    static void beforeAll() {
        JnaPathUtils.initJnaLibraryPath();
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
        DispatchMessageCallback.INSTANCE.addHandler(new FaceSnapFileStoreHandler("/appfile/snap"));

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
    void passThrough() {
        String xml =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?><DeviceInfo xmlns=\"http://www.hikvision.com/ver20/XMLSchema\" version=\"2.0\">\n"
                        + "<deviceName>IP CAMERA</deviceName>\n"
                        + "<deviceID>d9b04000-d0fb-11b2-80e4-988b0a0e517f</deviceID>\n"
                        + "<deviceDescription>IPCamera</deviceDescription>\n"
                        + "<deviceLocation>hangzhou</deviceLocation>\n"
                        + "<systemContact>Hikvision.China</systemContact>\n"
                        + "<model>DS-2CD7A47FWD-XZS</model>\n"
                        + "<serialNumber>DS-2CD7A47FWD-XZS20190228AACHC96234444</serialNumber>\n"
                        + "<macAddress>98:8b:0a:0e:51:7f</macAddress>\n"
                        + "<firmwareVersion>V5.6.1</firmwareVersion>\n"
                        + "<firmwareReleasedDate>build 190603</firmwareReleasedDate>\n"
                        + "<encoderVersion>V7.3</encoderVersion>\n"
                        + "<encoderReleasedDate>build 190527</encoderReleasedDate>\n"
                        + "<bootVersion>V1.3.4</bootVersion>\n"
                        + "<bootReleasedDate>100316</bootReleasedDate>\n"
                        + "<hardwareVersion>0x0</hardwareVersion>\n"
                        + "<deviceType>IPCamera</deviceType>\n"
                        + "<telecontrolID>88</telecontrolID>\n"
                        + "<supportBeep>false</supportBeep>\n"
                        + "<supportVideoLoss>false</supportVideoLoss>\n"
                        + "<firmwareVersionInfo>B-R-H3-0</firmwareVersionInfo>\n"
                        + "</DeviceIno>";
        HikResult<PassThroughResponse> result = deviceTemplate
                .passThrough(token.getUserId(), "PUT /ISAPI/System/deviceInfo", xml);
        assertTrue(result.isSuccess(), "透传失败: " + result.getError());
        System.out.println(result.getData());
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

    static VideoFileStoreCallback callback;

    @Test
    @SneakyThrows
    void realPlay() {
        callback = new VideoFileStoreCallback("/appfile/video");
        HikResult<Long> result = deviceTemplate.realPlay(token.getUserId(), callback);
        assertTrue(result.isSuccess(), "视频预览失败: " + result.getError());
        Thread.sleep(100000);
    }

    @Test
    void upgradeSync() {
        HikResult<UpgradeResponse> result = deviceTemplate
                .upgradeAcsSync(token.getUserId(), "C:\\appfile\\downloads\\rzj.dav", 0);
        assertTrue(result.isSuccess(), "请求升级: " + result.getErrorMsg());
        UpgradeResponse upgradeResponse = result.getData();
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