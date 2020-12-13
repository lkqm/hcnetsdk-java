package com.github.lkqm.hcnet.options;

import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_SDKABL;
import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_SDKSTATE;
import com.github.lkqm.hcnet.HikResult;

/**
 * sdk本地功能.
 */
public interface SdkOptions {

    /**
     * 获取sdk版本信息
     */
    String getVersion();

    /**
     * 获取sdk当前状态信息.
     */
    HikResult<NET_DVR_SDKSTATE> getState();

    /**
     * 获取sdk的能力
     */
    HikResult<NET_DVR_SDKABL> getAbility();

    /**
     * 设置sdk日志输出, 参考: NET_DVR_SetLogToFile
     */
    HikResult<?> setLogFile(int logLevel, String logDir, boolean autoDel);

    /**
     * 设置sdk超时配置.
     *
     * @param connectTimeoutMs    连接超时时间，默认3000毫秒, 取值范围[300,75000]
     * @param recvTimeoutMs       接收数据超时时间，默认5000毫秒
     * @param reconnectIntervalMs 重连时间间隔, 默认5000毫秒, 最小为 3000 毫秒
     */
    HikResult<?> setTimeout(int connectTimeoutMs, int recvTimeoutMs, int reconnectIntervalMs);
}
