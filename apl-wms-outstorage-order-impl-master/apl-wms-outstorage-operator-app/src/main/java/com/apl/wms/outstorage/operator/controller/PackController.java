package com.apl.wms.outstorage.operator.controller;

import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.operator.pojo.dto.PullMaterialsDto;
import com.apl.wms.outstorage.operator.pojo.dto.PullPackItemDto;
import com.apl.wms.outstorage.operator.pojo.vo.OrderRecordVo;
import com.apl.wms.outstorage.operator.pojo.vo.PackingInfo;
import com.apl.wms.outstorage.operator.service.PackService;
import com.apl.wms.warehouse.lib.feign.WarehouseFeign;
import com.apl.wms.warehouse.lib.pojo.vo.PackagingMaterialsInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/pack")
@Validated
@Api(value = "打包相关",tags = "打包相关")
public class PackController {

    @Autowired
    PackService packService;
    @Autowired
    WarehouseFeign warehouseFeign;

    @PostMapping(value = "/get-pack-info")
    @ApiOperation(value =  "获取订单打包详细" , notes = "获取订单打包详细")
    @ApiImplicitParam(name = "orderSn",value = "订单号",required = true  , paramType = "query")
    public ResultUtil<PackingInfo> getPackInfo(@NotBlank(message = "id不能为空") String orderSn)  throws Exception{

        return packService.getPackInfo(orderSn);
    }


    @PostMapping(value = "/submit-pack-info")
    @ApiOperation(value =  "提交打包信息" , notes = "提交打包信息")
    public ResultUtil<Boolean> submitPackInfo(@NotEmpty @RequestBody List<PullMaterialsDto> pullMaterialsDtoList){

        return packService.submitPackInfo(pullMaterialsDtoList);
    }


    @PostMapping(value = "/submit-pack-size")
    @ApiOperation(value =  "提交打包尺寸" , notes = "提交打包尺寸")
    public ResultUtil<Boolean> submitPackSize(@NotEmpty @RequestBody List<PullPackItemDto> pullPackItemList){

        return packService.submitPackSize(pullPackItemList);
    }


    @PostMapping(value = "/get-order-record")
    @ApiOperation(value =  "获取订单记录" , notes = "获取订单记录")
    public ResultUtil<List<OrderRecordVo>> getOrderRecord() throws IOException {
        return packService.getOrderRecord();
    }


    @PostMapping(value = "/get-pack-materials")
    @ApiOperation(value =  "扫描包装材料" , notes = "扫描包装材料")
    @ApiImplicitParam(name = "sku",value = "包装材料SKU",required = true  , paramType = "query")
    public ResultUtil<PackagingMaterialsInfoVo> getPackMaterials(@NotNull(message = "包装材料SKU不能为空") String sku) throws Exception {

        return packService.getPackMaterials(sku);
    }

}
