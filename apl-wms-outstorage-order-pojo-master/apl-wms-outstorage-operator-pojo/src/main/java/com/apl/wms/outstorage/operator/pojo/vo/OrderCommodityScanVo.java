package com.apl.wms.outstorage.operator.pojo.vo;

import com.apl.wms.outstorage.order.pojo.vo.OutOrderCommodityItemInfoVo;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderListVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.List;
import java.util.Map;

/**
 * @author hjr start
 * @date 2020/7/28 - 9:59
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OrderCommodityScanVo {

    @ApiModelProperty(value = "订单信息列表", notes = "订单信息列表")
    List<OutOrderListVo> outOrderList;

    @ApiModelProperty(value = "商品信息列表", notes = "商品信息列表")
    Map<String, List<OutOrderCommodityItemInfoVo>> outOrderCommodityItemMap;
}
