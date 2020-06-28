package com.apl.wms.outstorage.order.pojo.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * <p>
 * 出库订单 持久化对象
 * </p>
 *
 * @author cy
 * @since 2020-01-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("out_order")
@ApiModel(value="出库订单 持久化对象", description="出库订单 持久化对象")
public class OutOrderPo extends Model<OutOrderPo> {

    @TableId(value = "id", type = IdType.INPUT)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    //客户id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long customerId;

    //出库订单号
    private String orderSn;

    //出库订单状态  1创建中  2创建异常  3新建   4已确认(已分配拣货中)  5拣货中  6已拣货    7已分拣   8已打包  9已发货  10完成   11取消
    private Integer orderStatus;

    //拣货状态  1库存未锁定 2库存已锁定  3未分配拣货员  4已分配拣货员 5已分配批次    6拣货中  7已拣货    8分拣中   9已分拣
    private Integer pullStatus;

    //订单来源  1电商   2手动下单
    private Integer orderFrom;

    //电商平台
    private String ecPlatformCode;

    //参考单号
    private String referenceSn;

    //店铺id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long storeId;

    //仓库id
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long whId;

    //拣货员id
    private Long pullOperatorId;

    //packOperatorId
    private Long packOperatorId;

    //出库总毛重
    private BigDecimal outGw;

    //出库总体积
    private BigDecimal outVolume;

    //备注
    private String remark;

    //是否问题件
    private Integer isWrong;

    //订单创建时间
    private Timestamp crTime;

    private static final long serialVersionUID=1L;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
