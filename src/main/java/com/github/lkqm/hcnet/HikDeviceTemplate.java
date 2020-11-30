package com.github.lkqm.hcnet;

import com.github.lkqm.hcnet.HCNetSDK.FRealDataCallBack_V30;
import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_DEVICEINFO_V40;
import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_PREVIEWINFO;
import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_USER_LOGIN_INFO;
import com.github.lkqm.hcnet.model.DeviceUpgradeResponse;
import com.github.lkqm.hcnet.model.Token;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;

/**
 * 海康SDK工具类
 */
@SuppressWarnings("rawtypes")
public class HikDeviceTemplate {

    public static final int DEFAULT_PORT = 8000;

    @Getter
    @NonNull
    private final HCNetSDK hcnetsdk;

    public HikDeviceTemplate(@NonNull HCNetSDK hcnetsdk) {
        hcnetsdk.NET_DVR_Init();
        this.hcnetsdk = hcnetsdk;
    }

    /**
     * 登录设备
     */
    public HikResult<Token> login(String ip, int port, String user, String password) {
        HCNetSDK.NET_DVR_USER_LOGIN_INFO loginInfo = new NET_DVR_USER_LOGIN_INFO();
        System.arraycopy(ip.getBytes(), 0, loginInfo.sDeviceAddress, 0, ip.length());
        loginInfo.wPort = (short) port;
        System.arraycopy(user.getBytes(), 0, loginInfo.sUserName, 0, user.length());
        System.arraycopy(password.getBytes(), 0, loginInfo.sPassword, 0, password.length());
        loginInfo.bUseAsynLogin = 0;
        loginInfo.write();

        HCNetSDK.NET_DVR_DEVICEINFO_V40 deviceInfo = new NET_DVR_DEVICEINFO_V40();
        NativeLong userId = hcnetsdk.NET_DVR_Login_V40(loginInfo.getPointer(), deviceInfo.getPointer());
        deviceInfo.read();
        if (userId.longValue() == -1) {
            return lastError();
        }
        Token token = new Token();
        token.setUserId(userId.longValue());
        token.setDeviceSerialNumber(new String(deviceInfo.struDeviceV30.sSerialNumber).trim());
        return HikResult.ok(token);
    }

    /**
     * 注销登录
     */
    public HikResult logout(long userId) {
        if (userId > -1) {
            boolean result = hcnetsdk.NET_DVR_Logout(new NativeLong(userId));
            if (!result) {
                return lastError();
            }
        }
        return HikResult.ok();
    }

    /**
     * 执行动作
     */
    public HikResult doAction(String ip, int port, String user, String password,
            BiFunction<HCNetSDK, Token, HikResult> action) {
        HikResult<Token> loginResult = login(ip, port, user, password);
        if (!loginResult.isSuccess()) {
            return loginResult;
        }

        Token token = loginResult.getData();
        try {
            HikResult result = action.apply(hcnetsdk, token);
            if (result == null) {
                result = HikResult.ok();
            }
            return result;
        } finally {
            logout(token.getUserId());
        }
    }

    /**
     * 获取最后的错误的执行结果
     */
    public HikResult lastError() {
        int code = hcnetsdk.NET_DVR_GetLastError();
        if (code == 0) {
            return null;
        }

        String msg;
        if (code == 3) {
            msg = "sdk not init.";
        } else {
            msg = hcnetsdk.NET_DVR_GetErrorMsg(new NativeLongByReference(new NativeLong(code)));
        }
        return HikResult.fail(code, msg);
    }

    /**
     * 设备透传, 实现数据获取或配置修改.
     */
    public HikResult<String> passThrough(long userId, String url, String input) {
        //透传
        HCNetSDK.NET_DVR_STRING_POINTER stringRequest = new HCNetSDK.NET_DVR_STRING_POINTER();
        stringRequest.read();
        stringRequest.byString = url.getBytes();
        stringRequest.write();

        HCNetSDK.NET_DVR_STRING_POINTER stringInBuffer = new HCNetSDK.NET_DVR_STRING_POINTER();
        stringInBuffer.read();
        String strInbuffer = null == input ? "" : input;
        stringInBuffer.byString = strInbuffer.getBytes();
        stringInBuffer.write();

        HCNetSDK.NET_DVR_XML_CONFIG_INPUT struXMLInput = new HCNetSDK.NET_DVR_XML_CONFIG_INPUT();
        struXMLInput.read();
        struXMLInput.dwSize = struXMLInput.size();
        struXMLInput.lpRequestUrl = stringRequest.getPointer();
        struXMLInput.dwRequestUrlLen = stringRequest.byString.length;
        struXMLInput.lpInBuffer = stringInBuffer.getPointer();
        struXMLInput.dwInBufferSize = stringInBuffer.byString.length;
        struXMLInput.write();

        HCNetSDK.NET_DVR_STRING_POINTER stringXMLOut = new HCNetSDK.NET_DVR_STRING_POINTER();
        stringXMLOut.read();
        HCNetSDK.NET_DVR_STRING_POINTER struXMLStatus = new HCNetSDK.NET_DVR_STRING_POINTER();
        struXMLStatus.read();

        HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT struXMLOutput = new HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT();
        stringInBuffer.read();
        struXMLOutput.dwSize = struXMLOutput.size();
        struXMLOutput.lpOutBuffer = stringXMLOut.getPointer();
        struXMLOutput.dwOutBufferSize = stringXMLOut.size();
        struXMLOutput.lpStatusBuffer = struXMLStatus.getPointer();
        struXMLOutput.dwStatusSize = struXMLStatus.size();
        stringInBuffer.write();

        boolean result = hcnetsdk.NET_DVR_STDXMLConfig(new NativeLong(userId), struXMLInput, struXMLOutput);
        if (!result) {
            return lastError();
        }
        stringXMLOut.read();
        String strOutXML = new String(stringXMLOut.byString).trim();
        struXMLStatus.read();
        return HikResult.ok(strOutXML);
    }

    /**
     * 设置消息回调，并布防.
     */
    public HikResult<Long> registerMessageCallback(long userId, HCNetSDK.FMSGCallBack callback) {
        boolean result = hcnetsdk.NET_DVR_SetDVRMessageCallBack_V30(callback, null);
        if (!result) {
            return lastError();
        }
        NativeLong setupAlarmHandle = hcnetsdk.NET_DVR_SetupAlarmChan_V30(new NativeLong(userId));
        if (setupAlarmHandle.longValue() == -1) {
            return lastError();
        }
        return HikResult.ok(setupAlarmHandle.longValue());
    }

    /**
     * 重启设备.
     */
    public HikResult reboot(long userId) {
        boolean rebootResult = hcnetsdk.NET_DVR_RebootDVR(new NativeLong(userId));
        if (!rebootResult) {
            return lastError();
        }
        return HikResult.ok();
    }

    /**
     * 修改设备密码.
     */
    public HikResult modifyPassword(long userId, String username, String newPassword) {
        // 获取原始配置
        HCNetSDK.NET_DVR_USER_V30 test = new HCNetSDK.NET_DVR_USER_V30();
        test.write();
        boolean getResult = hcnetsdk.NET_DVR_GetDVRConfig(new NativeLong(userId), HCNetSDK.NET_DVR_GET_USERCFG_V30,
                new NativeLong(0), test.getPointer(), test.size(), new IntByReference(0));
        if (!getResult) {
            HikResult errorResult = lastError();
            errorResult.setSuccess(false);
            return errorResult;
        }

        // 修改指定用户密码
        test.read();
        for (HCNetSDK.NET_DVR_USER_INFO_V30 userInfo : test.struUser) {
            String name = new String(userInfo.sUserName).trim();
            if (Objects.equals(username, name)) {
                userInfo.sPassword = newPassword.getBytes();
            }
        }
        test.write();
        boolean setResult = hcnetsdk.NET_DVR_SetDVRConfig(new NativeLong(userId), HCNetSDK.NET_DVR_SET_USERCFG_V30,
                new NativeLong(0), test.getPointer(), test.dwSize);
        if (!setResult) {
            HikResult errorResult = lastError();
            errorResult.setSuccess(false);
            return errorResult;
        }
        return HikResult.ok();
    }

    /**
     * NVR重新绑定通道, 抓拍机修改密码后需要重新绑定.
     */
    public HikResult nvrRebindChannels(long userId, String dvrUsername, String dvrNewPassword) {
        // 获取已绑定通道配置
        IntByReference ibrBytesReturned = new IntByReference(0);
        HCNetSDK.NET_DVR_IPPARACFG mStrIpparaCfg = new HCNetSDK.NET_DVR_IPPARACFG();
        mStrIpparaCfg.write();
        Pointer lpIpParaConfig = mStrIpparaCfg.getPointer();
        boolean getResult = hcnetsdk
                .NET_DVR_GetDVRConfig(new NativeLong(userId), HCNetSDK.NET_DVR_GET_IPPARACFG, new NativeLong(33),
                        lpIpParaConfig, mStrIpparaCfg.size(), ibrBytesReturned);
        if (!getResult) {
            HikResult errorResult = lastError();
            errorResult.setSuccess(false);
            return errorResult;
        }
        mStrIpparaCfg.read();

        // 修改已绑定通道密码
        for (HCNetSDK.NET_DVR_IPDEVINFO deviceInfo : mStrIpparaCfg.struIPDevInfo) {
            String user = new String(deviceInfo.sUserName).trim();
            if (Objects.equals(dvrUsername, user)) {
                deviceInfo.sPassword = dvrNewPassword.getBytes();
                String ip = new String(deviceInfo.struIP.sIpV4).trim();
            }
        }
        mStrIpparaCfg.write();
        boolean setResult = hcnetsdk
                .NET_DVR_SetDVRConfig(new NativeLong(userId), HCNetSDK.NET_DVR_SET_IPPARACFG, new NativeLong(33),
                        mStrIpparaCfg.getPointer(), mStrIpparaCfg.dwSize);
        if (!setResult) {
            HikResult errorResult = lastError();
            errorResult.setSuccess(false);
            return errorResult;
        }
        return HikResult.ok();
    }

    /**
     * 设备校时
     */
    public HikResult adjustTime(long userId, Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);

        HCNetSDK.NET_DVR_TIME netDvrTime = new HCNetSDK.NET_DVR_TIME();
        netDvrTime.dwYear = calendar.get(Calendar.YEAR);
        netDvrTime.dwMonth = calendar.get(Calendar.MONTH) + 1;
        netDvrTime.dwDay = calendar.get(Calendar.DAY_OF_MONTH);
        netDvrTime.dwHour = calendar.get(Calendar.HOUR_OF_DAY);
        netDvrTime.dwMinute = calendar.get(Calendar.MINUTE);
        netDvrTime.dwSecond = calendar.get(Calendar.SECOND);
        return setDvrConfig(userId, 0, HCNetSDK.NET_DVR_SET_TIMECFG, netDvrTime);
    }

    /**
     * 获取设备配置数据.
     */
    @SneakyThrows
    public <T extends Structure> HikResult<T> getDvrConfig(long userId, long channel, int command,
            Class<T> clazz) {
        T data = clazz.newInstance();
        data.write();
        boolean result = hcnetsdk.NET_DVR_GetDVRConfig(new NativeLong(userId), command, new NativeLong(channel),
                data.getPointer(), data.size(), new IntByReference(0));
        if (!result) {
            return lastError();
        }
        data.read();
        return HikResult.ok(data);
    }

    /**
     * 获取设备配置数据.
     */
    public HikResult getDvrConfig(long userId, long channel, int command, Structure data) {
        data.write();
        boolean result = hcnetsdk.NET_DVR_GetDVRConfig(new NativeLong(userId), command, new NativeLong(channel),
                data.getPointer(), data.size(), new IntByReference(0));
        if (!result) {
            return lastError();
        }
        data.read();
        return HikResult.ok();
    }

    /**
     * 设置设备配置数据.
     */
    public HikResult setDvrConfig(long userId, long channel, int command, Structure data) {
        data.write();
        boolean result = hcnetsdk.NET_DVR_SetDVRConfig(new NativeLong(userId), command, new NativeLong(channel),
                data.getPointer(), data.size());
        if (!result) {
            return lastError();
        }
        return HikResult.ok();
    }

    /**
     * 设置视频实时预览
     */
    public HikResult<Long> realPlay(long userId, FRealDataCallBack_V30 callback) {
        HCNetSDK.NET_DVR_PREVIEWINFO previewInfo = new HCNetSDK.NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = new NativeLong(1);
        previewInfo.dwStreamType = 0;
        previewInfo.dwLinkMode = 1;
        previewInfo.hPlayWnd = null;
        previewInfo.bBlocked = false;
        previewInfo.bPassbackRecord = false;
        previewInfo.byPreviewMode = 0;
        return realPlay(userId, previewInfo, callback);
    }

    /**
     * 设置实时预览
     */
    public HikResult<Long> realPlay(long userId, NET_DVR_PREVIEWINFO previewInfo,
            FRealDataCallBack_V30 callback) {
        NativeLong realPlayHandle = hcnetsdk.NET_DVR_RealPlay_V40(new NativeLong(userId), previewInfo, callback, null);
        if (realPlayHandle.longValue() == -1) {
            return lastError();
        }
        return HikResult.ok(realPlayHandle.longValue());
    }

    /**
     * 停止实时预览
     */
    public HikResult stopRealPlay(long realHandle) {
        boolean result = hcnetsdk.NET_DVR_StopRealPlay(new NativeLong(realHandle));
        return result ? HikResult.ok() : lastError();
    }

    /**
     * 升级设备.
     */
    public HikResult<DeviceUpgradeResponse> upgradeAsync(long userId, String sdkFile) {
        // 请求升级
        NativeLong upgradeHandle = hcnetsdk.NET_DVR_Upgrade(new NativeLong(userId), sdkFile);
        if (upgradeHandle.longValue() == -1) {
            return lastError();
        }

        // 获取结果, 并关闭资源
        FutureTask<Integer> future = new FutureTask<>(() -> {
            int state;
            do {
                state = hcnetsdk.NET_DVR_GetUpgradeState(upgradeHandle);
                Thread.sleep(TimeUnit.SECONDS.toMillis(30));
            } while (state == 2);
            if (state != -1) {
                hcnetsdk.NET_DVR_CloseUpgradeHandle(upgradeHandle);
            }
            return state;
        });
        new Thread(future).start();

        DeviceUpgradeResponse response = new DeviceUpgradeResponse();
        response.setHandle(upgradeHandle.longValue());
        response.setFuture(future);
        return HikResult.ok(response);
    }

    /**
     * 升级设备同步
     */
    @SneakyThrows
    public HikResult<DeviceUpgradeResponse> upgradeSync(long userId, String sdkFile) {
        // 请求升级
        NativeLong upgradeHandle = hcnetsdk.NET_DVR_Upgrade(new NativeLong(userId), sdkFile);
        if (upgradeHandle.longValue() == -1) {
            return lastError();
        }

        // 获取结果，并关闭资源
        int state;
        do {
            state = hcnetsdk.NET_DVR_GetUpgradeState(upgradeHandle);
            Thread.sleep(TimeUnit.SECONDS.toMillis(30));
        } while (state == 2);
        if (state != -1) {
            hcnetsdk.NET_DVR_CloseUpgradeHandle(upgradeHandle);
        }

        DeviceUpgradeResponse response = new DeviceUpgradeResponse();
        response.setHandle(upgradeHandle.longValue());
        response.setState(state);
        if (state == -1) {
            response.setError(lastError());
        }
        return HikResult.ok(response);
    }

}
