package com.github.lkqm.hcnet.model;

import java.io.Serializable;
import lombok.Data;

/**
 * 设备透传响应数据.
 */
@Data
public class PassThroughResponse implements Serializable {

    /**
     * 执行透传成功返回数据.
     */
    private byte[] data;

    /**
     * 透传执行失败返回的详细错误信息.
     */
    private ResponseStatus responseStatus;

    /**
     * 返回字符串的数据.
     */
    public String getStringData() {
        if (data != null) {
            return new String(data);
        }
        return null;
    }

}
