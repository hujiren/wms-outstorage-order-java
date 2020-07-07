package com.apl.wms.outstorage.order.lib.pojo.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author hjr start
 * @date 2020/7/4 - 13:53
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class AllocationWarehouseOutOrderBo implements Serializable {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orderId;//订单id

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long whId;//仓库id

    private List<AllocationWarehouseOrderCommodityBo> allocationWarehouseOrderCommodityBoList;//分配仓库时需要的商品信息对象集合
}
