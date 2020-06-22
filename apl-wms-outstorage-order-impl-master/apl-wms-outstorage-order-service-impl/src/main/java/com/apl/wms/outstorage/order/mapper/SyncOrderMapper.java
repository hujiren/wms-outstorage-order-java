package com.apl.wms.outstorage.order.mapper;

import com.apl.wms.outstorage.order.lib.pojo.bo.SyncOutOrderTaskBo;
import com.apl.wms.outstorage.order.pojo.dto.SyncOutOrderKeyDto;
import com.apl.wms.outstorage.order.pojo.po.SyncOutOrderPo;
import com.apl.wms.outstorage.order.pojo.vo.SyncOutOrderInfoVo;
import com.apl.wms.outstorage.order.pojo.vo.SyncOutOrderListVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * <p>
 * 同步平台订单 Mapper 接口
 * </p>
 *
 * @author arran
 * @since 2019-12-25
 */
public interface SyncOrderMapper extends BaseMapper<SyncOutOrderPo> {

    /**
     * @Desc: 根据id 查找详情
     * @Author: ${cfg.author}
     * @Date: 2019-12-25
     */
    SyncOutOrderInfoVo getById(@Param("id") Long id);

    /**
     * @Desc: 查找列表
     * @Author: ${cfg.author}
     * @Date: 2019-12-25
     */
    List<SyncOutOrderListVo> getList(Page page, @Param("kd") SyncOutOrderKeyDto keyDto);


    /**
       * @Description : 更新状态
       * @Param ：
       * @Return ：
       * @Author : arran
       * @Date :
    */
    Integer updStatus(@Param("id") Long id, @Param("status") Integer status, @Param("customerId") Long customerId);


    /**
     * @Desc: 检测记录是否重复  Timestamp tspOrderStartTime,
     */
    List<Integer> exists(@Param("id") Long id, @Param("storeId") Long storeId, @Param("orderStartTime") Timestamp orderStartTime, @Param("orderEndTime") Timestamp orderEndTime);


    /**
     * @Description  获取店铺最后一个任务
     * @Param
     * @Return
     * @Author  arran
     * @Date  2019/12/31
     */
    SyncOutOrderInfoVo getLastSync(@Param("storeId") Long storeId);


    SyncOutOrderTaskBo bootTask(@Param("id") Long id, @Param("customerId") Long customerId);


    Integer getStatus(@Param("id") Long id);

}
