package com.apl.wms.outstorage.operator.controller;
import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.operator.service.PullAllocationItemService;
import com.apl.wms.warehouse.lib.pojo.bo.CompareStorageLocalStocksBo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @PostMapping(value = "/insert")
    @ApiOperation(value =  "获取分配明细对象列表 插入到数据库" , notes = "获取分配明细对象列表 插入到数据库")
    @ApiImplicitParams({@ApiImplicitParam(name = "outOrderId",value = "订单id",required = true  , paramType = "query"),
                        @ApiImplicitParam(name = "compareStorageLocalStocksBos", value = "分配明细对象列表", required = true, paramType = "query") })
    public ResultUtil<Integer> insertAllocationItem(Long outOrderId, List<CompareStorageLocalStocksBo> compareStorageLocalStocksBos){

        return pullAllocationItemService.insertAllocationItem(outOrderId, compareStorageLocalStocksBos);
    }
}
