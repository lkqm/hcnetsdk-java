package com.github.lkqm.hcnet;

import com.github.lkqm.hcnet.model.Token;
import com.sun.jna.Structure;
import java.util.Date;
import java.util.function.BiFunction;

/**
 * 设备操作接口.
 */
@SuppressWarnings("rawtypes")
public interface DeviceOptions {

    /**
     * 初始化.
     */
    HikResult init();

    /**
     * 销毁.
     */
    void destroy();

    /**
     * 执行动作.
     */
    HikResult doAction(BiFunction<HCNetSDK, Token, HikResult> action);

    /**
     * 设备回调.
     */
    HikResult<Long> registerMessageCallback(HCNetSDK.FMSGCallBack callback);

    /**
     * 透传.
     */
    HikResult<String> passThrough(String url, String data);

    /**
     * 获取设备配置.
     */
    <T extends Structure> HikResult<T> getDvrConfig(long channel, int command, Class<T> clazz);

    /**
     * 设置设备配置.
     */
    HikResult setDvrConfig(long channel, int command, Structure settings);

    /**
     * 修改指定用户密码.
     */
    HikResult modifyPassword(String targetUser, String newPassword);

    /**
     * 设备校时.
     */
    HikResult adjustTime(Date time);

    /**
     * 重启设备.
     */
    HikResult reboot();

}
