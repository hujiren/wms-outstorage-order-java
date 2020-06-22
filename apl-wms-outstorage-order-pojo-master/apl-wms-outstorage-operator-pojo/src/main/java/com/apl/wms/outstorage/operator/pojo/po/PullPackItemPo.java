package com.apl.wms.outstorage.operator.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 出库订单打包明细 持久化对象
 * </p>
 *
 * @author cy
 * @since 2020-06-13
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("pull_pack_item")
@ApiModel(value="出库订单打包明细 持久化对象", description="出库订单打包明细 持久化对象")
public class PullPackItemPo extends Model<PullPackItemPo> {


    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @ApiModelProperty(name = "outOrderId" , value = "出库订单id" , required = true)
    @NotNull(message = "出库订单id不能为空")
    @Min(value = 0 , message = "出库订单id不不合法")
    private Long outOrderId;

    @ApiModelProperty(name = "subSn" , value = "子单号" , required = true)
    @NotEmpty(message = "子单号不能为空")
    private String subSn;

    @ApiModelProperty(name = "gw" , value = "毛重" , required = true)
    @NotNull(message = "毛重不能为空")
    @Min(value = 0 , message = "毛重不合法")
    private BigDecimal gw;

    @ApiModelProperty(name = "sizeLength" , value = "长" , required = true)
    @NotNull(message = "长不能为空")
    @Min(value = 0 , message = "长不合法")
    private BigDecimal sizeLength;

    @ApiModelProperty(name = "sizeWidth" , value = "宽" , required = true)
    @NotNull(message = "宽不能为空")
    @Min(value = 0 , message = "宽不合法")
    private BigDecimal sizeWidth;

    @ApiModelProperty(name = "sizeHeight" , value = "高" , required = true)
    @NotNull(message = "高不能为空")
    @Min(value = 0 , message = "高不合法")
    private BigDecimal sizeHeight;

    @ApiModelProperty(name = "volume" , value = "体积" , required = true)
    @NotNull(message = "体积不能为空")
    @Min(value = 0 , message = "体积不合法")
    private BigDecimal volume;

    private static final long serialVersionUID=1L;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
