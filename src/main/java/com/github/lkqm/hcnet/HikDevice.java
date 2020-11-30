package com.github.lkqm.hcnet;

import com.github.lkqm.hcnet.HCNetSDK.FMSGCallBack;
import com.github.lkqm.hcnet.model.DeviceUpgradeResponse;
import com.github.lkqm.hcnet.model.Token;
import com.sun.jna.NativeLong;
import com.sun.jna.Structure;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.BiFunction;

/**
 * 设备.
 */
@SuppressWarnings("rawtypes")
public class HikDevice implements DeviceOptions {

    private final String ip;
    private final int port;
    private final String user;
    private final String password;
    private final HikDeviceTemplate deviceTemplate;

    private volatile Token token;
    private final List<Long> messageCallbackHandles = new CopyOnWriteArrayList<>();

    public HikDevice(HCNetSDK hcnetsdk, String ip, int port, String user, String password) {
        this.deviceTemplate = new HikDeviceTemplate(hcnetsdk);
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    @Override
    public synchronized HikResult init() {
        HikResult<Token> loginResult = deviceTemplate.login(ip, port, user, password);
        if (loginResult.isSuccess()) {
            if (token != null) {
                deviceTemplate.logout(token.getUserId());
            }
            this.token = loginResult.getData();
        }
        return loginResult;
    }

    @Override
    public synchronized void destroy() {
        // 消息回调取消布防
        for (Long callbackHandle : messageCallbackHandles) {
            if (callbackHandle != null) {
                deviceTemplate.getHcnetsdk().NET_DVR_CloseAlarmChan_V30(new NativeLong(callbackHandle));
            }
        }
        messageCallbackHandles.clear();

        // 登录注销
        if (token != null && token.getUserId() != null) {
            deviceTemplate.logout(token.getUserId());
        }
    }

    @Override
    public HikResult doAction(BiFunction<HCNetSDK, Token, HikResult> action) {
        return action.apply(deviceTemplate.getHcnetsdk(), token);
    }

    @Override
    public HikResult<Long> registerMessageCallback(FMSGCallBack callback) {
        HikResult<Long> result = deviceTemplate.registerMessageCallback(token.getUserId(), callback);
        if (result.isSuccess() && result.getData() != null) {
            messageCallbackHandles.add(result.getData());
        }
        return result;
    }

    @Override
    public HikResult<String> passThrough(String url, String data) {
        return deviceTemplate.passThrough(token.getUserId(), url, data);
    }

    @Override
    public <T extends Structure> HikResult<T> getDvrConfig(long channel, int command, Class<T> clazz) {
        return deviceTemplate.getDvrConfig(token.getUserId(), channel, command, clazz);
    }

    @Override
    public HikResult setDvrConfig(long channel, int type, Structure settings) {
        return deviceTemplate.setDvrConfig(token.getUserId(), channel, type, settings);
    }

    @Override
    public HikResult modifyPassword(String targetUser, String newPassword) {
        return deviceTemplate.modifyPassword(token.getUserId(), targetUser, newPassword);
    }

    @Override
    public HikResult adjustTime(Date time) {
        return deviceTemplate.adjustTime(token.getUserId(), time);
    }

    @Override
    public HikResult reboot() {
        return deviceTemplate.reboot(token.getUserId());
    }

    @Override
    public HikResult<DeviceUpgradeResponse> upgradeSync(String sdkFile) {
        return deviceTemplate.upgradeSync(token.getUserId(), sdkFile);
    }

    @Override
    public HikResult<DeviceUpgradeResponse> upgradeASync(String sdkFile) {
        return deviceTemplate.upgradeAsync(token.getUserId(), sdkFile);
    }
}
