package com.apl.wms.outstorage.operator.controller;


import com.apl.lib.utils.ResultUtils;
import com.apl.wms.outstorage.order.service.OutOrderService;
import com.apl.wms.outstorage.order.pojo.vo.OrderItemListVo;
import com.apl.wms.outstorage.operator.pojo.dto.PackOrderSubmitDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/pack")
@Validated
@Api(value = "打包相关",tags = "打包相关")
public class PackController {

    //@Autowired
    //PullMaterialsService pullMaterialsService;

    @Autowired
    OutOrderService outOrderService;


    @PostMapping(value = "/get-order-pack")
    @ApiOperation(value =  "获取订单打包详细" , notes = "获取订单打包详细")
    @ApiImplicitParam(name = "orderId",value = "订单id",required = true  , paramType = "query")
    public ResultUtils<OrderItemListVo> getOrderPackMsg(@NotNull(message = "id不能为空") @Min(value = 1 , message = "id不能小于1") Long orderId)  throws Exception{

        return outOrderService.getOrderPackMsg(orderId);
    }



    @PostMapping("/submit-pack")
    @ApiOperation(value =  "提交打包数据" , notes = "提交打包数据")
    public ResultUtils submitPackMsg(@RequestBody PackOrderSubmitDto packOrderSubmit) throws Exception {

        //return pullMaterialsService.submitPackMsg(packOrderSubmit);
        return  null;
    }


}
