package com.github.lkqm.hcnet.model;

import com.github.lkqm.hcnet.HikResult;
import java.io.Serializable;
import lombok.Data;

/**
 * 设备升级结果
 */
@Data
public class UpgradeResponse implements Serializable {

    /**
     * 升级的句柄
     */
    private long handle;

    /**
     * 升级状态
     */
    private int state;

    /**
     * 升级错误, 当state = -1时
     */
    private HikResult<?> error;

    /**
     * 是否升级成功.
     */
    public boolean isUpgradeSuccess() {
        return state == 1;
    }

}
