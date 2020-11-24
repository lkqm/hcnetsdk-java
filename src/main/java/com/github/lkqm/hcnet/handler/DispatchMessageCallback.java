package com.github.lkqm.hcnet.handler;

import com.github.lkqm.hcnet.HCNetSDK;
import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_ALARMER;
import com.github.lkqm.hcnet.HCNetSDK.RECV_ALARM;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import java.util.List;
import lombok.NonNull;

/**
 * 基于分发的海康设备消息回调处理类.
 */
public class DispatchMessageCallback implements HCNetSDK.FMSGCallBack {

    private List<Handler> handlers;

    public DispatchMessageCallback(@NonNull List<Handler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public void invoke(NativeLong lCommand, NET_DVR_ALARMER pAlarmer, RECV_ALARM pAlarmInfo, int dwBufLen,
            Pointer pUser) {
        int cmd = lCommand.intValue();

        for (Handler handler : handlers) {
            if (handler.accept(cmd)) {
                handler.invoke(lCommand, pAlarmer, pAlarmInfo, dwBufLen, pUser);
                return;
            }
        }
    }
}
