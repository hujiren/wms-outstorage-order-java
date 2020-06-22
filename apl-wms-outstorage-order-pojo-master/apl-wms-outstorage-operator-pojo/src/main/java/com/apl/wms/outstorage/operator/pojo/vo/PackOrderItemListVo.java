package com.apl.wms.outstorage.operator.pojo.vo;

import com.apl.wms.outstorage.order.pojo.vo.OrderItemListVo;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.List;

@Data
public class PackOrderItemListVo {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long batchId;

    private String batchSn;

    List<OrderItemListVo> orderItemListVos;

}
