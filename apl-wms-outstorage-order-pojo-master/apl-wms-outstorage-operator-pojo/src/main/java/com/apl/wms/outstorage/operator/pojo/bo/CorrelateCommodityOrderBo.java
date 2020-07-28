package com.apl.wms.outstorage.operator.pojo.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hjr start
 * @date 2020/7/25 - 11:56
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CorrelateCommodityOrderBo {

    @ApiModelProperty(value = "商品id")
    private Long commodityId;

    @ApiModelProperty(value = "订单id")
    private Long  orderId;

    @ApiModelProperty(value = "订单号")
    private String orderSn;

    @ApiModelProperty(value = "商品出库数量")
    private Integer orderQty;
}
