package com.apl.wms.outstorage.operator.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Min;
import com.baomidou.mybatisplus.annotation.TableName;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
@TableName("pull_batch")
@ApiModel(value="拣货批次 持久化对象", description="拣货批次 持久化对象")
public class PullBatchPo extends Model<PullBatchPo> {


    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    @ApiModelProperty(name = "batchSn" , value = "批次号" , required = true)
    @NotEmpty(message = "批次号不能为空")
    private String batchSn;

    @ApiModelProperty(name = "pullOperatorId" , value = "拣货员id" , required = true)
    @NotNull(message = "拣货员id不能为空")
    @Min(value = 0 , message = "拣货员id不不合法")
    private Long pullOperatorId;

    @ApiModelProperty(name = "pullFinishTime" , value = "拣货完成时间" , required = true)
    private Timestamp pullFinishTime;

    @ApiModelProperty(name = "sortingOperatorId" , value = "分拣员id" , required = true)
    @NotNull(message = "分拣员id不能为空")
    @Min(value = 0 , message = "分拣员id不不合法")
    private Long sortingOperatorId;

    @ApiModelProperty(name = "sortingFinishTime" , value = "分拣完成时间" , required = true)
    private Timestamp sortingFinishTime;

    @ApiModelProperty(name = "pullStatus" , value = "状态  5开始拣货  6已拣货    7已分拣" , required = true)
    private Integer pullStatus;

    @ApiModelProperty(name = "crTime" , value = "创建时间" , required = true)
    private Timestamp crTime;

    private static final long serialVersionUID=1L;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
