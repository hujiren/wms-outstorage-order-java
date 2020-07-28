package com.apl.wms.outstorage.operator.controller;


import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.operator.pojo.vo.PackingInfo;
import com.apl.wms.outstorage.operator.service.PackService;
import com.apl.wms.outstorage.operator.service.PullMaterialsService;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderCommodityItemInfoVo;
import com.apl.wms.outstorage.order.service.OutOrderCommodityItemService;
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
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/pack")
@Validated
@Api(value = "打包相关",tags = "打包相关")
public class PackController {

    @Autowired
    PackService packService;

    @PostMapping(value = "/get-pack-info")
    @ApiOperation(value =  "获取订单打包详细" , notes = "获取订单打包详细")
    @ApiImplicitParam(name = "orderId",value = "订单id",required = true  , paramType = "query")
    public ResultUtil<PackingInfo> getPackInfo(@NotBlank(message = "id不能为空") String orderSn)  throws Exception{

        return packService.getPackInfo(orderSn);
    }




}
