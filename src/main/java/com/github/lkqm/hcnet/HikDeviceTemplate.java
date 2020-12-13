package com.github.lkqm.hcnet;

import com.github.lkqm.hcnet.HCNetSDK.FExceptionCallBack;
import com.github.lkqm.hcnet.HCNetSDK.FRealDataCallBack_V30;
import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_DEVICEINFO_V40;
import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_PREVIEWINFO;
import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_USER_LOGIN_INFO;
import com.github.lkqm.hcnet.model.PassThroughResponse;
import com.github.lkqm.hcnet.model.ResponseStatus;
import com.github.lkqm.hcnet.options.MaintainOptions;
import com.github.lkqm.hcnet.options.MaintainOptionsImpl;
import com.github.lkqm.hcnet.options.PtzOptions;
import com.github.lkqm.hcnet.options.PtzOptionsImpl;
import com.github.lkqm.hcnet.options.SdkOptions;
import com.github.lkqm.hcnet.options.SdkOptionsImpl;
import com.github.lkqm.hcnet.util.BiFunction;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;

/**
 * 海康SDK工具类.
 */
public class HikDeviceTemplate implements DeviceTemplateOptions {

    public static final int DEFAULT_PORT = 8000;

    @Getter
    @NonNull
    private final HCNetSDK hcnetsdk;

    public HikDeviceTemplate(@NonNull HCNetSDK hcnetsdk) {
        hcnetsdk.NET_DVR_Init();
        this.hcnetsdk = hcnetsdk;
    }

    @Override
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

        Token token = Token.builder()
                .userId(userId.longValue())
                .deviceSerialNumber(new String(deviceInfo.struDeviceV30.sSerialNumber).trim())
                .build();
        return HikResult.ok(token);
    }

    @Override
    public HikResult<?> logout(long userId) {
        if (userId > -1) {
            boolean result = hcnetsdk.NET_DVR_Logout(new NativeLong(userId));
            if (!result) {
                return lastError();
            }
        }
        return HikResult.ok();
    }

    @Override
    public HikResult<?> doAction(String ip, int port, String user, String password,
            BiFunction<HCNetSDK, Token, HikResult<?>> action) {
        HikResult<Token> loginResult = login(ip, port, user, password);
        if (!loginResult.isSuccess()) {
            return loginResult;
        }

        Token token = loginResult.getData();
        try {
            HikResult<?> result = action.apply(hcnetsdk, token);
            if (result == null) {
                result = HikResult.ok();
            }
            return result;
        } finally {
            logout(token.getUserId());
        }
    }

    @Override
    public <T> HikResult<T> lastError() {
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

    @Override
    public HikResult<PassThroughResponse> passThrough(long userId, String url, String input) {
        byte[] bytes = input == null ? null : input.getBytes();
        return passThrough(userId, url, bytes, 3 * 1024 * 1024);
    }

    @Override
    public HikResult<PassThroughResponse> passThrough(long userId, String url, byte[] inputBytes,
            int exceptOutByteSize) {
        byte[] urlBytes = url.getBytes();
        inputBytes = (inputBytes == null || inputBytes.length == 0) ? " ".getBytes() : inputBytes;
        // 输入参数
        HCNetSDK.NET_DVR_STRING_POINTER urlPointer = new HCNetSDK.NET_DVR_STRING_POINTER();
        urlPointer.byString = urlBytes;
        urlPointer.write();

        HCNetSDK.NET_DVR_STRING_POINTER inputPointer = new HCNetSDK.NET_DVR_STRING_POINTER();
        inputPointer.byString = inputBytes;
        inputPointer.write();

        HCNetSDK.NET_DVR_XML_CONFIG_INPUT inputParams = new HCNetSDK.NET_DVR_XML_CONFIG_INPUT();
        inputParams.dwSize = inputParams.size();
        inputParams.lpRequestUrl = urlPointer.getPointer();
        inputParams.dwRequestUrlLen = urlPointer.byString.length;
        inputParams.lpInBuffer = inputPointer.getPointer();
        inputParams.dwInBufferSize = inputPointer.byString.length;
        inputParams.write();

        // 输出参数
        HCNetSDK.NET_DVR_STRING_POINTER outputPointer = new HCNetSDK.NET_DVR_STRING_POINTER();
        outputPointer.byString = new byte[exceptOutByteSize];
        HCNetSDK.NET_DVR_STRING_POINTER outputStatusPointer = new HCNetSDK.NET_DVR_STRING_POINTER();

        HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT outputParams = new HCNetSDK.NET_DVR_XML_CONFIG_OUTPUT();
        outputParams.dwSize = outputParams.size();
        outputParams.lpOutBuffer = outputPointer.getPointer();
        outputParams.dwOutBufferSize = outputPointer.size();
        outputParams.lpStatusBuffer = outputStatusPointer.getPointer();
        outputParams.dwStatusSize = outputStatusPointer.size();
        inputPointer.write();

        // 透传
        boolean result = hcnetsdk.NET_DVR_STDXMLConfig(new NativeLong(userId), inputParams, outputParams);
        outputPointer.read();
        outputStatusPointer.read();
        byte[] data = outputPointer.byString;
        byte[] statusData = outputStatusPointer.byString;
        String statusXml = new String(statusData).trim();

        HikResult<PassThroughResponse> hikResult = new HikResult<>();
        PassThroughResponse response = new PassThroughResponse();
        if (!result) {
            HikResult<?> error = lastError();
            hikResult.set(error);
            if (statusXml.trim().length() > 0) {
                response.setStatus(ResponseStatus.ofXml(statusXml));
            }
        } else {
            hikResult.setSuccess(true);
            response.setBytes(data);
        }

        hikResult.setData(response);
        return hikResult;
    }


    @Override
    public HikResult<Long> setupDeploy(long userId, HCNetSDK.FMSGCallBack messageCallback,
            FExceptionCallBack exceptionCallback) {
        // 消息回调
        if (messageCallback != null) {
            boolean result = hcnetsdk.NET_DVR_SetDVRMessageCallBack_V30(messageCallback, null);
            if (!result) {
                return lastError();
            }
        }

        // 建立通道
        NativeLong setupAlarmHandle = hcnetsdk.NET_DVR_SetupAlarmChan_V30(new NativeLong(userId));
        if (setupAlarmHandle.longValue() == -1) {
            return lastError();
        }

        // 异常回调
        if (exceptionCallback != null) {
            boolean setExceptionResult = hcnetsdk
                    .NET_DVR_SetExceptionCallBack_V30(0, setupAlarmHandle.intValue(), exceptionCallback, null);
            if (!setExceptionResult) {
                hcnetsdk.NET_DVR_CloseAlarmChan_V30(setupAlarmHandle);
                return lastError();
            }
        }
        return HikResult.ok(setupAlarmHandle.longValue());
    }

    @Override
    public HikResult<?> modifyPassword(long userId, String username, String newPassword) {
        // 获取原始配置
        HCNetSDK.NET_DVR_USER_V30 dvrUser = new HCNetSDK.NET_DVR_USER_V30();
        boolean getResult = hcnetsdk.NET_DVR_GetDVRConfig(new NativeLong(userId), HCNetSDK.NET_DVR_GET_USERCFG_V30,
                new NativeLong(0), dvrUser.getPointer(), dvrUser.size(), new IntByReference(0));
        if (!getResult) {
            HikResult<?> errorResult = lastError();
            errorResult.setSuccess(false);
            return errorResult;
        }

        // 修改指定用户密码
        dvrUser.read();
        for (HCNetSDK.NET_DVR_USER_INFO_V30 userInfo : dvrUser.struUser) {
            String name = new String(userInfo.sUserName).trim();
            if (Objects.equals(username, name)) {
                userInfo.sPassword = newPassword.getBytes();
            }
        }
        dvrUser.write();
        boolean setResult = hcnetsdk.NET_DVR_SetDVRConfig(new NativeLong(userId), HCNetSDK.NET_DVR_SET_USERCFG_V30,
                new NativeLong(0), dvrUser.getPointer(), dvrUser.dwSize);
        if (!setResult) {
            HikResult<?> errorResult = lastError();
            errorResult.setSuccess(false);
            return errorResult;
        }
        return HikResult.ok();
    }

    @Override
    public HikResult<?> nvrRebindChannels(long userId, String dvrUsername, String dvrNewPassword) {
        // 获取已绑定通道配置
        IntByReference ibrBytesReturned = new IntByReference(0);
        HCNetSDK.NET_DVR_IPPARACFG mStrIpparaCfg = new HCNetSDK.NET_DVR_IPPARACFG();
        mStrIpparaCfg.write();
        Pointer lpIpParaConfig = mStrIpparaCfg.getPointer();
        boolean getResult = hcnetsdk
                .NET_DVR_GetDVRConfig(new NativeLong(userId), HCNetSDK.NET_DVR_GET_IPPARACFG, new NativeLong(33),
                        lpIpParaConfig, mStrIpparaCfg.size(), ibrBytesReturned);
        if (!getResult) {
            HikResult<?> errorResult = lastError();
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
            HikResult<?> errorResult = lastError();
            errorResult.setSuccess(false);
            return errorResult;
        }
        return HikResult.ok();
    }

    @Override
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

    @Override
    public HikResult<?> getDvrConfig(long userId, long channel, int command, Structure data) {
        data.write();
        boolean result = hcnetsdk.NET_DVR_GetDVRConfig(new NativeLong(userId), command, new NativeLong(channel),
                data.getPointer(), data.size(), new IntByReference(0));
        if (!result) {
            return lastError();
        }
        data.read();
        return HikResult.ok();
    }

    @Override
    public HikResult<?> setDvrConfig(long userId, long channel, int command, Structure data) {
        data.write();
        boolean result = hcnetsdk.NET_DVR_SetDVRConfig(new NativeLong(userId), command, new NativeLong(channel),
                data.getPointer(), data.size());
        if (!result) {
            return lastError();
        }
        return HikResult.ok();
    }

    @Override
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

    @Override
    public HikResult<Long> realPlay(long userId, NET_DVR_PREVIEWINFO previewInfo,
            FRealDataCallBack_V30 callback) {
        NativeLong realPlayHandle = hcnetsdk.NET_DVR_RealPlay_V40(new NativeLong(userId), previewInfo, callback, null);
        if (realPlayHandle.longValue() == -1) {
            return lastError();
        }
        return HikResult.ok(realPlayHandle.longValue());
    }

    @Override
    public HikResult<?> stopRealPlay(long realHandle) {
        boolean result = hcnetsdk.NET_DVR_StopRealPlay(new NativeLong(realHandle));
        return result ? HikResult.ok() : lastError();
    }

    @Override
    public SdkOptions opsForSdk() {
        return new SdkOptionsImpl(this);
    }

    @Override
    public MaintainOptions opsForMaintain(long userId) {
        return new MaintainOptionsImpl(this, userId);
    }

    @Override
    public PtzOptions opsForPtz(long userId) {
        return new PtzOptionsImpl(this, userId);
    }

}
