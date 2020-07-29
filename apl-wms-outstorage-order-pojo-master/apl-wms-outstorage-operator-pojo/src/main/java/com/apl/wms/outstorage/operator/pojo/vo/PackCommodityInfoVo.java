package com.apl.wms.outstorage.operator.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hjr start
 * @date 2020/7/28 - 17:16
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PackCommodityInfoVo {

    @ApiModelProperty("订单id")
    private Long orderId;

    @ApiModelProperty("商品id")
    private Long commodityId;

    @ApiModelProperty("商品SKU")
    private String commoditySku;

    @ApiModelProperty("商品名称")
    private String commodityName;

    @ApiModelProperty("商品主图")
    private String imgUrl;

    @ApiModelProperty("商品下单数量")
    private Integer orderQty;
}
