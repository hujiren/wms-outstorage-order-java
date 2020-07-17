package com.apl.wms.outstorage.operator.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author hjr start
 * @date 2020/7/17 - 10:08
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OutOrderPickListVo implements Serializable {


    @ApiModelProperty(value = "订单Id", notes = "订单id")
    private Long orderId;

    @ApiModelProperty(value = "订单号", notes = "订单号")
    private String orderSn;

    @ApiModelProperty(value = "订单状态", notes = "订单状态")
    private Integer orderStatus;

    @ApiModelProperty(value = "订单创建日期", notes = "订单创建日期")
    private Timestamp crTime;

    @ApiModelProperty(value = "订单拣货状态", notes = "订单拣货状态")
    private Integer pullStatus;

    @ApiModelProperty(value = "客户id", notes = "客户id")
    private Long customerId;

    @ApiModelProperty(value = "客户姓名", notes = "客户姓名")
    private String customerName;

    @ApiModelProperty(value = "拣货员id", notes = "拣货员id")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long pullOperatorId;

    @ApiModelProperty(value = "订单来源", notes = "订单来源")
    private Integer orderFrom;

}
