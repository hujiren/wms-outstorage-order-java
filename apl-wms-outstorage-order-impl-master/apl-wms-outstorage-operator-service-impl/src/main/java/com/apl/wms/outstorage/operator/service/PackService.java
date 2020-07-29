package com.apl.wms.outstorage.operator.service;

import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.operator.pojo.dto.PullMaterialsDto;
import com.apl.wms.outstorage.operator.pojo.dto.PullPackItemDto;
import com.apl.wms.outstorage.operator.pojo.vo.PackingInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author hjr start
 * @date 2020/7/28 - 14:57
 */
public interface PackService extends IService<PackingInfo> {

    /**
     * 按订单号查询订单信息, 商品信息, 包装材料信息
     * @param orderSn
     * @return
     */
    ResultUtil<PackingInfo> getPackInfo(String orderSn) throws Exception;

    /**
     * 提交打包信息
     * @return
     */
    ResultUtil<Boolean> submitPackInfo(List<PullMaterialsDto> pullMaterialsDtoList);

    /**
     * 提交打包尺寸
     * @param pullPackItemList
     * @return
     */
    ResultUtil<Boolean> submitPackSize(List<PullPackItemDto> pullPackItemList);
}
