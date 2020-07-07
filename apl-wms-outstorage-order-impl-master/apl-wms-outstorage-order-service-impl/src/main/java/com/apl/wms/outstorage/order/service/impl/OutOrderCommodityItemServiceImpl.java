package com.apl.wms.outstorage.order.service.impl;

import com.apl.lib.amqp.RabbitSender;
import com.apl.lib.constants.CommonStatusCode;
import com.apl.lib.exception.AplException;
import com.apl.lib.join.JoinKeyValues;
import com.apl.lib.join.JoinUtils;
import com.apl.lib.utils.ResultUtil;
import com.apl.lib.utils.SnowflakeIdWorker;
import com.apl.wms.outstorage.order.lib.pojo.bo.AllocationWarehouseOrderCommodityBo;
import com.apl.wms.outstorage.order.lib.pojo.bo.AllocationWarehouseOutOrderBo;
import com.apl.wms.outstorage.order.lib.pojo.dto.OutOrderCommodityItemUpdDto;
import com.apl.wms.outstorage.order.mapper.OutOrderCommodityItemMapper;
import com.apl.wms.outstorage.order.pojo.po.OutOrderCommodityItemPo;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderCommodityItemInfoVo;
import com.apl.wms.outstorage.order.service.OutOrderCommodityItemService;
import com.apl.wms.warehouse.lib.cache.CommodityCacheBo;
import com.apl.wms.warehouse.lib.cache.JoinCommodity;
import com.apl.wms.warehouse.lib.feign.WarehouseFeign;
import com.apl.wms.warehouse.lib.pojo.bo.PlatformOutOrderStockBo;
import com.apl.wms.warehouse.lib.pojo.bo.PullBatchOrderItemBo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


/**
 * <p>
 * 出库订单商品 service实现类
 * </p>
 *
 * @author cy
 * @since 2020-01-07
 */
@Service
@Slf4j
public class OutOrderCommodityItemServiceImpl extends ServiceImpl<OutOrderCommodityItemMapper, OutOrderCommodityItemPo> implements OutOrderCommodityItemService {

    //状态code枚举
    enum OutOrderCommodityItemServiceCode {

        COMMODITY_NOT_EXIST("COMMODITY_NOT_EXIST", "商品不存在"),
        UPDATE_OUT_ORDER_ITEM_ERROR("UPDATE_OUT_ORDER_ITEM_ERROR", "更新出库商品项目失败");;



        private String code;
        private String msg;

        OutOrderCommodityItemServiceCode(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }

    @Autowired
    RabbitSender rabbitSender;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    WarehouseFeign warehouseFeign;

    @Override
    public List<OutOrderCommodityItemInfoVo> getOrderItemsByOrderId(Long orderId) {

        return baseMapper.getOrderItemsByOrderId(orderId);

    }

    @Override
    public List<OutOrderCommodityItemInfoVo> getOrderItemsByOrderIds(String orderIds) {

        return baseMapper.getOrderItemsByOrderIds(orderIds);

    }

    /**
     * 保存商品
     * @param orderId
     * @param whId
     * @param commodityItemsDto
     * @return
     */
    @Override
    public PlatformOutOrderStockBo saveItems(Long orderId , Long whId , List<OutOrderCommodityItemUpdDto> commodityItemsDto) {

        JoinCommodity joinCommodity = new JoinCommodity(1, warehouseFeign, redisTemplate);

        PlatformOutOrderStockBo platformOutOrderStockBo = new PlatformOutOrderStockBo();
        platformOutOrderStockBo.setWhId(whId);

        for (OutOrderCommodityItemUpdDto commodityItemDto : commodityItemsDto) {
            OutOrderCommodityItemPo entity = new OutOrderCommodityItemPo();

            if (commodityItemDto.getItemId() == null) {

                CommodityCacheBo commodityCacheBo = joinCommodity.getEntity(commodityItemDto.getCommodityId());
                if (commodityCacheBo == null) {
                    //商品不存在
                    throw new AplException(OutOrderCommodityItemServiceCode.COMMODITY_NOT_EXIST.code, OutOrderCommodityItemServiceCode.COMMODITY_NOT_EXIST.msg);
                }
                //商品持久化对象
                entity.setCommoditySpec(commodityItemDto.getCommoditySpec());
                entity.setCommodityId(commodityCacheBo.getId());
                entity.setCommoditySku(commodityCacheBo.getSku());
                entity.setCommodityName(commodityCacheBo.getCommodityName());
                entity.setCommodityNameEn(commodityCacheBo.getCommodityNameEn());
                entity.setOrderQty(commodityItemDto.getOrderQty());
                entity.setOrderId(orderId);
                entity.setId(SnowflakeIdWorker.generateId());
                save(entity);// 封装了baseMapper.insert();

                buildOrderItemStock(platformOutOrderStockBo, commodityItemDto.getCommodityId(), commodityItemDto.getOrderQty());

            } else {
                OutOrderCommodityItemPo outOrderCommodityItemPo = baseMapper.selectById(commodityItemDto.getItemId());

                if (outOrderCommodityItemPo == null) {
                    //订单子项不存在
                    throw new AplException(CommonStatusCode.SAVE_FAIL);
                }
                outOrderCommodityItemPo.setId(commodityItemDto.getItemId());
                outOrderCommodityItemPo.setOrderQty(commodityItemDto.getOrderQty());
                updateById(outOrderCommodityItemPo);
                buildOrderItemStock(platformOutOrderStockBo, commodityItemDto.getCommodityId(), outOrderCommodityItemPo.getOrderQty() - commodityItemDto.getOrderQty());

            }
        }


        return platformOutOrderStockBo;
    }



    @Override
    public ResultUtil<Boolean> delById(Long id) {

        boolean flag = removeById(id);
        if (flag) {
            return ResultUtil.APPRESULT(CommonStatusCode.DEL_SUCCESS, true);
        }

        return ResultUtil.APPRESULT(CommonStatusCode.DEL_FAIL, false);
    }


    @Override
    public Boolean delByOrderId(Long orderId) {

        return baseMapper.delByOrderId(orderId);
    }

    @Override
    public List<PullBatchOrderItemBo> getPullBatchOrderItem(List<Long> orderIds) {

        return baseMapper.getPullBatchOrderItem(orderIds);
    }


    private void buildOrderItemStock(PlatformOutOrderStockBo platformOutOrderStockBo , Long commodityId, Integer changeCount) {

        PlatformOutOrderStockBo.PlatformOutOrderStock platformOutOrderStock = new PlatformOutOrderStockBo.PlatformOutOrderStock();
        platformOutOrderStock.setCommodityId(commodityId);
        platformOutOrderStock.setChangeCount(changeCount);

        if(changeCount != 0){
            platformOutOrderStockBo.getPlatformOutOrderStocks().add(platformOutOrderStock);
        }

    }


    /**
     * 获取商品id和下单数量以及订单id和仓库id
     * @param: 多个订单id
     * @return
     */
    @Override
    public ResultUtil<List<AllocationWarehouseOutOrderBo>> getOrdersByAllocationWarehouse(List<Long> orderIds) throws Exception {

        JoinKeyValues joinKeyValues = JoinUtils.getLongKeys(orderIds);
        //根据分配仓库传来的多个订单id获取 订单id, 仓库id列表集合  database: out_order  result: orderId, whId
        List<AllocationWarehouseOutOrderBo> allocationWarehouseOutOrderList =
                baseMapper.getOutOrderEntityByIds(joinKeyValues.getSbKeys().toString(), joinKeyValues.getMinKey(), joinKeyValues.getMaxKey());


        //获取商品id和下单数量   database:out_order_commodity_item  result: orderId, commodityId, orderQty
        List<AllocationWarehouseOrderCommodityBo> allocationWarehouseOrderCommodityList = baseMapper.getOrdersByAllocationWarehouse
                (joinKeyValues.getSbKeys().toString(),  joinKeyValues.getMinKey(), joinKeyValues.getMaxKey());

//        JoinKeyValues CommodityIdValues = JoinUtils.getKeys(allocationWarehouseOrderCommodityList, "commodityId", Long.class);

        //将获取的商品信息对象按照订单id分组, orderId为key
        Map<String, List<AllocationWarehouseOrderCommodityBo>> maps =
                JoinUtils.listGrouping(allocationWarehouseOrderCommodityList, "orderId");


        //遍历订单 信息对象, 并将每个商品信息对象组合到订单信息对象中
        for (AllocationWarehouseOutOrderBo allocationWarehouseOutOrderIterator : allocationWarehouseOutOrderList) {

            //获取当前订单信息对象的orderId, 并作为key从分组的map中取出商品信息列表集合组合到订单信息对象中, 使orderId相对应
            List<AllocationWarehouseOrderCommodityBo> allocationWarehouseOrderCommodityBoList = maps.get(allocationWarehouseOutOrderIterator.getOrderId().toString());
            allocationWarehouseOutOrderIterator.setAllocationWarehouseOrderCommodityBoList(allocationWarehouseOrderCommodityBoList);
        }

        ResultUtil result = ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, allocationWarehouseOutOrderList);

        return result;
    }
}
