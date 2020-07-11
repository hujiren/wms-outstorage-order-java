package com.apl.wms.outstorage.operator.service.impl;

import com.apl.lib.amqp.RabbitSender;
import com.apl.lib.constants.CommonStatusCode;
import com.apl.lib.exception.AplException;
import com.apl.lib.join.JoinKeyValues;
import com.apl.lib.join.JoinUtil;
import com.apl.lib.security.SecurityUser;
import com.apl.lib.utils.CommonContextHolder;
import com.apl.lib.utils.ResultUtil;
import com.apl.lib.utils.SnowflakeIdWorker;
import com.apl.wms.outstorage.operator.dao.PullAllocationItemMapper;
import com.apl.wms.outstorage.operator.pojo.po.PullAllocationItemPo;
import com.apl.wms.outstorage.operator.service.PullAllocationItemService;
import com.apl.wms.outstorage.order.lib.pojo.bo.AllocationWarehouseOrderCommodityBo;
import com.apl.wms.outstorage.order.lib.pojo.bo.AllocationWarehouseOutOrderBo;
import com.apl.wms.warehouse.lib.pojo.bo.CompareStorageLocalStocksBo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
        ORDER_STATUS_UNTRUE("ORDER_STATUS_UNTRUE" ,"该订单不是<已提交状态>的订单"),
        PULL_STATUS_UNTRUE("PULL_STATUS_UNTRUE", "该订单不是<未分配仓库状态>的订单")
        ;

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
    RedisTemplate redisTemplate;



    /**
     * 获取商品id和下单数量以及订单id和仓库id
     * @ 1.根据订单Id获取订单信息对象
     *       2.根据订单Id获取商品对象集合  订单-商品 1对多
     *       3.将商品对象集合添加到订单对象中
     *       4.根据订单Id修改订单拣货状态为分配中
     *
     */
    @Override
    public ResultUtil<AllocationWarehouseOutOrderBo> getOrderForAllocationWarehouseManual(Long outOrderId) throws Exception {

        //根据分配仓库传来的单个订单id获取 订单id, 仓库id  database: out_order  result: orderId, whId
        AllocationWarehouseOutOrderBo orderBo = baseMapper.getOutOrderInfoById(outOrderId);

        StatusCheck(orderBo);

        //获取商品id和下单数量   database:out_order_commodity_item  result: orderId, commodityId, orderQty
        List<AllocationWarehouseOrderCommodityBo> commodityList = baseMapper.getCommodityInfoById(outOrderId);

        //将商品信息对象列表添加到订单信息对象中
        orderBo.setAllocationWarehouseOrderCommodityBoList(commodityList);

        //将分配过来的订单状态修改为2, 分配仓库中
        Integer resultType =  baseMapper.updateOrderStatus(orderBo.getOrderId(), 2);

        if(resultType == 0){
            throw new AplException(CommonStatusCode.SAVE_FAIL.code, CommonStatusCode.SAVE_FAIL.msg);
        }

        orderBo.setPullStatus(2);

        ResultUtil result = ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, orderBo);

        return result;
    }


    /**
     * 批量获取商品id和下单数量以及订单id和仓库id
     * @param: 多个订单id
     * @//
     *      1.将多个订单id集合转化为字符串用逗号分隔
     *      2.根据多个订单Id获取对应的订单信息对象集合
     *      3.根据多个订单Id获取对应的商品信息集合
     *      4.批量更新订单拣货状态为分配中
     *      5.将商品信息集合按照订单Id分组
     *      6.遍历订单信息对象, 将订单对象对应的商品列表添加到对象成员变量
     *      7.循环将每个订单对象发送到消息队列
     */
    @Override
    @Transactional
    public ResultUtil<Boolean> allocationWarehouseForOrderQueueSend(List<Long> orderIds) throws Exception {

        JoinKeyValues joinKeyValues = JoinUtil.getLongKeys(orderIds);

        //根据分配仓库传来的多个订单id获取 订单id, 仓库id列表集合  database: out_order  result: orderId, whId, orderSn
        List<AllocationWarehouseOutOrderBo> orderList =
                baseMapper.getOutOrderInfoByIds(joinKeyValues.getSbKeys().toString(), joinKeyValues.getMinKey(), joinKeyValues.getMaxKey());

        //批量检查订单状态和拣货状态
        for (AllocationWarehouseOutOrderBo outOrderBo : orderList) {
            StatusCheck(outOrderBo);
        }

        //获取商品id和下单数量   database:out_order_commodity_item  result: orderId, commodityId, orderQty
        List<AllocationWarehouseOrderCommodityBo> OrderCommodityList = baseMapper.getCommodityInfoByIds
                (joinKeyValues.getSbKeys().toString(),  joinKeyValues.getMinKey(), joinKeyValues.getMaxKey());


        //批量更新订单状态为2
        Integer integer = baseMapper.updateOrdersStatus(joinKeyValues.getSbKeys().toString(), joinKeyValues.getMinKey(), joinKeyValues.getMaxKey(), 2);
        if(integer == 0){
            return ResultUtil.APPRESULT(CommonStatusCode.SAVE_FAIL, false);
        }

        //将获取的商品信息对象按照订单id分组, orderId为key
        Map<String, List<AllocationWarehouseOrderCommodityBo>> maps =
                JoinUtil.listGrouping(OrderCommodityList, "orderId");

        SecurityUser securityUser = CommonContextHolder.getSecurityUser();

        //遍历订单信息对象, 并将每个商品信息对象组合到订单信息对象中
        for (AllocationWarehouseOutOrderBo outOrderBo : orderList) {

            outOrderBo.setPullStatus(2);

            //获取当前订单信息对象的orderId, 并作为key从分组的map中取出商品信息列表集合组合到订单信息对象中, 使orderId相对应
            List<AllocationWarehouseOrderCommodityBo> commodityBoList = maps.get(outOrderBo.getOrderId().toString());
            outOrderBo.setAllocationWarehouseOrderCommodityBoList(commodityBoList);

            // 循环发送分配的订单对象到队列, 携带安全用户一起发送
            outOrderBo.setSecurityUser(securityUser);
            rabbitSender.send("allocationWarehouseForOrderQueueExchange" ,"allocationWarehouseForOrderQueue" , outOrderBo);
        }

        ResultUtil result = ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, true);

        return result;
    }



    /**
     * 将订单id及构建的分配明细对象写入到分配明细表
     * @param tranId
     * @param outOrderId
     * @param compareStorageLocalStocksBos
     * @return
     */
    @Override
    @Transactional
    public ResultUtil<Integer> AllocOutOrderStockCallBack(String tranId, Long outOrderId, Integer pullStatus, List<CompareStorageLocalStocksBo> compareStorageLocalStocksBos) {

        if(null==compareStorageLocalStocksBos || compareStorageLocalStocksBos.size()==0){
            //分配的库位为空, 代表库存不足, 恢复订单拣货状态为1(未分配库存)
            baseMapper.updateOrderStatus(outOrderId, 1);
            redisTemplate.opsForValue().set(tranId, 1);
            return ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS, 0);
        }
        if(pullStatus == 0) pullStatus = 1;

        Integer integer = baseMapper.updatePullStatus(outOrderId, pullStatus);
        if(integer == 0){
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
        if(resultInteger==0) {
            throw new AplException(CommonStatusCode.SAVE_FAIL);
        }

        Integer result = baseMapper.updateOrderStatus(outOrderId, 3);
        if(result == 0) {
            throw new AplException(CommonStatusCode.SAVE_FAIL);
        }

        redisTemplate.opsForValue().set(tranId, 1);

        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS, integer);
    }


    /**
     * 状态校验
     * @param outOrderBo 订单信息对象
     */
    public void StatusCheck(AllocationWarehouseOutOrderBo outOrderBo){

            if(outOrderBo.getOrderStatus() != 3){
                throw new AplException(PullAllocationItemServiceCode.ORDER_STATUS_UNTRUE.code,
                        PullAllocationItemServiceCode.ORDER_STATUS_UNTRUE.msg + "order_id:" + outOrderBo.getOrderId());

            }else if(outOrderBo.getPullStatus() != 1){
                throw new AplException(PullAllocationItemServiceCode.PULL_STATUS_UNTRUE.code,
                        PullAllocationItemServiceCode.PULL_STATUS_UNTRUE.msg + "order_id:" + outOrderBo.getOrderId());
            }
    }
}
