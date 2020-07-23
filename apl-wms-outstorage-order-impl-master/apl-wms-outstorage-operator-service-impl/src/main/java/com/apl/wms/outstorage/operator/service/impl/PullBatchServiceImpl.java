package com.apl.wms.outstorage.operator.service.impl;
import com.apl.amqp.MqChannel;
import com.apl.amqp.MqConnection;
import com.apl.amqp.RabbitSender;
import com.apl.cache.AplCacheUtil;
import com.apl.db.adb.AdbContext;
import com.apl.db.adb.AdbPersistent;
import com.apl.lib.constants.CommonStatusCode;
import com.apl.lib.exception.AplException;
import com.apl.lib.join.JoinKeyValues;
import com.apl.lib.join.JoinUtil;
import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.security.SecurityUser;
import com.apl.lib.utils.*;
import com.apl.wms.outstorage.operator.pojo.bo.OrderCommodityInfoBo;
import com.apl.wms.outstorage.operator.pojo.po.*;
import com.apl.wms.outstorage.order.pojo.po.OutOrderPo;
import com.apl.wms.outstorage.order.service.OutOrderCommodityItemService;
import com.apl.wms.outstorage.order.service.OutOrderService;
import com.apl.wms.outstorage.order.pojo.vo.OrderItemListVo;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderCommodityItemInfoVo;
import com.apl.wms.outstorage.operator.dao.PullBatchMapper;
import com.apl.wms.outstorage.operator.pojo.dto.PullBatchKeyDto;
import com.apl.wms.outstorage.operator.pojo.dto.SubmitPickItemDto;
import com.apl.wms.outstorage.operator.pojo.dto.SortOrderSubmitDto;
import com.apl.wms.outstorage.operator.pojo.vo.*;
import com.apl.wms.outstorage.operator.service.PullBatchService;
import com.apl.wms.outstorage.operator.service.PullItemService;
import com.apl.wms.outstorage.order.lib.enumwms.OrderStatusEnum;
import com.apl.wms.outstorage.order.lib.enumwms.PullStatusType;
import com.apl.wms.warehouse.lib.cache.*;
import com.apl.wms.warehouse.lib.feign.StocksHistoryFeign;
import com.apl.wms.warehouse.lib.feign.WarehouseFeign;
import com.apl.wms.warehouse.lib.pojo.bo.PlatformOutOrderStockBo;
import com.apl.wms.warehouse.lib.utils.WmsWarehouseUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.util.privilegedactions.NewSchema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * <p>
 * 拣货批次 service实现类
 * </p>
 *
 * @author cy
 * @since 2020-06-08
 */
@Service
@Slf4j
public class PullBatchServiceImpl extends ServiceImpl<PullBatchMapper, PullBatchPo> implements PullBatchService {


    //状态code枚举
    enum PullBatchServiceCode {
        ORDER_ID_IS_NULL("ORDER_ID_IS_NULL", "没有接收到订单id, 创建失败"),
        SUBMIT_DATA_ERROR("SUBMIT_DATA_ERROR", "提交数据有误"),
        PULL_BATCH_NOT_EXIST("PULL_BATCH_NOT_EXIST", "拣货批次不存在"),
        SORT_COUNT_ERROR("SORT_COUNT_ERROR", "分拣数量错误"),
        UPDATE_ORDER_STATUS_FAILED("UPDATE_ORDER_STATUS_FAILED", "修改订单状态失败"),
        INSERT_BATCH_INFO_FAILED("INSERT_BATCH_INFO_FAILED", "插入批次信息失败"),
        GET_COMMODITY_INFO_FAILED("GET_COMMODITY_INFO_FAILED", "获取批次商品信息失败"),
        GET_STORAGE_LOCAL_ID_FAILED("GET_STORAGE_LOCAL_ID_FAILED", "批次库位id为空"),
        UNDER_STOCK("UNDER_STOCK", "库存不足")
        ;

        private String code;
        private String msg;

        PullBatchServiceCode(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }


    @Autowired
    AplCacheUtil redisTemplate;

    @Autowired
    OutOrderCommodityItemService outOrderCommodityItemService;

    @Autowired
    PullItemService pullItemService;

    @Autowired
    OutOrderService outOrderService;

    @Autowired
    WarehouseFeign warehouseFeign;

    @Autowired
    RabbitSender rabbitSender;

    @Autowired
    MqConnection mqConnection;

    @Autowired
    StocksHistoryFeign stocksHistoryFeign;

    // 根据订单id 获取打包信息
    @Override
    public ResultUtil<PackOrderItemListVo> getSortMsg(Long orderId) throws Exception {

        //获取批次信息
        PackOrderItemListVo packOrderItemListVo = baseMapper.getPullBatchMsg(orderId);

        if (packOrderItemListVo != null) {

            //获取打包 订单信息
            List<Long> orderIds = baseMapper.getBatchOrderListByOrderId(orderId);

            List<OrderItemListVo> orderItems = outOrderService.getMultiOrderMsg(orderIds, OrderStatusEnum.HAS_BEEN_COMMITED.getStatus()).getData();

            packOrderItemListVo.setOrderItemListVos(orderItems);
        }

        return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, packOrderItemListVo);
    }



    @Override
    public ResultUtil<List<PullBatchInfoVo>> listPullBatch(Integer pullStatus , String keyword , Long batchTime) {

        //缓存中操作对象
        OperatorCacheBo operator = WmsWarehouseUtils.checkOperator(warehouseFeign, redisTemplate);

        List<PullBatchInfoVo> pullBatchInfoVos = baseMapper.listOperatorBatchByStatus(operator.getMemberId(), pullStatus, keyword, new Timestamp(batchTime));

        ResultUtil result = ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, pullBatchInfoVos);

        return result;
    }


    @Override
    public ResultUtil<List<OrderItemListVo>> getPickMsgSortByOrder(Long batchId) throws Exception {

        List<Long> orderIds = baseMapper.getBatchOrderList(batchId);
        System.out.println(orderIds.toString());
        return outOrderService.getMultiOrderMsg(orderIds, OrderStatusEnum.HAS_BEEN_COMMITED.getStatus());

    }


    @Override
    public ResultUtil<List<PullAllocationItemMsgVo>> getPickMsgSortByCommodity(Long batchId) throws Exception {

        List<PullAllocationItemMsgVo> pullAllocationItemMsgVos = new ArrayList<>();
        //批次对应的 下架分组信息 key:商品id value:商品对应的库位列表

        List<PullAllocationItemInfoVo> pullAllocationItemInfoVoList = pullItemService.listPullItemByBatchId(batchId);
        Map<String, List<PullAllocationItemInfoVo>> pullItemInfoVos = JoinUtil.listGrouping(pullAllocationItemInfoVoList, "commodityId");

        if (CollectionUtils.isEmpty(pullItemInfoVos)) {
            return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, pullAllocationItemMsgVos);
        }

        //缓存对象
        JoinCommodity joinCommodity = new JoinCommodity(1, warehouseFeign, redisTemplate);
        JoinStorageLocal joinStorageLocal = new JoinStorageLocal(1, warehouseFeign, redisTemplate);

        for (Map.Entry<String, List<PullAllocationItemInfoVo>> pullItemInfoEntry : pullItemInfoVos.entrySet()) {

            //构建商品显示信息
            PullAllocationItemMsgVo pullAllocationItemMsgVo = buildPullItemMsg(joinCommodity, pullItemInfoEntry.getKey());

            pullAllocationItemMsgVos.add(pullAllocationItemMsgVo);

            //构建库位信息
            List<PullAllocationItemMsgVo.StorageLocalMsg> storageLocalMsgList = buildStorageLocalMsg(joinStorageLocal, pullItemInfoEntry);

            pullAllocationItemMsgVo.setStorageLocalMsgList(storageLocalMsgList);

        }

        return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, pullAllocationItemMsgVos);
    }


    /**
     * 创建批次
     * @param ids
     * @return
     */
    @Override
    @Transactional
    public ResultUtil<String> createPullBatch(List<Long> ids) {

        if(ids.size() == 0){
            return ResultUtil.APPRESULT(PullBatchServiceCode.ORDER_ID_IS_NULL.code, PullBatchServiceCode.ORDER_ID_IS_NULL.msg, null);
        }

        RedisLock.repeatSubmit(CommonContextHolder.getHeader("token"), redisTemplate);

        JoinKeyValues longKeys = JoinUtil.getLongKeys(ids);

        Integer pullStatus = 5;

        //批量更改订单状态
        Integer integer = baseMapper.updateOrderStatus(longKeys.getSbKeys().toString(), pullStatus, longKeys.getMinKey(), longKeys.getMaxKey());

        if(integer == 0){
            return ResultUtil.APPRESULT(PullBatchServiceCode.UPDATE_ORDER_STATUS_FAILED.code,
                    PullBatchServiceCode.UPDATE_ORDER_STATUS_FAILED.msg, null);
        }

        SecurityUser securityUser = CommonContextHolder.getSecurityUser(redisTemplate);

        //创建批次信息
        PullBatchPo pullBatchPo = new PullBatchPo();
        pullBatchPo.setPullOperatorId(securityUser.getMemberId());
        pullBatchPo.setCrTime(new Timestamp(System.currentTimeMillis()));
        pullBatchPo.setId(SnowflakeIdWorker.generateId());
        pullBatchPo.setBatchSn(UUID.randomUUID().toString());
        pullBatchPo.setPullStatus(PullStatusType.START_PICKING.getStatus());

        //插入批次信息
        Integer integer1 = baseMapper.insert(pullBatchPo);

        if(integer1 == 0){
            return ResultUtil.APPRESULT(PullBatchServiceCode.INSERT_BATCH_INFO_FAILED.code,
                    PullBatchServiceCode.INSERT_BATCH_INFO_FAILED.msg, null);
        }

        //插入批次中所有订单id
        Integer integer2 = baseMapper.insertBatchOrderIds(pullBatchPo.getId(), ids);

        if(integer2 == 0){
            return ResultUtil.APPRESULT(PullBatchServiceCode.INSERT_BATCH_INFO_FAILED.code,
                    PullBatchServiceCode.INSERT_BATCH_INFO_FAILED.msg, null);
        }

        ResultUtil result = ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS, pullBatchPo.getId().toString());

        return result;

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
    public ResultUtil<Page<PullBatchListVo>> getList(PageDto pageDto, PullBatchKeyDto keyDto) {

        Page<PullBatchListVo> page = new Page();
        page.setCurrent(pageDto.getPageIndex());
        page.setSize(pageDto.getPageSize());

        List<PullBatchListVo> list = baseMapper.getList(page, keyDto);
        page.setRecords(list);

        return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, page);
    }




    /**
     * 提交拣货数据
     * @param pullBatchSubmit
     * @return
     * @throws Exception
     */
    @Override
    @Transactional
    public ResultUtil<Boolean> submitPullBatch(SubmitPickItemDto pullBatchSubmit){

        //Session
        OperatorCacheBo operatorCacheBo = WmsWarehouseUtils.checkOperator(warehouseFeign, redisTemplate);

        //通过批次id获取对应的多个订单Id
        List<Long> orderIdList = baseMapper.getOrderIdByBatchId(pullBatchSubmit.getBatchId());

        //构建批次商品信息持久化对象列表
        List<PullBatchCommodityPo> pullBatchCommodityPoList  = new ArrayList<>();

        //构建批次商品信息持久化对象列表
        List<StocksPo> stocksPoList = new ArrayList<>();

        //构建库位库存对象列表
        List<StorageLocalPo> storageLocalPoList = new ArrayList<>();

        //构建去重和累加商品出库数量功能的总库存HashMap
        Map<Long, Integer> commodityQtyMap = new HashMap<>();

        //构建商品id列表集合
        List<Long> commodityIdList = new ArrayList<>();

        //构建去重和累加商品出库数量的库位库存HashMap
        Map<Long, Map<Long, Integer>> commodityStorageLocalMap = new HashMap<>();

        //构建总库存历史记录列表
        List<StocksHistoryPo> stocksHistoryPoList = new ArrayList<>();

        //构建库位库存历史记录
        List<StorageLocalStocksHistoryPo> storageLocalStocksHistoryPoList = new ArrayList<>();

        //构建商品信息存储map
        Map<Long, Integer> commodityMap = new HashMap<>();

        //构建商品批次信息
        createBatchCommodityInfo(pullBatchSubmit, pullBatchCommodityPoList, commodityQtyMap, commodityIdList, commodityMap, commodityStorageLocalMap);

        //远程调用查询总库存的实际库存
        ResultUtil<List<com.apl.wms.warehouse.po.StocksPo>> stocksRealityCount = warehouseFeign.getStocksRealityCountByCommodityId(commodityIdList);
        List<com.apl.wms.warehouse.po.StocksPo> stocksPoList1 = stocksRealityCount.getData();

        //关联查询订单Id, 订单号
        List<OrderCommodityInfoBo> orderCommodityInfoBoList = baseMapper.getOrderInfoByCommodityIds(commodityIdList);

        List<OutOrderPo> outOrderPos = baseMapper.getOrderInfoByIds(orderIdList);

        for (OrderCommodityInfoBo bo : orderCommodityInfoBoList) {

            for (OutOrderPo outOrderPo : outOrderPos) {

                if(bo.getOrderId() == outOrderPo.getId()){

                    bo.setOrderSn(outOrderPo.getOrderSn());
                }
            }
        }

        //构建总库存历史记录
        createStocksHistory(stocksHistoryPoList, orderCommodityInfoBoList, commodityMap, operatorCacheBo);

        //构建总库存对象
        createStocks(stocksPoList1, commodityQtyMap, stocksPoList);

        //远程调用查询库位库存的实际库存
        Map<Long, Map<Long, Integer>> storageLocalRealityCountMap = warehouseFeign.getStorageLocalRealityCountByCommodityId(commodityIdList);

        //构建库位库存历史记录和库位库存更新对象
        createStorageLocalHistory(storageLocalRealityCountMap, commodityStorageLocalMap, operatorCacheBo,
                storageLocalStocksHistoryPoList, storageLocalPoList, orderCommodityInfoBoList);

        //构建批次信息对象
        PullBatchPo pullBatchPo = new PullBatchPo();
        pullBatchPo.setPullStatus(6);
        pullBatchPo.setPullFinishTime(new Timestamp(System.currentTimeMillis()));
        pullBatchPo.setId(pullBatchSubmit.getBatchId());


        JoinKeyValues longKeys = JoinUtil.getLongKeys(orderIdList);

        //批量插入批次商品信息对象
        Integer integer1 = baseMapper.insertBatchCommodityPo(pullBatchCommodityPoList);

        //批量修改订单状态为"6", 已拣货状态
        Integer integer2 = baseMapper.updateOrderStatus(longKeys.getSbKeys().toString(), 6, null, null);

        //更新批次拣货状态和拣货完成时间
        Integer batchInteger = baseMapper.updatePullBatchById(pullBatchPo);

        //远程调用批量更新总库存
        Integer integer3 = warehouseFeign.updateStocksByCommodityId(stocksPoList);

        //远程调用批量更新库位库存
        Integer integer4 = warehouseFeign.updateStorageLocalByCommodityId(storageLocalPoList);

        //批量插入总库存历史记录
        Integer integer5 = stocksHistoryFeign.insertBatchStocksHistoryInfo(adbContext, stocksHistoryPoList);

        //批量插入库位库存历史记录
        Integer integer6 = stocksHistoryFeign.insertStorageLocalHistoryInfo(adbContext, storageLocalStocksHistoryPoList);

        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS);
    }



    /**
     * 构建批次商品信息
     */
    public void createBatchCommodityInfo(SubmitPickItemDto pullBatchSubmit, List<PullBatchCommodityPo> pullBatchCommodityPoList,
                                         Map<Long, Integer> commodityQtyMap, List<Long> commodityIdList, Map<Long, Integer> commodityMap,
                                         Map<Long, Map<Long, Integer>> commodityStorageLocalMap){

        //获取前端传来的批次商品信息对象
        List<SubmitPickItemDto.PullBatchCommodityDto> pullBatchCommodityDto = pullBatchSubmit.getPullBatchCommodityDto();

        //如果获取不到前端的批次商品信息对象, 直接返回false
        if(pullBatchCommodityDto.size() == 0){

            throw new AplException(PullBatchServiceCode.GET_COMMODITY_INFO_FAILED.code,
                    PullBatchServiceCode.GET_COMMODITY_INFO_FAILED.msg, null);

        }

        //前端传过来的商品信息对象
        for (SubmitPickItemDto.PullBatchCommodityDto batchCommodityDto : pullBatchCommodityDto) {

            Map<Long, Integer> storageLocalMap = new HashMap<>();

            //获取批次商品的多个库位id及对应的库位出货数量
            List<SubmitPickItemDto.PullBatchStorageLocalIds> storageLocalIdList = batchCommodityDto.getPullBatchStorageLocalIdsList();

            if(storageLocalIdList.size() == 0){

                throw new AplException(PullBatchServiceCode.GET_STORAGE_LOCAL_ID_FAILED.code,
                        PullBatchServiceCode.GET_STORAGE_LOCAL_ID_FAILED.msg, null);

            }

            //一个商品信息对象有多个库位Id, 每个库位Id对应生成一个批次商品信息持久化对象
            for (SubmitPickItemDto.PullBatchStorageLocalIds list : storageLocalIdList) {

                if(list != null){

                    //构建批次商品信息持久化对象pull_batch_commodity
                    PullBatchCommodityPo pullBatchCommodityPo = new PullBatchCommodityPo();
                    pullBatchCommodityPo.setStorageLocalId(list.getStorageLocalId());
                    pullBatchCommodityPo.setId(SnowflakeIdWorker.generateId());
                    pullBatchCommodityPo.setBatchId(pullBatchSubmit.getBatchId());
                    pullBatchCommodityPo.setCommodityId(batchCommodityDto.getCommodityId());
                    pullBatchCommodityPo.setPullQty(list.getStorageLocalPullQty());

                    pullBatchCommodityPoList.add(pullBatchCommodityPo);

                    storageLocalMap.put(list.getStorageLocalId(), list.getStorageLocalPullQty());

                }

            }

            //以商品id作为map的key
            Long key = batchCommodityDto.getCommodityId();

            //key:商品Id value:多个相同的商品累加后的出库数量
            if(commodityQtyMap.containsKey(key)){

                //如果map中包含这个key, 表示是同一件商品, 将value取出来并与新的出库数量进行累加(总库存)
                Integer countQty = commodityQtyMap.get(key);
                commodityQtyMap.put(key, countQty + batchCommodityDto.getPullQty());


            }else{

                //如果没有这个key, 直接将key和出库数量存入map
                commodityQtyMap.put(key, batchCommodityDto.getPullQty());
                commodityIdList.add(key);
                commodityMap.put(key, batchCommodityDto.getPullQty());

            }

            //key:商品id value:storageLocalMap key:库位id, value: 单库位商品出库数量
            if(commodityStorageLocalMap.containsKey(key)){

                //如果map中包含这个key, 循环累加库位库存(库位库存)
                Map<Long, Integer> longIntegerMap = commodityStorageLocalMap.get(key);

                //key 库位id, value 库位出库数量
                for(Map.Entry<Long, Integer> entry : longIntegerMap.entrySet()){

                    Integer value = null;

                    //遍历map, 将相同的key取出来, 并将value累加放入longIntegerMap key:库位id value:单库位商品出库数量
                    for(Map.Entry<Long, Integer> entry1 : storageLocalMap.entrySet()){

                        if(entry.getKey() == entry1.getKey()){

                            value = entry.getValue() + entry1.getValue();
                        }
                    }

                    entry.setValue(value);
                }

                //最后将库位和对应库存的map在放回商品库位库存map, 覆盖掉原来的value
                commodityStorageLocalMap.put(key, longIntegerMap);

            }else{

                //如果没有这个key, 将库位库存与对应的出库数量添加到map
                commodityStorageLocalMap.put(key, storageLocalMap);

            }

        }

    }


    /**
     * 构建总库存历史记录
     * @param list
     * @param orderCommodityInfoBoList
     * @param commodityMap
     * @param operatorCacheBo
     */
    public void createStocksHistory(List<StocksHistoryPo> list, List<OrderCommodityInfoBo> orderCommodityInfoBoList, Map<Long, Integer> commodityMap ,OperatorCacheBo operatorCacheBo){

        //for:构建总库存历史记录
        for (OrderCommodityInfoBo bo : orderCommodityInfoBoList) {
            //构建总库存历史记录对象
            StocksHistoryPo stocksHistoryPo = new StocksHistoryPo();

            //key:商品id , value:单件商品总出库数量
            for(Map.Entry<Long, Integer> entry : commodityMap.entrySet()){

                if(bo.getCommodityId() == entry.getKey()){

                    stocksHistoryPo.setOrderId(bo.getOrderId());
                    stocksHistoryPo.setOrderType(2);
                    stocksHistoryPo.setStocksType(2);
                    stocksHistoryPo.setOrderSn(bo.getOrderSn());
                    stocksHistoryPo.setWhId(operatorCacheBo.getWhId());
                    stocksHistoryPo.setCommodityId(bo.getCommodityId());
                    stocksHistoryPo.setInQty(0);
                    stocksHistoryPo.setOutQty(entry.getValue());
                    stocksHistoryPo.setOperatorTime(new Timestamp(System.currentTimeMillis()));

                }
            }

            list.add(stocksHistoryPo);
        }

    }



    /**
     * 构建总库存对象
     */
    public void createStocks(List<com.apl.wms.warehouse.po.StocksPo> stocksPoList1, Map<Long, Integer> commodityQtyMap, List<StocksPo> stocksPoList){

        //批量构建总库存对象
        for(com.apl.wms.warehouse.po.StocksPo list : stocksPoList1){

            //构建总库存对象
            StocksPo newStocksPo = new StocksPo();

            for(Map.Entry<Long, Integer> entry1 : commodityQtyMap.entrySet()){

                if(entry1.getKey() == list.getCommodityId()){

                    if(list.getRealityCount() > entry1.getValue()) {

                        //遍历实际库存map和出库数量map, 并添加到总库存更新对象中
                        newStocksPo.setCommodityId(entry1.getKey());
                        newStocksPo.setRealityCount(list.getRealityCount() - entry1.getValue());

                    }else{

                        throw new AplException(PullBatchServiceCode.UNDER_STOCK.code, PullBatchServiceCode.UNDER_STOCK.msg, null);
                    }
                }
            }
            stocksPoList.add(newStocksPo);
        }

    }



    /**
     * 构建库位库存&库位库存历史记录
     * @param
     * @return
     * @throws Exception
     */
    public void createStorageLocalHistory(Map<Long, Map<Long, Integer>> storageLocalRealityCountMap,
                                          Map<Long, Map<Long, Integer>> commodityStorageLocalMap,
                                          OperatorCacheBo operatorCacheBo,
                                          List<StorageLocalStocksHistoryPo> storageLocalStocksHistoryPoList,
                                          List<StorageLocalPo> storageLocalPoList,
                                          List<OrderCommodityInfoBo> orderCommodityInfoBoList){

        //for:更新库位库存    key:商品Id  value:表中库位库存的实际库存 key:库位id, value:表中单个库位实际库存
        for(Map.Entry<Long, Map<Long, Integer>> entry : storageLocalRealityCountMap.entrySet()){

            for(Map.Entry<Long, Map<Long, Integer>> entry1 : commodityStorageLocalMap.entrySet()){

                if(entry.getKey() == entry1.getKey()){

                    for(Map.Entry<Long, Integer> entry2 : entry.getValue().entrySet()){

                        //构建库位库存对象
                        StorageLocalPo storageLocalPo = new StorageLocalPo();

                        for(Map.Entry<Long, Integer> entry3 : entry1.getValue().entrySet()){

                            if(entry2.getKey() == entry3.getKey()){

                                if(entry2.getValue() > entry3.getValue()) {

                                    Integer realityCount = entry2.getValue() - entry3.getValue();
                                    storageLocalPo.setRealityCount(realityCount);
                                    storageLocalPo.setCommodityId(entry.getKey());

                                    //构建库位库存历史记录对象
                                    StorageLocalStocksHistoryPo storageLocalStocksHistoryPo = new StorageLocalStocksHistoryPo();
                                    storageLocalStocksHistoryPo.setCommodityId(entry.getKey());
                                    storageLocalStocksHistoryPo.setOrderType(2);
                                    storageLocalStocksHistoryPo.setStocksType(2);
                                    storageLocalStocksHistoryPo.setInQty(0);
                                    storageLocalStocksHistoryPo.setOutQty(entry3.getValue());
                                    storageLocalStocksHistoryPo.setWhId(operatorCacheBo.getWhId());
                                    storageLocalStocksHistoryPo.setStorageLocalId(entry3.getKey());
                                    storageLocalStocksHistoryPo.setStocksQty(storageLocalPo.getRealityCount());
                                    storageLocalStocksHistoryPo.setOperatorTime(new Timestamp(System.currentTimeMillis()));
                                    storageLocalStocksHistoryPoList.add(storageLocalStocksHistoryPo);

                                }else{

                                    throw new AplException(PullBatchServiceCode.UNDER_STOCK.code,
                                            PullBatchServiceCode.UNDER_STOCK.msg, null);

                                }
                            }
                        }

                        storageLocalPoList.add(storageLocalPo);
                    }
                }
            }
        }

        //为库位库存历史记录对象添加订单Id
        for (OrderCommodityInfoBo bo : orderCommodityInfoBoList) {

            for (StorageLocalStocksHistoryPo storageLocalPo : storageLocalStocksHistoryPoList) {

                if(bo.getCommodityId() == storageLocalPo.getCommodityId()){

                    storageLocalPo.setOrderId(bo.getOrderId());
                }
            }
        }
    }




    @Override
    @Transactional
    public ResultUtil submitSortMsg(SortOrderSubmitDto sortOrderSubmitDto) throws Exception {

        //前端提交的订单列表
        List<SortOrderSubmitDto.Order> orders = sortOrderSubmitDto.getOrders();

        //查找批次对应的订单列表
        List<Long> orderIds = baseMapper.getBatchOrderList(sortOrderSubmitDto.getBatchId());

        if (CollectionUtils.isEmpty(orderIds)) {
            return ResultUtil.APPRESULT(PullBatchServiceCode.PULL_BATCH_NOT_EXIST.code, PullBatchServiceCode.PULL_BATCH_NOT_EXIST.msg, null);
        }
        //订单对应的订单项目 数据
        List<OrderItemListVo> orderItems = outOrderService.getMultiOrderMsg(orderIds, OrderStatusEnum.HAS_BEEN_COMMITED.getStatus()).getData();

        //提交的批次的订单数量 和保存的批次的订单数量不一致
        if (orders.size() != orderItems.size()) {
            throw new AplException(PullBatchServiceCode.SUBMIT_DATA_ERROR.code, PullBatchServiceCode.SUBMIT_DATA_ERROR.msg);
        }

        //校验 提交数据的合法性
        validateSortMsg(orders, orderItems);
        //更新订单状态
        outOrderService.batchUpdateOrderPullStatus(orderIds, PullStatusType.HAS_BEEN_SORTED.getStatus(), null);

        updatePullBatchStatus(sortOrderSubmitDto.getBatchId(), PullStatusType.HAS_BEEN_SORTED.getStatus());

        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS, null);
    }

    /**
     * @Desc: 校验 提交数据的合法性
     * @Author: CY
     * @Date: 2020/6/12 17:40
     */
    private void validateSortMsg(List<SortOrderSubmitDto.Order> orders, List<OrderItemListVo> orderItems) {

        //将前端提交的数据 根据订单id 进行分组
        Map<Long, SortOrderSubmitDto.Order> orderMap = orders.stream().collect(Collectors.toMap(SortOrderSubmitDto.Order::getOrderId, order -> order));

        //将数据库查询出来的订单 根据订单id 进行分组
        Map<Long, OrderItemListVo> orderItemMap = orderItems.stream().collect(Collectors.toMap(OrderItemListVo::getId, orderItemListVo -> orderItemListVo));

        //遍历订单列表，比对订单 对应的订单子项 列表的数量，校验数量是否一致
        for (Map.Entry<Long, SortOrderSubmitDto.Order> orderEntry : orderMap.entrySet()) {

            //比对方式 ： 查询出 前段传入的订单对应的订单子项 与 批次对应的订单的订单子项 ，比对两者的数量是否一致，不一致 提示分拣数量错误
            List<SortOrderSubmitDto.Order.CommodityItem> itemList = orderEntry.getValue().getItems();
            List<OutOrderCommodityItemInfoVo> orderItemList = orderItemMap.get(orderEntry.getKey()).getOrderItemInfos();

            Map<Long, SortOrderSubmitDto.Order.CommodityItem> itemMap = itemList.stream().collect(Collectors.toMap(SortOrderSubmitDto.Order.CommodityItem::getItemId, orderItem -> orderItem));

            for (OutOrderCommodityItemInfoVo orderCommodityItemInfoVo : orderItemList) {

                if (!orderCommodityItemInfoVo.getOrderQty().equals(itemMap.get(orderCommodityItemInfoVo.getId()).getQty())) {
                    throw new AplException(PullBatchServiceCode.SORT_COUNT_ERROR.code, PullBatchServiceCode.SORT_COUNT_ERROR.msg);
                }

            }


        }


    }


    /**
     * @Desc: 更新拣货批次状态
     * @Author: CY
     * @Date: 2020/6/10 17:42
     */
    private void updatePullBatchStatus(Long batchId, Integer status) {

        PullBatchPo pullBatchPo = baseMapper.selectById(batchId);

        if (pullBatchPo == null) {
            throw new AplException(PullBatchServiceCode.PULL_BATCH_NOT_EXIST.code, PullBatchServiceCode.PULL_BATCH_NOT_EXIST.msg);
        }

        pullBatchPo.setPullStatus(status);

        //拣货完成，填充 完成时间
        if (PullStatusType.HAS_BEEN_PICKED.getStatus().equals(status)) {
            pullBatchPo.setPullFinishTime(new Timestamp(System.currentTimeMillis()));
        }
        //分拣完成，填充完成时间，且填充分拣员id
        else if (PullStatusType.HAS_BEEN_SORTED.getStatus().equals(status)) {
            pullBatchPo.setSortingFinishTime(new Timestamp(System.currentTimeMillis()));
            pullBatchPo.setSortingOperatorId(CommonContextHolder.getSecurityUser().getMemberId());
        }

        baseMapper.updateById(pullBatchPo);
    }


    /**
     * @Desc: 构建 下架信息 商品详细
     * @Author: CY
     * @Date: 2020/6/10 14:54
     */
    private PullAllocationItemMsgVo buildPullItemMsg(JoinCommodity joinCommodity, String pullItemId) {

        PullAllocationItemMsgVo pullAllocationItemMsgVo = new PullAllocationItemMsgVo();

        CommodityCacheBo entity = joinCommodity.getEntity(Long.parseLong(pullItemId));

        pullAllocationItemMsgVo.setCommodityId(Long.parseLong(pullItemId));
        pullAllocationItemMsgVo.setSku(entity.getSku());
        pullAllocationItemMsgVo.setImg(entity.getImgUrl());
        pullAllocationItemMsgVo.setCommodityName(entity.getCommodityName());
        return pullAllocationItemMsgVo;
    }

    /**
     * @Desc: 构建下架信息 商品对应库位 拣货数量
     * @Author: CY
     * @Date: 2020/6/10 14:55
     */
    private List<PullAllocationItemMsgVo.StorageLocalMsg> buildStorageLocalMsg(JoinStorageLocal joinStorageLocal, Map.Entry<String, List<PullAllocationItemInfoVo>> pullItemInfoEntry) {

        List<PullAllocationItemInfoVo> pullAllocationItemInfoVoList = pullItemInfoEntry.getValue();

        List<PullAllocationItemMsgVo.StorageLocalMsg> storageLocalMsgList = new ArrayList<>();

        for (PullAllocationItemInfoVo pullAllocationItemInfoVo : pullAllocationItemInfoVoList) {

            PullAllocationItemMsgVo.StorageLocalMsg storageLocalMsg = new PullAllocationItemMsgVo.StorageLocalMsg();
            storageLocalMsg.setStorageLocalId(pullAllocationItemInfoVo.getStorageLocalId());
            StorageLocalCacheBo storageLocalEntity = joinStorageLocal.getEntity(pullAllocationItemInfoVo.getStorageLocalId());
            if (storageLocalEntity != null) {
                storageLocalMsg.setStorageName(storageLocalEntity.getStorageLocalName());
            }
            storageLocalMsg.setCount(pullAllocationItemInfoVo.getAllocationQty());
            storageLocalMsgList.add(storageLocalMsg);

        }

        return storageLocalMsgList;
    }



    /**
     * @Desc: 校验每一个商品提交的数量
     * @Author: CY
     * @Date: 2020/6/11 10:19
     */
    private void validateCommodityCount(Set<Long> orderIds, List<PullAllocationItemInfoVo> pullAllocationItemInfoVos, SubmitPickItemDto.PullBatchCommodityDto commodityCount) {

        PullAllocationItemInfoVo pullItemInfo = null;

        for (PullAllocationItemInfoVo pullAllocationItemInfoVo : pullAllocationItemInfoVos) {
            //判断提交的数据 是否在数据库有保存
            if (pullAllocationItemInfoVo.getCommodityId().equals(commodityCount.getCommodityId())) {

                pullItemInfo = pullAllocationItemInfoVo;
                orderIds.add(pullAllocationItemInfoVo.getOutOrderId());
                break;
            }
        }

        //判断提交的数据 在数据库没有，提示前端
        if (pullItemInfo == null) {
            throw new AplException(PullBatchServiceCode.SUBMIT_DATA_ERROR.code, PullBatchServiceCode.SUBMIT_DATA_ERROR.msg);
        }


    }

    /**
     * @Desc: 构建库存减扣数量信息
     * @Author: CY
     * @Date: 2020/6/11 10:19
     */
    private void buildStockCount(List<PlatformOutOrderStockBo.PlatformOutOrderStock> stockCounts, SubmitPickItemDto.PullBatchCommodityDto pullBatchCommodityDto) {

        PlatformOutOrderStockBo.PlatformOutOrderStock stockCount = new PlatformOutOrderStockBo.PlatformOutOrderStock();
        stockCount.setCommodityId(pullBatchCommodityDto.getCommodityId());
        stockCounts.add(stockCount);

    }


}