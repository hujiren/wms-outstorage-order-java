package com.apl.wms.outstorage.order.lib.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="出库订单-保存目的地信息", description="出库订单-保存目的地信息")
public class OutOrderDestUpdDto implements Serializable {

    @ApiModelProperty(name = "orderId" , value = "订单id" , required = true)
    @NotNull(message = "订单id不能为空")
    @Min(value = 1 , message = "订单id不合法")
    private Long orderId;

    @ApiModelProperty(name = "ecPlatformCode" , value = "电商平台")
    private String ecPlatformCode;

    @ApiModelProperty(name = "referenceSn" , value = "参考单号")
    private String referenceSn;

    @ApiModelProperty(name = "storeId" , value = "店铺id" , required = true)
    @NotNull(message = "店铺id不能为空")
    @Min(value=0, message = "店铺id不能小于0")
    private Long storeId;

    @ApiModelProperty(name = "isInsure" , value = "是否投保 1投保  2不投保")
    @NotNull(message = "是否投保不能为空")
    @Range(min = 1, max = 2, message = "是否投保值不对")
    private Integer isInsure;

    @ApiModelProperty(name = "insureAmount" , value = "投保金额" )
    @Min(value=0, message = "投保金额不能小于0")
    private BigDecimal insureAmount;

    @ApiModelProperty(name = "toDescLogistics" , value = "物流方式:   1快递;   2空运;   3海运")
    @NotNull(message = "物流方式不能为空")
    @Range(min = 1, max = 3, message = "物流方式不对")
    private Integer toDescLogistics;

    @ApiModelProperty(name = "toDescCarrier" , value = "航线")
    private String toDescCarrier;

    @ApiModelProperty(name = "remark" , value = "备注")
    private String remark;

    @ApiModelProperty(name = "destContact" , value = "收件人" , required = true )
    @NotEmpty(message = "收件人不能为空")
    private String destContact;

    @ApiModelProperty(name = "destTel" , value = "收件人联系电话" , required = true)
    @NotEmpty(message = "收件人联系电话不能为空")
    private String destTel;

    @ApiModelProperty(name = "destPhone" , value = "收件人手机号" , required = true )
    @NotEmpty(message = "目的地-收件人手机号不能为空")
    private String destPhone;

    @ApiModelProperty(name = "destEmail" , value = "收件人邮箱" )
    //@NotEmpty(message = "目的地-收件人邮箱不能为空")
    private String destEmail;

    @ApiModelProperty(name = "destCountryCode" , value = "目的地国家简码" , required = true )
    @NotEmpty(message = "目的地-国家简码不能为空")
    private String destCountryCode;

    @ApiModelProperty(name = "destState" , value = "目的地-州" )
    //@NotEmpty(message = "目的地-州")
    private String destState;

    @ApiModelProperty(name = "destZipCode" , value = "目的地-邮编" )
    //@NotEmpty(message = "目的地-邮编不能为空")
    private String destZipCode;

    @ApiModelProperty(name = "destCity" , value = "目的地-城市" , required = true)
    //@NotEmpty(message = "目的地-城市不能为空")
    private String destCity;

    @ApiModelProperty(name = "destCompanyName" , value = "目的地-公司名")
    //@NotEmpty(message = "目的地-公司名不能为空")
    private String destCompanyName;

    @ApiModelProperty(name = "destStreet" , value = "目的地-街道")
    //@NotEmpty(message = "目的地-街道不能为空")
    private String destStreet;

    @ApiModelProperty(name = "destAddress1" , value = "目的地-地址1行" , required = true)
    @NotEmpty(message = "目的地-地址1不能为空")
    private String destAddress1;

    @ApiModelProperty(name = "destAddress2" , value = "目的地-地址2行")
    //@NotEmpty(message = "目的地-地址2不能为空")
    private String destAddress2;

    @ApiModelProperty(name = "destAddress3" , value = "目的地-地址3行")
    //@NotEmpty(message = "目的地-地址3不能为空")
    private String destAddress3;

}
