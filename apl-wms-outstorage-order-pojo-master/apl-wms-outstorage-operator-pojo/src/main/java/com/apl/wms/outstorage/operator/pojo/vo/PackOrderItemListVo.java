package com.apl.wms.outstorage.operator.pojo.vo;

import com.apl.wms.outstorage.order.pojo.vo.OrderItemListVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

@Data
public class PackOrderItemListVo {


    @ApiModelProperty("批次id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long batchId;

    @ApiModelProperty("拣货状态")
    private Integer pullStatus;

    @ApiModelProperty("批次号")
    private String batchSn;

    @ApiModelProperty("订单商品条目")
    List<OrderItemListVo> orderItemListVos;

}
