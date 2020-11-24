package com.github.lkqm.hcnet.model;

import com.sun.jna.NativeLong;
import java.io.Serializable;
import lombok.Data;

/**
 * 海康设备登录响应结果
 */
@Data
public class Token implements Serializable {

    /**
     * 登录后的用户标识
     */
    private NativeLong userId;

    /**
     * 设备序列号
     */
    private String deviceSerialNumber;

}
