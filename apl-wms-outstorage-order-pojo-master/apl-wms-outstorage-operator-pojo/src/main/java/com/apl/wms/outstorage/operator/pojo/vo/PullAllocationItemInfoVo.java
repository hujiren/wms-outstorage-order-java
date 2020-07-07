package com.apl.wms.outstorage.operator.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.Min;

import com.baomidou.mybatisplus.annotation.TableName;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 商品下架 持久化对象
 * </p>
 *
 * @author cy
 * @since 2020-06-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("pull_allocation_item")
@ApiModel(value = "商品下架 持久化对象", description = "商品下架 持久化对象")
public class PullAllocationItemInfoVo implements Serializable {


    @TableId(value = "id", type = IdType.UUID)
    private Long id;

    @ApiModelProperty(name = "batchId", value = "拣货批次id", required = true)
    @NotNull(message = "拣货批次id不能为空")
    @Min(value = 0, message = "拣货批次id不不合法")
    private Long batchId;

    @ApiModelProperty(name = "outOrderId", value = "出库订单id", required = true)
    @NotNull(message = "出库订单id不能为空")
    @Min(value = 0, message = "出库订单id不不合法")
    private Long outOrderId;

    @ApiModelProperty(name = "commodityId", value = "商品id", required = true)
    @NotNull(message = "商品id不能为空")
    @Min(value = 0, message = "商品id不不合法")
    private Long commodityId;

    @ApiModelProperty(name = "storageLocalId", value = "库位id", required = true)
    @NotNull(message = "库位id不能为空")
    @Min(value = 0, message = "库位id不不合法")
    private Long storageLocalId;

    @ApiModelProperty(name = "pullQty", value = "拣货数量", required = true)
    @NotNull(message = "拣货数量不能为空")
    @Min(value = 0, message = "拣货数量不合法")
    private Integer allocationQty;



    private static final long serialVersionUID = 1L;


}
