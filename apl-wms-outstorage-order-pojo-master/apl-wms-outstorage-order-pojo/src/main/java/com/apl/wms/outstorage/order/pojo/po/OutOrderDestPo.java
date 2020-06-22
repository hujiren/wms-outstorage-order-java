package com.apl.wms.outstorage.order.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 出库订单其他信息 持久化对象
 * </p>
 *
 * @author cy
 * @since 2020-01-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("out_order_attachment")
//出库订单-目的地-持久化对象
public class OutOrderDestPo extends Model<OutOrderDestPo> {

    @TableId(value = "order_id", type = IdType.INPUT)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orderId;

    //是否投保 1投保  2不投保
    private Integer isInsure;

    //投保金额
    private BigDecimal insureAmount;

    //物流方式  1快递  2空运  3海运
    private Integer toDescLogistics;

    //物流航线
    private String toDescCarrier;

    //是否已获取物流单号  1已有   2没有
    private Integer isCarrierSn;

    @ApiModelProperty(name = "carrierSn" , value = "物流单号" , hidden = true)
    private String carrierSn;

    @ApiModelProperty(name = "destContact" , value = "收件人联系人" , required = true)
    @NotEmpty(message = "收件人联系人不能为空")
    private String destContact;

    @ApiModelProperty(name = "destTel" , value = "收件人联系电话" , required = true)
    @NotEmpty(message = "收件人联系电话不能为空")
    private String destTel;

    @ApiModelProperty(name = "destPhone" , value = "收件人手机号" , required = true)
    @NotEmpty(message = "收件人手机号不能为空")
    private String destPhone;

    @ApiModelProperty(name = "destEmail" , value = "收件人邮箱")
    @NotEmpty(message = "收件人邮箱不能为空")
    private String destEmail;

    @ApiModelProperty(name = "destCountryCode" , value = "收件人国家简码" , required = true)
    @NotEmpty(message = "收件人国家简码不能为空")
    private String destCountryCode;

    @ApiModelProperty(name = "destZipCode" , value = "收件人邮编")
    private String destZipCode;

    @ApiModelProperty(name = "destState" , value = "收件人 州")
    private String destState;

    @ApiModelProperty(name = "destCity" , value = "收件人城市" , required = true)
    @NotEmpty(message = "收件人城市不能为空")
    private String destCity;

    @ApiModelProperty(name = "destCompanyName" , value = "收件人公司名" )
    //@NotEmpty(message = "收件人公司名不能为空")
    private String destCompanyName;

    @ApiModelProperty(name = "destStreet" , value = "收件人街道")
    //@NotEmpty(message = "收件人街道不能为空")
    private String destStreet;

    @ApiModelProperty(name = "destAddress1" , value = "收件人地址1", required = true)
    @NotEmpty(message = "收件人地址1不能为空")
    private String destAddress1;

    @ApiModelProperty(name = "destAddress2" , value = "收件人地址2" )
    //@NotEmpty(message = "收件人地址2不能为空")
    private String destAddress2;

    @ApiModelProperty(name = "destAddress3" , value = "收件人地址3")
    //@NotEmpty(message = "收件人地址3不能为空")
    private String destAddress3;

    private static final long serialVersionUID=1L;


    @Override
    protected Serializable pkVal() {
        return this.orderId;
    }

}
