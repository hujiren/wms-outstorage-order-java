package com.apl.wms.outstorage.operator.service;

import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.operator.pojo.vo.PackingInfo;
import com.baomidou.mybatisplus.extension.service.IService;

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
    ResultUtil<PackingInfo> getPackInfo(String orderSn);
}
