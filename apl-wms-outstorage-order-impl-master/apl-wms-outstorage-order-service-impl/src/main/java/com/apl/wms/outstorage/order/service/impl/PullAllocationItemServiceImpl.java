package com.apl.wms.outstorage.order.service.impl;


import com.apl.amqp.RabbitMqUtil;
import com.apl.amqp.RabbitSender;
import com.apl.cache.AplCacheUtil;
import com.apl.lib.constants.CommonStatusCode;
import com.apl.lib.exception.AplException;
import com.apl.lib.join.JoinBase;
import com.apl.lib.join.JoinFieldInfo;
import com.apl.lib.join.JoinKeyValues;
import com.apl.lib.join.JoinUtil;
import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.security.SecurityUser;
import com.apl.lib.utils.CommonContextHolder;
import com.apl.lib.utils.ResultUtil;
import com.apl.lib.utils.SnowflakeIdWorker;
import com.apl.sys.lib.cache.JoinCustomer;
import com.apl.sys.lib.feign.InnerFeign;
import com.apl.wms.outstorage.operator.pojo.dto.StockManageKeyDto;
import com.apl.wms.outstorage.operator.pojo.po.PullAllocationItemPo;
import com.apl.wms.outstorage.operator.pojo.vo.OutOrderPickListVo;
import com.apl.wms.outstorage.order.dao.PullAllocationItemMapper;
import com.apl.wms.outstorage.order.service.PullAllocationItemService;
import com.apl.wms.outstorage.order.lib.pojo.bo.AllocationWarehouseOrderCommodityBo;
import com.apl.wms.outstorage.order.lib.pojo.bo.AllocationWarehouseOutOrderBo;
import com.apl.wms.warehouse.lib.cache.JoinWarehouse;
import com.apl.wms.warehouse.lib.cache.OperatorCacheBo;
import com.apl.wms.warehouse.lib.feign.WarehouseFeign;
import com.apl.wms.warehouse.lib.pojo.bo.CompareStorageLocalStocksBo;
import com.apl.wms.warehouse.lib.utils.WmsWarehouseUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author hjr start
 * @date 2020/7/7 - 11:05
 */

@Service
@Slf4j
public class PullAllocationItemServiceImpl extends ServiceImpl<PullAllocationItemMapper, PullAllocationItemPo> implements PullAllocationItemService {


    //状态code枚举
    enum PullAllocationItemServiceCode {
        ORDER_IS_ALLOCATING("ORDER_IS_ALLOCATING", "该订单正在分配库位中"),
        ORDER_IS_ALLOCATED_STORAGE("ORDER_IS_ALLOCATED_STORAGE", "订单已经分配库位"),
        PULL_STATUS_IS_WRONG("PULL_STATUS_IS_WRONG", "该订单拣货状态错误"),
        YOUR_ORDER_NOT_YET_ALLOCATED("YOUR_ORDER_NOT_YET_ALLOCATED", "您的订单尚未被分配, 请稍后再试"),
        YOUR_ORDER_HAS_BEEN_ALLOCATED_PICKING_MEMBER("YOUR_ORDER_HAS_BEEN_ALLOCATED_PICKING_MEMBER", "您的订单已经分配拣货员, 无法取消分配");

        private String code;
        private String msg;

        PullAllocationItemServiceCode(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }

    @Autowired
    RabbitSender rabbitSender;

    @Autowired
    RabbitMqUtil rabbitMqUtil;

    @Autowired
    AplCacheUtil redisTemplate;

    static JoinFieldInfo joinCustomerFieldInfo = null; //跨项目跨库关联 客户表 反射字段缓存
    static JoinFieldInfo joinWareHouseFieldInfo = null;

    @Autowired
    WarehouseFeign warehouseFeign;

    @Autowired
    InnerFeign innerFeign;
    /**
     * 获取商品id和下单数量以及订单id和仓库id
     *
     * @ 1.根据订单Id获取订单信息对象
     * 2.根据订单Id获取商品对象集合  订单-商品 1对多
     * 3.将商品对象集合添加到订单对象中
     * 4.根据订单Id修改订单拣货状态为分配中
     */
    @Override
    public ResultUtil<AllocationWarehouseOutOrderBo> getOrderForAllocationWarehouseManual(Long outOrderId) {

        //根据分配仓库传来的单个订单id获取 订单id, 仓库id  database: out_order  result: orderId, whId
        AllocationWarehouseOutOrderBo orderBo = baseMapper.getOutOrderInfoById(outOrderId);

        if (orderBo.getPullStatus() != 1) {

            if (orderBo.getPullStatus() == 2) {
                throw new AplException(PullAllocationItemServiceCode.ORDER_IS_ALLOCATING.code,
                        PullAllocationItemServiceCode.ORDER_IS_ALLOCATING.msg + "order_id:" + orderBo.getOrderId());

            } else {

                throw new AplException(PullAllocationItemServiceCode.ORDER_IS_ALLOCATED_STORAGE.code,
                        PullAllocationItemServiceCode.ORDER_IS_ALLOCATED_STORAGE.msg + "order_id:" + orderBo.getOrderId());

            }
        }

        ResultUtil<AllocationWarehouseOutOrderBo> result = this.buildOutOrderBoForAllocStock(outOrderId, orderBo, 2);

        return result;
    }


    /**
     * 获取单个订单信息详情
     *
     * @param outOrderId
     * @return
     */
    @Override
    public ResultUtil<AllocationWarehouseOutOrderBo> getOrderForCancelAllocationWarehouseManual(Long outOrderId) {

        //根据分配仓库传来的单个订单id获取 订单id, 仓库id  database: out_order  result: orderId, whId, order_sn, pull_status, order_status
        AllocationWarehouseOutOrderBo orderBo = baseMapper.getOutOrderInfoById(outOrderId);

        if (orderBo.getPullStatus() < 3) {

            throw new AplException(PullAllocationItemServiceCode.YOUR_ORDER_NOT_YET_ALLOCATED.code,
                    PullAllocationItemServiceCode.YOUR_ORDER_NOT_YET_ALLOCATED.msg, null);

        }

        if (orderBo.getPullStatus() > 3) {

            throw new AplException(PullAllocationItemServiceCode.YOUR_ORDER_HAS_BEEN_ALLOCATED_PICKING_MEMBER.code,
                    PullAllocationItemServiceCode.YOUR_ORDER_HAS_BEEN_ALLOCATED_PICKING_MEMBER.msg, null);

        }

        ResultUtil<AllocationWarehouseOutOrderBo> result = this.buildOutOrderBoForAllocStock(outOrderId, orderBo, 1);

        return result;
    }


    /**
     * 查询并构建订单信息对象
     *
     * @param outOrderId
     * @param orderBo
     * @param PullStatus
     * @return
     */
    public ResultUtil<AllocationWarehouseOutOrderBo> buildOutOrderBoForAllocStock(Long outOrderId, AllocationWarehouseOutOrderBo orderBo, Integer PullStatus) {

        //获取商品id和下单数量   database:out_order_commodity_item  result: orderId, commodityId, orderQty
        List<AllocationWarehouseOrderCommodityBo> commodityList = baseMapper.getCommodityInfoById(outOrderId);

        //将商品信息对象列表添加到订单信息对象中
        orderBo.setAllocationWarehouseOrderCommodityBoList(commodityList);

        //将分配过来的订单状态修改, 分配仓库中
        Integer resultType = baseMapper.updateOrderStatus(orderBo.getOrderId(), PullStatus);

        if (resultType == 0) {
            throw new AplException(CommonStatusCode.SAVE_FAIL.code, CommonStatusCode.SAVE_FAIL.msg);
        }

        orderBo.setPullStatus(PullStatus);

        ResultUtil result = ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, orderBo);

        return result;

    }


    /**
     * 批量获取商品id和下单数量以及订单id和仓库id
     *
     * @param: 多个订单id
     * @// 1.将多个订单id集合转化为字符串用逗号分隔
     * 2.根据多个订单Id获取对应的订单信息对象集合
     * 3.根据多个订单Id获取对应的商品信息集合
     * 4.批量更新订单拣货状态为分配中
     * 5.将商品信息集合按照订单Id分组
     * 6.遍历订单信息对象, 将订单对象对应的商品列表添加到对象成员变量
     * 7.循环将每个订单对象发送到消息队列
     */
    @Override
    public ResultUtil<Boolean> allocationWarehouseForOrderQueueSend(List<Long> orderIds) throws Exception {

        ResultUtil<Boolean> booleanResultUtil = this.buildOutOrderBoForAllocStocks(orderIds, 2, 1);

        return booleanResultUtil;
    }


    @Override
    public ResultUtil<Boolean> cancelAllocationWarehouseForOrderQueueSend(List<Long> orderIds) throws Exception {

        ResultUtil<Boolean> booleanResultUtil = this.buildOutOrderBoForAllocStocks(orderIds, 1, 3);

        return booleanResultUtil;
    }


    public ResultUtil<Boolean> buildOutOrderBoForAllocStocks(List<Long> orderIds, Integer setPullStatus, Integer getPullStatus) throws Exception {


        JoinKeyValues joinKeyValues = JoinUtil.getLongKeys(orderIds);

        //根据分配仓库传来的多个订单id获取 订单id, 仓库id列表集合  database: out_order  result: orderId, whId, orderSn
        List<AllocationWarehouseOutOrderBo> orderList =
                baseMapper.getOutOrderInfoByIds(joinKeyValues.getSbKeys().toString(), joinKeyValues.getMinKey(), joinKeyValues.getMaxKey());

        //批量检查订单状态和拣货状态
        List<Long> orderIds2 = new ArrayList<>();
        for (AllocationWarehouseOutOrderBo outOrderBo : orderList) {
            if (outOrderBo.getPullStatus() == getPullStatus)
                orderIds2.add(outOrderBo.getOrderId());
        }

        if (orderIds2.size() == 0)
            return ResultUtil.APPRESULT(PullAllocationItemServiceCode.PULL_STATUS_IS_WRONG.code,
                    PullAllocationItemServiceCode.PULL_STATUS_IS_WRONG.msg, false);

        joinKeyValues = JoinUtil.getLongKeys(orderIds2);

        //获取商品id和下单数量   database:out_order_commodity_item  result: orderId, commodityId, orderQty
        List<AllocationWarehouseOrderCommodityBo> OrderCommodityList = baseMapper.getCommodityInfoByIds
                (joinKeyValues.getSbKeys().toString(), joinKeyValues.getMinKey(), joinKeyValues.getMaxKey());

        //将获取的商品信息对象按照订单id分组, orderId为key
        Map<String, List<AllocationWarehouseOrderCommodityBo>> maps =
                JoinUtil.listGrouping(OrderCommodityList, "orderId");

        SecurityUser securityUser = CommonContextHolder.getSecurityUser();

        Channel channel = rabbitMqUtil.createChannel("1", true);

        try {
            //遍历订单信息对象, 并将每个商品信息对象组合到订单信息对象中
            for (AllocationWarehouseOutOrderBo outOrderBo : orderList) {
                if (outOrderBo.getPullStatus() == getPullStatus) {
                    outOrderBo.setPullStatus(setPullStatus);

                    //获取当前订单信息对象的orderId, 并作为key从分组的map中取出商品信息列表集合组合到订单信息对象中, 使orderId相对应
                    List<AllocationWarehouseOrderCommodityBo> commodityBoList = maps.get(outOrderBo.getOrderId().toString());
                    outOrderBo.setAllocationWarehouseOrderCommodityBoList(commodityBoList);

                    // 循环发送分配的订单对象到队列, 携带安全用户一起发送
                    outOrderBo.setSecurityUser(securityUser);

                    if (outOrderBo.getPullStatus() == 2) {

                        rabbitSender.send("allocationWarehouseForOrderQueueExchange", "allocationWarehouseForOrderQueue", outOrderBo);
                        rabbitMqUtil.send(channel, "allocationWarehouseForOrderQueue", outOrderBo);

                    } else if (outOrderBo.getPullStatus() == 1) {

                        rabbitSender.send("cancelAllocWarehouseForOrderQueueExchange", "cancelAllocWarehouseForOrderQueue", outOrderBo);
                        rabbitMqUtil.send(channel, "cancelAllocWarehouseForOrderQueue", outOrderBo);
                    }
                }
            }

            //批量更新拣货状态
            Integer integer = baseMapper.updateOrdersStatus(joinKeyValues.getSbKeys().toString(), joinKeyValues.getMinKey(), joinKeyValues.getMaxKey(), setPullStatus);
            if (integer == 0) {
                return ResultUtil.APPRESULT(CommonStatusCode.SAVE_FAIL, false);
            }

            channel.txCommit(); // 提交amqp事务
        }
        catch (Exception e){
            e.printStackTrace();
            channel.txRollback(); // 回滚amqp事务
        }

        ResultUtil result = ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS, true);

        return result;
    }


    /**
     * 将订单id及构建的分配明细对象写入到分配明细表
     *
     * @param tranId
     * @param outOrderId
     * @param compareStorageLocalStocksBos
     * @return
     */
    @Override
    @Transactional
    public ResultUtil<Integer> AllocOutOrderStockCallBack(String tranId, Long outOrderId, Integer pullStatus, List<CompareStorageLocalStocksBo> compareStorageLocalStocksBos) {

        if (null == compareStorageLocalStocksBos || compareStorageLocalStocksBos.size() == 0) {
            //分配的库位为空, 代表库存不足, 恢复订单拣货状态为1(未分配库存)
            baseMapper.updateOrderStatus(outOrderId, 1);
            redisTemplate.opsForValue().set(tranId, 1);
            return ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS, 0);
        }

        Integer integer = baseMapper.updatePullStatus(outOrderId, pullStatus);
        if (integer == 0) {
            throw new AplException(CommonStatusCode.SAVE_FAIL.code, CommonStatusCode.SAVE_FAIL.msg, null);
        }

        List<PullAllocationItemPo> itemPoList = new ArrayList<>();

        for (CompareStorageLocalStocksBo stock : compareStorageLocalStocksBos) {
            PullAllocationItemPo po = new PullAllocationItemPo();
            po.setId(SnowflakeIdWorker.generateId());
            po.setAllocationQty(stock.getAllocationQty());
            po.setOutOrderId(outOrderId);
            po.setCommodityId(stock.getCommodityId());
            po.setStorageLocalId(stock.getStorageLocalId());
            itemPoList.add(po);
        }

        Integer resultInteger = baseMapper.AllocOutOrderStockCallBack(itemPoList);
        if (resultInteger == 0) {
            throw new AplException(CommonStatusCode.SAVE_FAIL);
        }

        Integer result = baseMapper.updateOrderStatus(outOrderId, 3);
        if (result == 0) {
            throw new AplException(CommonStatusCode.SAVE_FAIL);
        }

        redisTemplate.opsForValue().set(tranId, 1);

        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS, 1);
    }


    /**
     * 删除订单分配明细
     *
     * @param outOrderId
     * @return
     */
    @Override
    public ResultUtil<Integer> deleteOrderAllocationItem(Long outOrderId, String tranId) {

        Integer integer = baseMapper.deleteByOrderId(outOrderId);

        if (integer == 0) {
            return ResultUtil.APPRESULT(CommonStatusCode.DEL_FAIL, CommonStatusCode.DEL_FAIL);
        }
        redisTemplate.opsForValue().set(tranId, 1);
        return ResultUtil.APPRESULT(CommonStatusCode.DEL_SUCCESS,  1);
    }


    @Override
    public ResultUtil<Page<OutOrderPickListVo>> stockManage(PageDto pageDto, StockManageKeyDto keyDto) throws Exception {


        OperatorCacheBo operatorCacheBo = WmsWarehouseUtils.checkOperator(warehouseFeign, redisTemplate);

        Long whId = operatorCacheBo.getWhId();

        if (null != whId && whId != 0) {
            keyDto.setWhId(whId);
        }

        Page<OutOrderPickListVo> page = new Page();
        page.setCurrent(pageDto.getPageIndex());
        page.setSize(pageDto.getPageSize());


        List<OutOrderPickListVo> outOrderInfoList = baseMapper.queryOrderPickInfoByPage(page, keyDto);

        page.setRecords(outOrderInfoList);


        //跨项目跨库关联表数组
        List<JoinBase> joinTabs = new ArrayList<>();

        //关联客户表字段信息
        JoinCustomer joinCustomer = new JoinCustomer(1, innerFeign, redisTemplate);

        if (null != joinCustomerFieldInfo) {

            joinCustomer.setJoinFieldInfo(joinCustomerFieldInfo);

        } else {

            joinCustomer.addField("customerId", Long.class, "customerName", String.class);

            joinCustomerFieldInfo = joinCustomer.getJoinFieldInfo();

        }

        joinTabs.add(joinCustomer);


        //关联仓库表字段信息
        JoinWarehouse joinWarehouse = new JoinWarehouse(1, warehouseFeign, redisTemplate);
        if (null != joinWareHouseFieldInfo) {
            joinWarehouse.setJoinFieldInfo(joinWareHouseFieldInfo);
        } else {
            joinWarehouse.addField("whId", Long.class, "whName", "whName", String.class);
            joinWareHouseFieldInfo = joinWarehouse.getJoinFieldInfo();
        }
        joinTabs.add(joinWarehouse);

        //执行跨项目跨库关联
        JoinUtil.join(outOrderInfoList, joinTabs);


        ResultUtil result = ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, page);

        return result;
    }
}
