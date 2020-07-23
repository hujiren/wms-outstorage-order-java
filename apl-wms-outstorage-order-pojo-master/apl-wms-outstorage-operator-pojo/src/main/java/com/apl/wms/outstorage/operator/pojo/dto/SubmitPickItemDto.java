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

        @ApiModelProperty(name = "storageLocalIds" , notes = "库位ids" , required = true)
        @NotNull(message = "订单id不能为空")
        @Min(value = 0, message = "订单id不能小于0")
        private List<Long> storageLocalIds;

        @ApiModelProperty(name = "commodityId" , notes = "商品id" , required = true)
        @NotNull(message = "订单id不能为空")
        @Min(value = 0, message = "订单id不能小于0")
        private Long commodityId;

        @ApiModelProperty(name = "pullQty" , notes = "拣货数量" , required = true)
        @NotNull(message = "订单id不能为空")
        @Min(value = 0, message = "订单id不能小于0")
        private Integer pullQty;


    }

}
