# hcnetsdk
一个对海康网络HCNetSDK二次封装的Java库.

## 例子
```
// 时间校准
HikDevice device = new HikDevice(hcnetsdk, "192.168.0.239", HikDeviceTemplate.DEFAULT_PORT, "admin", "wxb888888");
device.init();
device.adjustTime(new Date());
```

## 核心
- HikDevice: 面向对象方式操作设备.
- HikDeviceTemplate: 封装底层sdk提供便捷对设备的操作.
- DispatchMessageCallback: 事件分发的消息处理回调.
- JnaPathUtils: 约定大于配置的本地依赖库加载.

## 功能
- 登录 (login)
- 注销 (logout)
- 执行操作 (doAction)
- 获取错误 (lastError)
- 透传 (passThrough)
- 消息回调 (registerMessageCallback)
- 修改密码 (modifyPassword)
- 校准时间 (adjustTime)
- 重启设备 (reboot)
- 获取设备配置 (getNvrConfig)
- 修改设备配置 (setNvrConfig)
- ...

## 事件
`DispatchMessageCallback`通过事件分发处理回调消息, 提供特定事件的抽象处理类：
- 人脸抓拍事件: `AbstractFaceSnapHandler`
- 刷证事件: `AbstractFreshCardHandler`


