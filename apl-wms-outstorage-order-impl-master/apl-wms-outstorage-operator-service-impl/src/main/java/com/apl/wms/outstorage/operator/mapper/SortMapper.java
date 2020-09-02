package com.apl.wms.outstorage.operator.mapper;

import com.apl.wms.outstorage.operator.pojo.po.PullBatchPo;
import com.apl.wms.outstorage.operator.pojo.vo.OrderCommodityScanVo;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderCommodityItemInfoVo;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderListVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author hjr start
 * @date 2020/7/28 - 9:55
 */
@Repository
@Mapper
public interface SortMapper extends BaseMapper<OrderCommodityScanVo> {

    /**
     * 根据订单号获取批次id
     * @param orderSn
     * @return
     */
    Long getBatchIdByOrderSn(@Param("orderSn") String orderSn);


    /**
     * 通过批次号获取所有订单id
     * @param batchId
     * @return
     */
    List<Long> getOrderIdsByBatchId(@Param("batchId") Long batchId);


    /**
     * 根据订单id查询所有订单详情
     * @param
     * @return
     */
    List<OutOrderListVo> getOrderInfoByIds(@Param("ids") String ids);


    /**
     * 根据订单id查询所有商品详情
     * @param ids
     * @return
     */
    List<OutOrderCommodityItemInfoVo> getCommodityInfoByIds(@Param("ids") String ids);


    /**
     * 根据订单Id批量修改订单状态
     * @param ids
     * @return
     */
    Integer batchUpdateOrderStatus(@Param("ids") List<Long> ids);


    /**
     * 根据订单Id获取批次id
     * @param orderId
     * @return
     */
    Long getBatchIdByOrderId(@Param("orderId") Long orderId);


    /**
     * 根据批次id修改批次表信息
     * @param
     * @return
     */
    Integer updateBatchInfo(@Param("pullBatchPo") PullBatchPo pullBatchPo);


    /**
     * 根据订单Id查询订单拣货状态
     * @param ids
     * @return
     */
    List<Integer> getPullStatusByOrderIds(@Param("ids") List<Long> ids);
}
