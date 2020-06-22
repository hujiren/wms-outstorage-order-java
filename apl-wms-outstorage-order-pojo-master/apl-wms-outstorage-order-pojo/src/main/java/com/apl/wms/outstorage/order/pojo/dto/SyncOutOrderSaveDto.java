package com.apl.wms.outstorage.order.pojo.dto;

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
import java.sql.Timestamp;

/**
 * <p>
 * 同步平台订单 持久化对象
 * </p>
 *
 * @author arran
 * @since 2019-12-25
 */

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sync_out_order")
@ApiModel(value="同步平台订单保存", description="同步平台订单保存")
public class SyncOutOrderSaveDto extends Model<SyncOutOrderSaveDto> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(name = "customerId" , value = "客户id" , required = true)
    @NotNull(message = "客户id不能为空")
    @Min(value = 1 , message = "客户id不合法")
    private Long customerId;

    @ApiModelProperty(name = "storeId" , value = "店铺id" , required = true)
    @NotNull(message = "店铺id不能为空")
    @Min(value = 1 , message = "店铺id不合法")
    private Long storeId;

    @ApiModelProperty(name = "ecPlatformCode" , value = "电商平台CODE" , required = true)
    @NotEmpty(message = "电商平台CODE不能为空")
    private String ecPlatformCode;

    @ApiModelProperty(name = "orderStartTime" , value = "订单起始时间戳")
    @NotNull(message = "订单起始时间戳不能为空")
    @Min(value = 1 , message = "订单起始时间戳不合法")
    private Long orderStartTime;

    @ApiModelProperty(name = "orderEndTime" , value = "订单截止时间戳")
    @NotNull(message = "订单截止时间戳不能为空")
    @Min(value = 1 , message = "订单截止时间戳不合法")
    private Long orderEndTime;

    @ApiModelProperty(name = "status" , value = "状态  1等待同步  2正在同步  3已完成同步   4同步异常   5暂停同步   6作废" , hidden = true)
    private Integer status;

    private static final long serialVersionUID=1L;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }


    public Timestamp getOrderStartTime() {
        if(this.orderStartTime!=null && this.orderStartTime>1)
            return new Timestamp(this.orderStartTime);

        return null;
    }

    public Long getOrderStartTimeMillis() {
        return this.orderStartTime;
    }


    public Timestamp getOrderEndTime() {
        if(this.orderEndTime!=null && this.orderEndTime>1)
            return new Timestamp(this.orderEndTime);

        return null;
    }


    public Long getOrderEndTimeMillis() {
        return this.orderEndTime;
    }
}
