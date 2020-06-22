package com.apl.wms.outstorage.order.lib.pojo.bo;

import com.apl.wms.outstorage.order.lib.pojo.dto.OutOrderCommodityItemUpdDto;
import com.apl.wms.outstorage.order.lib.pojo.dto.OutOrderDestUpdDto;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SyncOutOrderBo implements Serializable {

    private Long id;

    //订单信息
    private OutOrderDestUpdDto destDto;

    //商品信息
    private List<OutOrderCommodityItemUpdDto> commodityItems;

}
