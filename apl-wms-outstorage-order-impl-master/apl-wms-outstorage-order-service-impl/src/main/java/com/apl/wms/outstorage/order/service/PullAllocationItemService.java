package com.apl.wms.outstorage.order.service;

import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.operator.pojo.dto.StockManageKeyDto;
import com.apl.wms.outstorage.operator.pojo.po.PullAllocationItemPo;
import com.apl.wms.outstorage.operator.pojo.vo.OutOrderPickListVo;
import com.apl.wms.outstorage.order.lib.pojo.bo.AllocationWarehouseOutOrderBo;
import com.apl.wms.warehouse.lib.pojo.bo.CompareStorageLocalStocksBo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @author hjr start
 * @date 2020/7/7 - 11:04
 */
public interface PullAllocationItemService extends IService<PullAllocationItemPo> {

    /**
     * 将订单id及构建的分配明细对象写入到分配明细表
     * @param tranId
     * @param outOrderId
     * @param compareStorageLocalStocksBos
     * @return
     */
    ResultUtil<Integer> AllocOutOrderStockCallBack(String tranId, Long outOrderId, Integer pullStatus, List<CompareStorageLocalStocksBo> compareStorageLocalStocksBos);


    /**
     * 删除订单分配明细
     * @param outOrderId
     * @return
     */
    ResultUtil<Integer> deleteOrderAllocationItem(Long outOrderId, String tranId);


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
     * 获取单一订单商品id和下单数量
     * @param: 多个订单id
     * @return
     */
    ResultUtil<AllocationWarehouseOutOrderBo> getOrderForCancelAllocationWarehouseManual(Long outOrderId);



    /**
     * 批量获取多个订单中商品id和下单数量
     * @param: 多个订单id
     * @return
     */
    ResultUtil<Boolean> cancelAllocationWarehouseForOrderQueueSend(List<Long> orderIds) throws Exception;


    /**
     * 库位管理
     * @param pageDto
     * @param keyDto
     * @return
     * @throws Exception
     */
    ResultUtil<Page<OutOrderPickListVo>> stockManage(PageDto pageDto, StockManageKeyDto keyDto) throws Exception;
}
