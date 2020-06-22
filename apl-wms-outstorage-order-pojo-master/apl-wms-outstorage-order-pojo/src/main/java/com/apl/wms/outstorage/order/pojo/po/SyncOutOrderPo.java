package com.apl.wms.outstorage.order.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import com.baomidou.mybatisplus.annotation.TableName;
import java.sql.Timestamp;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
@ApiModel(value="同步平台订单 持久化对象", description="同步平台订单 持久化对象")
public class SyncOutOrderPo extends Model<SyncOutOrderPo> {


    @TableId(value = "id", type = IdType.INPUT)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    //客户id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    //店铺id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long storeId;

    //电商平台CODE
    private String ecPlatformCode;

    //订单起始时间
    private Timestamp orderStartTime;

    //订单截止时间
    private Timestamp orderEndTime;


    //状态  1正在同步  2已完成   3暂停   4取消   5异常
    private Integer status;

    //创建时间
    private Timestamp crTime;


    private static final long serialVersionUID=1L;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
