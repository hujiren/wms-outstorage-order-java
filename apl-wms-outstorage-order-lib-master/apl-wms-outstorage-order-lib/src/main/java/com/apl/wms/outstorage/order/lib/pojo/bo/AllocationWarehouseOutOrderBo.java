package com.apl.wms.outstorage.order.lib.pojo.bo;

import com.apl.lib.security.SecurityUser;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author hjr start
 * @date 2020/7/4 - 13:53
 * 通过订单id查询到的商品信息列表及订单信息组成的对象
 *
 * 商品列表包含 订单id, 订单中包含的商品的(commodity_id), 该订单中的商品下单数量(order_qty) , 一个订单可能包含多个商品
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class AllocationWarehouseOutOrderBo implements Serializable {

    private SecurityUser securityUser;//安全对象用于使用消息队列调用时生成token

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orderId;//订单id

    private String orderSn;

    private Integer orderStatus;

    private Integer pullStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long whId;//仓库id

    private List<AllocationWarehouseOrderCommodityBo> allocationWarehouseOrderCommodityBoList;//分配仓库时需要的商品信息对象集合
}
