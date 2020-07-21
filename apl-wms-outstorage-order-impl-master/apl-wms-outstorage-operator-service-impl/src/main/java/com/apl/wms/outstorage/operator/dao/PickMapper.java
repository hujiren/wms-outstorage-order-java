package com.apl.wms.outstorage.operator.dao;
import com.apl.wms.outstorage.operator.pojo.dto.PullOrderKeyDto;
import com.apl.wms.outstorage.operator.pojo.vo.OutOrderPickListVo;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderListVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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


}
