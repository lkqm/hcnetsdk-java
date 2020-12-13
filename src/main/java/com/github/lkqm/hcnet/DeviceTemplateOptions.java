package com.github.lkqm.hcnet;

import com.github.lkqm.hcnet.HCNetSDK.FExceptionCallBack;
import com.github.lkqm.hcnet.HCNetSDK.FRealDataCallBack_V30;
import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_PREVIEWINFO;
import com.github.lkqm.hcnet.model.PassThroughResponse;
import com.github.lkqm.hcnet.options.MaintainOptions;
import com.github.lkqm.hcnet.options.PtzOptions;
import com.github.lkqm.hcnet.options.SdkOptions;
import com.github.lkqm.hcnet.util.BiFunction;
import com.sun.jna.Structure;
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
     * 修改设备密码.
     */
    HikResult<?> modifyPassword(long userId, String username, String newPassword);

    /**
     * NVR重新绑定通道, 抓拍机修改密码后需要重新绑定.
     */
    HikResult<?> nvrRebindChannels(long userId, String dvrUsername, String dvrNewPassword);

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
     * 本地sdk操作.
     */
    SdkOptions opsForSdk();

    /**
     * 设备维护.
     */
    MaintainOptions opsForMaintain(long userId);

    /**
     * 云台操作.
     */
    PtzOptions opsForPtz(long userId);

}
