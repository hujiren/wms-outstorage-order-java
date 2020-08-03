package com.apl.wms.outstorage.order.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class OutOrderMainDto {

    @ApiModelProperty(name = "orderId" , value = "订单id", required = true)
    private Long orderId;

    @ApiModelProperty(name = "storeId" , value = "店铺id", required = true)
    private Long storeId;

    @ApiModelProperty(name = "customerId" , value = "客户id", required = true)
    @NotNull(message = "订单不能为空")
    @Min(value = 1, message = "客户id值不合法")
    private Long customerId;

    @ApiModelProperty(name = "whId" , value = "仓库id")
    @NotNull(message = "仓库不能为空")
    @Min(value = 1, message = "仓库id值不合法")
    private Long whId;

    @ApiModelProperty(name = "customerNo" , value = "客户编号", hidden = true)
    private String customerNo;

    @ApiModelProperty(name = "ecPlatformCode" , value = "电商平台", hidden = true)
    private String ecPlatformCode;

    @ApiModelProperty(name = "referenceSn" , value = "参考单号", hidden = true)
    private String referenceSn;

    @ApiModelProperty(name = "orderFrom" , value = "订单来源 1自动同步平台订单  2手动下单", hidden = true)
    private Integer orderFrom;

    @ApiModelProperty(hidden = true)
    private Integer pullStatus;

    @ApiModelProperty(hidden = true)
    private  Long innerOrgId;
}
