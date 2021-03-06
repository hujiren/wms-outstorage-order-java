package com.apl.wms.outstorage.operator.service.impl;

import com.apl.cache.AplCacheHelper;
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
import com.apl.wms.outstorage.operator.mapper.PickMapper;
import com.apl.wms.outstorage.operator.pojo.bo.CorrelateCommodityBo;
import com.apl.wms.outstorage.operator.pojo.bo.CorrelateCommodityOrderBo;
import com.apl.wms.outstorage.operator.pojo.dto.PullOrderKeyDto;
import com.apl.wms.outstorage.operator.pojo.dto.SubmitPickItemDto;
import com.apl.wms.outstorage.operator.pojo.po.PullBatchCommodityPo;
import com.apl.wms.outstorage.operator.pojo.po.PullBatchPo;
import com.apl.wms.outstorage.operator.pojo.vo.OutOrderPickListVo;
import com.apl.wms.outstorage.operator.service.PickService;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderListVo;
import com.apl.wms.warehouse.lib.cache.JoinOperator;
import com.apl.wms.warehouse.lib.cache.bo.OperatorCacheBo;
import com.apl.wms.warehouse.lib.feign.WarehouseFeign;
import com.apl.wms.warehouse.lib.pojo.po.StocksHistoryPo;
import com.apl.wms.warehouse.lib.pojo.po.StorageLocalStocksHistoryPo;
import com.apl.wms.warehouse.lib.pojo.vo.StocksVo;
import com.apl.wms.warehouse.lib.pojo.vo.StorageLocalInfoVo;
import com.apl.wms.warehouse.lib.utils.WmsWarehouseUtils;
import com.apl.wms.warehouse.po.StocksPo;
import com.apl.wms.warehouse.po.StorageLocalPo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;

/**
 * @author hjr start
 * @date 2020/7/13 - 15:41
 */
@Service
@Slf4j
public class PickServiceImpl extends ServiceImpl<PickMapper, OutOrderListVo> implements PickService {

    //状态code枚举
    enum PickServiceCode {
        ORDER_STATUS_IS_CANCEL("ORDER_STATUS_IS_CANCEL", "该订单状态为取消状态"),
        ORDER_STATUS_IS_NOT_COMMIT("ORDER_STATUS_IS_NOT_COMMIT", "该订单不是已提交状态"),
        ORDER_INFO_IS_NULL_BY_QUERY("ORDER_INFO_IS_NULL_BY_QUERY", "查询出来的订单信息为空"),
        PULL_STATUS_IS_WRONG("PULL_STATUS_IS_WRONG", "拣货状态错误"),
        THE_ORDER_HAS_BEEN_ALLOCATION_PICKING_MEMBER("THE_ORDER_HAS_BEEN_ALLOCATION_PICKING_MEMBER", "该订单已经分配拣货员"),
        THE_ORDER_DOES_NOT_ALLOCATION_STOCK("THE_ORDER_DOES_NOT_ALLOCATION_STOCK", "该订单尚未分配库存"),
        THE_QUANTITY_OF_COMMODITY_OUT_OF_STOCK_DOES_NOT_CORRESPOND_WITH_THE_ACTUAL_QUANTITY(
                "THE_QUANTITY_OF_COMMODITY_OUT_OF_STOCK_DOES_NOT_CORRESPOND_WITH_THE_ACTUAL_QUANTITY",
                "商品出库数量与实际出库数量不符, 请检查是否有遗漏信息")
        ;

        private String code;
        private String msg;

        PickServiceCode(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }

    static JoinFieldInfo joinCustomerFieldInfo = null; //跨项目跨库关联 客户表 反射字段缓存

    static JoinFieldInfo joinOperatorFieldInfo = null;//跨项目关联  拣货员表 反射字段缓存

    @Autowired
    AplCacheHelper AplCacheHelper;

    @Autowired
    WarehouseFeign warehouseFeign;

    @Autowired
    InnerFeign innerFeign;

    @Autowired
    StocksHistoryDataSourceServiceImpl stocksHistoryDataSourceServiceImpl;

    @Autowired
    StocksDatasourceServiceImpl stocksDatasourceServiceImpl;

    @Override
    public ResultUtil<OutOrderPickListVo> allocationPickingMember(List<String> orderSns) throws Exception {

        List<OutOrderPickListVo> outOrderPickListVo = baseMapper.getListByOrderSns(orderSns);

        if (outOrderPickListVo.isEmpty()) {

            return ResultUtil.APPRESULT(PickServiceCode.ORDER_INFO_IS_NULL_BY_QUERY.code,
                    PickServiceCode.ORDER_INFO_IS_NULL_BY_QUERY.msg, null);

        }

        //订单列表
        List<Long> orderIds = new ArrayList<>();

        for (OutOrderPickListVo vo : outOrderPickListVo) {

            if (vo.getOrderStatus() == 6) {

                // 订单已取消状态
                return ResultUtil.APPRESULT(PickServiceCode.ORDER_STATUS_IS_CANCEL.code,
                        PickServiceCode.ORDER_STATUS_IS_CANCEL.msg
                                + ", orderSn:" + vo.getOrderSn(), null);

            } else if (vo.getOrderStatus() != 3) {

                // 订单不是已提交状态
                return ResultUtil.APPRESULT(PickServiceCode.ORDER_STATUS_IS_NOT_COMMIT.code,
                        PickServiceCode.ORDER_STATUS_IS_NOT_COMMIT.msg
                                + ", orderSn:" + vo.getOrderSn(), null);

            }

            if (vo.getPullStatus() >= 4) {

                return ResultUtil.APPRESULT(PickServiceCode.THE_ORDER_HAS_BEEN_ALLOCATION_PICKING_MEMBER.code,
                        PickServiceCode.THE_ORDER_HAS_BEEN_ALLOCATION_PICKING_MEMBER.msg, vo.getOrderSn());

            } else if (vo.getPullStatus() < 3) {

                return ResultUtil.APPRESULT(PickServiceCode.THE_ORDER_DOES_NOT_ALLOCATION_STOCK.code,
                        PickServiceCode.THE_ORDER_DOES_NOT_ALLOCATION_STOCK.msg, vo.getOrderSn());

            }

            orderIds.add(vo.getOrderId());
        }

        OperatorCacheBo operatorCacheBo = WmsWarehouseUtils.checkOperator(warehouseFeign, AplCacheHelper);

        //批量更新订单拣货员信息和订单状态
        Integer integer = baseMapper.updateOrderPickingMember(operatorCacheBo.getMemberId(), orderIds);

        if (integer == 0) {

            throw new AplException(CommonStatusCode.SAVE_FAIL.code, CommonStatusCode.SAVE_FAIL.msg, integer);
        }

        //跨项目跨库关联表数组
        List<JoinBase> joinTabs = new ArrayList<>();

        //关联客户表字段信息
        JoinCustomer joinCustomer = new JoinCustomer(1, innerFeign, AplCacheHelper);
        if (null != joinCustomerFieldInfo) {
            joinCustomer.setJoinFieldInfo(joinCustomerFieldInfo);
        } else {
            joinCustomer.addField("customerId", Long.class, "customerName", String.class);
            joinCustomerFieldInfo = joinCustomer.getJoinFieldInfo();
        }
        joinTabs.add(joinCustomer);

        //执行跨项目跨库关联
        JoinUtil.join(outOrderPickListVo, joinTabs);

        return ResultUtil.APPRESULT(CommonStatusCode.SYSTEM_SUCCESS, outOrderPickListVo);

    }


    /**
     * 拣货管理
     *
     * @param pageDto
     * @param keyDto
     * @return
     * @throws Exception
     */
    @Override
    public ResultUtil<Page<OutOrderPickListVo>> pickManage(PageDto pageDto, PullOrderKeyDto keyDto) throws Exception {

        if (keyDto.getPullStatus() == 1 || keyDto.getPullStatus() == 2) {

            return ResultUtil.APPRESULT(PickServiceCode.PULL_STATUS_IS_WRONG.code,
                    PickServiceCode.PULL_STATUS_IS_WRONG.msg, null);

        }

        OperatorCacheBo operatorCacheBo = WmsWarehouseUtils.checkOperator(warehouseFeign, AplCacheHelper);
        Long whId = operatorCacheBo.getWhId();

        keyDto.setWhId(whId);

        Page<OutOrderPickListVo> page = new Page();
        page.setCurrent(pageDto.getPageIndex());
        page.setSize(pageDto.getPageSize());


        List<OutOrderPickListVo> list = baseMapper.queryOrderPickInfoByPage(page, keyDto);


        page.setRecords(list);

        //跨项目跨库关联表数组
        List<JoinBase> joinTabs = new ArrayList<>();

        JoinOperator joinOperator = new JoinOperator(1, warehouseFeign, AplCacheHelper);
        if (null != joinOperatorFieldInfo) {
            joinOperator.setJoinFieldInfo(joinOperatorFieldInfo);

        } else {//memberName
            joinOperator.addField("pullOperatorId", Long.class, "memberName", "pullOperatorName", String.class);
            joinOperatorFieldInfo = joinOperator.getJoinFieldInfo();
        }

        joinTabs.add(joinOperator);

        //关联客户表字段信息
        JoinCustomer joinCustomer = new JoinCustomer(1, innerFeign, AplCacheHelper);
        if (null != joinCustomerFieldInfo) {
            joinCustomer.setJoinFieldInfo(joinCustomerFieldInfo);
        } else {
            joinCustomer.addField("customerId", Long.class, "customerName", String.class);
            joinCustomerFieldInfo = joinCustomer.getJoinFieldInfo();
        }

        joinTabs.add(joinCustomer);
        JoinUtil.join(list, joinTabs);

        ResultUtil result = ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, page);

        return result;
    }


    /**
     * 提交拣货数据
     */
    @Override
    @Transactional
    public ResultUtil<Boolean> submitPick(Long batchId, List<SubmitPickItemDto> submitPickItemDtoList) throws Exception {

        OperatorCacheBo operatorCacheBo = WmsWarehouseUtils.checkOperator(warehouseFeign, AplCacheHelper);

        Long whId = operatorCacheBo.getWhId();

        //根据批次id,获取批次下多个订单id
        List<Long> orderIds = baseMapper.getOrderIdsByBatchId(batchId);

        JoinKeyValues longKeys = JoinUtil.getLongKeys(orderIds);

        //根据订单ids批量查询订单拣货状态
        List<Integer> pullStatusList = baseMapper.getPullStatusByOrderIds(orderIds);

        for (Integer integer : pullStatusList) {
            if(integer != 5){
                return ResultUtil.APPRESULT(PickServiceCode.PULL_STATUS_IS_WRONG.code, PickServiceCode.PULL_STATUS_IS_WRONG.msg, false);
            }
        }

        //构建批次对象
        PullBatchPo pullBatchPo = new PullBatchPo();
        pullBatchPo.setId(batchId);
        pullBatchPo.setPullStatus(6);
        pullBatchPo.setPullFinishTime(new Timestamp(System.currentTimeMillis()));

        //构建批次商品信息对象列表
        List<PullBatchCommodityPo> newPullBatchCommodityPoList = new ArrayList<>();

        //构建库位id列表
        List<Long> storageLocalIdList = new ArrayList<>();

        //构建库位库存更新对象列表
        List<StorageLocalPo> newStorageLocalList = new ArrayList<>();

        //构建统计总库存信息Map
        Map<Long, Integer> stocksCountMap = new HashMap<>();

        //构建商品id组成的list
        List<Long> commodityIdList = new ArrayList<>();

        //构建总库存更新对象列表
        List<StocksPo> newStocksPoList = new ArrayList<>();

        //构建总库存历史记录对象列表
        List<StocksHistoryPo> newStocksHistoryPoList = new ArrayList<>();

        //构建库位库存历史记录对象列表
        List<StorageLocalStocksHistoryPo> newStorageLocalStocksHistoryPoList = new ArrayList<>();

        //校验商品是否有漏填
        Integer pullQtySum = 0;
        Integer orderQtySum = baseMapper.getOrderQtySumByOrderIds(orderIds);

        //批量构建拣货信息到批次商品表 pull_batch_commodity
        for (SubmitPickItemDto submitPickItemDto : submitPickItemDtoList) {

            pullQtySum += submitPickItemDto.getPullQty();

            //构建批次商品表更新对象
            PullBatchCommodityPo newPullBatchCommodityPo = new PullBatchCommodityPo();

            newPullBatchCommodityPo.setPullQty(submitPickItemDto.getPullQty());
            newPullBatchCommodityPo.setCommodityId(submitPickItemDto.getCommodityId());
            newPullBatchCommodityPo.setBatchId(batchId);
            newPullBatchCommodityPo.setId(SnowflakeIdWorker.generateId());
            newPullBatchCommodityPo.setStorageLocalId(submitPickItemDto.getStorageLocalId());

            newPullBatchCommodityPoList.add(newPullBatchCommodityPo);

            storageLocalIdList.add(submitPickItemDto.getStorageLocalId());

            //以商品id作为stocksCountMap的key
            Long key = submitPickItemDto.getCommodityId();

            if (stocksCountMap.containsKey(key)) {
                stocksCountMap.put(key, stocksCountMap.get(key) + submitPickItemDto.getPullQty());
            } else {
                stocksCountMap.put(key, submitPickItemDto.getPullQty());
                commodityIdList.add(key);
            }
        }

        //判断所有订单出库数量和传过来的所有商品出库数量累加是否相等
        if(pullQtySum != orderQtySum){
            return ResultUtil.APPRESULT(PickServiceCode.THE_QUANTITY_OF_COMMODITY_OUT_OF_STOCK_DOES_NOT_CORRESPOND_WITH_THE_ACTUAL_QUANTITY.code,
                    PickServiceCode.THE_QUANTITY_OF_COMMODITY_OUT_OF_STOCK_DOES_NOT_CORRESPOND_WITH_THE_ACTUAL_QUANTITY.msg, false);
        }

        SecurityUser securityUser = CommonContextHolder.getSecurityUser();
        Long innerOrgId = securityUser.getInnerOrgId();

        JoinKeyValues longKeys1 = JoinUtil.getLongKeys(storageLocalIdList);

        //通过切换数据源根据库位id查询库位表中的对应实际库存
        ResultUtil<List<StorageLocalInfoVo>> storageLocalList = warehouseFeign.getStorageLocalList(longKeys1.getSbKeys().toString());

        List<StorageLocalInfoVo> oldStorageLocalList = storageLocalList.getData();

        //构建库位库存
        createStorageLocalInfo(oldStorageLocalList, newPullBatchCommodityPoList, newStorageLocalList);

        //构建库位库存历史记录
        createStorageLocalHistory(submitPickItemDtoList, newStorageLocalStocksHistoryPoList, whId, orderIds, oldStorageLocalList, innerOrgId);

        //根据商品id查询总库存信息 commodityId应该为累加后去重的实际商品Id
        ResultUtil<List<StocksVo>> result = warehouseFeign.getStocksByCommodityId(commodityIdList);
        List<StocksVo> stocksPoList = result.getData();

        //构建总库存和总库存历史记录
        createStocksInfo(commodityIdList, stocksCountMap, newStocksPoList, newStocksHistoryPoList, whId, stocksPoList, batchId, innerOrgId);

        //批量插入批次商品表更新对象 Table_Name:pull_batch_commodity
        Integer batchInsertInteger = baseMapper.batchInsertPullBatchCommodity(newPullBatchCommodityPoList);

        //批量修改订单拣货状态 为6已拣货状态 out_order
        Integer integer = baseMapper.updatePullStatus(longKeys.getSbKeys().toString(), longKeys.getMinKey(), longKeys.getMaxKey(), 6);

        //通过切换数据源保存库存记录
        //AdbContext adbContextStocksHistory = stocksHistoryDataSourceServiceImpl.connectDb();

        //通过切换数据源批量更新总库存实际库存和库位实际库存
        //AdbContext adbContextWareHouse = stocksDatasourceServiceImpl.connectDb();

        try {

            //切换数据源,开启事务
            //AdbTransactional.beginTrans(adbContextStocksHistory);
            //AdbTransactional.beginTrans(adbContextWareHouse);

            //根据批次id修改批次表中的拣货状态为6 , 拣货完成时间 pull_batch
            Integer batchInteger = baseMapper.updateBatchByBatchId(pullBatchPo);

            //批量更新库位库存信息和总库存信息 Table_Name:stocks  storage_local
            stocksDatasourceServiceImpl.batchUpdateStorageLocal(newStorageLocalList, newStocksPoList);

            //保存总库存历史记录列表和库位库存历史记录列表
            stocksHistoryDataSourceServiceImpl.saveStocksHistoryPos(newStocksHistoryPoList, newStorageLocalStocksHistoryPoList);

            // 库存历史记录事务提交
            //AdbTransactional.commit(adbContextStocksHistory);
            //AdbTransactional.commit(adbContextWareHouse);

        } catch (Exception e) {

            //AdbTransactional.rollback(adbContextStocksHistory);
            //AdbTransactional.rollback(adbContextWareHouse);
            throw new AplException(CommonStatusCode.SAVE_FAIL.code, CommonStatusCode.SAVE_FAIL.msg);

        }
        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS, true);
    }


    //构建库位库存历史记录
    public void createStorageLocalHistory(List<SubmitPickItemDto> submitPickItemDtoList,
                                          List<StorageLocalStocksHistoryPo> newStorageLocalStocksHistoryPoList,
                                          Long whId,
                                          List<Long> orderIds,
                                          List<StorageLocalInfoVo> oldStorageLocalList,
                                          Long innerOrgId) throws Exception {
        //根据批次中的所有订单id查询对应的商品Id, 出库数量, 订单号
        List<CorrelateCommodityOrderBo> orderCommodityItemList = baseMapper.getCommodityInfoByOrderIds(orderIds);

        Long orderId;
        Long commodityId;
        Integer orderQty;//单个商品对应的总出库数量, 所有库位
        Integer residueQty;

        LinkedHashMap<String, List<SubmitPickItemDto>> pickItemMaps = JoinUtil.listGrouping(submitPickItemDtoList, "commodityId");


        for (CorrelateCommodityOrderBo outOrderCommodityItemPo : orderCommodityItemList) {
            orderId = outOrderCommodityItemPo.getOrderId();
            commodityId = outOrderCommodityItemPo.getCommodityId();
            orderQty = outOrderCommodityItemPo.getOrderQty();

            List<SubmitPickItemDto> oneCommodityStorageLocalList = pickItemMaps.get(commodityId.toString());
            if(oneCommodityStorageLocalList == null)
                continue;
            for (SubmitPickItemDto submitPickItemDto : oneCommodityStorageLocalList) {
                    //residueQty:单商品单库位出库数量
                    residueQty = submitPickItemDto.getPullQty();
                    if (residueQty <= 0)
                        continue;

                    StorageLocalStocksHistoryPo storageLocalStocksHistoryPo = new StorageLocalStocksHistoryPo();
                    storageLocalStocksHistoryPo.setOrderId(orderId);
                    storageLocalStocksHistoryPo.setOrderType(2);
                    storageLocalStocksHistoryPo.setStocksType(2);
                    storageLocalStocksHistoryPo.setOrderSn(outOrderCommodityItemPo.getOrderSn());
                    storageLocalStocksHistoryPo.setWhId(whId);
                    storageLocalStocksHistoryPo.setStorageLocalId(submitPickItemDto.getStorageLocalId());
                    storageLocalStocksHistoryPo.setCommodityId(commodityId);
                    storageLocalStocksHistoryPo.setInQty(0);
                    storageLocalStocksHistoryPo.setOperatorTime(new Timestamp(System.currentTimeMillis()));
                    storageLocalStocksHistoryPo.setInnerOrgId(innerOrgId);
                    newStorageLocalStocksHistoryPoList.add(storageLocalStocksHistoryPo);

                    //根据库位Ids查询到的对应实际库存和库位Ids
                for (StorageLocalInfoVo list : oldStorageLocalList) {

                    if(list.getId() == submitPickItemDto.getStorageLocalId()){
                        //单商品单库位出库数量 >= 单个商品对应的总出库数量, 所有库位
                        if (residueQty >= orderQty) {//表示单个库位即可满足该商品的出库数量
                            storageLocalStocksHistoryPo.setOutQty(orderQty);
                            residueQty -= orderQty;
                            submitPickItemDto.setPullQty(residueQty);
                            storageLocalStocksHistoryPo.setStocksQty(list.getRealityCount() - orderQty);
                        } else {
                            //小于则说明是从多个库位上拿的
                            storageLocalStocksHistoryPo.setOutQty(residueQty);
                            residueQty = 0;
                            submitPickItemDto.setPullQty(residueQty);
                            storageLocalStocksHistoryPo.setStocksQty(0);
                        }
                    }
                }
            }
        }
    }

    /**
     * 构建库位库存对象
     *
     * @param
     * @param newPullBatchCommodityPoList
     * @param newStorageLocalList
     */
    public void createStorageLocalInfo(List<StorageLocalInfoVo> oldStorageLocalList,
                                       List<PullBatchCommodityPo> newPullBatchCommodityPoList,
                                       List<StorageLocalPo> newStorageLocalList) {


        //遍历表实际库存list和 商品更新对象列表 生成库位库存更新对象
        for (StorageLocalInfoVo list : oldStorageLocalList) {

            //构建库位库存更新对象
            StorageLocalPo newStorageLocalPo = new StorageLocalPo();

            for (PullBatchCommodityPo pullBatchCommodityPo : newPullBatchCommodityPoList) {

                if (list.getId() == pullBatchCommodityPo.getStorageLocalId()) {

                    newStorageLocalPo.setId(list.getId());
                    newStorageLocalPo.setRealityCount(list.getRealityCount() - pullBatchCommodityPo.getPullQty());
                }

                newStorageLocalList.add(newStorageLocalPo);
            }
        }

    }

    /**
     * 构建总库存更新对象和总库存历史记录更新对象
     */
    public void createStocksInfo(List<Long> commodityIdList,
                                 Map<Long, Integer> stocksCountMap,
                                 List<StocksPo> newStocksPoList,
                                 List<StocksHistoryPo> newStocksHistoryPoList,
                                 Long whId,
                                 List<StocksVo> stocksPoList,
                                 Long batchId,
                                 Long innerOrgId) {


        //根据商品id关联查询
        List<CorrelateCommodityBo> correlateCommodityBoList = baseMapper.getOrderSnByCommodityId(commodityIdList, batchId);


        for (StocksVo stocksVo : stocksPoList) {

            //构建总库存对象
            StocksPo stocksPo1 = new StocksPo();

            //构建总库存历史记录对象
            StocksHistoryPo stocksHistoryPo = new StocksHistoryPo();

            for (Map.Entry<Long, Integer> entry : stocksCountMap.entrySet()) {
                if (stocksVo.getCommodityId() == entry.getKey()) {
                    stocksPo1.setCommodityId(stocksVo.getCommodityId());
                    stocksPo1.setRealityCount(stocksVo.getRealityCount() - entry.getValue());
                    stocksPo1.setId(stocksVo.getId());
                    newStocksPoList.add(stocksPo1);
                    stocksHistoryPo.setOrderType(2);
                    stocksHistoryPo.setStocksType(2);
                    stocksHistoryPo.setWhId(whId);
                    stocksHistoryPo.setCommodityId(entry.getKey());
                    stocksHistoryPo.setInQty(0);
                    stocksHistoryPo.setOutQty(entry.getValue());
                    stocksHistoryPo.setStocksQty(stocksPo1.getRealityCount());
                    stocksHistoryPo.setOperatorTime(new Timestamp(System.currentTimeMillis()));
                    stocksHistoryPo.setInnerOrgId(innerOrgId);
                    for (CorrelateCommodityBo correlateCommodityBo : correlateCommodityBoList) {
                        if (correlateCommodityBo.getCommodityId() == entry.getKey()) {
                            stocksHistoryPo.setOrderSn(correlateCommodityBo.getOrderSn());
                            stocksHistoryPo.setOrderId(correlateCommodityBo.getOrderId());
                        }
                    }
                    newStocksHistoryPoList.add(stocksHistoryPo);
                }
            }

        }
    }


}

