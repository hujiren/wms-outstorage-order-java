package com.apl.wms.outstorage.order.business.controller;

import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.security.SecurityUser;
import com.apl.lib.utils.CommonContextHolder;
import com.apl.lib.utils.ResultUtil;
import com.apl.lib.validate.ApiParamValidate;
import com.apl.wms.outstorage.order.pojo.dto.SyncOutOrderKeyDto;
import com.apl.wms.outstorage.order.pojo.dto.SyncOutOrderSaveDto;
import com.apl.wms.outstorage.order.pojo.vo.SyncOutOrderInfoVo;
import com.apl.wms.outstorage.order.pojo.vo.SyncOutOrderListVo;
import com.apl.wms.outstorage.order.service.SyncOutOrderService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 *
 * @author arran
 * @since 2019-12-25
 */
@RestController
@RequestMapping("/sync-out-order")
@Validated
@Api(value = "同步平台订单",tags = "同步平台订单")
@Slf4j
public class SyncOutOrderController {

    @Autowired
    SyncOutOrderService syncOutOrderService;

    @PostMapping(value = "/add")
    @ApiOperation(value =  "添加", notes ="GET_STORE_API_CONFIG_FAIL : 获取店铺API配置失败\n  " +
            "STORE_NOT_CONFIG_API : 店铺没有配置API")
    public ResultUtil<Integer> add(SyncOutOrderSaveDto syncOrder) {
        ApiParamValidate.validate(syncOrder);

        return syncOutOrderService.add(syncOrder);
    }


    @PostMapping(value = "/upd")
    @ApiOperation(value =  "更新",  notes ="")
    public ResultUtil<Boolean> updById(SyncOutOrderSaveDto syncOrder) {
        ApiParamValidate.notEmpty("id", syncOrder.getId());
        ApiParamValidate.validate(syncOrder);

        SecurityUser securityUser = CommonContextHolder.getSecurityUser();

        return syncOutOrderService.updById(syncOrder, securityUser.getOuterOrgId());
    }


    @PostMapping(value = "/upd-status")
    @ApiOperation(value =  "更新状态",  notes ="")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "status" , value = "状态  1等待同步  2正在同步  3已完成同步   4同步异常   5暂停同步   6作废" ,paramType = "query", required = true)
    })
    public ResultUtil<Boolean> updStatus(Long id,  @Range(min = 1, max = 6, message = "状态值不正确") Integer status) {
        ApiParamValidate.notEmpty("id", id);
        ApiParamValidate.notEmpty("status", status);

        return syncOutOrderService.updStatus(id, status, 0l);
    }


    @PostMapping(value = "/del")
    @ApiOperation(value =  "删除" , notes = "")
    @ApiImplicitParam(name = "id",value = " id",required = true  , paramType = "query")
    public ResultUtil<Boolean> delById(@NotNull(message = "id不能为空") @Min(value = 1 , message = "id不能小于1") Long id) {

        SecurityUser securityUser = CommonContextHolder.getSecurityUser();

        return syncOutOrderService.delById(id, securityUser.getOuterOrgId());
    }


    @PostMapping(value = "/get")
    @ApiOperation(value =  "获取详细" , notes = "")
    @ApiImplicitParam(name = "id",value = "id",required = true  , paramType = "query")
    public ResultUtil<SyncOutOrderInfoVo> selectById(@NotNull(message = "id不能为空") @Min(value = 1 , message = "id不能小于1") Long id) {

        SecurityUser securityUser = CommonContextHolder.getSecurityUser();

        return syncOutOrderService.selectById(id, securityUser.getOuterOrgId(), 1);
    }


    @PostMapping("/get-list")
    @ApiOperation(value =  "分页查找" , notes = "状态  1等待同步  2正在同步  3已完成同步   4同步异常   5暂停同步   6作废")
    public ResultUtil<Page<SyncOutOrderListVo>> getList(PageDto pageDto, @Validated SyncOutOrderKeyDto keyDto)  throws Exception {
        ApiParamValidate.notEmpty("startTime", keyDto.getStartTime());
        ApiParamValidate.notEmpty("endTime", keyDto.getEndTime());

        return syncOutOrderService.getList(pageDto , keyDto, 1);
    }


    @PostMapping(value = "/boot-task")
    @ApiOperation(value =  "启动任务",  notes ="TASK_ALREADY_BOOT : 启动任务已启动\n " +
            "GET_STORE_API_CONFIG_FAIL : 获取店铺API配置失败\n  " +
            "STORE_NOT_CONFIG_API : 店铺没有配置API")
    public ResultUtil<Boolean> bootTask(Long id) {
        ApiParamValidate.notEmpty("id", id);
        SecurityUser securityUser = CommonContextHolder.getSecurityUser();

        return syncOutOrderService.bootTask(id,  securityUser.getOuterOrgId());
    }


    @PostMapping(value = "/get-status")
    @ApiOperation(value =  "获取任务状态",  notes ="状态  1等待同步  2正在同步  3已完成同步   4同步异常   5暂停同步   6作废")
    public ResultUtil<Integer> getStatus(Long id) {
        ApiParamValidate.notEmpty("id", id);

        return syncOutOrderService.getStatus(id);
    }

}
