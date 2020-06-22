package com.apl.wms.outstorage.order.lib.pojo.bo;

import lombok.Data;
import java.io.Serializable;

@Data
public class SyncOutOrderCommodityItemApiBo implements Serializable {

    private String sku;

    private Integer qty;
}
