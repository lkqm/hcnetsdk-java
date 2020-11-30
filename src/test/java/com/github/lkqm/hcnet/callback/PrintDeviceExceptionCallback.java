package com.github.lkqm.hcnet.callback;

import com.github.lkqm.hcnet.HCNetSDK.FExceptionCallBack;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;

public class PrintDeviceExceptionCallback implements FExceptionCallBack {

    public static final PrintDeviceExceptionCallback INSTANCE = new PrintDeviceExceptionCallback();

    @Override
    public void invoke(int dwType, NativeLong lUserID, NativeLong lHandle, Pointer pUser) throws InterruptedException {
        System.out.println(String.format("异常回调: type=%s, userId=%s, handle=%s", dwType, lUserID, lHandle));
    }
}
