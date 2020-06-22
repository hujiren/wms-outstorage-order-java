package com.apl.wms.outstorage.order.lib.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
public class OutOrderCommodityItemUpdDto implements Serializable {

    @ApiModelProperty(name = "itemId" , value = "商品项目id")
    private Long itemId;

    @ApiModelProperty(name = "commodityId" , value = "商品id" , required = true)
    @NotNull(message = "商品id不能为空")
    @Min(value = 0 , message = "商品id不合法")
    private Long commodityId;

    @ApiModelProperty(name = "orderQty" , value = "商品数量" , required = true)
    @NotNull(message = "商品数量不能为空")
    @Min(value = 0 , message = "商品数量不合法")
    private Integer orderQty;

    @ApiModelProperty(name = "CommoditySpec" , value = "商品规格")
    private String CommoditySpec;
}
