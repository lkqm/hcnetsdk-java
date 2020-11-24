package com.github.lkqm.hcnet.handler;

import com.github.lkqm.hcnet.HCNetSDK;

/**
 * 消息回调处理器.
 */
public interface Handler extends HCNetSDK.FMSGCallBack {

    /**
     * 是否接收命令.
     */
    boolean accept(long command);

}
