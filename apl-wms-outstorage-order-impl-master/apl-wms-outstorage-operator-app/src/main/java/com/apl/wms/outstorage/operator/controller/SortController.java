package com.apl.wms.outstorage.operator.controller;

import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.operator.service.PullBatchService;
import com.apl.wms.outstorage.operator.pojo.dto.SortOrderSubmitDto;
import com.apl.wms.outstorage.operator.pojo.vo.PackOrderItemListVo;
import com.apl.wms.outstorage.operator.service.PullPackItemService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order-sort")
@Validated
@Api(value = "分拣相关",tags = "分拣相关")
public class SortController {

    @Autowired
    PullBatchService pullBatchService;

    @Autowired
    PullPackItemService  pullPackItemService;

    @PostMapping("/get-sort-msg")
    @ApiOperation(value =  "获取分拣信息" , notes = "根据订单id 获取分拣信息，包含批次信息，订单信息，以及订单子项下单数量")
        public ResultUtil<PackOrderItemListVo> getSortMsg(Long orderId) throws Exception {

        return pullBatchService.getSortMsg(orderId);
    }


    @PostMapping("/submit-sort")
    @ApiOperation(value =  "提交分拣数据" , notes = "提交分拣数据")
    public ResultUtil submitSortMsg(@RequestBody SortOrderSubmitDto sortOrderSubmitDto) throws Exception {

        return pullBatchService.submitSortMsg(sortOrderSubmitDto);
    }


}
