package com.github.lkqm.hcnet;

import com.github.lkqm.hcnet.HCNetSDK.FExceptionCallBack;
import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_UPGRADE_PARAM;
import com.github.lkqm.hcnet.model.PassThroughResponse;
import com.github.lkqm.hcnet.model.Token;
import com.github.lkqm.hcnet.model.UpgradeAsyncResponse;
import com.github.lkqm.hcnet.model.UpgradeResponse;
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
     *
     * @return
     */
    HikResult<PassThroughResponse> passThrough(String url, String data);

    /**
     * 透传.
     *
     * @return
     */
    HikResult<PassThroughResponse> passThrough(String url, String data, int exceptOutByteSize);

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
     * 升级设备(同步).
     */
    HikResult<UpgradeResponse> upgradeSync(NET_DVR_UPGRADE_PARAM upgradeParam);

    /**
     * 升级设备(异步).
     */
    HikResult<UpgradeAsyncResponse> upgradeAsync(NET_DVR_UPGRADE_PARAM upgradeParam);

    /**
     * 升级普通设备(同步).
     */
    HikResult<UpgradeResponse> upgradeDvrSync(String sdkPath);

    /**
     * 升级普通设备(异步).
     */
    HikResult<UpgradeAsyncResponse> upgradeDvrAsync(String sdkPath);

    /**
     * 升级门禁/人证机设备(同步).
     */
    HikResult<UpgradeResponse> upgradeAcsSync(String sdkPath, int deviceNo);

    /**
     * 升级门禁/人证机器设备(异步).
     */
    HikResult<UpgradeAsyncResponse> upgradeAcsAsync(String sdkPath, int deviceNo);

    /**
     * 云台控制.
     */
    HikResult ptzControl(int command, int stop, int speed);

    /**
     * 云台控制开始
     */
    HikResult ptzControlStart(int command, int speed);

    /**
     * 云台控制停止
     */
    HikResult ptzControlStop(int command, int speed);

    /**
     * 云台点位设置.
     */
    HikResult ptzPresetSet(int presetIndex);

    /**
     * 云台点位清除.
     */
    HikResult ptzPresetClean(int presetIndex);

    /**
     * 云台点位跳转.
     */
    HikResult ptzPresetGoto(int presetIndex);

    /**
     * 云台点位控制.
     */
    HikResult ptzPreset(int presetCommand, int presetIndex);

    /**
     * 云台巡航。
     */
    HikResult ptzCruise(int cruiseCommand, int cruiseRoute, int cruisePoint, int speed);

    /**
     * 云台巡航运行.
     */
    HikResult ptzCruiseRun(int cruiseRoute);

    /**
     * 云台巡航运行.
     */
    HikResult ptzCruiseStop(int cruiseRoute);

    /**
     * 云台巡航添加点位.
     */
    HikResult ptzCruiseFillPreset(int cruiseRoute, int cruisePoint, int speed);

    /**
     * 云台轨迹操作。
     */
    HikResult ptzTrack(int trackCommand);

    /**
     * 云台轨迹开始记录.
     */
    HikResult ptzTrackStartRecord();

    /**
     * 云台轨迹停止记录.
     */
    HikResult ptzTrackStopRecord();

    /**
     * 云台轨迹运行.
     */
    HikResult ptzTrackRun();

    /**
     * 云台图像缩放.
     */
    HikResult ptzZoom(int xTop, int yTop, int xBottom, int yBottom);
}
