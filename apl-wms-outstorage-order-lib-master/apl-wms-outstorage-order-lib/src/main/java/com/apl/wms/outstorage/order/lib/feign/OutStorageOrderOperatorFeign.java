package com.apl.wms.outstorage.order.lib.feign;

import com.apl.lib.utils.ResultUtils;
import com.apl.wms.outstorage.order.lib.feign.impl.OutStorageOrderOperatorFeignImpl;
import com.apl.wms.outstorage.order.lib.pojo.bo.AllocationWarehouseOutOrderBo;
import com.apl.wms.warehouse.lib.pojo.bo.CompareStorageLocalStocksBo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author hjr start
 * @date 2020/7/6 - 14:45
 */
@Component
@FeignClient(name = "apl-wms-outstorage-operator-app",fallback = OutStorageOrderOperatorFeignImpl.class)
public interface OutStorageOrderOperatorFeign {

    @PostMapping(value = "/order-pick/get-orders-commodity-by-allocation-warehouse")
    ResultUtils<List<AllocationWarehouseOutOrderBo>> getOrdersByAllocationWarehouse(@RequestParam("orderIds")String orderIds);

    @PostMapping(value = "/pull-allocation-item/insert")
    ResultUtils<Integer>  insertAllocationItem(@RequestParam("outOrderId")Long outOrderId, @RequestParam("compareStorageLocalStocksBos")List<CompareStorageLocalStocksBo> compareStorageLocalStocksBos);
}
