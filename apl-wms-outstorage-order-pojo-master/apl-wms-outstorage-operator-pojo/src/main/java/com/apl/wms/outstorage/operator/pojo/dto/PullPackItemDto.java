package com.apl.wms.outstorage.operator.pojo.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author hjr start
 * @date 2020/7/29 - 11:43
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PullPackItemDto {

    @NotNull(message = "包装尺寸id")
    @ApiModelProperty(name = "id", value = "包装尺寸id", hidden = true)
    @Range(min = 0, message = "包装尺寸id不能小于0")
    private Long id;

    @NotNull(message = "出库订单id不能为空")
    @ApiModelProperty(name = "outOrderId", value = "出库订单id", required = true)
    @Range(min = 0, message = "出库订单id不能小于0")
    private Long outOrderId;

    @NotBlank(message = "物流子单号不能为空")
    @ApiModelProperty(name = "subSn", value = "物流子单号", required = true)
    private String subSn;

    @NotNull(message = "包装商品实重不能为空")
    @ApiModelProperty(name = "gw", value = "包装商品实重", required = true)
    @Range(min = 0, message = "包装商品实重不能小于0")
    private Double gw;

    @NotNull(message = "包装尺寸长度不能为空")
    @ApiModelProperty(name = "sizeLength", value = "包装尺寸长度", required = true)
    @Range(min = 0, message = "包装尺寸长度不能小于0")
    private Double sizeLength;

    @NotNull(message = "包装尺寸宽度不能为空")
    @ApiModelProperty(name = "sizeWidth", value = "包装尺寸宽度", required = true)
    @Range(min = 0, message = "包装尺寸宽度不能小于0")
    private Double sizeWidth;

    @NotNull(message = "包装尺寸高度不能为空")
    @ApiModelProperty(name = "sizeHeight", value = "包装尺寸高度", required = true)
    @Range(min = 0, message = "包装尺寸高度不能小于0")
    private Double sizeHeight;

    @NotNull(message = "包装尺寸体积不能为空")
    @ApiModelProperty(name = "volume", value = "包装尺寸体积", required = true)
    @Range(min = 0, message = "包装尺寸体积不能小于0")
    private Double volume;
}
