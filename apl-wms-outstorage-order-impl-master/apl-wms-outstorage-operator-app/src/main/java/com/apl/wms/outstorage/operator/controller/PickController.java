package com.apl.wms.outstorage.operator.controller;


import com.apl.cache.AplCacheUtil;
import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.utils.ResultUtil;
import com.apl.lib.validate.TypeValidator;
import com.apl.wms.outstorage.operator.pojo.vo.OutOrderPickListVo;
import com.apl.wms.outstorage.operator.service.PickService;
import com.apl.wms.outstorage.order.service.OutOrderCommodityItemService;
import com.apl.wms.outstorage.operator.service.PullBatchService;
import com.apl.wms.outstorage.order.pojo.vo.OrderItemListVo;
import com.apl.wms.outstorage.operator.pojo.dto.SubmitPickItemDto;
import com.apl.wms.outstorage.operator.pojo.dto.PullOrderKeyDto;
import com.apl.wms.outstorage.operator.pojo.vo.PullAllocationItemMsgVo;
import com.apl.wms.warehouse.lib.cache.OperatorCacheBo;
import com.apl.wms.warehouse.lib.feign.WarehouseFeign;
import com.apl.wms.warehouse.lib.utils.WmsWarehouseUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.hibernate.validator.constraints.Range;
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
    PullBatchService pullBatchService;

    @Autowired
    OutOrderCommodityItemService outOrderCommodityItemService;

    @Autowired
    AplCacheUtil redisTemplate;

    @Autowired
    WarehouseFeign warehouseFeign;

    @Autowired
    PickService pickService;

    @PostMapping("/pick-manage")
    @ApiOperation(value =  "PC分页获取订单拣货列表" , notes = "PC分页获取订单拣货列表")
    public ResultUtil<Page<OutOrderPickListVo>> pickManage(PageDto pageDto, @Validated PullOrderKeyDto keyDto) throws Exception{
        keyDto.setTerminal(1);

        return pickService.pickManage(pageDto , keyDto);
    }


    @PostMapping("/pick-manage-pda")
    @ApiOperation(value =  "pda分页获取订单拣货列表" , notes = "pda分页获取订单拣货列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pullStatus", value = "捡货状态", required = true, paramType = "query")
    })
    public ResultUtil<Page<OutOrderPickListVo>> pickManagePad(PageDto pageDto,
          @NotNull(message = "状态不能为空") @TypeValidator(value = {"4", "6"}, message = "拣货状态错误") Integer pullStatus) throws Exception{

        PullOrderKeyDto keyDto = new PullOrderKeyDto();
        OperatorCacheBo operatorCacheBo = WmsWarehouseUtils.checkOperator(warehouseFeign, redisTemplate);
        keyDto.setPullOperatorId(operatorCacheBo.getMemberId());
        keyDto.setTerminal(2);
        keyDto.setPullStatus(pullStatus);

        return pickService.pickManage(pageDto , keyDto);
    }


    @PostMapping(value = "/sort/order/msg")
    @ApiOperation(value =  "获取拣货信息 根据订单进行分组" , notes = "根据批次id ，获取拣货信息，根据订单进行分组")
    @ApiImplicitParam(name = "batchId",value = "批次id",required = true  , paramType = "query")
    public ResultUtil<List<OrderItemListVo>> getPickMsgSortByOrder(@NotNull(message = "batchId 不能为空") @Min(value = 0, message = "批次id不能小于0") Long batchId) throws Exception {

        return pullBatchService.getPickMsgSortByOrder(batchId);
    }


    @PostMapping(value = "/sort/commodity/msg")
    @ApiOperation(value =  "获取拣货信息 根据商品进行分组" , notes = "根据批次id ，获取拣货信息，根据商品进行分组")
    @ApiImplicitParam(name = "batchId",value = "批次id",required = true  , paramType = "query")
    public ResultUtil<List<PullAllocationItemMsgVo>> getPickMsgSortByCommodity(@Min(value = 0, message = "批次id不能小于0") @NotNull(message = "batchId 不能为空")Long batchId) throws Exception {

        return pullBatchService.getPickMsgSortByCommodity(batchId);
    }


    @PostMapping(value = "/submit-pick-item")
    @ApiOperation(value =  "提交拣货数据" , notes = "提交拣货数据 ， 进行库存减扣")
    public ResultUtil<Boolean> submitPick(@Range(min = 0, message = "批次id不能小于0") @NotNull(message = "批次id不能为空")Long batchId,
                                          @Validated @RequestBody List<SubmitPickItemDto> submitPickItemDtoList) throws Exception {

        return pickService.submitPick(batchId, submitPickItemDtoList);
    }


    @PostMapping(value = "/allocation-picking-member")
    @ApiOperation(value = "分配拣货员", notes = "分配拣货员")
    public ResultUtil<OutOrderPickListVo> allocationPickingMember(@RequestBody @NotNull(message = "订单号不能为空") List<String> orderSns) throws Exception {

        return pickService.allocationPickingMember(orderSns);
    }
}
