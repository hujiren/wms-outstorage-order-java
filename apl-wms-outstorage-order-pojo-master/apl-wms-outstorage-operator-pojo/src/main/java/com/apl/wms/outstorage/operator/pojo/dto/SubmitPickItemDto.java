package com.apl.wms.outstorage.operator.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@ApiModel(value = "拣货提交实体")
public class SubmitPickItemDto {

    @ApiModelProperty(name = "batchId" , notes = "批次id" , required = true)
    @NotNull(message = "批次Id不能为空")
    private Long batchId;

    private List<PullBatchCommodityDto> pullBatchCommodityDto;

    @Data
    public static class PullBatchCommodityDto {

        @ApiModelProperty(name = "pullBatchStorageLocalIdsList" , notes = "库位及对应拣货数量" , required = true)
        @NotNull(message = "库位及对应拣货数量")
        @Min(value = 0, message = "库位对应拣货数量不能小于0")
        private List<PullBatchStorageLocalIds> pullBatchStorageLocalIdsList;

        @ApiModelProperty(name = "commodityId" , notes = "商品id" , required = true)
        @NotNull(message = "商品id不能为空")
        @Min(value = 0, message = "订单id不能小于0")
        private Long commodityId;

        @ApiModelProperty(name = "pullQty" , notes = "拣货数量" , required = true)
        @NotNull(message = "拣货数量不能为空")
        @Min(value = 0, message = "拣货数量不能小于0")
        private Integer pullQty;

    }

    @Data
    public static class PullBatchStorageLocalIds{

        @ApiModelProperty(name = "storageLocalId" , notes = "库位id" , required = true)
        @NotNull(message = "库位Id")
        @Min(value = 0, message = "库位id")
        private Long storageLocalId;

        @ApiModelProperty(name = "storageLocalPullQty" , notes = "库位拣货数量" , required = true)
        @NotNull(message = "库位拣货数量")
        @Min(value = 0, message = "库位拣货数量")
        private Integer storageLocalPullQty;

    }

}
