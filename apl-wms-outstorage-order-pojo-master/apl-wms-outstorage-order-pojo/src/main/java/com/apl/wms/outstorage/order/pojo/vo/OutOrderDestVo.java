package com.apl.wms.outstorage.order.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 出库订单其他信息 详细实体
 * </p>
 *
 * @author cy
 * @since 2020-01-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OutOrderDestVo implements Serializable {


private static final long serialVersionUID=1L;

    // 出库订单id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orderId;

    // 是否投保 1投保  2不投保
    private Integer isInsure;

    // 投保金额
    private BigDecimal insureAmount;

    // 物流方式  1快递  2空运  3海运
    private Integer toDescLogistics;

    // 物流承运商
    private String toDescCarrier;

    // 是否已获取物流单号  1已有   2没有
    private Integer isCarrierSn;

    // 物流单号
    private String carrierSn;

    // 收件人联系人
    private String destContact;

    // 收件人联系电话
    private String destTel;

    // 收件人手机号
    private String destPhone;

    // 收件人邮箱
    private String destEmail;

    // 收件人国家简码
    private String destCountryCode;

    // 收件人邮编
    private String destZipCode;

    // 收件人州
    private String destState;

    // 收件人城市
    private String destCity;

    // 收件人公司名
    private String destCompanyName;

    // 收件人街道
    private String destStreet;

    // 收件人地址1
    private String destAddress1;

    // 收件人地址2
    private String destAddress2;

    // 收件人地址3
    private String destAddress3;
}
