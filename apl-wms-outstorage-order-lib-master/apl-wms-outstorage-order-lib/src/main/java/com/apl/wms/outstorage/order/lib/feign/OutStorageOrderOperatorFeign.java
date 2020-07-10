package com.apl.wms.outstorage.order.lib.feign;

import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.order.lib.feign.impl.OutStorageOrderOperatorFeignImpl;
import com.apl.wms.outstorage.order.lib.pojo.bo.AllocationWarehouseOutOrderBo;
import com.apl.wms.warehouse.lib.pojo.bo.CompareStorageLocalStocksBo;
import com.apl.wms.warehouse.lib.pojo.bo.OutOrderAlloStocksBo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

/**
 * @author hjr start
 * @date 2020/7/6 - 14:45
 */
@Component
@FeignClient(name = "apl-wms-outstorage-operator-app", fallback = OutStorageOrderOperatorFeignImpl.class)
public interface OutStorageOrderOperatorFeign {


    @PostMapping(value = "/pull-allocation-item/get-order-by-allocation-warehouse-manual")
    ResultUtil<AllocationWarehouseOutOrderBo> getOrderByAllocationWarehouseManual(@RequestParam("outOrderId")Long outOrderId);

    @PostMapping(value = "/pull-allocation-item/insert", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResultUtil<Integer>  insertAllocationItem(@RequestParam("tranId")String tranId, @RequestParam("outOrderId")Long outOrderId, @RequestBody @RequestParam("compareStorageLocalStocksBos")List<CompareStorageLocalStocksBo> compareStorageLocalStocksBos);


    @PostMapping(value = "/pull-allocation-item/insert2", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResultUtil<Integer>  insertAllocationItem2(@RequestBody OutOrderAlloStocksBo alloStocksBo);

}
