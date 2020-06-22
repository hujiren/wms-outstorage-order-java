package com.apl.wms.outstorage.order.lib.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StorageLocalStock {

    @ApiModelProperty(name = "id", value = "库位库存id")
    private Long id;

    @ApiModelProperty(name = "commodityId", value = "商品id")
    private Long commodityId;

    @ApiModelProperty(name = "storageLocalId", value = "库位id")
    private Long storageLocalId;

    @ApiModelProperty(name = "storageLocalName", value = "库位名称")
    private String storageLocalName;

    @ApiModelProperty(name = "realityCount", value = "库位库存")
    private Integer storageLocalCount;

}
