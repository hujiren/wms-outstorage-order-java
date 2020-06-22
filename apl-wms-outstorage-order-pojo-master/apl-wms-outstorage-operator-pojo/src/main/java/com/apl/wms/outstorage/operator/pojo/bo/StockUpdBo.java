package com.apl.wms.outstorage.operator.pojo.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class StockUpdBo implements Serializable {

    @ApiModelProperty(name = "whId", value = "创建id")
    private Long whId;

    @ApiModelProperty(name = "commodityId", value = "商品id")
    private Long commodityId;

    @ApiModelProperty(name = "storageLocalId", value = "库位id")
    private Long storageLocalId;

    @ApiModelProperty(name = "qty", value = "数量")
    private Integer qty;


}
