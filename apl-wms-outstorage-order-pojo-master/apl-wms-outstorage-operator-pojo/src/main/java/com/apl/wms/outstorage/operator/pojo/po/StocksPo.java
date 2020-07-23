package com.apl.wms.outstorage.operator.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import java.io.Serializable;

/**
 * <p>
 * 库存
 * </p>
 *
 * @author cy
 * @since 2019-12-12
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("stocks")
@ApiModel(value="StocksPo对象", description="库存")
public class StocksPo extends Model<StocksPo> {

    private static final long serialVersionUID=1L;

    @TableId(value = "id", type = IdType.AUTO)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty(name = "whId" , value = "仓库id" , required = true)
    @Min(value = 0 , message = "仓库id不能小于1")
    private Long whId;

    @ApiModelProperty(name = "commodityId" , value = "商品id" , required = true)
    @Min(value = 0 , message = "商品id不能小于1")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long commodityId;

    @ApiModelProperty(name = "availableCount" , value = "商品可售库存" , required = true)
    @Min(value = 0 , message = "可售库存 不能小于0")
    private Integer availableCount;

    @ApiModelProperty(name = "realityCount" , value = "商品实际库存" , required = true)
    @Min(value = 0 , message = "实际库存 不能小于0")
    private Integer realityCount;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
