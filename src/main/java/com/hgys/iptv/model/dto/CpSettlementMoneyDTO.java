package com.hgys.iptv.model.dto;

import lombok.Data;

import java.sql.Timestamp;

/**
 * @ClassName CpSettlementMoneyDTO
 * @Auther: wangz
 * @Date: 2019/5/28 16:15
 * @Description: TODO
 */
@Data
public class CpSettlementMoneyDTO {

//    private Integer id;
//
//    /** 分账结算编码 */
//    private String masterCode;
//
//    /** 分账结算名称 */
//    private String masterName;

    /** cp编码 */
//    private String cpcode;

    /** cp名称 */
    private String cpname;
//
//    /** 产品编码 */
//    private String productCode;
//
//    /** 产品名称 */
//    private String productName;
//
//    /** 业务编码 */
//    private String businessCode;
//
//    /** 业务名称 */
//    private String businessName;
//
//    /** 结算金额 */
//    private BigDecimal settlementMoney;

    /** 创建时间 */
//    private Timestamp createTime;
    private Timestamp setStartTime;
    private Timestamp setEndTime;
}
