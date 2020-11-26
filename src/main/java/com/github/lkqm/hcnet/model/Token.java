package com.github.lkqm.hcnet.model;

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
    private Long userId;

    /**
     * 设备序列号
     */
    private String deviceSerialNumber;

}
