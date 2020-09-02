package com.apl.wms.outstorage.order.mapper;

import com.apl.wms.outstorage.order.pojo.po.OutOrderCommodityItemPo;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderCommodityItemInfoVo;
import com.apl.wms.warehouse.lib.pojo.bo.PullBatchOrderItemBo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 出库订单商品 Mapper 接口
 * </p>
 *
 * @author cy
 * @since 2020-01-07
 */
public interface OutOrderCommodityItemMapper extends BaseMapper<OutOrderCommodityItemPo> {

    /**
     * @Desc: 根据订单id 获取子订单项
     * @Author: CY
     * @Date: 2019/12/24 14:26
     */
    List<OutOrderCommodityItemInfoVo> getOrderItemsByOrderId(@Param("orderId") Long orderId);



    /**
     * @Desc: 根据订单id 列表字符串 获取子订单项
     * @Author: CY
     * @Date: 2019/12/24 14:26
     */
    List<OutOrderCommodityItemInfoVo> getOrderItemsByOrderIds(@Param("orderIds") String orderIds);



    /**
     * @Desc: 获取下架订单数量信息
     * @Author: CY
     * @Date: 2020/6/9 10:41
     */
    List<PullBatchOrderItemBo> getPullBatchOrderItem(@Param("orderIds") List<Long> orderIds);


    /**
     * @Desc: 删除子订单
     * @Author: CY
     * @Date: 2020/1/9 14:34
     */
    Boolean delByOrderId(@Param("orderId") Long orderId);



}
