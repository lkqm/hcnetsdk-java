package com.github.lkqm.hcnet;

import com.github.lkqm.hcnet.HCNetSDK.FExceptionCallBack;
import com.github.lkqm.hcnet.HCNetSDK.FMSGCallBack;
import com.github.lkqm.hcnet.model.PassThroughResponse;
import com.github.lkqm.hcnet.options.MaintainOptions;
import com.github.lkqm.hcnet.options.PtzOptions;
import com.github.lkqm.hcnet.util.BiFunction;
import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import lombok.Getter;

/**
 * 设备.
 * <p>
 * 线程安全的.
 */
public class HikDevice implements DeviceOptions {

    @Getter
    private final String ip;
    @Getter
    private final int port;

    private final String user;
    private final String password;
    @Getter
    private final HikDeviceTemplate deviceTemplate;

    @Getter
    private volatile Token token;
    private volatile Long setupAlarmHandle;

    public HikDevice(HCNetSDK hcnetsdk, String ip, int port, String user, String password) {
        this.deviceTemplate = new HikDeviceTemplate(hcnetsdk);
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    @Override
    public HikResult<?> init() {
        if (token == null) {
            synchronized (this) {
                if (token == null) {
                    HikResult<Token> loginResult = deviceTemplate.login(ip, port, user, password);
                    if (loginResult.isSuccess()) {
                        token = loginResult.getData();
                    }
                    return loginResult;
                }
            }
        }
        return HikResult.ok();
    }

    @Override
    public synchronized void destroy() {
        // 消息回调取消布防
        if (setupAlarmHandle != null) {
            deviceTemplate.getHcnetsdk().NET_DVR_CloseAlarmChan_V30(new NativeLong(setupAlarmHandle));
            setupAlarmHandle = null;
        }

        // 登录注销
        if (token != null && token.getUserId() != null) {
            deviceTemplate.logout(token.getUserId());
        }
    }

    @Override
    public HikResult<?> doAction(BiFunction<HCNetSDK, Token, HikResult<?>> action) {
        checkInit();
        return action.apply(deviceTemplate.getHcnetsdk(), token);
    }

    @Override
    public HikResult<Long> setupDeploy(FMSGCallBack messageCallback, FExceptionCallBack exceptionCallback) {
        checkInit();
        if (setupAlarmHandle != null) {
            throw new RuntimeException("重复布防.");
        }
        HikResult<Long> deployResult = deviceTemplate
                .setupDeploy(token.getUserId(), messageCallback, exceptionCallback);
        if (deployResult.isSuccess() && deployResult.getData() != null) {
            setupAlarmHandle = deployResult.getData();
        }
        return deployResult;
    }

    @Override
    public HikResult<PassThroughResponse> passThrough(String url, String data) {
        checkInit();
        return deviceTemplate.passThrough(token.getUserId(), url, data);
    }

    @Override
    public HikResult<PassThroughResponse> passThrough(String url, String data, int exceptOutByteSize) {
        checkInit();
        return deviceTemplate.passThrough(token.getUserId(), url, data.getBytes(), exceptOutByteSize);
    }

    @Override
    public <T extends Structure> HikResult<T> getDvrConfig(long channel, int command, Class<T> clazz) {
        checkInit();
        return deviceTemplate.getDvrConfig(token.getUserId(), channel, command, clazz);
    }

    @Override
    public HikResult<?> setDvrConfig(long channel, int type, Structure settings) {
        checkInit();
        return deviceTemplate.setDvrConfig(token.getUserId(), channel, type, settings);
    }

    @Override
    public HikResult<?> modifyPassword(String targetUser, String newPassword) {
        checkInit();
        return deviceTemplate.modifyPassword(token.getUserId(), targetUser, newPassword);
    }

    @Override
    public MaintainOptions opsForMaintain() {
        checkInit();
        return deviceTemplate.opsForMaintain(token.getUserId());
    }

    @Override
    public PtzOptions opsForPtz() {
        checkInit();
        return deviceTemplate.opsForPtz(token.getUserId());
    }

    private void checkInit() {
        HikResult<?> result = init();
        if (!result.isSuccess()) {
            throw new RuntimeException(result.getError());
        }
    }

}
