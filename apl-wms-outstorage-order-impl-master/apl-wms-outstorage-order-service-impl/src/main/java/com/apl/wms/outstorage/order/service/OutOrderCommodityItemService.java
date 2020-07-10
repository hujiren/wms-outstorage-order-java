package com.apl.wms.outstorage.order.service;

import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.order.lib.pojo.bo.AllocationWarehouseOutOrderBo;
import com.apl.wms.outstorage.order.lib.pojo.dto.OutOrderCommodityItemUpdDto;
import com.apl.wms.outstorage.order.pojo.po.OutOrderCommodityItemPo;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderCommodityItemInfoVo;
import com.apl.wms.warehouse.lib.pojo.bo.PlatformOutOrderStockBo;
import com.apl.wms.warehouse.lib.pojo.bo.PullBatchOrderItemBo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 出库订单商品 service接口
 * </p>
 *
 * @author cy
 * @since 2020-01-07
 */
public interface OutOrderCommodityItemService extends IService<OutOrderCommodityItemPo> {


        /**
         * @Desc: 获取多个商品
         * @Author: arran
         * @Date: 2019/12/24 14:24
         */
        List<OutOrderCommodityItemInfoVo> getOrderItemsByOrderId(Long orderId);

        /**
         * @Desc: 获取多个商品
         * @Author: arran
         * @Date: 2019/12/24 14:24
         */
        List<OutOrderCommodityItemInfoVo> getOrderItemsByOrderIds(String orderIds);


        /**
         * @Desc: 保存商品项目
         * @Author: CY
         * @Date: 2020/1/7 18:34
         */
        PlatformOutOrderStockBo saveItems(Long orderId , Long whId , List<OutOrderCommodityItemUpdDto> outOrderCommodityItemUpdDtos) throws Exception;




        /**
         * @Desc: 删除行
         * @author arran
         * @since 2020-01-07
         */
        ResultUtil<Boolean> delById(Long id);


        /**
         * @Desc: 删除整个订单商品
         * @Author: CY
         * @Date: 2020/1/9 14:34
         */
        Boolean delByOrderId(Long orderId);

        /**
         * @Desc: 获取捡货id 列表
         * @Author: CY
         * @Date: 2020/6/9 10:48
         */
        List<PullBatchOrderItemBo> getPullBatchOrderItem(List<Long> orderIds);




}
