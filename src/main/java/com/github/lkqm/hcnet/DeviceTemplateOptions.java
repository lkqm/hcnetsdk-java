package com.github.lkqm.hcnet;

public interface DeviceTemplateOptions {

    /**
     * 云台控制.
     */
    HikResult ptzControl(long userId, int command, int stop, int speed);

    /**
     * 云台控制开始
     */
    HikResult ptzControlStart(long userId, int command, int speed);

    /**
     * 云台控制停止
     */
    HikResult ptzControlStop(long userId, int command, int speed);

    /**
     * 云台点位设置.
     */
    HikResult ptzPresetSet(long userId, int presetIndex);

    /**
     * 云台点位清除.
     */
    HikResult ptzPresetClean(long userId, int presetIndex);

    /**
     * 云台点位跳转.
     */
    HikResult ptzPresetGoto(long userId, int presetIndex);

    /**
     * 云台点位控制.
     */
    HikResult ptzPreset(long userId, int presetCommand, int presetIndex);

    /**
     * 云台巡航。
     */
    HikResult ptzCruise(long userId, int cruiseCommand, int cruiseRoute, int cruisePoint, int speed);

    /**
     * 云台巡航运行.
     */
    HikResult ptzCruiseRun(long userId, int cruiseRoute);

    /**
     * 云台巡航运行.
     */
    HikResult ptzCruiseStop(long userId, int cruiseRoute);

    /**
     * 云台巡航添加点位.
     */
    HikResult ptzCruiseFillPreset(long userId, int cruiseRoute, int cruisePoint, int speed);

    /**
     * 云台轨迹操作。
     */
    HikResult ptzTrack(long userId, int trackCommand);

    /**
     * 云台轨迹开始记录.
     */
    HikResult ptzTrackStartRecord(long userId);

    /**
     * 云台轨迹停止记录.
     */
    HikResult ptzTrackStopRecord(long userId);

    /**
     * 云台轨迹运行.
     */
    HikResult ptzTrackRun(long userId);
}
