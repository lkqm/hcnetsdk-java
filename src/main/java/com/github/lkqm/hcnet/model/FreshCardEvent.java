package com.github.lkqm.hcnet.model;

import lombok.Data;

/**
 * 人证机刷证事件数据.
 */
@Data
public class FreshCardEvent {

    private IDCardInfo cardInfo;

    private DeviceInfo deviceInfo;

}
