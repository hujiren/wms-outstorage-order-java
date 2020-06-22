package com.apl.wms.outstorage.order.lib.vo;

import lombok.Data;

import java.util.List;

@Data
public class OrderCountVo {

    private Long id;

    private String orderSn;

    List<OrderItem> orderItems;

    @Data
    public static class OrderItem {

        private Long id;

        // 商品id
        private Long commodityId;

        // 商品 总数
        private Integer orderQty;


    }
}
