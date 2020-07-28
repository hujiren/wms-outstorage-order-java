package com.apl.wms.outstorage.operator.service.impl;
import com.apl.amqp.MqConnection;
import com.apl.amqp.RabbitSender;
import com.apl.cache.AplCacheUtil;
import com.apl.lib.constants.CommonStatusCode;
import com.apl.lib.exception.AplException;
import com.apl.lib.join.JoinKeyValues;
import com.apl.lib.join.JoinUtil;
import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.security.SecurityUser;
import com.apl.lib.utils.*;
import com.apl.wms.outstorage.operator.pojo.po.*;
import com.apl.wms.outstorage.order.service.OutOrderCommodityItemService;
import com.apl.wms.outstorage.order.service.OutOrderService;
import com.apl.wms.outstorage.order.pojo.vo.OrderItemListVo;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderCommodityItemInfoVo;
import com.apl.wms.outstorage.operator.dao.PullBatchMapper;
import com.apl.wms.outstorage.operator.pojo.dto.PullBatchKeyDto;
import com.apl.wms.outstorage.operator.pojo.dto.SortOrderSubmitDto;
import com.apl.wms.outstorage.operator.pojo.vo.*;
import com.apl.wms.outstorage.operator.service.PullBatchService;
import com.apl.wms.outstorage.operator.service.PullItemService;
import com.apl.wms.outstorage.order.lib.enumwms.OrderStatusEnum;
import com.apl.wms.outstorage.order.lib.enumwms.PullStatusType;
import com.apl.wms.warehouse.lib.cache.*;
import com.apl.wms.warehouse.lib.feign.StocksHistoryFeign;
import com.apl.wms.warehouse.lib.feign.WarehouseFeign;
import com.apl.wms.warehouse.lib.utils.WmsWarehouseUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
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
        SUBMIT_DATA_ERROR("SUBMIT_DATA_ERROR", "提交数据有误"),
        PULL_BATCH_NOT_EXIST("PULL_BATCH_NOT_EXIST", "拣货批次不存在"),
        SORT_COUNT_ERROR("SORT_COUNT_ERROR", "分拣数量错误")
        ;

        private String code;
        private String msg;

        PullBatchServiceCode(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }


    @Autowired
    AplCacheUtil aplCacheUtil;

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

    @Value("${apl.wms.pick.batchSnPrefix.PB}")
    String batchSnPrefix;



    /**
     * 根据条件获取获取批次列表
     * @param pullStatus
     * @param keyword
     * @param batchTime
     * @return
     */
    @Override
    public ResultUtil<List<PullBatchInfoVo>> listPullBatch(Integer pullStatus , String keyword , Long batchTime) {

        //缓存中操作对象
        OperatorCacheBo operator = WmsWarehouseUtils.checkOperator(warehouseFeign, aplCacheUtil);

        List<PullBatchInfoVo> pullBatchInfoVos = baseMapper.listOperatorBatchByStatus(operator.getMemberId(), pullStatus, keyword, new Timestamp(batchTime));

        ResultUtil result = ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, pullBatchInfoVos);

        return result;
    }


    @Override
    public ResultUtil<List<OrderItemListVo>> getPickMsgSortByOrder(Long batchId) throws Exception {

        List<Long> orderIds = baseMapper.getBatchOrderList(batchId);
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
        JoinCommodity joinCommodity = new JoinCommodity(1, warehouseFeign, aplCacheUtil);
        JoinStorageLocal joinStorageLocal = new JoinStorageLocal(1, warehouseFeign, aplCacheUtil);

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
    public ResultUtil<String> createPullBatch(List<Long> ids) throws Exception {

        OperatorCacheBo operatorCacheBo = WmsWarehouseUtils.checkOperator(warehouseFeign, aplCacheUtil);

        Long whId = operatorCacheBo.getWhId();


        Integer batchIndex = baseMapper.getBatchIndex(whId);

        if(whId == null){
            return ResultUtil.APPRESULT(CommonStatusCode.SYSTEM_FAIL, null);
        }

        JoinKeyValues longKeys = JoinUtil.getLongKeys(ids);

        Integer pullStatus = 5;

        SnBo snBo = createPullBatchSn(operatorCacheBo.getWhId());

        //批量更改订单状态
        Integer integer = baseMapper.updateOrderStatus(longKeys.getSbKeys().toString(), pullStatus, longKeys.getMinKey(), longKeys.getMaxKey());

        SecurityUser securityUser = CommonContextHolder.getSecurityUser(aplCacheUtil);

        //创建批次信息
        PullBatchPo pullBatchPo = new PullBatchPo();
        pullBatchPo.setId(SnowflakeIdWorker.generateId());
        pullBatchPo.setWhId(whId);
        pullBatchPo.setBatchIndex(snBo.getIndex());
        pullBatchPo.setBatchSn(snBo.getSn());
        pullBatchPo.setPullOperatorId(securityUser.getMemberId());
        pullBatchPo.setPullStatus(PullStatusType.START_PICKING.getStatus());
        pullBatchPo.setCrTime(new Timestamp(System.currentTimeMillis()));

        //插入批次信息
        Integer integer1 = baseMapper.insertPullBatch(pullBatchPo);

        List<PullBatchOrderPo> pullBatchOrderList = new ArrayList<>();

        for (Long id : ids) {
            PullBatchOrderPo pullBatchOrderPo = new PullBatchOrderPo();
            pullBatchOrderPo.setOrderId(id);
            pullBatchOrderPo.setBatchId(pullBatchPo.getId());
            pullBatchOrderPo.setId(SnowflakeIdWorker.generateId());
            pullBatchOrderList.add(pullBatchOrderPo);
        }

        //插入批次中所有订单id
        Integer integer2 = baseMapper.insertBatchOrderIds(pullBatchOrderList);

        ResultUtil result = ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS, pullBatchPo.getId().toString());

        return result;

    }

    /**
     * 创建批次号 : inner class
     * @param whId
     * @return
     * @throws Exception
     */
    SnBo createPullBatchSn(Long whId) throws Exception {

        SnBo snBo = new SnBo();
        JoinWarehouse joinWarehouse = new JoinWarehouse(1, warehouseFeign, aplCacheUtil);
        WarehouseCacheBo warehouseCacheBo = joinWarehouse.getEntity(whId);

        String cacheKey = "apl-wms:pick-batch-sn-index";
        RedisLock.lock(aplCacheUtil, cacheKey, 10);
        Integer batchIndex = 0;

        if(!aplCacheUtil.hasKey(cacheKey)){
            Integer index = baseMapper.getBatchIndex(whId);
            batchIndex = index;
        }
        else{
            batchIndex = (Integer)aplCacheUtil.opsForValue().get(cacheKey);
        }

        batchIndex++;
        aplCacheUtil.opsForValue().set(cacheKey, batchIndex);

        String sn = this.batchSnPrefix+"-"+warehouseCacheBo.getWhCode().toUpperCase()+"-"+String.format("%3d", batchIndex);
        snBo.setSn(sn);
        snBo.setIndex(batchIndex);

        RedisLock.unlock(aplCacheUtil, cacheKey);

        return snBo;
    }

    /**
     * 创建批次号 inner class
     */
    class SnBo{
        private String sn;

        private Integer index;

        public String getSn() {
            return sn;
        }

        public void setSn(String sn) {
            this.sn = sn;
        }

        public Integer getIndex() {
            return index;
        }

        public void setIndex(Integer index) {
            this.index = index;
        }
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







}