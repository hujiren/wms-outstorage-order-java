package com.apl.wms.outstorage.order.business.controller;

import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.security.SecurityUser;
import com.apl.lib.utils.CommonContextHolder;
import com.apl.lib.utils.ResultUtils;
import com.apl.lib.utils.StringUtil;
import com.apl.lib.validate.ApiParamValidate;
import com.apl.wms.outstorage.order.lib.enumwms.OrderStatusEnum;
import com.apl.wms.outstorage.order.service.OutOrderService;
import com.apl.wms.outstorage.order.lib.pojo.dto.OutOrderCommodityItemUpdDto;
import com.apl.wms.outstorage.order.lib.pojo.dto.OutOrderDestUpdDto;
import com.apl.wms.outstorage.order.pojo.dto.OutOrderKeyDto;
import com.apl.wms.outstorage.order.pojo.dto.OutOrderMainDto;
import com.apl.wms.outstorage.order.service.OutOrderCommodityItemService;
import com.apl.wms.outstorage.order.service.OutOrderService;
import com.apl.wms.outstorage.order.pojo.vo.OrderItemListVo;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderListResultVo;
import com.apl.wms.outstorage.order.pojo.vo.StatisticsOrderVo;
import com.apl.wms.outstorage.operator.pojo.dto.PullOrderKeyDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 *
 * @author cy
 * @since 2020-01-07
 */
@RestController
@RequestMapping("/out-order")
@Validated
@Api(value = "出库订单",tags = "出库订单")
@Slf4j
public class OutOrderController {

    @Autowired
    public OutOrderService outOrderService;

    @Autowired
    public OutOrderCommodityItemService outOrderCommodityItemService;//订单商品业务层 out_order_commodity_item


    @PostMapping(value = "/save-commodity")
    @ApiOperation(value =  "保存商品", notes ="保存商品 ， 锁定商品库存")
    @ResponseBody
    public ResultUtils<String> saveCommodity(OutOrderMainDto outOrderMainDto , @RequestBody  List<OutOrderCommodityItemUpdDto> outOrderCommodityItemUpdDtos) throws Exception {
        ApiParamValidate.validate(outOrderCommodityItemUpdDtos);

        SecurityUser securityUser = CommonContextHolder.getSecurityUser();
        outOrderMainDto.setInnerOrgId(securityUser.getInnerOrgId()); //内部组织id, 生成订单时用到
        outOrderMainDto.setOrderFrom(2); //订单来源:手动下单
        ResultUtils<String> result = outOrderService.saveCommodity(outOrderMainDto , outOrderCommodityItemUpdDtos, 0);

        //System.out.println("saveCommodity code:"+result.getCode()+"  msg:"+result.getMsg());
        return result;
    }

    @PostMapping(value = "/save-dest")
    @ApiOperation(value =  "保存目的地信息", notes ="")
    @ResponseBody
    public ResultUtils<Boolean> saveDestInfo(OutOrderDestUpdDto dto) {

        ApiParamValidate.validate(dto);

        return outOrderService.saveDestInfo(dto, 0l, 2);
    }

    @ResponseBody
    @PostMapping(value = "/upd-status")
    @ApiOperation(value =  "更新订单状态",  notes ="更新订单状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id" , value = "订单id" ,paramType = "query", required = true),
            @ApiImplicitParam(name = "status" , value = "出库订单状态  1创建中  2创建异常  3新建  4已发货  5完成   6取消" ,paramType = "query", required = true)
    })
    public ResultUtils<Boolean> updStatus(Long id,  @Range(min = 1, max = 6, message = "状态值不正确") Integer status) {
        ApiParamValidate.notEmpty("id", id);
        ApiParamValidate.notEmpty("status", status);

        return outOrderService.updStatus(id, status, 0l);
    }

    @ResponseBody
    @PostMapping(value = "/del")
    @ApiOperation(value =  "删除订单" , notes = "")
    @ApiImplicitParam(name = "orderId",value = " 订单id",required = true  , paramType = "query")
    public ResultUtils<Boolean> delById(@NotNull(message = "id不能为空") @Min(value = 1 , message = "id不能小于1") Long orderId) {

        return outOrderService.delById(orderId , 0l);
    }

    @ResponseBody
    @PostMapping(value = "/del-commodity")
    @ApiOperation(value =  "删除商品" , notes = "")
    @ApiImplicitParam(name = "id",value = " id",required = true  , paramType = "query")
    public ResultUtils<Boolean> delCommodity(@NotNull(message = "id不能为空") @Min(value = 1 , message = "id不能小于1") Long id) {

        return outOrderCommodityItemService.delById(id);
    }

    @ResponseBody
    @PostMapping(value = "/get")
    @ApiOperation(value =  "获取订单详细" , notes = "")
    @ApiImplicitParam(name = "id",value = "id",required = true  , paramType = "query")
    public ResultUtils<Map> selectById(@NotNull(message = "id不能为空") @Min(value = 1 , message = "id不能小于1") Long id)  throws Exception{

        return outOrderService.selectById(id, 0l);
    }

    @ResponseBody
    @PostMapping(value = "/get-multi-order")
    @ApiOperation(value =  "获取多个订单信息" , notes = "获取多个订单信息")
    @ApiImplicitParam(name = "ids",value = "订单列表",required = true  , paramType = "query")
    public ResultUtils<List<OrderItemListVo>> getMultiOrderMsg(@NotNull(message = "ids 不能为空")String ids) throws Exception {

        return outOrderService.getMultiOrderMsg(StringUtil.stringToLongList(ids) , OrderStatusEnum.CREATE.getStatus());
    }

    @ResponseBody
    @PostMapping("/get-list")
    @ApiOperation(value =  "分页查找订单详情" , notes = "分页查找订单详情")
    public ResultUtils<OutOrderListResultVo> getList(PageDto pageDto, @Validated OutOrderKeyDto keyDto) throws Exception{

        return outOrderService.getList(pageDto , keyDto);
    }


    @ResponseBody
    @PostMapping("/list-wrong-order")
    @ApiOperation(value =  "获取问题订单" , notes = "获取问题订单")
    public ResultUtils<OutOrderListResultVo> listWrongOrder(PageDto pageDto, @Validated OutOrderKeyDto keyDto) throws Exception{

        ResultUtils<OutOrderListResultVo> outOrderListResultVoResultUtils = outOrderService.listWrongOrder(pageDto, keyDto);

        return outOrderListResultVoResultUtils;
    }


    @ResponseBody
    @PostMapping("/statistics-order")
    @ApiOperation(value =  "订单统计" , notes = "订单统计")
    public ResultUtils<List<StatisticsOrderVo>> statisticsOrder(@Validated PullOrderKeyDto keyDto) throws Exception{

        return outOrderService.statisticsOrder(keyDto);
    }


    @ResponseBody
    @PostMapping("/cancel")
    @ApiOperation(value =  "取消订单" , notes = "取消订单")
    @ApiImplicitParam(name = "orderId",value = "订单id",required = true  , paramType = "query")
    public ResultUtils<Boolean> cancelOrder(@NotNull(message = "orderId 不能为空") @Min(value = 1 , message = "orderId 不能小于1")Long orderId) throws Exception{

        return outOrderService.cancelOrder(orderId);
    }


}
