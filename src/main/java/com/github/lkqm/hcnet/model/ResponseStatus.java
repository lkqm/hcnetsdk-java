package com.github.lkqm.hcnet.model;

import com.github.lkqm.hcnet.util.InnerUtils;
import java.io.Serializable;
import java.util.Map;
import lombok.Data;

/**
 * 错误响应结果.
 */
@Data
public class ResponseStatus implements Serializable {

    /**
     * 请求的地址.
     */
    private String requestURL;

    /**
     * 状态码.
     */
    private Integer statusCode;

    /**
     * 子状态码.
     */
    private String subStatusCode;

    /**
     * 状态码文字描述.
     */
    private String statusString;

    public static ResponseStatus ofXml(String xml) {
        ResponseStatus instance = new ResponseStatus();
        Map<String, String> map = InnerUtils.xmlToMap(xml, "ResponseStatus");
        instance.setRequestURL(map.get("requestURL"));
        instance.setStatusCode(Integer.valueOf(map.get("statusCode")));
        instance.setSubStatusCode(map.get("subStatusCode"));
        instance.setStatusString(map.get("statusString"));
        return instance;
    }

}
