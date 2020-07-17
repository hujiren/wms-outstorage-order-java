package com.apl.wms.outstorage.order.dao;


import com.apl.wms.outstorage.operator.pojo.vo.OutOrderPickListVo;
import com.apl.wms.outstorage.order.pojo.po.OutOrderPo;
import com.apl.wms.outstorage.order.pojo.dto.OutOrderKeyDto;
import com.apl.wms.outstorage.order.pojo.vo.OrderItemListVo;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderInfoVo;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderListVo;
import com.apl.wms.outstorage.operator.pojo.dto.PullOrderKeyDto;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * <p>
 * 出库订单 Mapper 接口
 * </p>
 *
 * @author cy
 * @since 2020-01-07
 */
public interface OutOrderMapper extends BaseMapper<OutOrderPo> {


    /**
     * @Desc: 根据id 查找详情
     * @Author: ${cfg.author}
     * @Date: 2020-01-07
     */
    OutOrderInfoVo getById(@Param("id") Long id, Long customerId);

    /**
     * @Desc: 根据订单id 获取打包订单信息
     * @Author: CY
     * @Date: 2020/6/15 9:50
     */
    OrderItemListVo getPackOrderMsg(@Param("orderId") Long orderId);

    /**
     * @Desc: 根据id 列表 ，获取订单信息
     * @Author: CY
     * @Date: 2020/6/8 17:27
     */
    List<OrderItemListVo> selectOrderByIds(@Param("ids") List<Long> ids , @Param("orderStatus") Integer orderStatus);

    /**
     * @Desc: 查找订单列表, 查找条件sku和商品名称为空
     * @Author: ${cfg.author}
     * @Date: 2019-12-23
     */
    List<OutOrderListVo> getList(Page page , @Param("kd" ) OutOrderKeyDto keyDto);

    /**
     * @Desc: 根据商品/sku 获取订单分拣信息
     * @Author: CY
     * @Date: 2020/6/2 10:16
     */
    List<OutOrderInfoVo> pageOrderPull(Page page , @Param("kd" ) PullOrderKeyDto keyDto);


    /**
     * @Desc: 分页获取问题订单
     * @Author: CY
     * @Date: 2020/6/2 10:15
     */
    List<OutOrderListVo> listWrongOrder(Page page, @Param("kd" ) OutOrderKeyDto keyDto , @Param("isWrong") Integer isWrong);


    /**
     * @Desc: 获取拣货员对应的订单
     * @Author: CY
     * @Date: 2020/6/8 15:44
     */
    List<OutOrderInfoVo> listOrderByOrderStatusPullStatusAndPullId(@Param("orderStatus" )Integer orderStatus, @Param("pullStatus" )Integer pullStatus,@Param("memberId" ) Long memberId);


    /**
     * @Desc: 查找订单，返回ids
     * @Author: ${cfg.author}
     * @Date: 2019-12-23
     */
    List<Long> getOrderIds(Page page, @Param("kd" ) OutOrderKeyDto keyDto);


    /**
     * @Desc: 根据订单id列表，查找订单列表
     * @Author: ${cfg.author}
     * @Date: 2019-12-23
     */
    List<OutOrderListVo> getListByIds(@Param("ids" ) String ids, @Param("minId" )Long minId, @Param("maxId" ) Long maxId);



    /**
     * @Desc: 批量更新 拣货状态
     * @Author: CY
     * @Date: 2020/6/10 10:50
     */
    Integer batchUpdateOrderPullStatus(@Param("orderIds") List<Long> orderIds, @Param("status") Integer status, @Param("customerId") Long customerId);

    /**
     * @Desc: 判断 订单是否存在
     * @Author: CY
     * @Date: 2020/1/9 14:49
     */
    OutOrderInfoVo exists(@Param("orderId") Long orderId, @Param("customerId") Long customerId);


    //根据参考号查询订单号是否存在
    OutOrderInfoVo existsByRefSn(@Param("referenceSn") String referenceSn, @Param("customerId") Long customerId, @Param("startTime") Timestamp startTime);


    /**
     * 根据多个Id查询列表
     * @param ids
     * @return
     */
    List<OutOrderInfoVo> queryList(@Param("ids") String ids);


    /**
     * 修改订单状态
     * @param
     * @return
     */
    Integer updateOrderStatus(@Param("ids")  List<Long> ids, @Param("status") Integer status, @Param("customerId") Long customerId);

}