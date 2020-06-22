package com.apl.wms.outstorage.order.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.List;

@Data
public class OrderItemListVo {

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    //订单编号
    private String orderSn;
    //物流单号
    private String referenceSn;

    //物流方式  1快递  2空运  3海运
    private Integer toDescLogistics;

    //拣货状态
    private Integer pullStatus;

    private List<OutOrderCommodityItemInfoVo> orderItemInfos;

}
