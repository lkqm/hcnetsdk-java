package com.github.lkqm.hcnet;

import com.github.lkqm.hcnet.HCNetSDK.FExceptionCallBack;
import com.github.lkqm.hcnet.HCNetSDK.FMSGCallBack;
import com.github.lkqm.hcnet.model.DeviceUpgradeResponse;
import com.github.lkqm.hcnet.model.Token;
import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import java.util.Date;
import java.util.function.Function;
import lombok.Getter;

/**
 * 设备.
 * <p>
 * 线程安全的.
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class HikDevice implements DeviceOptions {

    @Getter
    private final String ip;
    @Getter
    private final int port;

    private final String user;
    private final String password;
    private final HikDeviceTemplate deviceTemplate;

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
    public HikResult init() {
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
    public HikResult doAction(Function<Token, HikResult> action) {
        HikResult init = init();
        if (!init.isSuccess()) {
            return init;
        }
        return action.apply(token);
    }

    @Override
    public HikResult<Long> setupDeploy(FMSGCallBack messageCallback, FExceptionCallBack exceptionCallback) {
        HikResult init = init();
        if (!init.isSuccess()) {
            return init;
        }

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
    public HikResult<String> passThrough(String url, String data) {
        HikResult init = init();
        if (!init.isSuccess()) {
            return init;
        }
        return deviceTemplate.passThrough(token.getUserId(), url, data);
    }

    @Override
    public <T extends Structure> HikResult<T> getDvrConfig(long channel, int command, Class<T> clazz) {
        HikResult init = init();
        if (!init.isSuccess()) {
            return init;
        }
        return deviceTemplate.getDvrConfig(token.getUserId(), channel, command, clazz);
    }

    @Override
    public HikResult setDvrConfig(long channel, int type, Structure settings) {
        HikResult init = init();
        if (!init.isSuccess()) {
            return init;
        }
        return deviceTemplate.setDvrConfig(token.getUserId(), channel, type, settings);
    }

    @Override
    public HikResult modifyPassword(String targetUser, String newPassword) {
        HikResult init = init();
        if (!init.isSuccess()) {
            return init;
        }
        return deviceTemplate.modifyPassword(token.getUserId(), targetUser, newPassword);
    }

    @Override
    public HikResult adjustTime(Date time) {
        HikResult init = init();
        if (!init.isSuccess()) {
            return init;
        }
        return deviceTemplate.adjustTime(token.getUserId(), time);
    }

    @Override
    public HikResult reboot() {
        HikResult init = init();
        if (!init.isSuccess()) {
            return init;
        }
        return deviceTemplate.reboot(token.getUserId());
    }

    @Override
    public HikResult<DeviceUpgradeResponse> upgradeSync(String sdkFile) {
        HikResult init = init();
        if (!init.isSuccess()) {
            return init;
        }
        return deviceTemplate.upgradeSync(token.getUserId(), sdkFile);
    }

    @Override
    public HikResult<DeviceUpgradeResponse> upgradeASync(String sdkFile) {
        HikResult init = init();
        if (!init.isSuccess()) {
            return init;
        }
        return deviceTemplate.upgradeAsync(token.getUserId(), sdkFile);
    }
}
