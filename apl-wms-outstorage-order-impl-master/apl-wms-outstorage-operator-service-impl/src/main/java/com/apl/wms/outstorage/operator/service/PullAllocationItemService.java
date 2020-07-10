package com.apl.wms.outstorage.operator.service;

import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.operator.pojo.po.PullAllocationItemPo;
import com.apl.wms.outstorage.order.lib.pojo.bo.AllocationWarehouseOutOrderBo;
import com.apl.wms.warehouse.lib.pojo.bo.CompareStorageLocalStocksBo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author hjr start
 * @date 2020/7/7 - 11:04
 */
public interface PullAllocationItemService extends IService<PullAllocationItemPo> {



    /**
     * 获取单一订单商品id和下单数量
     * @param: 多个订单id
     * @return
     */
    ResultUtil<AllocationWarehouseOutOrderBo> getOrderForAllocationWarehouseManual(Long orderId) throws Exception;



    /**
     * 批量获取多个订单中商品id和下单数量
     * @param: 多个订单id
     * @return
     */
    ResultUtil<Boolean> allocationWarehouseForOrderQueueSend(List<Long> orderIds) throws Exception ;



    /**
     * 将订单id及构建的分配明细对象写入到分配明细表
     * @param tranId
     * @param outOrderId
     * @param compareStorageLocalStocksBos
     * @return
     */
    ResultUtil<Integer> insertAllocationItem(String tranId, Long outOrderId, List<CompareStorageLocalStocksBo> compareStorageLocalStocksBos);

}
