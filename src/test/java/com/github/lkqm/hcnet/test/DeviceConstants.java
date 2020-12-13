package com.github.lkqm.hcnet.test;

import lombok.AllArgsConstructor;

public interface DeviceConstants {

    DeviceInfo dvrDevice = new DeviceInfo("192.168.0.123", 8000, "admin", "wxb888888");
    DeviceInfo acsDevice = new DeviceInfo("192.168.0.155", 8000, "admin", "wxb888888");


    @AllArgsConstructor
    class DeviceInfo {

        public final String ip;
        public final int port;
        public final String user;
        public final String password;
    }

}
