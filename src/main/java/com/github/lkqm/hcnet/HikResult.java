package com.github.lkqm.hcnet;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 执行海康动作的响应结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("rawtypes")
public class HikResult<T> {

    protected boolean success;
    protected Integer errorCode;
    protected String errorMsg;
    protected T data;

    public static HikResult<?> ok() {
        return new HikResult<>(true, null, null, null);
    }

    public static <T> HikResult<T> ok(T data) {
        return new HikResult<>(true, null, null, data);
    }

    public static HikResult fail(int code, String msg) {
        return new HikResult<>(false, code, msg, null);
    }

    public void set(HikResult data) {
        this.success = data.success;
        this.errorCode = data.errorCode;
        this.errorMsg = data.errorMsg;
    }

    public String getError() {
        if (success) {
            return "";
        }
        return errorCode + "," + errorMsg;
    }
}
