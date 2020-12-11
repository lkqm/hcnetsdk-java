package com.github.lkqm.hcnet;

import com.github.lkqm.hcnet.HCNetSDK.FExceptionCallBack;
import com.github.lkqm.hcnet.HCNetSDK.FRealDataCallBack_V30;
import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_PREVIEWINFO;
import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_UPGRADE_PARAM;
import com.github.lkqm.hcnet.model.PassThroughResponse;
import com.github.lkqm.hcnet.model.UpgradeAsyncResponse;
import com.github.lkqm.hcnet.model.UpgradeResponse;
import com.github.lkqm.hcnet.util.BiFunction;
import com.sun.jna.Structure;
import java.util.Date;
import lombok.SneakyThrows;

public interface DeviceTemplateOptions {

    /**
     * 登录设备
     */
    HikResult<Token> login(String ip, int port, String user, String password);

    /**
     * 注销登录
     */
    HikResult<?> logout(long userId);

    /**
     * 执行动作
     */
    HikResult<?> doAction(String ip, int port, String user, String password,
            BiFunction<HCNetSDK, Token, HikResult<?>> action);

    /**
     * 获取最后的错误的执行结果
     */
    <T> HikResult<T> lastError();

    /**
     * 设备透传, 实现数据获取或配置修改.
     */
    HikResult<PassThroughResponse> passThrough(long userId, String url, String input);

    /**
     * 设备透传, 实现数据获取或配置修改.
     */
    HikResult<PassThroughResponse> passThrough(long userId, String url, byte[] inputBytes,
            int exceptOutByteSize);

    /**
     * 布防.
     * <p>
     * 包括3个步骤: a.设置回调消息, b.建立上传通道, c.设置异常回调.
     */
    HikResult<Long> setupDeploy(long userId, HCNetSDK.FMSGCallBack messageCallback,
            FExceptionCallBack exceptionCallback);

    /**
     * 重启设备.
     */
    HikResult<?> reboot(long userId);

    /**
     * 修改设备密码.
     */
    HikResult<?> modifyPassword(long userId, String username, String newPassword);

    /**
     * NVR重新绑定通道, 抓拍机修改密码后需要重新绑定.
     */
    HikResult<?> nvrRebindChannels(long userId, String dvrUsername, String dvrNewPassword);

    /**
     * 设备校时
     */
    HikResult<?> adjustTime(long userId, Date time);

    /**
     * 获取设备配置数据.
     */
    @SneakyThrows
    <T extends Structure> HikResult<T> getDvrConfig(long userId, long channel, int command,
            Class<T> clazz);

    /**
     * 获取设备配置数据.
     */
    HikResult<?> getDvrConfig(long userId, long channel, int command, Structure data);

    /**
     * 设置设备配置数据.
     */
    HikResult<?> setDvrConfig(long userId, long channel, int command, Structure data);

    /**
     * 设置视频实时预览
     */
    HikResult<Long> realPlay(long userId, FRealDataCallBack_V30 callback);

    /**
     * 设置实时预览
     */
    HikResult<Long> realPlay(long userId, NET_DVR_PREVIEWINFO previewInfo,
            FRealDataCallBack_V30 callback);

    /**
     * 停止实时预览
     */
    HikResult<?> stopRealPlay(long realHandle);

    /**
     * 升级设备(同步)
     */
    @SneakyThrows
    HikResult<UpgradeResponse> upgradeSync(long userId, NET_DVR_UPGRADE_PARAM upgradeParam);

    /**
     * 升级设备(异步).
     */
    HikResult<UpgradeAsyncResponse> upgradeAsync(long userId, NET_DVR_UPGRADE_PARAM upgradeParam);

    /**
     * 升级普通设备(同步).
     */
    @SneakyThrows
    HikResult<UpgradeResponse> upgradeDvrSync(long userId, String sdkPath);

    /**
     * 升级普通设备(异步).
     */
    HikResult<UpgradeAsyncResponse> upgradeDvrAsync(long userId, String sdkPath);

    /**
     * 升级门禁/人证机设备(同步).
     */
    HikResult<UpgradeResponse> upgradeAcsSync(long userId, String sdkPath, int deviceNo);

    /**
     * 升级门禁/人证机器设备(异步).
     */
    HikResult<UpgradeAsyncResponse> upgradeAcsAsync(long userId, String sdkPath, int deviceNo);

    /**
     * 云台控制.
     */
    HikResult<?> ptzControl(long userId, int command, int stop, int speed);

    /**
     * 云台控制开始
     */
    HikResult<?> ptzControlStart(long userId, int command, int speed);

    /**
     * 云台控制停止
     */
    HikResult<?> ptzControlStop(long userId, int command, int speed);

    /**
     * 云台点位设置.
     */
    HikResult<?> ptzPresetSet(long userId, int presetIndex);

    /**
     * 云台点位清除.
     */
    HikResult<?> ptzPresetClean(long userId, int presetIndex);

    /**
     * 云台点位跳转.
     */
    HikResult<?> ptzPresetGoto(long userId, int presetIndex);

    /**
     * 云台点位控制.
     */
    HikResult<?> ptzPreset(long userId, int presetCommand, int presetIndex);

    /**
     * 云台巡航。
     */
    HikResult<?> ptzCruise(long userId, int cruiseCommand, int cruiseRoute, int cruisePoint, int speed);

    /**
     * 云台巡航运行.
     */
    HikResult<?> ptzCruiseRun(long userId, int cruiseRoute);

    /**
     * 云台巡航运行.
     */
    HikResult<?> ptzCruiseStop(long userId, int cruiseRoute);

    /**
     * 云台巡航添加点位.
     */
    HikResult<?> ptzCruiseFillPreset(long userId, int cruiseRoute, int cruisePoint, int speed);

    /**
     * 云台轨迹操作。
     */
    HikResult<?> ptzTrack(long userId, int trackCommand);

    /**
     * 云台轨迹开始记录.
     */
    HikResult<?> ptzTrackStartRecord(long userId);

    /**
     * 云台轨迹停止记录.
     */
    HikResult<?> ptzTrackStopRecord(long userId);

    /**
     * 云台轨迹运行.
     */
    HikResult<?> ptzTrackRun(long userId);

    /**
     * 云台图像缩放.
     */
    HikResult<?> ptzZoom(long userId, int xTop, int yTop, int xBottom, int yBottom);
}
