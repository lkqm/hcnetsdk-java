package com.github.lkqm.hcnet.handler;

import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_ALARMER;
import com.github.lkqm.hcnet.model.DeviceInfo;

public abstract class AbstractHandler implements Handler {

    // 解析设备信息
    protected DeviceInfo resolveDeviceInfo(NET_DVR_ALARMER alarm) {
        DeviceInfo deviceInfo = new DeviceInfo();
        if (alarm.byUserIDValid == 1) {
            deviceInfo.setUserId(alarm.lUserID.longValue());
        }
        if (alarm.byDeviceIPValid == 1) {
            String deviceIp = new String(alarm.sDeviceIP).trim();
            deviceInfo.setDeviceIp(deviceIp);
        }
        if (alarm.byDeviceNameValid == 1) {
            String deviceName = new String(alarm.sDeviceName).trim();
            deviceInfo.setDeviceIp(deviceName);
        }
        if (alarm.bySerialValid == 1) {
            String serialNumber = new String(alarm.sSerialNumber).trim();
            deviceInfo.setSerialNumber(serialNumber);
        }
        if (alarm.byMacAddrValid == 1) {
            String macAddr = new String(alarm.byMacAddr).trim();
            deviceInfo.setDeviceMacAddr(macAddr);
        }
        return deviceInfo;
    }

}
