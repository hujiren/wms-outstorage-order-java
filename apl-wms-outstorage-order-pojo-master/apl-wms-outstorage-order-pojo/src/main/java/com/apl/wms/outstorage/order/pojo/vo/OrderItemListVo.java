package com.apl.wms.outstorage.order.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class OrderItemListVo {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty("订单编号")
    private String orderSn;

    @ApiModelProperty("物流单号")
    private String referenceSn;

    @ApiModelProperty("物流方式  1 快递  2 空运  3 海运")
    private Integer toDescLogistics;

    @ApiModelProperty("拣货状态")
    private Integer pullStatus;

    @ApiModelProperty("订单商品条目")
    private List<OutOrderCommodityItemInfoVo> orderItemInfos;

}
