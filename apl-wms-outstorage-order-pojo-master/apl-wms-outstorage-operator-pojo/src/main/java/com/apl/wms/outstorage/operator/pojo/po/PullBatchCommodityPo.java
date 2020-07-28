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
import javax.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * @author hjr start
 * @date 2020/7/22 - 18:14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("pull_batch_commodity")
@ApiModel(value="批次商品信息对象", description="批次商品信息对象")
public class PullBatchCommodityPo extends Model<PullBatchCommodityPo> {

    @TableId(value = "id", type = IdType.INPUT)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty(name = "storageLocalIds" , notes = "库位ids" , required = true)
    @NotNull(message = "订单id不能为空")
    @Min(value = 0, message = "订单id不能小于0")
    private Long storageLocalId;

    @ApiModelProperty(name = "batchId" , notes = "批次Id" , required = true)
    @NotNull(message = "批次Id不能为空")
    @Min(value = 0, message = "批次id不能小于0")
    private Long batchId;

    @ApiModelProperty(name = "commodityId" , notes = "商品id" , required = true)
    @NotNull(message = "订单id不能为空")
    @Min(value = 0, message = "订单id不能小于0")
    private Long commodityId;

    @ApiModelProperty(name = "pullQty" , notes = "拣货数量" , required = true)
    @NotNull(message = "拣货数量")
    @Min(value = 0, message = "拣货数量不能小于0")
    private Integer pullQty;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
