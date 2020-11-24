package com.github.lkqm.hcnet;

import com.github.lkqm.hcnet.model.Token;
import com.sun.jna.NativeLong;

public class HikDevice {

    private String ip;
    private int port;
    private String user;
    private String password;

    private HikService hikService;
    private NativeLong userId;

    public HikDevice(String ip, int port, String user, String password) {
        this.ip = ip;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    /**
     * 设备初始化
     */
    public synchronized HikResult init() {
        HikResult<Token> loginResult = hikService.login(ip, port, user, password);
        if (loginResult.isSuccess()) {
            if (userId != null) {
                hikService.logout(userId);
            }
            this.userId = loginResult.getData().getUserId();
        }
        return loginResult;
    }

}
