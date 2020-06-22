package com.apl.wms.outstorage.order.lib.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
@ApiModel(value="商品仓库库存 和 库位库存相关实体", description="商品仓库库存 和 库位库存相关实体")
public class CheckOrderStockDetailsVo implements Serializable {

private static final long serialVersionUID=1L;



    @ApiModelProperty(name = "orderId" , value = "盘点订单id")
    private Long orderId;

    @ApiModelProperty(name = "whId" , value = "仓库id")
    private Long whId;

    @ApiModelProperty(name = "commodityId" , value = "商品id")
    private Long commodityId;


    @ApiModelProperty(name = "allStockCount" , value = "仓库总库存")
    private Integer allStockCount;


    @ApiModelProperty(name = "storageLocalStocks" , value = "商品对应的库位库存列表")
    private List<StorageLocalStock> storageLocalStocks;



}
