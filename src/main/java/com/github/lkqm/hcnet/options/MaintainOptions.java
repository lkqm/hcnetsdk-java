package com.github.lkqm.hcnet.options;

import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_UPGRADE_PARAM;
import com.github.lkqm.hcnet.HikResult;
import com.github.lkqm.hcnet.model.UpgradeAsyncResponse;
import com.github.lkqm.hcnet.model.UpgradeResponse;
import com.sun.jna.Pointer;
import java.util.Date;
import lombok.SneakyThrows;

/**
 * 设备维护.
 */
public interface MaintainOptions {

    /**
     * 是否在线.
     */
    boolean isOnline();

    /**
     * 重启设备.
     */
    HikResult<?> reboot();

    /**
     * 设备校时.
     */
    HikResult<Date> getDeviceTime();

    /**
     * 设备校时.
     */
    HikResult<?> setDeviceTime(Date time);

    /**
     * 升级设备(同步)
     */
    @SneakyThrows
    HikResult<UpgradeResponse> upgradeSync(NET_DVR_UPGRADE_PARAM upgradeParam);

    /**
     * 升级设备(异步).
     */
    HikResult<UpgradeAsyncResponse> upgradeAsync(NET_DVR_UPGRADE_PARAM upgradeParam);

    /**
     * 升级普通设备(同步).
     */
    @SneakyThrows
    HikResult<UpgradeResponse> upgradeSyncForDVR(String sdkPath);

    /**
     * 升级普通设备(异步).
     */
    HikResult<UpgradeAsyncResponse> upgradeAsyncForDVR(String sdkPath);

    /**
     * 升级门禁/人证机设备(同步).
     */
    HikResult<UpgradeResponse> upgradeSyncForACS(String sdkPath, int deviceNo);

    /**
     * 升级门禁/人证机器设备(异步).
     */
    HikResult<UpgradeAsyncResponse> upgradeAsyncForACS(String sdkPath, int deviceNo);

    /**
     * 获取配置.
     */
    HikResult<String> getConfig();

    /**
     * 导出设备配置.
     */
    HikResult<?> getConfigFile(String file);

    /**
     * 获取配置.
     */
    HikResult<?> setConfig(String configContent);

    /**
     * 设置配置.
     */
    HikResult<?> setConfigFile(String file);

    /**
     * 远程控制.
     */
    HikResult<?> remoteControl(int command, Pointer inBuffer, int inBufferSize);
}
