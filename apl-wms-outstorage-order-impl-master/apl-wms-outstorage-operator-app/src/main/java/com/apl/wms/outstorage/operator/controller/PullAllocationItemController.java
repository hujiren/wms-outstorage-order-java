package com.apl.wms.outstorage.operator.controller;
import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.operator.pojo.dto.PullOrderKeyDto;
import com.apl.wms.outstorage.operator.pojo.dto.StockManageKeyDto;
import com.apl.wms.outstorage.operator.pojo.vo.OutOrderPickListVo;
import com.apl.wms.outstorage.order.service.OutOrderService;
import com.apl.wms.outstorage.order.service.PullAllocationItemService;
import com.apl.wms.outstorage.order.lib.pojo.bo.AllocationWarehouseOutOrderBo;
import com.apl.wms.warehouse.lib.pojo.bo.CompareStorageLocalStocksBo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
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

    @Autowired
    public OutOrderService outOrderService;


    @PostMapping(value = "/get-order-by-allocation-warehouse-manual")
    @ApiOperation(value =  "查询分配仓库时的订单与商品" , notes = "查询分配仓库时的订单与商品", hidden = true)
    @ApiImplicitParam(name = "outOrderId",value = "订单id",required = true  , paramType = "query")
    public ResultUtil<AllocationWarehouseOutOrderBo> getOrderForAllocationWarehouseManual(
            @NotNull(message = "订单id不能为空") @Min(value = 0, message = "订单id不能小于0") Long outOrderId)  throws Exception{

        return pullAllocationItemService.getOrderForAllocationWarehouseManual(outOrderId);
    }


    @PostMapping(value = "/get-order-for-cancel-allocation-warehouse-manual")
    @ApiOperation(value =  "查询取消分配仓库时的订单与商品" , notes = "查询取消分配仓库时的订单与商品", hidden = true)
    @ApiImplicitParam(name = "outOrderId",value = "订单id",required = true  , paramType = "query")
    public ResultUtil<AllocationWarehouseOutOrderBo> getOrderForCancelAllocationWarehouseManual(@NotNull(message = "订单id不能为空") Long outOrderId)  throws Exception{

        return pullAllocationItemService.getOrderForCancelAllocationWarehouseManual(outOrderId);
    }


    @PostMapping(value = "/get-order-by-allocation-warehouse-queue")
    @ApiOperation(value =  "批量分配库位" , notes = "批量分配库位")
    public ResultUtil<Boolean> allocationWarehouseForOrderQueueSend(@RequestBody @NotNull(message = "订单id不能为空") List<Long> orderIds)  throws Exception{
        return pullAllocationItemService.allocationWarehouseForOrderQueueSend(orderIds);
    }


    @PostMapping(value = "/get-order-for-cancel-allocation-warehouse-queue")
    @ApiOperation(value =  "批量取消分配" , notes = "批量取消分配")
    public ResultUtil<Boolean> cancelAllocationWarehouseForOrderQueueSend(@RequestBody @NotNull(message = "订单id不能为空") List<Long> orderIds)  throws Exception{
        return pullAllocationItemService.cancelAllocationWarehouseForOrderQueueSend(orderIds);
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
    public ResultUtil<Integer> deleteOrderAllocationItem(
            @NotNull(message = "订单id不能为空") @Min(value = 0, message = "订单id不能小于0") Long outOrderId,
                                                         @NotEmpty(message = "tranId不能为空") String tranId){
        return pullAllocationItemService.deleteOrderAllocationItem(outOrderId, tranId);
    }


    @PostMapping("/stock-manage")
    @ApiOperation(value =  "分页获取分配库位信息" , notes = "分页获取分配库位信息")
    public ResultUtil<Page<OutOrderPickListVo>> pickManage(PageDto pageDto, @Validated StockManageKeyDto keyDto) throws Exception{

        return pullAllocationItemService.stockManage(pageDto , keyDto);
    }


    @PostMapping("/cancel-allocation-operator")
    @ApiOperation(value =  "订单拣货分配取消" , notes = "订单拣货分配取消")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "memberId",value = "拣货员id",required = true  , paramType = "query"),
            @ApiImplicitParam(name = "orderIdList",value = "订单id 列表",required = true  , paramType = "query")
    })
    public ResultUtil<Boolean> cancelAllocationOperator(@NotNull(message = "memberId 不能为空") @Min(value = 1 , message = "memberId 不能小于1")Long memberId ,
                                                        @NotNull(message = "orderIdList 不能为空")String orderIdList){

        return outOrderService.cancelAllocationOperator(memberId , orderIdList);
    }

}
