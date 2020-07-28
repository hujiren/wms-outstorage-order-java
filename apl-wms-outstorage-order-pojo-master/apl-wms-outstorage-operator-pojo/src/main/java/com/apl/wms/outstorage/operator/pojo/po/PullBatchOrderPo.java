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
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <p>
 * 拣货批次 持久化对象
 * </p>
 *
 * @author cy
 * @since 2020-06-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("pull_batch_order")
@ApiModel(value="批次订单 持久化对象", description="批次订单 持久化对象")
public class PullBatchOrderPo extends Model<PullBatchOrderPo> {


    @TableId(value = "id", type = IdType.INPUT)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty(name = "batchId" , value = "批次id" , required = true)
    @NotEmpty(message = "批次id不能为空")
    private Long batchId;

    @ApiModelProperty(name = "orderId" , value = "订单Id" , required = true)
    @NotNull(message = "订单Id不能为空")
    @Min(value = 0 , message = "订单id不不合法")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long orderId;

    private static final long serialVersionUID=1L;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
