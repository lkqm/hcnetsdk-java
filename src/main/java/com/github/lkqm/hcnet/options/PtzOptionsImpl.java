package com.github.lkqm.hcnet.options;

import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_POINT_FRAME;
import com.github.lkqm.hcnet.HikDeviceTemplate;
import com.github.lkqm.hcnet.HikResult;
import com.sun.jna.NativeLong;

/**
 * 云台操作.
 */
public class PtzOptionsImpl extends BaseOptions implements PtzOptions {

    private final NativeLong userId;

    public PtzOptionsImpl(HikDeviceTemplate deviceTemplate, long userId) {
        super(deviceTemplate);
        this.userId = new NativeLong(userId);
    }

    @Override
    public HikResult<?> control(int command, int stop, int speed) {
        boolean result = getHcnetsdk().NET_DVR_PTZControlWithSpeed_Other(userId, new NativeLong(1),
                command, stop, speed);
        return result ? HikResult.ok() : lastError();
    }

    @Override
    public HikResult<?> controlStart(int command, int speed) {
        return control(command, 0, speed);
    }

    @Override
    public HikResult<?> controlStop(int command, int speed) {
        return control(command, 1, speed);
    }


    @Override
    public HikResult<?> presetSet(int presetIndex) {
        return preset(8, presetIndex);
    }

    @Override
    public HikResult<?> presetClean(int presetIndex) {
        return preset(9, presetIndex);
    }

    @Override
    public HikResult<?> presetGoto(int presetIndex) {
        return preset(39, presetIndex);
    }

    @Override
    public HikResult<?> preset(int presetCommand, int presetIndex) {
        boolean result = getHcnetsdk().NET_DVR_PTZPreset_Other(userId, new NativeLong(1),
                presetCommand, presetIndex);
        return result ? HikResult.ok() : lastError();
    }

    @Override
    public HikResult<?> cruise(int cruiseCommand, int cruiseRoute, int cruisePoint, int speed) {
        boolean result = getHcnetsdk().NET_DVR_PTZCruise_Other(userId, new NativeLong(1), cruiseCommand,
                (byte) cruiseRoute, (byte) cruisePoint, (byte) speed);
        return result ? HikResult.ok() : lastError();
    }

    @Override
    public HikResult<?> cruiseRun(int cruiseRoute) {
        return cruise(37, cruiseRoute, 0, 0);
    }

    @Override
    public HikResult<?> cruiseStop(int cruiseRoute) {
        return cruise(38, cruiseRoute, 0, 0);
    }

    @Override
    public HikResult<?> cruiseFillPreset(int cruiseRoute, int cruisePoint, int speed) {
        return cruise(30, cruiseRoute, cruisePoint, speed);
    }

    @Override
    public HikResult<?> track(int trackCommand) {
        boolean result = getHcnetsdk().NET_DVR_PTZTrack_Other(userId, new NativeLong(1), trackCommand);
        return result ? HikResult.ok() : lastError();
    }

    @Override
    public HikResult<?> trackStartRecord() {
        return track(34);
    }

    @Override
    public HikResult<?> trackStopRecord() {
        return track(35);
    }

    @Override
    public HikResult<?> trackRun() {
        return track(35);
    }

    @Override
    public HikResult<?> zoom(int xTop, int yTop, int xBottom, int yBottom) {
        NET_DVR_POINT_FRAME point = new NET_DVR_POINT_FRAME();
        point.xTop = xTop;
        point.yTop = yTop;
        point.xBottom = xBottom;
        point.yBottom = yBottom;
        point.write();
        boolean result = getHcnetsdk().NET_DVR_PTZSelZoomIn_EX(userId, new NativeLong(1), point);
        return result ? HikResult.ok() : lastError();
    }
}
