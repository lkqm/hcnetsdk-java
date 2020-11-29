package com.github.lkqm.hcnet.handler;

import com.github.lkqm.hcnet.HCNetSDK;
import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_ALARMER;
import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_DATE;
import com.github.lkqm.hcnet.HCNetSDK.NET_DVR_ID_CARD_INFO;
import com.github.lkqm.hcnet.HCNetSDK.RECV_ALARM;
import com.github.lkqm.hcnet.model.FreshCardEvent;
import com.github.lkqm.hcnet.model.IDCardInfo;
import com.sun.jna.NativeLong;
import com.sun.jna.Pointer;
import java.util.Calendar;
import java.util.Date;

/**
 * 刷证消息处理方法.
 */
public abstract class AbstractFreshCardHandler extends AbstractHandler {

    /**
     * 处理刷证事件
     */
    public abstract void handle(FreshCardEvent event);

    @Override
    public boolean accept(long command) {
        return command == HCNetSDK.COMM_ID_INFO_ALARM;
    }

    @Override
    public void invoke(NativeLong lCommand, NET_DVR_ALARMER pAlarmer, RECV_ALARM pAlarmInfo, int dwBufLen,
            Pointer pUser) {
        if (accept(lCommand.longValue())) {
            FreshCardEvent event = new FreshCardEvent();
            event.setCardInfo(resolveIdCardInfo(pAlarmInfo));
            event.setDeviceInfo(resolveDeviceInfo(pAlarmer));
            this.handle(event);
        }
    }

    // 解析身份证信息
    private IDCardInfo resolveIdCardInfo(RECV_ALARM pAlarmInfo) {
        IDCardInfo cardInfo = new IDCardInfo();

        HCNetSDK.NET_DVR_ID_CARD_INFO_ALARM idCardInfoAlarm = new HCNetSDK.NET_DVR_ID_CARD_INFO_ALARM();
        idCardInfoAlarm.write();
        Pointer pCardInfo = idCardInfoAlarm.getPointer();
        pCardInfo.write(0, pAlarmInfo.getPointer().getByteArray(0, idCardInfoAlarm.size()), 0, idCardInfoAlarm.size());
        idCardInfoAlarm.read();

        NET_DVR_ID_CARD_INFO idCardCfg = idCardInfoAlarm.struIDCardCfg;
        cardInfo.setIdNumber(new String(idCardCfg.byIDNum).trim());
        cardInfo.setName(new String(idCardCfg.byName).trim());
        cardInfo.setAddress(new String(idCardCfg.byAddr).trim());
        cardInfo.setSex(idCardCfg.bySex);
        cardInfo.setNation(idCardCfg.byNation);
        cardInfo.setIssuingAuthority(new String(idCardCfg.byIssuingAuthority).trim());
        cardInfo.setTermValidity(idCardCfg.byTermOfValidity);

        cardInfo.setBirth(convertToDate(idCardCfg.struBirth));
        cardInfo.setValidityStartTime(convertToDate(idCardCfg.struStartDate));
        if (idCardCfg.byTermOfValidity == 0) {
            cardInfo.setValidityEndTime(convertToDate(idCardCfg.struEndDate));
        }
        return cardInfo;
    }

    private Date convertToDate(NET_DVR_DATE dvrDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(dvrDate.wYear, dvrDate.byMonth - 1, dvrDate.byDay);
        return calendar.getTime();
    }

}
