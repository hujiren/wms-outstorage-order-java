package com.apl.wms.outstorage.operator.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StatisticsOrderDto {


    @ApiModelProperty(name = "whId", value = "仓库id")
    private Long whId;

    @ApiModelProperty(name = "customerId", value = "客户id")
    private Long customerId;

    @ApiModelProperty(name = "orderSn", value = "订单号")
    private String orderSn;

    @ApiModelProperty(name = "commodityName", value = "品名")
    private String commodityName;

    @ApiModelProperty(name = "commoditySku", value = "SKU")
    private String commoditySku;

}
