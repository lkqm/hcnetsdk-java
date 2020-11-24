package com.github.lkqm.hcnet.model;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 身份证信息
 */
@Data
public class IDCardInfo implements Serializable {

    /**
     * 身份证号
     */
    private String idNumber;

    /**
     * 姓名
     */
    private String name;
    /**
     * 家庭住址
     */
    private String address;

    /**
     * 性别, 1- 男，2- 女
     */
    private int sex;

    /**
     * 民族
     */
    private int nation;

    /**
     * 出生日期
     */
    private Date birth;

    /**
     * 是否长期有效, 0- 否，1- 是（有效截止日期无效）
     */
    private int termValidity;

    /**
     * 有效开始时间
     */
    private Date validityStartTime;

    /**
     * 有效结束时间（长期有效时，该值为null）
     */
    private Date validityEndTime;

    /**
     * 签发机关
     */
    private String issuingAuthority;

}
