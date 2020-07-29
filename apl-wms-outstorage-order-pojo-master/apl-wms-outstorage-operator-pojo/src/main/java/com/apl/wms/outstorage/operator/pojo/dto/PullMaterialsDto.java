package com.apl.wms.outstorage.operator.pojo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * @author hjr start
 * @date 2020/7/29 - 10:42
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="打包信息提交", description="打包信息提交")
public class PullMaterialsDto {

    @NotNull(message = "包装信息id不能为空")
    @ApiModelProperty(name = "commodityId", value = "商品id", hidden = true)
    @Range(min = 0, message = "包装信息id不能小于0")
    private Long id;

    @NotNull(message = "订单id不能为空")
    @ApiModelProperty(name = "outOrderId", value = "订单id", required = true)
    @Range(min = 0, message = "订单id不能小于0")
    private Long outOrderId;

    @NotNull(message = "包装材料id不能为空")
    @ApiModelProperty(name = "materialsId", value = "包装材料id", required = true)
    @Range(min = 0, message = "包装材料id不能小于0")
    private Long materialsId;

    @NotNull(message = "包装材料数量不能为空")
    @ApiModelProperty(name = "qty", value = "包装材料数量", required = true)
    @Range(min = 0, message = "包装材料数量不能小于0")
    private Integer qty;
}
