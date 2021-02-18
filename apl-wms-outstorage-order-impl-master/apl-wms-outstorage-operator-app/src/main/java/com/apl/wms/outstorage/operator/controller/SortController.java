package com.apl.wms.outstorage.operator.controller;
import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.operator.pojo.vo.OrderCommodityScanVo;
import com.apl.wms.outstorage.operator.service.SortService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/order-sort")
@Validated
@Api(value = "分拣相关",tags = "分拣相关")
public class SortController {

    @Autowired
    SortService sortService;


    @PostMapping("/scan-order-sn")
    @ApiOperation(value =  "扫描订单号" , notes = "扫描订单号")
    @ApiImplicitParam(name = "orderSn",value = "订单号",required = true  , paramType = "query")
    public ResultUtil<OrderCommodityScanVo> scanOrderSn(@NotBlank(message = "订单号不能为空") String orderSn) throws Exception {

        return sortService.scanOrderSn(orderSn);
    }


    @PostMapping("/submit-sort-info")
    @ApiOperation(value =  "提交分拣信息" , notes = "提交分拣信息")
    public ResultUtil<Boolean> submitSortInfo(@RequestBody @NotEmpty(message = "订单ids不能为空")List<Long> orderIds) throws IOException {

        return sortService.submitSortInfo(orderIds);
    }



}
