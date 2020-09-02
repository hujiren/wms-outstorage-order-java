package com.apl.wms.outstorage.operator.service.impl;
import com.apl.cache.AplCacheUtil;
import com.apl.lib.constants.CommonStatusCode;
import com.apl.lib.exception.AplException;
import com.apl.lib.join.JoinKeyValues;
import com.apl.lib.join.JoinUtil;
import com.apl.lib.utils.ResultUtil;
import com.apl.wms.outstorage.operator.mapper.SortMapper;
import com.apl.wms.outstorage.operator.pojo.po.PullBatchPo;
import com.apl.wms.outstorage.operator.pojo.vo.OrderCommodityScanVo;
import com.apl.wms.outstorage.operator.service.SortService;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderCommodityItemInfoVo;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderListVo;
import com.apl.wms.warehouse.lib.cache.bo.OperatorCacheBo;
import com.apl.wms.warehouse.lib.feign.WarehouseFeign;
import com.apl.wms.warehouse.lib.utils.WmsWarehouseUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author hjr start
 * @date 2020/7/28 - 9:54
 */
@Service
@Slf4j
public class SortServiceImpl extends ServiceImpl<SortMapper, OrderCommodityScanVo> implements SortService {

    //状态code枚举
    enum SortServiceCode {
        ORDER_SN_IS_WRONG("ORDER_SN_IS_WRONG", "订单号不正确"),
        PULL_STATUS_IS_WRONG("PULL_STATUS_IS_WRONG","该订单拣货状态不正确")
        ;

        private String code;
        private String msg;

        SortServiceCode(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }


    @Autowired
    WarehouseFeign warehouseFeign;

    @Autowired
    AplCacheUtil aplCacheUtil;

    /**
     * 扫描订单号
     * @param orderSn
     * @return
     * warning 订单拣货状态必须为6, 且订单要有对应的批次
     */
    @Override
    @Transactional
    public ResultUtil<OrderCommodityScanVo> scanOrderSn(String orderSn) throws Exception {

        //拿到批次id
        Long batchId = baseMapper.getBatchIdByOrderSn(orderSn);

        if(batchId == null){
            throw new AplException(SortServiceCode.ORDER_SN_IS_WRONG.code, SortServiceCode.ORDER_SN_IS_WRONG.msg, null);

        }
        //通过批次id查询所有的订单id
        List<Long> orderIdList = baseMapper.getOrderIdsByBatchId(batchId);

        JoinKeyValues longKeys = JoinUtil.getLongKeys(orderIdList);

        //根据订单id查询所有订单详情
        List<OutOrderListVo> orderList = baseMapper.getOrderInfoByIds(longKeys.getSbKeys().toString());

        //根据订单id查询所有商品详情
        List<OutOrderCommodityItemInfoVo> commodityList = baseMapper.getCommodityInfoByIds(longKeys.getSbKeys().toString());

        //根据商品sku分组
        LinkedHashMap<String, List<OutOrderCommodityItemInfoVo>> commoditySkuMap = JoinUtil.listGrouping(commodityList, "commoditySku");

        //组装对象
        OrderCommodityScanVo orderCommodityScanVo = new OrderCommodityScanVo();
        orderCommodityScanVo.setOutOrderList(orderList);
        orderCommodityScanVo.setOutOrderCommodityItemMap(commoditySkuMap);

        return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, orderCommodityScanVo);
    }


    /**
     * 提交分拣信息
     * @param orderIds
     * @return
     */
    @Override
    @Transactional
    public ResultUtil<Boolean> submitSortInfo(List<Long> orderIds) {

        OperatorCacheBo operatorCacheBo = WmsWarehouseUtils.checkOperator(warehouseFeign, aplCacheUtil);
        Long memberId = operatorCacheBo.getMemberId();
        Long whId = operatorCacheBo.getWhId();

        List<Integer> pullStatus = baseMapper.getPullStatusByOrderIds(orderIds);

        for (Integer status : pullStatus) {
            if(status != 6){
                return ResultUtil.APPRESULT(SortServiceCode.PULL_STATUS_IS_WRONG.code, SortServiceCode.PULL_STATUS_IS_WRONG.code, false);
            }
        }

        //根据ids批量修改订单状态
        Integer integer = baseMapper.batchUpdateOrderStatus(orderIds);

        Long orderId = orderIds.get(0);

        //获取批次id
        Long batchId = baseMapper.getBatchIdByOrderId(orderId);

        PullBatchPo pullBatchPo = new PullBatchPo();
        pullBatchPo.setWhId(whId);
        pullBatchPo.setSortingFinishTime(new Timestamp(System.currentTimeMillis()));
        pullBatchPo.setSortingOperatorId(memberId);
        pullBatchPo.setPullStatus(7);
        pullBatchPo.setId(batchId);

        //根据批次Id修改批次表信息
        Integer integer1 = baseMapper.updateBatchInfo(pullBatchPo);
        return ResultUtil.APPRESULT(CommonStatusCode.SYSTEM_SUCCESS, true);
    }
}
