package com.apl.wms.outstorage.operator.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hjr start
 * @date 2020/7/28 - 16:40
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OutOrderAttachInfoVo {

    @ApiModelProperty("订单id")
    private Long id;

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("订单拣货状态")
    private Integer pullStatus;

    @ApiModelProperty("物流方式")
    private Integer toDescLogistics;

    @ApiModelProperty("物流单号")
    private String carrierSn;

}
