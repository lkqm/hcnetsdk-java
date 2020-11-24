package com.github.lkqm.hcnet.model;

import java.io.Serializable;
import lombok.Data;

/**
 * 设备信息
 */
@Data
public class DeviceInfo implements Serializable {

    /**
     * 登录标识
     */
    private Long userId;

    /**
     * 设备ip地址
     */
    private String deviceIp;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 设备序列号
     */
    private String serialNumber;

    /**
     * 设备mac地址
     */
    private String deviceMacAddr;
}
