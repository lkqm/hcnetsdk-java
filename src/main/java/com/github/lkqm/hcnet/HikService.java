package com.github.lkqm.hcnet;

import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_DEVICEINFO_V40;
import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_USER_LOGIN_INFO;
import com.github.lkqm.hcnet.model.Token;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.NativeLongByReference;
import java.util.Objects;
import java.util.function.Function;
import lombok.Getter;
import lombok.NonNull;

/**
 * 海康SDK工具类
 */
@SuppressWarnings("rawtypes")
public class HikService {

    public static final int DEFAULT_PORT = 8000;

    @Getter
    @NonNull
    private final HCNetSDK hcnetsdk;


    public HikService(@NonNull HCNetSDK hcnetsdk) {
        hcnetsdk.NET_DVR_Init();
        this.hcnetsdk = hcnetsdk;
    }

    /**
     * 登录设备
     */
    public HikResult<Token> login(String ip, int port, String username, String password) {
        HCNetSDK.NET_DVR_USER_LOGIN_INFO loginInfo = new NET_DVR_USER_LOGIN_INFO();
        System.arraycopy(ip.getBytes(), 0, loginInfo.sDeviceAddress, 0, ip.length());
        loginInfo.wPort = (short) port;
        System.arraycopy(username.getBytes(), 0, loginInfo.sUserName, 0, username.length());
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
        token.setUserId(userId);
        token.setDeviceSerialNumber(new String(deviceInfo.struDeviceV30.sSerialNumber).trim());
        return HikResult.ok(token);
    }

    /**
     * 注销登录
     */
    public HikResult logout(NativeLong userId) {
        if (userId.longValue() > -1) {
            boolean result = hcnetsdk.NET_DVR_Logout(userId);
            if (!result) {
                return lastError();
            }
        }
        return HikResult.ok();
    }

    /**
     * 执行动作
     */
    public HikResult doAction(String ip, int port, String username, String password,
            Function<Token, HikResult> action) {
        HikResult<Token> loginResult = login(ip, port, username, password);
        if (!loginResult.isSuccess()) {
            return loginResult;
        }

        Token token = loginResult.getData();
        try {
            HikResult result = action.apply(token);
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
    public HikResult<String> passThrough(NativeLong userId, String url, String input) {
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

        boolean result = hcnetsdk.NET_DVR_STDXMLConfig(userId, struXMLInput, struXMLOutput);
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
    public HikResult<Long> registerMessageCallback(NativeLong userId, HCNetSDK.FMSGCallBack callback) {
        boolean result = hcnetsdk.NET_DVR_SetDVRMessageCallBack_V30(callback, null);
        if (!result) {
            return lastError();
        }
        NativeLong setupAlarmHandle = hcnetsdk.NET_DVR_SetupAlarmChan_V30(userId);
        if (setupAlarmHandle.longValue() == -1) {
            return lastError();
        }
        return HikResult.ok(setupAlarmHandle.longValue());
    }

    /**
     * 重启设备.
     */
    public HikResult reboot(NativeLong userId) {
        boolean rebootResult = hcnetsdk.NET_DVR_RebootDVR(userId);
        if (!rebootResult) {
            return lastError();
        }
        return HikResult.ok();
    }

    /**
     * 修改设备密码.
     */
    public HikResult modifyPassword(NativeLong userId, String username, String newPassword) {
        // 获取原始配置
        HCNetSDK.NET_DVR_USER_V30 test = new HCNetSDK.NET_DVR_USER_V30();
        test.write();
        boolean getResult = hcnetsdk.NET_DVR_GetDVRConfig(userId, HCNetSDK.NET_DVR_GET_USERCFG_V30,
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
        boolean setResult = hcnetsdk.NET_DVR_SetDVRConfig(userId, HCNetSDK.NET_DVR_SET_USERCFG_V30,
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
    public HikResult nvrRebindChannels(NativeLong userId, String dvrUsername, String dvrNewPassword) {
        // 获取已绑定通道配置
        IntByReference ibrBytesReturned = new IntByReference(0);
        HCNetSDK.NET_DVR_IPPARACFG mStrIpparaCfg = new HCNetSDK.NET_DVR_IPPARACFG();
        mStrIpparaCfg.write();
        Pointer lpIpParaConfig = mStrIpparaCfg.getPointer();
        boolean getResult = hcnetsdk.NET_DVR_GetDVRConfig(userId, HCNetSDK.NET_DVR_GET_IPPARACFG, new NativeLong(33),
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
        boolean setResult = hcnetsdk.NET_DVR_SetDVRConfig(userId, HCNetSDK.NET_DVR_SET_IPPARACFG, new NativeLong(33),
                mStrIpparaCfg.getPointer(), mStrIpparaCfg.dwSize);
        if (!setResult) {
            HikResult errorResult = lastError();
            errorResult.setSuccess(false);
            return errorResult;
        }
        return HikResult.ok();
    }

}
