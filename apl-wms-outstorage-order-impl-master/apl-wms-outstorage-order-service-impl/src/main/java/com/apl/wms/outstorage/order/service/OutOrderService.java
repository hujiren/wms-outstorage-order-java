package com.apl.wms.outstorage.order.service;

import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.operator.pojo.vo.OutOrderPickListVo;
import com.apl.wms.outstorage.order.lib.pojo.bo.OutOrderMultipleBo;
import com.apl.wms.outstorage.order.lib.pojo.dto.OutOrderCommodityItemUpdDto;
import com.apl.wms.outstorage.order.lib.pojo.dto.OutOrderDestUpdDto;
import com.apl.wms.outstorage.order.pojo.dto.OutOrderKeyDto;
import com.apl.wms.outstorage.order.pojo.dto.OutOrderMainDto;
import com.apl.wms.outstorage.order.pojo.po.OutOrderPo;
import com.apl.wms.outstorage.order.pojo.vo.OrderItemListVo;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderInfoVo;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderListResultVo;
import com.apl.wms.outstorage.order.pojo.vo.StatisticsOrderVo;
import com.apl.wms.outstorage.operator.pojo.dto.PullOrderKeyDto;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 出库订单 service接口
 * </p>
 *
 * @author arran
 * @since 2020-01-07
 */
public interface OutOrderService extends IService<OutOrderPo> {



        /**
         * @Desc: 保存商品
         * @author arran
         * @since 2020-01-07
         */
        ResultUtil<String> saveCommodity(OutOrderMainDto outOrderMainDto , List<OutOrderCommodityItemUpdDto> outOrderCommodityItemUpdDtos) throws Exception;



        /**
         * @Desc: 保存目的地信息
         * @author arran
         * @since 2020-01-07
         */
        ResultUtil<Boolean> saveDestInfo(OutOrderDestUpdDto destDto, Long customerId, Integer orderFrom);



        //保存多个订单
        Integer saveOrders(OutOrderMultipleBo outOrderMultipleBo) throws Exception;



        //更新订单状态
        ResultUtil<Boolean> updStatus(Long id, Integer status, Long customerId);



        /**
         * @Desc: 批量更新 拣货状态
         * @Author: CY
         * @Date: 2020/6/10 10:52
         */
        Integer batchUpdateOrderPullStatus(List<Long> orderIds, Integer status, Long customerId);



        /**
         * @Desc: 删除订单
         * @author arran
         * @since 2020-01-07
         */
        ResultUtil<Boolean> delById(Long orderId , Long customerId);



        /**
         * @Desc: 获取订单详细信息
         * @author arran
         * @since 2020-01-07
         */
        ResultUtil<Map> selectById(Long id, Long customerId)  throws Exception;



        /**
         * @Desc: 获取订单打包详细
         * @Author: CY
         * @Date: 2020/6/13 12:10
         */
        ResultUtil<OrderItemListVo> getOrderPackMsg(Long orderId) throws Exception;



        /**
         * @Desc: 获取多个订单信息
         * @Author: CY
         * @Date: 2020/6/8 16:32
         */
        ResultUtil<List<OrderItemListVo>> getMultiOrderMsg(List<Long> orderIds , Integer orderStatus) throws Exception;



        /**
         * @Desc: 查询
         * @author arran
         * @since 2020-01-07
         */
        ResultUtil<OutOrderListResultVo>getList(PageDto pageDto, OutOrderKeyDto keyDto)  throws Exception;



        /**
         * @Desc: 获取问题订单
         * @Author: CY
         * @Date: 2020/6/2 10:11
         */
        ResultUtil<OutOrderListResultVo> listWrongOrder(PageDto pageDto, OutOrderKeyDto keyDto) throws Exception;



        /**
         * @Desc: 获取某个拣货员 的订单列表
         * @Author: CY
         * @Date: 2020/6/8 10:10
         */
        ResultUtil<List<OutOrderInfoVo>> listOperatorOrders() throws Exception;



        /**
         * @Desc: 分页获取订单捡货信息
         * @Author: CY
         * @Date: 2020/6/1 11:39
         */
        ResultUtil<Page<OutOrderPickListVo>> queryOrderPickInfoByPage(PageDto pageDto, PullOrderKeyDto keyDto) throws Exception;



        /**
         * @Desc: 统计订单信息，包括订单类型 -- > 对应数量
         * @Author: CY
         * @Date: 2020/6/1 17:34
         */
        ResultUtil<List<StatisticsOrderVo>> statisticsOrder(PullOrderKeyDto keyDto);



        /**
         * @Desc: 分配 订单给对应的拣货员
         * @Author: CY
         * @Date: 2020/1/13 9:54
         */
        ResultUtil<Boolean> allocationOperator(Long memberId, String orderIdList);



        /**
         * @Desc: 订单拣货分配取消
         * @Author: CY
         * @Date: 2020/1/13 9:54
         */
        ResultUtil<Boolean> cancelAllocationOperator(Long memberId, String orderIdList);



        /**
         * @Desc: 取消订单
         * @Author: CY
         * @Date: 2020/5/30 10:15
         */
        ResultUtil<String> cancelOrder(List<Long> orderIds) throws Exception;



        /**
         * 提交订单
         * @param
         * @return
         */
        ResultUtil<Boolean> commitOrder(List<Long> outOrderIds, Long customerId) throws Exception;
}
