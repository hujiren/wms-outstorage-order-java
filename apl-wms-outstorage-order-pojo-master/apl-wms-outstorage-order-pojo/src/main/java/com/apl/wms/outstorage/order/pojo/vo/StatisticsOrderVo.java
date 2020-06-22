package com.apl.wms.outstorage.order.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StatisticsOrderVo {

    //订单类型
    @ApiModelProperty(name = "orderType" , value = "订单类型 1创建中  2创建异常  3新建  4已发货  5完成   6取消 7问题订单 ")
    private Integer orderType;

    //订单数量
    private Integer orderCount;

}
