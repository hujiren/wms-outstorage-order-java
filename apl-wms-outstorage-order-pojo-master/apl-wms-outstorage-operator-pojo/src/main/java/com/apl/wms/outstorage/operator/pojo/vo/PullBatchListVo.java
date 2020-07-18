package com.apl.wms.outstorage.operator.pojo.vo;

import com.baomidou.mybatisplus.annotation.IdType;

import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Min;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 拣货批次
 * </p>
 *
 * @author cy
 * @since 2020-06-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PullBatchListVo implements Serializable {


    @TableId(value = "id", type = IdType.UUID)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    @ApiModelProperty(name = "batchSn", value = "批次号", required = true)
    @NotEmpty(message = "批次号不能为空")
    private String batchSn;

    @ApiModelProperty(name = "pullOperatorId", value = "拣货员id", required = true)
    @NotNull(message = "拣货员id不能为空")
    @Min(value = 0, message = "拣货员id不不合法")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long pullOperatorId;

    @ApiModelProperty(name = "pullFinishTime", value = "拣货完成时间", required = true)
    private Timestamp pullFinishTime;

    @ApiModelProperty(name = "sortingOperatorId", value = "分拣员id", required = true)
    @NotNull(message = "分拣员id不能为空")
    @Min(value = 0, message = "分拣员id不不合法")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long sortingOperatorId;

    @ApiModelProperty(name = "sortingFinishTime", value = "分拣完成时间", required = true)
    private Timestamp sortingFinishTime;

    @ApiModelProperty(name = "pullStatus", value = "状态 3已分配批次    4拣货中  5已拣货    6分拣中   7已分拣", required = true)
    private Boolean pullStatus;

    @ApiModelProperty(name = "crTime", value = "创建时间", required = true)
    private Timestamp crTime;

    private static final long serialVersionUID = 1L;


}