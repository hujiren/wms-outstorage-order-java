package com.apl.wms.outstorage.order.dao;


import com.apl.wms.outstorage.order.pojo.po.OutOrderDestPo;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderDestVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 出库订单其他信息 Mapper 接口
 * </p>
 *
 * @author cy
 * @since 2020-01-07
 */
public interface OutOrderAttachmentMapper extends BaseMapper<OutOrderDestPo> {

    /**
     * @Desc: 根据id 查找详情
     * @Author: ${cfg.author}
     * @Date: 2020-01-07
     */
    OutOrderDestVo getById(@Param("id") Long id);



    /**
     * @Desc: 查找客户 订单
     * @Author: CY
     * @Date: 2020/1/9 11:21
     */
    OutOrderDestPo findOrderDetails(@Param("orderId") Long orderId, @Param("customerId") Long customerId);

    /**
     * @Desc: 删除 外部订单子订单项
     * @Author: CY
     * @Date: 2020/1/9 14:28
     */
    Boolean delOutOrderItem(@Param("id") Long id, @Param("customerId") Long customerId);


    OutOrderDestVo exists(@Param("orderId") Long orderId);
}
