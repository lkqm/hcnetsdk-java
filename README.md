# hcnetsdk
一个对海康网络HCNetSDK二次封装的Java库.

## 一个例子
```
    // 修改设备密码
    HikResult actionResult = hikService.doAction(ip, port, user, password, 
        token -> hikService.modifyPassword(token.getUserId(), user, "123456")
    );
```

## 功能
- 登录 (login)
- 注销 (logout)
- 执行操作 (doAction)
- 获取错误 (lastError)
- 透传 (passThrough)
- 消息回调 (registerMessageCallback)
- 修改密码 (modifyPassword)
- 重新绑定通道 (nvrRebindChannels)
- 重启设备 (reboot)

## 事件处理
- 人脸抓拍事件: `AbstractFaceSnapHandler`
- 刷证事件: `AbstractFreshCardHandler`


