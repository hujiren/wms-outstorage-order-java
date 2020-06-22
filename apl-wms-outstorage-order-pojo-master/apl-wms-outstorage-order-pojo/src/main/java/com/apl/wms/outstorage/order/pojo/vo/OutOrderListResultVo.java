package com.apl.wms.outstorage.order.pojo.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 入库订单
 * </p>
 *
 * @author cy
 * @since 2019-12-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OutOrderListResultVo implements Serializable {


private static final long serialVersionUID=1L;

    private long total;

    private long size;

    private long current;

    //订单列表
    private List<OutOrderListVo> outOrders;

   //订单商品
    private Map<String, List<OutOrderCommodityItemInfoVo>> commodityItems;


}
