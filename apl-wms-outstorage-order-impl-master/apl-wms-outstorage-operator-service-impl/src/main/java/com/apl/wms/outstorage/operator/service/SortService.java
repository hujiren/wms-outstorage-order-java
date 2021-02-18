package com.apl.wms.outstorage.operator.service;

import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.operator.pojo.vo.OrderCommodityScanVo;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderListVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.io.IOException;
import java.util.List;

/**
 * @author hjr start
 * @date 2020/7/28 - 9:54
 */
public interface SortService extends IService<OrderCommodityScanVo> {

    /**
     * 扫描订单号
     * @param orderSn
     * @return
     */
    ResultUtil<OrderCommodityScanVo> scanOrderSn(String orderSn) throws Exception;


    /**
     * 提交分拣信息
     * @param orderIds
     * @return
     */
    ResultUtil<Boolean> submitSortInfo(List<Long> orderIds) throws IOException;
}
