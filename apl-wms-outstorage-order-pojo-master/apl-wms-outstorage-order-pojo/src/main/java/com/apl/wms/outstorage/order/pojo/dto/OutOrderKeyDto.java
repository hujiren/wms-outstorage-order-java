package com.apl.wms.outstorage.order.pojo.dto;

import com.apl.lib.pojo.dto.TspDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 出库订单 查询参数
 * </p>
 *
 * @author cy
 * @since 2020-01-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="出库订单 查询参数", description="出库订单 查询参数")
public class OutOrderKeyDto extends TspDto implements Serializable {

    private static final long serialVersionUID=1L;

    @ApiModelProperty(name = "whId", value = "仓库id")
    @ApiParam
    private Long whId;

    @ApiModelProperty(name = "customerId", value = "客户id")
    private Long customerId;

    @ApiModelProperty(name = "orderStatus", value = "出库订单状态  1创建中  2创建异常  3新建  4已发货  5完成   6取消")
    private Integer orderStatus;

    @ApiModelProperty(name = "orderSn", value = "订单号")
    private String orderSn;

    @ApiModelProperty(name = "commodityName", value = "SKU/品名")
    private String commodityName;


    public String getCommodityName() {
        if (commodityName == null || commodityName.trim().equals("")){
            return null;
        }

        return commodityName;
    }


    public String getOrderSn() {
        if (orderSn == null || orderSn.trim().equals("")){
            return null;
        }

        return orderSn;
    }
}
