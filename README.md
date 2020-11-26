# hcnetsdk
一个对海康网络HCNetSDK二次封装的Java库.

## 例子
```
// modify password
HikResult<Token> tokenResult = hikService.login("192.168.0.239", HikService.DEFAULT_PORT, "admin", "hik123456");
if (tokenResult.isSuccess()) {
    hikService.modifyPassword(tokenResult.getData().getUserId(), "admin", "123456");
}
```

## 功能
`HikService`对底层sdk进行封装，常见的操作如下：
- 登录 (login)
- 注销 (logout)
- 执行操作 (doAction)
- 获取错误 (lastError)
- 透传 (passThrough)
- 消息回调 (registerMessageCallback)
- 修改密码 (modifyPassword)
- 重启设备 (reboot)
- 重新绑定通道 (nvrRebindChannels)

## 事件
设备消息回调处理提供了基于分发的方式`DispatchMessageCallback`,`Handler`, 以下抽象类简化特定类型事件的回调处理：
- 人脸抓拍事件: `AbstractFaceSnapHandler`
- 刷证事件: `AbstractFreshCardHandler`


