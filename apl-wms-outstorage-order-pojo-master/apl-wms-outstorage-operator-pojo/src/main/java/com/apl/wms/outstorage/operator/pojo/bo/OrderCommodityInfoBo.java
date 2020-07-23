package com.apl.wms.outstorage.operator.pojo.bo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author hjr start
 * @date 2020/7/23 - 16:08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OrderCommodityInfoBo implements Serializable {

    @ApiModelProperty(value = "订单id")
    private Long orderId;

    @ApiModelProperty(value = "商品id")
    private Long CommodityId;

    @ApiModelProperty(value = "订单号")
    private String orderSn;


}
