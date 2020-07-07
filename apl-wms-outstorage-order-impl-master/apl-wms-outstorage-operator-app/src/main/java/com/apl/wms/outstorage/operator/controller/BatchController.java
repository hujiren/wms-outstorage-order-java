package com.apl.wms.outstorage.operator.controller;

import com.apl.lib.utils.ResultUtil;
import com.apl.lib.utils.SnowflakeIdWorker;
import com.apl.lib.validate.TypeValidator;
import com.apl.wms.outstorage.operator.service.PullBatchService;
import com.apl.wms.outstorage.operator.pojo.vo.PullBatchInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/batch")
@Validated
@Api(value = "批次相关",tags = "批次相关")
public class BatchController {


    @Autowired
    PullBatchService pullBatchService;


    @PostMapping(value = "/list-pull-batch")
    @ApiOperation(value =  "获取批次列表" , notes = "获取拣货信息列表 状态 5开始拣货 6已拣货 7分拣中 8已分拣")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pullStatus",value = "拣货状态 状态 5开始拣货  6已拣货    7分拣中   8已分拣",required = true  , paramType = "query"),
            @ApiImplicitParam(name = "keyword",value = "订单编号 关键字" ,  paramType = "query"),
            @ApiImplicitParam(name = "batchTime",value = "批次创建时间",required = true  , paramType = "query")
    })
    public ResultUtil<List<PullBatchInfoVo>> listPullBatch(@TypeValidator(value = {"5" , "6" , "7" , "8"} , message = "拣货状态错误")
                                                                @NotNull(message = "pullStatus 不能为空")Integer pullStatus,
                                                            String keyword,
                                                            @NotNull(message = "pullStatus 不能为空") Long batchTime){

        ResultUtil ResultUtil = pullBatchService.listPullBatch(pullStatus, keyword, batchTime);
        return ResultUtil;
    }




    @PostMapping(value = "/create-pull-batch")
    @ApiOperation(value =  "创建收货批次" , notes = "保存收货批次，并且完成库位库存锁定")
    @ApiImplicitParam(name = "ids",value = "订单列表",required = true  , paramType = "query")
    public ResultUtil<String> createPullBatch(@NotNull(message = "ids 不能为空")String ids){

        return pullBatchService.createPullBatch(ids);
    }


}
