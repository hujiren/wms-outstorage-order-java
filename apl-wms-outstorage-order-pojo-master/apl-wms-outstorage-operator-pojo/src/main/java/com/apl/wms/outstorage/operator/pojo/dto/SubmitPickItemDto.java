package com.apl.wms.outstorage.operator.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * @author hjr start
 * @date 2020/7/24 - 9:54
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="提交拣货信息对象", description="提交拣货信息对象")
public class SubmitPickItemDto {

    @NotNull(message = "商品id不能为空")
    @ApiModelProperty(name = "commodityId", value = "商品id", required = true)
    @Range(min = 0, message = "商品Id不能小于0")
    private Long commodityId;

    @NotNull(message = "库位出库数量不能为空")
    @ApiModelProperty(name = "pullQty", value = "单库位出库数量")
    @Range(min = 0, message = "出库数量不能小于0")
    private Integer pullQty;

    @NotNull(message = "库位id不能为空")
    @ApiModelProperty(name = "storageLocalId", value = "库位id")
    @Range(min = 0, message = "库位Id不能小于0")
    private Long storageLocalId;

}
