package com.github.lkqm.hcnet.callback;

import com.github.lkqm.hcnet.handler.AbstractFaceSnapHandler;
import com.github.lkqm.hcnet.model.FaceSnapEvent;

public class PrintFaceSnapHandler extends AbstractFaceSnapHandler {

    public static PrintFaceSnapHandler INSTANCE = new PrintFaceSnapHandler();

    @Override
    public void handle(FaceSnapEvent event) {
        System.out.println("抓拍回调: " + event.getDeviceInfo() + "," + event.getFaceSnapInfo().getFaceScore());
    }

}
