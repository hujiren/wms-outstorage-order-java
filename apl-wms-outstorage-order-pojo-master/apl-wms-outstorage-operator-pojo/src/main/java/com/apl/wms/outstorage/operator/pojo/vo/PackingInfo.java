package com.apl.wms.outstorage.operator.pojo.vo;
import com.apl.wms.warehouse.lib.pojo.vo.PackagingMaterialsInfoVo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author hjr start
 * @date 2020/7/28 - 16:09
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PackingInfo {

    @ApiModelProperty("订单信息对象")
    private OutOrderAttachInfoVo outOrderAttachInfoVo;

    @ApiModelProperty("商品信息对象")
    private List<PackCommodityInfoVo> packCommodityInfoVoList;

    @ApiModelProperty("包装材料信息对象")
    private List<PackagingMaterialsInfoVo> packagingMaterialsInfoVo;
}
