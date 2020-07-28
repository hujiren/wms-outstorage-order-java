package com.apl.wms.outstorage.operator.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 包装材料 详细实体
 * </p>
 *
 * @author cy
 * @since 2019-12-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PackagingMaterialsInfoVo implements Serializable {

private static final long serialVersionUID=1L;

    @ApiModelProperty(name = "id", value = "包装材料Id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty(name = "sku", value = "包装材料SKU")
    private String sku;

    @ApiModelProperty(name = "commodityId", value = "商品Id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long commodityId;

    @ApiModelProperty(name = "commodityName", value = "商品名称")
    private String commodityName;

    @ApiModelProperty(name = "commodityNameEn", value = "商品英文名")
    private String commodityNameEn;

    @ApiModelProperty(name = "specName", value = "规格名称")
    private String specName;

    @ApiModelProperty(name = "specNameEn", value = "规格英文名")
    private String specNameEn;

    @ApiModelProperty(name = "unitCode", value = "单位code")
    private String unitCode;

    @ApiModelProperty(name = "imgUrl", value = "商品主图")
    private String imgUrl;

    @ApiModelProperty(name = "textureName", value = "材质")
    private String textureName;

    @ApiModelProperty(name = "textureNameEn", value = "材质英文名")
    private String textureNameEn;

    @ApiModelProperty(name = "colorName", value = "颜色名称")
    private String colorName;

    @ApiModelProperty(name = "colorNameEn", value = "颜色英文名")
    private String colorNameEn;

    @ApiModelProperty(name = "useWay", value = "用途")
    private String useWay;

    @ApiModelProperty(name = "useWayEn", value = "用途英文")
    private String useWayEn;

    @ApiModelProperty(name = "remark", value = "备注")
    private String remark;

    @ApiModelProperty(name = "capacity", value = "包装材料容量")
    private Integer capacity;

    @ApiModelProperty(name = "count", value = "所需包装材料数量")
    private Integer count;


}
