package com.apl.wms.outstorage.order.dao;
import com.apl.wms.outstorage.operator.pojo.dto.PullAllocationItemKeyDto;
import com.apl.wms.outstorage.operator.pojo.po.PullAllocationItemPo;
import com.apl.wms.outstorage.operator.pojo.vo.PullAllocationItemInfoVo;
import com.apl.wms.outstorage.operator.pojo.vo.PullAllocationItemListVo;
import com.apl.wms.outstorage.order.lib.pojo.bo.AllocationWarehouseOrderCommodityBo;
import com.apl.wms.outstorage.order.lib.pojo.bo.AllocationWarehouseOutOrderBo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * <p>
 * 商品下架,订单分配 Mapper 接口
 * </p>
 *
 * @author cy
 * @since 2020-06-09
 */
public interface PullAllocationItemMapper extends BaseMapper<PullAllocationItemPo> {


    /**
     * @Desc: 根据批次id ，获取下架项列表
     * @Author: CY
     * @Date: 2020/6/10 14:24
     */
    List<PullAllocationItemInfoVo> listPullItemByBatchId(@Param("batchId") Long batchId);


    /**
     * @Desc: 根据id 查找详情
     * @Author: ${cfg.author}
     * @Date: 2020-06-09
     */
    public PullAllocationItemInfoVo getById(@Param("id") Long id);


    /**
     * @Desc: 查找列表
     * @Author: ${cfg.author}
     * @Date: 2020-06-09
     */
    List<PullAllocationItemListVo> getList(Page page, @Param("kd") PullAllocationItemKeyDto keyDto);


    /**
     * 批量插入分配明细对象列表
     * @param itemPoList
     * @return
     */
     Integer AllocOutOrderStockCallBack(@Param("itemPoList") List<PullAllocationItemPo> itemPoList);


    /**
     * 修改订单的拣货/分配状态
     * @param outOrderId
     * @return
     */
     Integer updateOrderStatus(@Param("orderId") Long outOrderId, @Param("status") Integer status);




    /**
     * 修改订单的拣货/分配状态
     * @param orderIds
     * @return
     */
    Integer updateOrdersStatus(@Param("orderIds") String orderIds, @Param("minId")Long minId, @Param("maxId") Long maxId, @Param("status") Integer status);



    /**
     * 根据订单Id查询商品id和下单数量
     * @param
     * @return
     */
    List<AllocationWarehouseOrderCommodityBo>  getCommodityInfoById(@Param("orderId") Long orderId);


    /**
     * 根据多个订单id,批量查询商品id和下单数量
     * @param orderIds
     * @param minId
     * @param maxId
     * @return
     */
    List<AllocationWarehouseOrderCommodityBo>  getCommodityInfoByIds(@Param("orderIds") String orderIds, @Param("minId")Long minId, @Param("maxId") Long maxId);


    /**
     * 根据分配仓库传递的一个订单id, 获取订单表中的订单id和仓库id的列表集合
     * @return
     */
    AllocationWarehouseOutOrderBo getOutOrderInfoById(@Param("orderId") Long orderId);


    /**
     * 根据分配仓库时传递的多个订单id, 获取订单表中的订单id和仓库id
     * @param orderIds
     * @param minId
     * @param maxId
     * @return
     */
    List<AllocationWarehouseOutOrderBo> getOutOrderInfoByIds(@Param("orderIds") String orderIds, @Param("minId")Long minId, @Param("maxId") Long maxId);



    Integer updatePullStatus(@Param("outOrderId") Long outOrderId, @Param("pullStatus")Integer pullStatus);


    /**
     * 删除订单分配明细
     * @param outOrderId
     * @return
     */
    Integer deleteByOrderId(@Param("outOrderId") Long outOrderId);
}