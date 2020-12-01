package com.github.lkqm.hcnet;

import com.github.lkqm.hcnet.HCNetSDK.FExceptionCallBack;
import com.github.lkqm.hcnet.model.DeviceUpgradeResponse;
import com.github.lkqm.hcnet.model.Token;
import com.github.lkqm.hcnet.util.Function;
import com.sun.jna.Structure;
import java.util.Date;

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
    HikResult doAction(Function<Token, HikResult> action);

    /**
     * 布防.
     */
    HikResult<Long> setupDeploy(HCNetSDK.FMSGCallBack messageCallback, FExceptionCallBack exceptionCallback);

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

    /**
     * 升级设备（同步），注意升级完成后需要手动重启
     */
    HikResult<DeviceUpgradeResponse> upgradeSync(String sdkFile);

    /**
     * 升级设备（异步），注意升级完成后需要手动重启
     */
    HikResult<DeviceUpgradeResponse> upgradeASync(String sdkFile);
}
