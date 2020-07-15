package com.apl.wms.outstorage.operator.dao;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderListVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author hjr start
 * @date 2020/7/13 - 15:46
 */
public interface PickMapper extends BaseMapper<OutOrderListVo> {

    List<OutOrderListVo> getListByOrderSns(@Param("orderSns") List<String> orderSns);

    Integer updateOrderPickingMember(@Param("pullOperatorId") Long pullOperatorId, @Param("ids") List<Long> ids);
}
