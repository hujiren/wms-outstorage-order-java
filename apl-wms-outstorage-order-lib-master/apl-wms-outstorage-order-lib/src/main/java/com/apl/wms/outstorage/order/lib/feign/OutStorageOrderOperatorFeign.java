package com.apl.wms.outstorage.order.lib.feign;
import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.order.lib.feign.impl.OutStorageOrderOperatorFeignImpl;
import com.apl.wms.outstorage.order.lib.pojo.bo.AllocationWarehouseOutOrderBo;
import com.apl.wms.warehouse.lib.pojo.bo.CompareStorageLocalStocksBo;
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

    @PostMapping(value = "/pull-allocation-item/get-order-for-cancel-allocation-warehouse-manual")
    ResultUtil<AllocationWarehouseOutOrderBo> getOrderForCancelAllocationWarehouseManual(@RequestParam("outOrderId")Long outOrderId);

    @PostMapping(value = "/pull-allocation-item/insert", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResultUtil<Integer>  AllocOutOrderStockCallBack(@RequestParam("tranId")String tranId, @RequestParam("outOrderId")Long outOrderId, @RequestParam("pullStatus")Integer pullStatus, @RequestBody List<CompareStorageLocalStocksBo> compareStorageLocalStocksBos);

    @PostMapping(value = "/pull-allocation-item/delete")
    ResultUtil<Integer>  deleteOrderAllocationItem(@RequestParam("outOrderId") Long outOrderId,  @RequestParam("tranId") String tranId);

}
