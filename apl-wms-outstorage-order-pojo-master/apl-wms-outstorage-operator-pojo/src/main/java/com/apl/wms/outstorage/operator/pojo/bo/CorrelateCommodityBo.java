package com.apl.wms.outstorage.operator.pojo.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author hjr start
 * @date 2020/7/25 - 9:57
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CorrelateCommodityBo implements Serializable {

    @ApiModelProperty(value = "商品id")
    private Long commodityId;

    @ApiModelProperty(value = "订单id")
    private Long  orderId;

    @ApiModelProperty(value = "订单号")
    private String orderSn;
}
