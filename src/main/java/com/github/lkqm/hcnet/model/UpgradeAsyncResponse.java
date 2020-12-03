package com.github.lkqm.hcnet.model;

import java.io.Serializable;
import java.util.concurrent.Future;
import lombok.Data;

/**
 * 设备升级结果.
 */
@Data
public class UpgradeAsyncResponse implements Serializable {

    /**
     * 升级的句柄
     */
    private long handle;

    /**
     * 异步升级结果
     */
    private Future<UpgradeResponse> future;
}
