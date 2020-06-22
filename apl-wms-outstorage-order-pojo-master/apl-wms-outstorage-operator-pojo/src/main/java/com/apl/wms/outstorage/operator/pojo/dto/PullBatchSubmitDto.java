package com.apl.wms.outstorage.operator.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value = "拣货提交实体")
public class PullBatchSubmitDto {

    @ApiModelProperty(name = "batchId" , notes = "批次id" , required = true)
    private Long batchId;

    private List<CommodityCount> commodityCounts;

    @Data
    public static class CommodityCount {

        @ApiModelProperty(name = "orderId" , notes = "订单id" , required = true)
        private Long orderId;

        @ApiModelProperty(name = "storageLocalId" , notes = "库位id" , required = true)
        private Long storageLocalId;

        @ApiModelProperty(name = "commodityId" , notes = "商品id" , required = true)
        private Long commodityId;

        @ApiModelProperty(name = "totalCount" , notes = "拣货总数")
        private Integer totalCount;

        @ApiModelProperty(name = "submitCount" , notes = "提交总数" , required = true)
        private Integer submitCount;

    }

}
