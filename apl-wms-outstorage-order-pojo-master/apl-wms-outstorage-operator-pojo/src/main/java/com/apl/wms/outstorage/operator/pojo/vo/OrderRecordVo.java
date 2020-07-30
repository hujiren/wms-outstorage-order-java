package com.apl.wms.outstorage.operator.pojo.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hjr start
 * @date 2020/7/30 - 16:33
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class OrderRecordVo {

    @ApiModelProperty("订单号")
    private String orderSn;

    @ApiModelProperty("物流单号")
    private String carrierSn;
}
