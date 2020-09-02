package com.apl.wms.outstorage.operator.mapper;

import com.apl.wms.outstorage.operator.pojo.dto.PullMaterialsDto;
import com.apl.wms.outstorage.operator.pojo.dto.PullPackItemDto;
import com.apl.wms.outstorage.operator.pojo.vo.OrderRecordVo;
import com.apl.wms.outstorage.operator.pojo.vo.OutOrderAttachInfoVo;
import com.apl.wms.outstorage.operator.pojo.vo.PackCommodityInfoVo;
import com.apl.wms.outstorage.operator.pojo.vo.PackingInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author hjr start
 * @date 2020/7/28 - 14:58
 */
@Repository
@Mapper
public interface PackMapper extends BaseMapper<PackingInfo> {

    /**
     * 根据订单号查询物流详情
     * @param orderSn
     * @return
     */
    OutOrderAttachInfoVo getOrderAttachInfoByOrderSn(@Param("orderSn") String orderSn);

    /**
     * 通过订单号查询商品详情
     * @param orderSn
     * @return
     */
    List<PackCommodityInfoVo> getCommodityInfoByOrderSn(@Param("orderSn") String orderSn);

    /**
     * 根据订单获取订单拣货状态列表
     * @param orderIds
     * @return
     */
    List<Integer> getPullStatusByIds(@Param("orderIds") List<Long> orderIds);

    /**
     * 批量插入订单包装材料数据
     * @param list
     * @return
     */
    Integer insertPullMaterials(@Param("list") List<PullMaterialsDto> list);

    /**
     * 批量修改订单状态为8
     * @param orderIds
     * @return
     */
    Integer updatePullStatusByIds(@Param("orderIds") List<Long> orderIds);

    /**
     * 批量插入打包尺寸数据
     * @param list
     * @return
     */
    Integer insertPullPackItem(@Param("list") List<PullPackItemDto> list);


    /**
     * 获取订单记录
     * @return
     */
    List<OrderRecordVo> getOrderRecord(@Param("orderIds") List<Long> orderIds);


    /**
     * 根据拣货员和时间戳查询批次和订单
     * @param memberId
     * @param timestamp
     * @return
     */
    List<Long> getOrderIdByTimestamp(@Param("memberId") Long memberId, @Param("timestamp") Timestamp timestamp);
}
