package com.apl.wms.outstorage.operator.pojo.vo;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderCommodityItemInfoVo;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderListVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author hjr start
 * @date 2020/7/28 - 16:09
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PackingInfo {

    @ApiModelProperty("订单信息对象")
    private OutOrderListVo outOrderListVo;

    @ApiModelProperty("商品信息对象")
    private OutOrderCommodityItemInfoVo outOrderCommodityItemInfoVo;

    @ApiModelProperty("包装材料信息对象")
    private PackagingMaterialsInfoVo packagingMaterialsInfoVo;
}
