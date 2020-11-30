package com.github.lkqm.hcnet.model;

import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

/**
 * 海康设备登录响应结果
 */
@Data
@Builder
public class Token implements Serializable {

    /**
     * 登录后的用户标识
     */
    private final Long userId;

    /**
     * 设备序列号
     */
    private final String deviceSerialNumber;

}
