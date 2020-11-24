package com.github.lkqm.hcnet.model;

import java.io.Serializable;
import lombok.Data;

/**
 * 人脸抓拍数据.
 */
@Data
public class FaceSnapInfo implements Serializable {

    /**
     * 人脸评分
     */
    private int faceScore;

    /**
     * 人脸图
     */
    private byte[] faceImageBytes;

    /**
     * 背景图
     */
    private byte[] backgroundImageBytes;

    /**
     * 年龄段分组
     */
    private int ageGroup;

    /**
     * 性别, 1- 男，2- 女
     */
    private int sex;

    /**
     * 是否戴眼镜：1- 不戴，2- 戴
     */
    private int eyeGlass;

    /**
     * 年龄
     */
    private int age;

    /**
     * 年龄误差
     */
    private int ageDeviation;

    /**
     * 是否戴口罩：0-表示“未知”（算法不支持）；1- 不戴口罩；2-戴口罩；0xff-算法支持，但是没有识别出来
     */
    private int mask;

    /**
     * 是否微笑：0-表示“未知”（算法不支持）；1- 不微笑；2- 微笑；0xff-算法支持，但是没有识别出来
     */
    private int smile;

    /**
     * 帽子：0- 不支持；1- 不戴帽子；2- 戴帽子；0xff- 未知,算法支持未检出
     */
    private int hat;

    private long snapTimestamp;
}
