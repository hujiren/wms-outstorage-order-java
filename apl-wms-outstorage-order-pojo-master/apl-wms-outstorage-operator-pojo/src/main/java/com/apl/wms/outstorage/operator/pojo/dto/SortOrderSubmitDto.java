package com.apl.wms.outstorage.operator.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "提交分拣信息实体" , description = "提交分拣信息实体")
public class SortOrderSubmitDto {

    @ApiModelProperty(name = "batchId", value = "批次id")
    private Long batchId;

    List<Order> orders;

    /**
     * @Author: CY
     * @Desc: 订单信息
     * @Date: 2020/6/15 11:57
     */
    @Data
    @ApiModel
    public static class Order {

        @ApiModelProperty(name = "orderId", value = "订单id")
        private Long orderId;

        @ApiModelProperty(name = "orderSn", value = "订单编号")
        private String orderSn;

        List<CommodityItem> items;

        /**
         * @Desc: 子订单信息
         * @Author: CY
         * @Date: 2020/6/15 11:57
         */
        @Data
        @ApiModel
        public static class CommodityItem {

            @ApiModelProperty(name = "itemId", value = "订单子项id")
            private Long itemId;

            @ApiModelProperty(name = "commodityId", value = "商品id")
            private Long commodityId;

            @ApiModelProperty(name = "qty", value = "订单数量")
            private Integer qty;


        }
    }



}
