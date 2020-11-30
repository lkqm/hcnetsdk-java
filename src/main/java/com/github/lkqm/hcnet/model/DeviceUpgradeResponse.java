package com.github.lkqm.hcnet.model;

import com.github.lkqm.hcnet.HikResult;
import java.io.Serializable;
import java.util.concurrent.Future;
import lombok.Data;

/**
 * 设备升级结果
 */
@Data
public class DeviceUpgradeResponse implements Serializable {

    /**
     * 升级的句柄
     */
    private long handle;

    private Future<Integer> future;

    /**
     * 同步升级状态
     */
    private int state;

    /**
     * 同步升级错误
     */
    private HikResult<?> error;


}
