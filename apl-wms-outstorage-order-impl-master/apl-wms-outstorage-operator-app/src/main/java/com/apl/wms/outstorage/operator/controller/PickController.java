package com.apl.wms.outstorage.operator.controller;


import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.utils.ResultUtil;
import com.apl.lib.utils.StringUtil;
import com.apl.wms.outstorage.operator.pojo.vo.OutOrderPickListVo;
import com.apl.wms.outstorage.operator.service.PickService;
import com.apl.wms.outstorage.order.lib.pojo.bo.AllocationWarehouseOutOrderBo;
import com.apl.wms.outstorage.order.service.OutOrderCommodityItemService;
import com.apl.wms.outstorage.order.service.OutOrderService;
import com.apl.wms.outstorage.operator.service.PullBatchService;
import com.apl.wms.outstorage.order.pojo.vo.OrderItemListVo;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderInfoVo;
import com.apl.wms.outstorage.operator.pojo.dto.PullBatchSubmitDto;
import com.apl.wms.outstorage.operator.pojo.dto.PullOrderKeyDto;
import com.apl.wms.outstorage.operator.pojo.vo.PullAllocationItemMsgVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/order-pick")
@Validated
@Api(value = "拣货相关",tags = "拣货相关")
public class PickController {


    @Autowired
    public OutOrderService outOrderService;

    @Autowired
    PullBatchService pullBatchService;

    @Autowired
    OutOrderCommodityItemService outOrderCommodityItemService;

    @Autowired
    PickService pickService;

    @PostMapping("/pick-manage")
    @ApiOperation(value =  "分页获取订单拣货列表" , notes = "分页获取订单拣货列表")
    public ResultUtil<Page<OutOrderPickListVo>> pickManage(PageDto pageDto, @Validated PullOrderKeyDto keyDto) throws Exception{

        return pickService.pickManage(pageDto , keyDto);
    }


    @PostMapping(value = "/sort/order/msg")
    @ApiOperation(value =  "获取拣货信息 根据订单进行分组" , notes = "根据批次id ，获取拣货信息，根据订单进行分组")
    @ApiImplicitParam(name = "batchId",value = "批次id",required = true  , paramType = "query")
    public ResultUtil<List<OrderItemListVo>> getPickMsgSortByOrder(@NotNull(message = "batchId 不能为空")Long batchId) throws Exception {

        return pullBatchService.getPickMsgSortByOrder(batchId);
    }


    @PostMapping(value = "/sort/commodity/msg")
    @ApiOperation(value =  "获取拣货信息 根据商品进行分组" , notes = "根据批次id ，获取拣货信息，根据商品进行分组")
    @ApiImplicitParam(name = "batchId",value = "批次id",required = true  , paramType = "query")
    public ResultUtil<List<PullAllocationItemMsgVo>> getPickMsgSortByCommodity(@NotNull(message = "batchId 不能为空")Long batchId) throws Exception {

        return pullBatchService.getPickMsgSortByCommodity(batchId);
    }


//    @PostMapping("/allocation-operator")
//    @ApiOperation(value =  "为订单分配拣货员" , notes = "为订单分配拣货员")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "memberId",value = "拣货员id",required = true  , paramType = "query"),
//            @ApiImplicitParam(name = "orderIdList",value = "订单id 列表",required = true  , paramType = "query")
//    })
//    public ResultUtil<Boolean> allocationOperator(@NotNull(message = "memberId 不能为空") @Min(value = 1 , message = "memberId 不能小于1")Long memberId ,
//                                                  @NotNull(message = "orderIdList 不能为空")String orderIdList){
//
//        return outOrderService.allocationOperator(memberId , orderIdList);
//    }


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

    @PostMapping("/list-operator-order")
    @ApiOperation(value =  "获取拣货员对应的订单列表" , notes = "获取分配给某个拣货员的订单列表")
    public ResultUtil<List<OutOrderInfoVo>> listOperatorOrders() throws Exception {

        return outOrderService.listOperatorOrders();
    }


    @PostMapping(value = "/submit-pick")
    @ApiOperation(value =  "提交拣货数据" , notes = "提交拣货数据 ， 进行库存减扣")
    public ResultUtil submitPick(@RequestBody PullBatchSubmitDto pullBatchSubmit) throws Exception {

        return pullBatchService.submitPullBatch(pullBatchSubmit);
    }



    @PostMapping(value = "/allocation-picking-member")
    @ApiOperation(value = "分配拣货员", notes = "分配拣货员")
    public ResultUtil<OutOrderPickListVo> allocationPickingMember(@RequestBody @NotNull(message = "订单号不能为空")List<String> orderSns) throws Exception {
        return pickService.allocationPickingMember(orderSns);
    }
}
