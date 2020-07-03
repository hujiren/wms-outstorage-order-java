package com.apl.wms.outstorage.operator.service.impl;

import com.apl.lib.amqp.RabbitSender;
import com.apl.lib.constants.CommonStatusCode;
import com.apl.lib.exception.AplException;
import com.apl.lib.join.JoinUtils;
import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.security.SecurityUser;
import com.apl.lib.utils.*;
import com.apl.wms.outstorage.order.service.OutOrderCommodityItemService;
import com.apl.wms.outstorage.order.service.OutOrderService;
import com.apl.wms.outstorage.order.pojo.vo.OrderItemListVo;
import com.apl.wms.outstorage.order.pojo.vo.OutOrderCommodityItemInfoVo;
import com.apl.wms.outstorage.operator.mapper.PullBatchMapper;
import com.apl.wms.outstorage.operator.pojo.dto.PullBatchKeyDto;
import com.apl.wms.outstorage.operator.pojo.dto.PullBatchSubmitDto;
import com.apl.wms.outstorage.operator.pojo.dto.SortOrderSubmitDto;
import com.apl.wms.outstorage.operator.pojo.po.PullBatchPo;
import com.apl.wms.outstorage.operator.pojo.vo.*;
import com.apl.wms.outstorage.operator.service.PullBatchService;
import com.apl.wms.outstorage.operator.service.PullItemService;
import com.apl.wms.outstorage.order.lib.enumwms.OrderStatusEnum;
import com.apl.wms.outstorage.order.lib.enumwms.PullStatusType;
import com.apl.wms.warehouse.lib.cache.*;
import com.apl.wms.warehouse.lib.feign.WarehouseFeign;
import com.apl.wms.warehouse.lib.pojo.bo.PlatformOutOrderStockBo;
import com.apl.wms.warehouse.lib.pojo.bo.PullBatchOrderItemBo;
import com.apl.wms.warehouse.lib.utils.WmsWarehouseUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
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
        CREATE_PULL_BATCH_FAIL("CREATE_PULL_BATCH_FAIL", "创建拣货批次失败"),
        SUBMIT_DATA_ERROR("SUBMIT_DATA_ERROR", "提交数据有误"),
        PULL_BATCH_NOT_EXIST("PULL_BATCH_NOT_EXIST", "拣货批次不存在"),
        SORT_COUNT_ERROR("SORT_COUNT_ERROR", "分拣数量错误"),
        ;

        private String code;
        private String msg;

        PullBatchServiceCode(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }


    @Autowired
    RedisTemplate redisTemplate;



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


    // 根据订单id 获取打包信息
    @Override
    public ResultUtils<PackOrderItemListVo> getSortMsg(Long orderId) throws Exception {

        //获取批次信息
        PackOrderItemListVo packOrderItemListVo = baseMapper.getPullBatchMsg(orderId);


        if (packOrderItemListVo != null) {

            //获取打包 订单信息
            List<Long> orderIds = baseMapper.getBatchOrderListByOrderId(orderId);

            List<OrderItemListVo> orderItems = outOrderService.getMultiOrderMsg(orderIds, OrderStatusEnum.CREATE.getStatus()).getData();

            packOrderItemListVo.setOrderItemListVos(orderItems);
        }

        return ResultUtils.APPRESULT(CommonStatusCode.GET_SUCCESS, packOrderItemListVo);
    }



    @Override
    public ResultUtils<List<PullBatchInfoVo>> listPullBatch(Integer pullStatus , String keyword , Long batchTime) {

        //缓存中操作对象
        OperatorCacheBo operator = WmsWarehouseUtils.checkOperator(warehouseFeign, redisTemplate);

        List<PullBatchInfoVo> pullBatchInfoVos = baseMapper.listOperatorBatchByStatus(operator.getMemberId(), pullStatus, keyword, new Timestamp(batchTime));

        ResultUtils result = ResultUtils.APPRESULT(CommonStatusCode.GET_SUCCESS, pullBatchInfoVos);

        return result;
    }


    @Override
    public ResultUtils<List<OrderItemListVo>> getPickMsgSortByOrder(Long batchId) throws Exception {

        List<Long> orderIds = baseMapper.getBatchOrderList(batchId);
        System.out.println(orderIds.toString());
        return outOrderService.getMultiOrderMsg(orderIds, OrderStatusEnum.CREATE.getStatus());

    }


    @Override
    public ResultUtils<List<PullItemMsgVo>> getPickMsgSortByCommodity(Long batchId) throws Exception {

        List<PullItemMsgVo> pullItemMsgVos = new ArrayList<>();

        //批次对应的 下架分组信息 key:商品id value:商品对应的库位列表

        List<PullItemInfoVo> pullItemInfoVoList = pullItemService.listPullItemByBatchId(batchId);
        Map<String, List<PullItemInfoVo>> pullItemInfoVos = JoinUtils.listGrouping(pullItemInfoVoList, "commodityId");

        if (CollectionUtils.isEmpty(pullItemInfoVos)) {
            return ResultUtils.APPRESULT(CommonStatusCode.GET_SUCCESS, pullItemMsgVos);
        }

        //缓存对象
        JoinCommodity joinCommodity = new JoinCommodity(1, warehouseFeign, redisTemplate);
        JoinStorageLocal joinStorageLocal = new JoinStorageLocal(1, warehouseFeign, redisTemplate);

        for (Map.Entry<String, List<PullItemInfoVo>> pullItemInfoEntry : pullItemInfoVos.entrySet()) {

            //构建商品显示信息
            PullItemMsgVo pullItemMsgVo = buildPullItemMsg(joinCommodity, pullItemInfoEntry.getKey());

            pullItemMsgVos.add(pullItemMsgVo);

            //构建库位信息
            List<PullItemMsgVo.StorageLocalMsg> storageLocalMsgList = buildStorageLocalMsg(joinStorageLocal, pullItemInfoEntry);

            pullItemMsgVo.setStorageLocalMsgList(storageLocalMsgList);

        }

        return ResultUtils.APPRESULT(CommonStatusCode.GET_SUCCESS, pullItemMsgVos);
    }


    @Override
    @Transactional
    public ResultUtils<String> createPullBatch(String ids) {

        LockTool.repeatSubmit(CommonContextHolder.getHeader("token"), redisTemplate);

        List<Long> orderIds = StringUtil.stringToLongList(ids);
        //获取订单出库数量信息
        List<PullBatchOrderItemBo> pullBatchOrderItems = outOrderCommodityItemService.getPullBatchOrderItem(orderIds);

        //创建批次
        Long batchId = createBatch();

        //更改出库订单状态
        outOrderService.batchUpdateOrderPullStatus(orderIds, PullStatusType.START_PULL.getStatus(), null);
        //商品下架
        pullItemService.pullCommodity(batchId, pullBatchOrderItems);

        return ResultUtils.APPRESULT(CommonStatusCode.SAVE_SUCCESS, batchId.toString());
    }


    @Override
    public ResultUtils<Boolean> delById(Long id) {

        boolean flag = removeById(id);
        if (flag) {
            return ResultUtils.APPRESULT(CommonStatusCode.DEL_SUCCESS, true);
        }

        return ResultUtils.APPRESULT(CommonStatusCode.DEL_FAIL, false);
    }


    @Override
    public ResultUtils<Page<PullBatchListVo>> getList(PageDto pageDto, PullBatchKeyDto keyDto) {

        Page<PullBatchListVo> page = new Page();
        page.setCurrent(pageDto.getPageIndex());
        page.setSize(pageDto.getPageSize());

        List<PullBatchListVo> list = baseMapper.getList(page, keyDto);
        page.setRecords(list);

        return ResultUtils.APPRESULT(CommonStatusCode.GET_SUCCESS, page);
    }

    @Override
    public Long createBatch() {

        SecurityUser securityUser = CommonContextHolder.getSecurityUser(redisTemplate);

        PullBatchPo pullBatch = new PullBatchPo();
        pullBatch.setId(SnowflakeIdWorker.generateId());

        //需要修改，按照什么规则进行生成彼此编码
        pullBatch.setBatchSn(UUID.randomUUID().toString());

        pullBatch.setPullOperatorId(securityUser.getMemberId());
        pullBatch.setPullStatus(PullStatusType.START_PULL.getStatus());

        baseMapper.insert(pullBatch);

        return pullBatch.getId();
    }

    @Override
    @Transactional
    public ResultUtils submitPullBatch(PullBatchSubmitDto pullBatchSubmit) throws Exception {

        OperatorCacheBo operatorCacheBo = WmsWarehouseUtils.checkOperator(warehouseFeign, redisTemplate);

        PlatformOutOrderStockBo orderStock = new PlatformOutOrderStockBo();
        orderStock.setWhId(operatorCacheBo.getWhId());

        //校验前端提交的数据
        Set<Long> orderIds = checkSubmitPullBatch(orderStock, pullBatchSubmit);

        //更新拣货批次状态
        updatePullBatchStatus(pullBatchSubmit.getBatchId(), PullStatusType.PULL_DONE.getStatus());

        //更改出库订单状态
        outOrderService.batchUpdateOrderPullStatus(new ArrayList<>(orderIds), PullStatusType.PULL_DONE.getStatus(), null);

        orderStock.setSecurityUser(CommonContextHolder.getSecurityUser());
        //进行库存减扣 （仓库库存 / 库位库存)
        rabbitSender.send("pullBatchSubmitStockReduceExchange", "pullBatchSubmitStockReduceQueue", orderStock);

        return ResultUtils.APPRESULT(CommonStatusCode.SAVE_SUCCESS);
    }

    @Override
    @Transactional
    public ResultUtils submitSortMsg(SortOrderSubmitDto sortOrderSubmitDto) throws Exception {

        //前端提交的订单列表
        List<SortOrderSubmitDto.Order> orders = sortOrderSubmitDto.getOrders();

        //查找批次对应的订单列表
        List<Long> orderIds = baseMapper.getBatchOrderList(sortOrderSubmitDto.getBatchId());

        if (CollectionUtils.isEmpty(orderIds)) {
            return ResultUtils.APPRESULT(PullBatchServiceCode.PULL_BATCH_NOT_EXIST.code, PullBatchServiceCode.PULL_BATCH_NOT_EXIST.msg, null);
        }
        //订单对应的订单项目 数据
        List<OrderItemListVo> orderItems = outOrderService.getMultiOrderMsg(orderIds, OrderStatusEnum.CREATE.getStatus()).getData();

        //提交的批次的订单数量 和保存的批次的订单数量不一致
        if (orders.size() != orderItems.size()) {
            throw new AplException(PullBatchServiceCode.SUBMIT_DATA_ERROR.code, PullBatchServiceCode.SUBMIT_DATA_ERROR.msg);
        }

        //校验 提交数据的合法性
        validateSortMsg(orders, orderItems);
        //更新订单状态
        outOrderService.batchUpdateOrderPullStatus(orderIds, PullStatusType.SORT_DONE.getStatus(), null);

        updatePullBatchStatus(sortOrderSubmitDto.getBatchId(), PullStatusType.SORT_DONE.getStatus());

        return ResultUtils.APPRESULT(CommonStatusCode.SAVE_SUCCESS, null);
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
        if (PullStatusType.PULL_DONE.getStatus().equals(status)) {
            pullBatchPo.setPullFinishTime(new Timestamp(System.currentTimeMillis()));
        }
        //分拣完成，填充完成时间，且填充分拣员id
        else if (PullStatusType.SORT_DONE.getStatus().equals(status)) {
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
    private PullItemMsgVo buildPullItemMsg(JoinCommodity joinCommodity, String pullItemId) {

        PullItemMsgVo pullItemMsgVo = new PullItemMsgVo();

        CommodityCacheBo entity = joinCommodity.getEntity(Long.parseLong(pullItemId));

        pullItemMsgVo.setCommodityId(Long.parseLong(pullItemId));
        pullItemMsgVo.setSku(entity.getSku());
        pullItemMsgVo.setImg(entity.getImgUrl());
        pullItemMsgVo.setCommodityName(entity.getCommodityName());
        return pullItemMsgVo;
    }

    /**
     * @Desc: 构建下架信息 商品对应库位 拣货数量
     * @Author: CY
     * @Date: 2020/6/10 14:55
     */
    private List<PullItemMsgVo.StorageLocalMsg> buildStorageLocalMsg(JoinStorageLocal joinStorageLocal, Map.Entry<String, List<PullItemInfoVo>> pullItemInfoEntry) {

        List<PullItemInfoVo> pullItemInfoVoList = pullItemInfoEntry.getValue();

        List<PullItemMsgVo.StorageLocalMsg> storageLocalMsgList = new ArrayList<>();

        for (PullItemInfoVo pullItemInfoVo : pullItemInfoVoList) {

            PullItemMsgVo.StorageLocalMsg storageLocalMsg = new PullItemMsgVo.StorageLocalMsg();
            storageLocalMsg.setStorageLocalId(pullItemInfoVo.getStorageLocalId());
            StorageLocalCacheBo storageLocalEntity = joinStorageLocal.getEntity(pullItemInfoVo.getStorageLocalId());
            if (storageLocalEntity != null) {
                storageLocalMsg.setStorageName(storageLocalEntity.getStorageLocalName());
            }
            storageLocalMsg.setCount(pullItemInfoVo.getPullQty());
            storageLocalMsgList.add(storageLocalMsg);

        }

        return storageLocalMsgList;
    }


    /**
     * @Desc: 校验前端提交的数据 , 并且提取出 订单id
     * @Author: CY
     * @Date: 2020/6/10 17:22
     */
    private Set<Long> checkSubmitPullBatch(PlatformOutOrderStockBo orderStock, PullBatchSubmitDto pullBatchSubmit) throws Exception {

        //提交的拣货数量信息
        List<PullBatchSubmitDto.CommodityCount> commodityCounts = pullBatchSubmit.getCommodityCounts();

        List<PlatformOutOrderStockBo.PlatformOutOrderStock> stockCounts = new ArrayList<>();
        orderStock.setPlatformOutOrderStocks(stockCounts);

        Set<Long> orderIds = new HashSet<>();

        //根据批次 ， 获取批次项目 对应的库位信息，以及商品数量
        List<PullItemInfoVo> pullItemInfoVoList = pullItemService.listPullItemByBatchId(pullBatchSubmit.getBatchId());

        //比对提交的商品数量 与 批次对应的商品数量是否一致，不一致提示提交的数据错误
        if (commodityCounts.size() != pullItemInfoVoList.size()) {
            throw new AplException(PullBatchServiceCode.SUBMIT_DATA_ERROR.code, PullBatchServiceCode.SUBMIT_DATA_ERROR.msg);
        }

        //循环校验每一个商品数量是否一致，并且构建 商品对应数量实体，用来减扣库存
        for (PullBatchSubmitDto.CommodityCount commodityCount : commodityCounts) {

            //校验提交参数
            validateCommodityCount(orderIds, pullItemInfoVoList, commodityCount);
            //构建库存数量 ， 用于减库存
            buildStockCount(stockCounts, commodityCount);

        }
        return orderIds;
    }

    /**
     * @Desc: 校验每一个商品提交的数量
     * @Author: CY
     * @Date: 2020/6/11 10:19
     */
    private void validateCommodityCount(Set<Long> orderIds, List<PullItemInfoVo> pullItemInfoVos, PullBatchSubmitDto.CommodityCount commodityCount) {

        PullItemInfoVo pullItemInfo = null;

        for (PullItemInfoVo pullItemInfoVo : pullItemInfoVos) {
            //判断提交的数据 是否在数据库有保存
            if (pullItemInfoVo.getCommodityId().equals(commodityCount.getCommodityId())
                    && pullItemInfoVo.getStorageLocalId().equals(commodityCount.getStorageLocalId())
                    && pullItemInfoVo.getOutOrderId().equals(commodityCount.getOrderId())) {

                pullItemInfo = pullItemInfoVo;
                orderIds.add(pullItemInfoVo.getOutOrderId());
                break;
            }
        }

        //判断提交的数据 在数据库没有，提示前端
        if (pullItemInfo == null) {
            throw new AplException(PullBatchServiceCode.SUBMIT_DATA_ERROR.code, PullBatchServiceCode.SUBMIT_DATA_ERROR.msg);
        }

        //提交数量不正确，提示前端
        if (commodityCount.getSubmitCount() - pullItemInfo.getPullQty() != 0) {
            throw new AplException(PullBatchServiceCode.SUBMIT_DATA_ERROR.code, PullBatchServiceCode.SUBMIT_DATA_ERROR.msg);
        }

    }

    /**
     * @Desc: 构建库存减扣数量信息
     * @Author: CY
     * @Date: 2020/6/11 10:19
     */
    private void buildStockCount(List<PlatformOutOrderStockBo.PlatformOutOrderStock> stockCounts, PullBatchSubmitDto.CommodityCount commodityCount) {

        PlatformOutOrderStockBo.PlatformOutOrderStock stockCount = new PlatformOutOrderStockBo.PlatformOutOrderStock();

        stockCount.setCommodityId(commodityCount.getCommodityId());
        stockCount.setStorageLocalId(commodityCount.getStorageLocalId());
        stockCount.setChangeCount(commodityCount.getTotalCount());
        stockCounts.add(stockCount);

    }


}