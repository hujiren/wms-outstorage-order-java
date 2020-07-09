package com.apl.wms.outstorage.operator.dao;


import com.apl.wms.outstorage.operator.pojo.dto.PullBatchKeyDto;
import com.apl.wms.outstorage.operator.pojo.po.PullBatchPo;
import com.apl.wms.outstorage.operator.pojo.vo.PackOrderItemListVo;
import com.apl.wms.outstorage.operator.pojo.vo.PullBatchInfoVo;
import com.apl.wms.outstorage.operator.pojo.vo.PullBatchListVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * <p>
 * 拣货批次 Mapper 接口
 * </p>
 *
 * @author cy
 * @since 2020-06-08
 */
public interface PullBatchMapper extends BaseMapper<PullBatchPo> {

    /**
     * @Desc: 根据id 查找详情
     * @Author: ${cfg.author}
     * @Date: 2020-06-08
     */
     PullBatchInfoVo getById(@Param("id") Long id);

    /**
     * @Desc: 查找列表
     * @Author: ${cfg.author}
     * @Date: 2020-06-08
     */
    List<PullBatchListVo> getList(Page page, @Param("kd") PullBatchKeyDto keyDto);


    /**
     * @Desc: 根据状态，获取拣货员的批次列表
     * @Author: CY
     * @Date: 2020/6/10 11:06
     */
    List<PullBatchInfoVo> listOperatorBatchByStatus(@Param("operatorId") Long operatorId, @Param("status") Integer status,
                                                    @Param("keyword") String keyword, @Param("batchTime") Timestamp batchTime);

    /**
     * @Desc: 根据订单id 获取批次信息
     * @Author: CY
     * @Date: 2020/6/12 15:25
     */
    PackOrderItemListVo getPullBatchMsg(@Param("orderId") Long orderId);

    /**
     * @Desc:
     * @Author: CY
     * @Date: 2020/6/12 15:33
     */
    List<Long> getBatchOrderListByOrderId(@Param("orderId") Long orderId);

    /**
     * @Desc: 根据批次id 获取订单id 列表
     * @Author: CY
     * @Date: 2020/6/12 17:34
     */
    List<Long> getBatchOrderList(@Param("batchId") Long batchId);

}