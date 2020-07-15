package com.apl.wms.outstorage.operator.controller;
import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.order.service.PullAllocationItemService;
import com.apl.wms.outstorage.order.lib.pojo.bo.AllocationWarehouseOutOrderBo;
import com.apl.wms.warehouse.lib.pojo.bo.CompareStorageLocalStocksBo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author hjr start
 * @date 2020/7/7 - 10:37
 */
@RestController
@RequestMapping("/pull-allocation-item")
@Validated
@Api(value = "分配明细",tags = "分配明细")
public class PullAllocationItemController {


    @Autowired
    private PullAllocationItemService pullAllocationItemService;


    @PostMapping(value = "/get-order-by-allocation-warehouse-manual")
    @ApiOperation(value =  "查询分配仓库时的订单与商品" , notes = "查询分配仓库时的订单与商品")
    @ApiImplicitParam(name = "outOrderId",value = "订单id",required = true  , paramType = "query")
    public ResultUtil<AllocationWarehouseOutOrderBo> getOrderForAllocationWarehouseManual(@NotNull(message = "订单id不能为空") Long outOrderId)  throws Exception{

        return pullAllocationItemService.getOrderForAllocationWarehouseManual(outOrderId);
    }


    @PostMapping(value = "/get-order-by-allocation-warehouse-queue")
    @ApiOperation(value =  "批量查询分配仓库时的订单与商品" , notes = "批量查询分配仓库时的订单与商品")
    public ResultUtil<Boolean> allocationWarehouseForOrderQueueSend(@RequestBody @NotNull(message = "订单id不能为空") List<Long> orderIds)  throws Exception{
        return pullAllocationItemService.allocationWarehouseForOrderQueueSend(orderIds);
    }


    @PostMapping(value = "/insert", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value =  "插入分配明细" , notes = "插入分配明细")
    public ResultUtil<Integer> AllocOutOrderStockCallBack(@NotNull(message = "tranId不能为空") String tranId,
                                                          @NotNull(message = "outOrderId不能为空") Long outOrderId,
                                                          @NotNull(message = "pullStatus不能为空") Integer pullStatus,
                                                          @NotNull(message = "compareStorageLocalStocksBos不能为空")  @RequestBody List<CompareStorageLocalStocksBo> compareStorageLocalStocksBos){

        return pullAllocationItemService.AllocOutOrderStockCallBack(tranId, outOrderId, pullStatus, compareStorageLocalStocksBos);
    }


    @PostMapping(value = "/delete")
    @ApiOperation(value =  "删除分配明细" , notes = "删除分配明细")
    @ApiImplicitParam(name = "outOrderId",value = "订单id",required = true  , paramType = "query")
    public ResultUtil<Integer> deleteOrderAllocationItem(@NotNull(message = "订单id不能为空")Long outOrderId){
        return pullAllocationItemService.deleteOrderAllocationItem(outOrderId);
    }

}
