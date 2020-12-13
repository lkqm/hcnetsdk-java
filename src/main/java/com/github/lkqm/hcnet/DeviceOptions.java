package com.github.lkqm.hcnet;

import com.github.lkqm.hcnet.HCNetSDK.FExceptionCallBack;
import com.github.lkqm.hcnet.model.PassThroughResponse;
import com.github.lkqm.hcnet.options.MaintainOptions;
import com.github.lkqm.hcnet.options.PtzOptions;
import com.github.lkqm.hcnet.util.BiFunction;
import com.sun.jna.Structure;

/**
 * 设备操作接口.
 */
public interface DeviceOptions {

    /**
     * 初始化.
     */
    HikResult<?> init();

    /**
     * 销毁.
     */
    void destroy();

    /**
     * 执行动作.
     */
    HikResult<?> doAction(BiFunction<HCNetSDK, Token, HikResult<?>> action);

    /**
     * 布防.
     */
    HikResult<Long> setupDeploy(HCNetSDK.FMSGCallBack messageCallback, FExceptionCallBack exceptionCallback);

    /**
     * 透传.
     */
    HikResult<PassThroughResponse> passThrough(String url, String data);

    /**
     * 透传.
     */
    HikResult<PassThroughResponse> passThrough(String url, String data, int exceptOutByteSize);

    /**
     * 获取设备配置.
     */
    <T extends Structure> HikResult<T> getDvrConfig(long channel, int command, Class<T> clazz);

    /**
     * 设置设备配置.
     */
    HikResult<?> setDvrConfig(long channel, int command, Structure settings);

    /**
     * 修改指定用户密码.
     */
    HikResult<?> modifyPassword(String targetUser, String newPassword);

    /**
     * 设备维护.
     */
    MaintainOptions opsForMaintain();

    /**
     * 云台操作.
     */
    PtzOptions opsForPtz();
}
