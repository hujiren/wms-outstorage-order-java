package com.apl.wms.outstorage.order.lib.pojo.bo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author hjr start 分配仓库时需要的商品信息对象
 * @date 2020/7/4 - 13:49
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class AllocationWarehouseOrderCommodityBo implements Serializable {

    private static final long serialVersionUID=1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orderId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long commodityId;//商品id

    private Integer orderQty;//商品下单数量

}
