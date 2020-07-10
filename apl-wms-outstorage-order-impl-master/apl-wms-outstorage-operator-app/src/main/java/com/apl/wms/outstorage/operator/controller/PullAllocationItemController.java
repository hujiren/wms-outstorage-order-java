package com.apl.wms.outstorage.operator.controller;
import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.operator.service.PullAllocationItemService;
import com.apl.wms.outstorage.order.lib.pojo.bo.AllocationWarehouseOutOrderBo;
import com.apl.wms.warehouse.lib.pojo.bo.CompareStorageLocalStocksBo;
import com.apl.wms.warehouse.lib.pojo.bo.OutOrderAlloStocksBo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.awt.*;
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
    @ApiImplicitParam(name = "orderIds",value = "订单id",required = true  , paramType = "query")
    public ResultUtil<Boolean> allocationWarehouseForOrderQueueSend(@NotNull(message = "订单id不能为空") List<Long> orderIds)  throws Exception{

        return pullAllocationItemService.allocationWarehouseForOrderQueueSend(orderIds);
    }


    @PostMapping(value = "/insert")
    @ApiOperation(value =  "插入分配明细" , notes = "插入分配明细")
    public ResultUtil<Integer> insertAllocationItem(String tranId, Long outOrderId, @RequestBody List<CompareStorageLocalStocksBo> compareStorageLocalStocksBos){


        return pullAllocationItemService.insertAllocationItem(tranId, outOrderId, compareStorageLocalStocksBos);
    }

    @PostMapping(value = "/insert2", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ApiOperation(value =  "插入分配明细2" , notes = "插入分配明细2")
    public ResultUtil<Integer> insertAllocationItem2(@RequestBody OutOrderAlloStocksBo alloStocksBo){

        String tranId ="tranId:1234546789";
        Long outOrderId=441039997610778624l;
        return pullAllocationItemService.insertAllocationItem(tranId, outOrderId, null);
    }
}
