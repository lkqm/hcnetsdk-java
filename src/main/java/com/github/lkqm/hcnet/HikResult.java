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

    /**
     * 返回成功的实例.
     */
    public static HikResult<?> ok() {
        return new HikResult<>(true, null, null, null);
    }

    /**
     * 创建成功的实例.
     */
    public static <T> HikResult<T> ok(T data) {
        return new HikResult<>(true, null, null, data);
    }

    /**
     * 创建失败的实例.
     */
    public static <T> HikResult<T> fail(int code, String msg) {
        return new HikResult<>(false, code, msg, null);
    }

    /**
     * 创建失败的实例.
     */
    public static <T> HikResult<T> fail(int code, String msg, T data) {
        return new HikResult<>(false, code, msg, data);
    }

    /**
     * 设置错误码，错误消息，成功状态.
     */
    public void set(HikResult data) {
        this.success = data.success;
        this.errorCode = data.errorCode;
        this.errorMsg = data.errorMsg;
    }

    /**
     * 获取错误信息，格式: $code,$msg
     */
    public String getError() {
        if (success) {
            return "";
        }
        return errorCode + "," + errorMsg;
    }

    /**
     * 是否是密码或用户名错误.
     */
    public boolean isPasswordError() {
        return errorCode != null && errorCode == 1;
    }

    /**
     * 是否是设备离线错误.
     */
    public boolean isDeviceOfflineError() {
        return errorCode != null && errorCode == 7;
    }
}
