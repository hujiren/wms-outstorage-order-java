package com.apl.wms.outstorage.operator.dao;
import com.apl.wms.outstorage.operator.pojo.bo.CorrelateCommodityBo;
import com.apl.wms.outstorage.operator.pojo.bo.CorrelateCommodityOrderBo;
import com.apl.wms.outstorage.operator.pojo.dto.PullOrderKeyDto;
import com.apl.wms.outstorage.operator.pojo.po.PullBatchCommodityPo;
import com.apl.wms.outstorage.operator.pojo.po.PullBatchPo;
import com.apl.wms.outstorage.operator.pojo.po.StocksPo;
import com.apl.wms.outstorage.operator.pojo.vo.OutOrderPickListVo;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderListVo;
import com.apl.wms.warehouse.po.StorageLocalPo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author hjr start
 * @date 2020/7/13 - 15:46
 */
public interface PickMapper extends BaseMapper<OutOrderListVo> {

    List<OutOrderPickListVo> getListByOrderSns(@Param("orderSns") List<String> orderSns);


    Integer updateOrderPickingMember(@Param("pullOperatorId") Long pullOperatorId, @Param("ids") List<Long> ids);

    /**
     * @Desc: 根据商品/sku 获取订单分拣信息
     * @Author: CY
     * @Date: 2020/6/2 10:16
     */
    List<OutOrderPickListVo> queryOrderPickInfoByPage(Page page , @Param("kd") PullOrderKeyDto keyDto);


    /**
     * 根据批次id获取批量订单id
     * @param batchId
     * @return
     */
    List<Long> getOrderIdsByBatchId(@Param("batchId") Long batchId);


    /**
     * 批量修改订单拣货状态
     * @param ids
     * @param minKey
     * @param maxKey
     * @param pullStatus
     * @return
     */
    Integer updatePullStatus(@Param("ids") String ids, @Param("minKey") Long minKey, @Param("maxKey") Long maxKey, @Param("pullStatus") Integer pullStatus);


    /**
     * 根据批次id修改批次拣货状态和拣货完成时间
     * @param pullBatchPo
     * @return
     */
    Integer updateBatchByBatchId(@Param("po") PullBatchPo pullBatchPo);


    /**
     * 根据库位id获取表中的对应实际库存
     * @param ids 库位id
     * @return
     */
    @MapKey("mapKey")
    Map<Long, Integer> getStorageLocalMap(@Param("ids") StringBuilder ids);


    /**
     * 批量插入批次商品信息对象
     * @param list
     * @return
     */
    Integer batchInsertPullBatchCommodity(@Param("list") List<PullBatchCommodityPo> list);


    /**
     * 批量更新库位库存信息
     * @param newStorageLocalList
     * @return
     */
    Integer batchUpdateStorageLocal(@Param("list") List<StorageLocalPo> newStorageLocalList);




    /**
     * 批量更新总库存对象
     * @param newStocksPoList
     * @return
     */
    Integer batchUpdateStocks(@Param("list") List<StocksPo> newStocksPoList);


    /**
     * 根据商品id关联查询订单号, 订单 id
     * @param list
     * @return
     */
    List<CorrelateCommodityBo> getOrderSnByCommodityId(@Param("list") List<Long> list);


    /**
     * 通过订单id批量查询商品信息关联订单信息
     * @param ids
     * @return
     */
    List<CorrelateCommodityOrderBo> getCommodityInfoByOrderIds(@Param("ids") List<Long> ids);
}
