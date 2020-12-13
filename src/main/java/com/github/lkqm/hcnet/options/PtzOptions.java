package com.github.lkqm.hcnet.options;

import com.github.lkqm.hcnet.HikResult;

/**
 * 云台操作.
 */
public interface PtzOptions {

    /**
     * 云台控制.
     */
    HikResult<?> control(int command, int stop, int speed);

    /**
     * 云台控制开始
     */
    HikResult<?> controlStart(int command, int speed);

    /**
     * 云台控制停止
     */
    HikResult<?> controlStop(int command, int speed);

    /**
     * 云台点位控制.
     */
    HikResult<?> preset(int presetCommand, int presetIndex);

    /**
     * 云台点位设置.
     */
    HikResult<?> presetSet(int presetIndex);

    /**
     * 云台点位清除.
     */
    HikResult<?> presetClean(int presetIndex);

    /**
     * 云台点位跳转.
     */
    HikResult<?> presetGoto(int presetIndex);

    /**
     * 云台巡航。
     */
    HikResult<?> cruise(int cruiseCommand, int cruiseRoute, int cruisePoint, int speed);

    /**
     * 云台巡航运行.
     */
    HikResult<?> cruiseRun(int cruiseRoute);

    /**
     * 云台巡航运行.
     */
    HikResult<?> cruiseStop(int cruiseRoute);

    /**
     * 云台巡航添加点位.
     */
    HikResult<?> cruiseFillPreset(int cruiseRoute, int cruisePoint, int speed);

    /**
     * 云台轨迹操作。
     */
    HikResult<?> track(int trackCommand);

    /**
     * 云台轨迹开始记录.
     */
    HikResult<?> trackStartRecord();

    /**
     * 云台轨迹停止记录.
     */
    HikResult<?> trackStopRecord();

    /**
     * 云台轨迹运行.
     */
    HikResult<?> trackRun();

    /**
     * 云台图像缩放.
     */
    HikResult<?> zoom(int xTop, int yTop, int xBottom, int yBottom);

}
