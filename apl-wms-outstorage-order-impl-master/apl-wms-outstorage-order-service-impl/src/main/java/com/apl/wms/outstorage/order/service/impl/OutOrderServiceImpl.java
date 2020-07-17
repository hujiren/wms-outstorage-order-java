package com.apl.wms.outstorage.order.service.impl;

import com.apl.amqp.RabbitSender;
import com.apl.lib.constants.CommonStatusCode;
import com.apl.lib.exception.AplException;
import com.apl.lib.join.JoinBase;
import com.apl.lib.join.JoinFieldInfo;
import com.apl.lib.join.JoinKeyValues;
import com.apl.lib.join.JoinUtil;
import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.security.SecurityUser;
import com.apl.lib.utils.*;
import com.apl.sys.lib.cache.CustomerCacheBo;
import com.apl.sys.lib.cache.JoinCustomer;
import com.apl.sys.lib.feign.InnerFeign;
import com.apl.sys.lib.utils.CheckCacheUtils;
import com.apl.wms.outstorage.operator.pojo.vo.OutOrderPickListVo;
import com.apl.wms.outstorage.order.lib.enumwms.OutStorageOrderStatusEnum;
import com.apl.wms.outstorage.order.lib.enumwms.PullStatusType;
import com.apl.wms.outstorage.order.service.*;
import com.apl.wms.warehouse.lib.cache.*;
import com.apl.wms.warehouse.lib.feign.WarehouseFeign;
import com.apl.wms.warehouse.lib.pojo.bo.PlatformOutOrderStockBo;
import com.apl.wms.warehouse.lib.pojo.vo.OrderCountVo;
import com.apl.wms.warehouse.lib.utils.WmsWarehouseUtils;
import com.apl.wms.outstorage.order.dao.OutOrderMapper;
import com.apl.wms.outstorage.order.lib.cache.JoinStore;
import com.apl.wms.outstorage.order.lib.pojo.bo.OutOrderMultipleBo;
import com.apl.wms.outstorage.order.lib.pojo.bo.SyncOutOrderBo;
import com.apl.wms.outstorage.order.lib.pojo.dto.OutOrderCommodityItemUpdDto;
import com.apl.wms.outstorage.order.lib.pojo.dto.OutOrderDestUpdDto;
import com.apl.wms.outstorage.order.pojo.dto.OutOrderKeyDto;
import com.apl.wms.outstorage.order.pojo.dto.OutOrderMainDto;
import com.apl.wms.outstorage.order.pojo.po.OutOrderDestPo;
import com.apl.wms.outstorage.order.pojo.po.OutOrderPo;
import com.apl.wms.outstorage.operator.pojo.dto.PullOrderKeyDto;
import com.apl.wms.outstorage.order.pojo.vo.*;
import com.apl.wms.outstorage.order.utils.OutstorageOrderSnGenUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
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
 * 出库订单 service实现类
 * </p>
 *
 * @author arran
 * @since 2020-01-07
 */
@Service
@Slf4j
public class OutOrderServiceImpl extends ServiceImpl<OutOrderMapper, OutOrderPo> implements OutOrderService {

    //状态code枚举
    enum OutOrderServiceCode {

        CUSTOMER_NOT_EXIST("CUSTOMER_NOT_EXIST", "客户不存在"),
        OUT_ORDER_NOT_EXIST("IN_ORDER_NOT_EXIST", "出库订单不存在"),
        ORDER_ALREADY_ALLOCATION("ORDER_ALREADY_ALLOCATION", "订单已经分配"),
        ORDER_ALLOCATION_ERROR("ORDER_ALLOCATION_ERROR", "订单分配异常"),
        STORAGE_LOCAL_NOT_LOCK("STORAGE_LOCAL_NOT_LOCK", "库位未锁定，不能分配拣货员"),
        THESE_ORDER_CAN_NOT_BE_CANCEL("THESE_ORDER_CAN_NOT_BE_CANCEL", "这些订单不能取消"),

        ;

        private String code;
        private String msg;

        OutOrderServiceCode(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }


    @Autowired
    OutstorageOrderSnGenUtils orderSnGenUtils;

    @Autowired
    OutOrderCommodityItemService outOrderCommodityItemService;//出库订单商品 out_order_commodity_item

    @Autowired
    OutOrderDestService outOrderDestService;//出库订单附加信息 out_order_attachment

    @Autowired
    PullAllocationItemService pullAllocationItemService;

    @Autowired
    SyncOutOrderService syncOutOrderService;//同步出库订单

    @Autowired
    RabbitSender rabbitSender;

    @Autowired
    WarehouseFeign warehouseFeign;

    @Autowired
    InnerFeign innerFeign;

    @Autowired
    RedisTemplate redisTemplate;


    static JoinFieldInfo joinCommodityFieldInfo = null; //跨项目跨库关联 商品表 反射字段缓存
    static JoinFieldInfo joinCustomerFieldInfo = null; //跨项目跨库关联 客户表 反射字段缓存
    static JoinFieldInfo joinWarehouseInfo = null; //跨项目跨库关联 仓库表 反射字段缓存
    static JoinFieldInfo joinStoreInfo = null; //跨项目跨库关联 店铺表 反射字段缓存

    //保存出库订单商品
    @Override
    @Transactional
    public ResultUtil<String> saveCommodity(OutOrderMainDto outOrderMainDto, List<OutOrderCommodityItemUpdDto> outOrderCommodityItemUpdDtos) throws Exception {


        //校验客户是否存在
        CheckCacheUtils.checkCustomer(innerFeign, redisTemplate, outOrderMainDto.getCustomerId());

        String lockKey = "lock-stocks-" + outOrderMainDto.getCustomerId();
        RedisLock.lock(redisTemplate, lockKey, 2);

        //更新主订单并写入到DB, 如果没有则新建并插入数据到DB
        Long orderId = updateMainOrder(outOrderMainDto, outOrderMainDto.getOrderId());

        //保存商品 ,返回 需要锁定库存的数量
        outOrderCommodityItemService.saveItems(orderId, outOrderMainDto.getWhId(), outOrderCommodityItemUpdDtos);

        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS, orderId.toString());

    }



    //保存目的地信息
    @Override
    @Transactional
    public ResultUtil<Boolean> saveDestInfo(OutOrderDestUpdDto destDto, Long customerId, Integer orderFrom) {

        OutOrderInfoVo findOrder = baseMapper.exists(destDto.getOrderId(), customerId);
        if (null == findOrder) {
            //订单不存在
            return ResultUtil.APPRESULT(OutOrderServiceCode.OUT_ORDER_NOT_EXIST.code, OutOrderServiceCode.OUT_ORDER_NOT_EXIST.msg, null);
        }

        if (orderFrom == 2) {//手动添加
            OutOrderPo entity = new OutOrderPo();
            entity.setId(destDto.getOrderId());
            entity.setEcPlatformCode(destDto.getEcPlatformCode());//平台
            entity.setReferenceSn(destDto.getReferenceSn());//参考号
            entity.setStoreId(destDto.getStoreId());//店铺id
            entity.setRemark(destDto.getRemark());//备注
            baseMapper.updateById(entity);
        }

        OutOrderDestPo outOrderDestPo = new OutOrderDestPo();//出库订单目的地对象
        BeanUtils.copyProperties(destDto, outOrderDestPo);
        Long id = outOrderDestService.saveDest(outOrderDestPo);

        if (id > 0) {
            return ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS, null);
        }

        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_FAIL, false);
    }


    /**
     * 提交订单
     *
     * @param
     * @return
     * @throws Exception
     */
    @Override
    public ResultUtil<Boolean> commitOrder(List<Long> outOrderIds, Long customerId) throws Exception {

        baseMapper.updateOrderStatus(outOrderIds, 3, customerId);

        ResultUtil<Boolean> booleanResultUtil = pullAllocationItemService.allocationWarehouseForOrderQueueSend(outOrderIds);

        return booleanResultUtil;
    }


    //保存多个订单
    @Transactional
    public Integer saveOrders(OutOrderMultipleBo outOrderMultipleBo) throws Exception {

        JoinCustomer joinCustomer = new JoinCustomer(1, innerFeign, redisTemplate);
        CustomerCacheBo customerCacheBo = joinCustomer.getEntity(outOrderMultipleBo.getCustomerId());
        if (customerCacheBo == null) {
            //客户不存在
            throw new AplException(OutOrderServiceCode.CUSTOMER_NOT_EXIST.code, OutOrderServiceCode.CUSTOMER_NOT_EXIST.msg, null);
        }

        //生成一个月内的起始时间
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.MONTH, -1);
        Date startDate = c.getTime();
        Timestamp startTime = new Timestamp(startDate.getTime());

        SecurityUser securityUser = CommonContextHolder.getSecurityUser();

        warehouseFeign.addCommodityCacheBySku(null, outOrderMultipleBo.getCustomerId());

        for (SyncOutOrderBo syncOutOrderBo : outOrderMultipleBo.getOrders()) {
            OutOrderInfoVo outOrderInfoVo = baseMapper.existsByRefSn(syncOutOrderBo.getDestDto().getReferenceSn(), outOrderMultipleBo.getCustomerId(), startTime);
            if (outOrderInfoVo != null)
                syncOutOrderBo.setId(outOrderInfoVo.getId());
        }

        saveOrders2(outOrderMultipleBo, customerCacheBo.getCustomerNo().toUpperCase(), securityUser.getInnerOrgId());

        String key = "TASK_STATUS:" + securityUser.getInnerOrgId().toString() + "_" + outOrderMultipleBo.getTrskId().toString();
        redisTemplate.opsForValue().set(key, 3);

        return outOrderMultipleBo.getOrders().size();
    }

    //保存多个订单
    public void saveOrders2(OutOrderMultipleBo outOrderMultipleBo, String customerNo, Long innerOrgId) throws Exception {

        for (SyncOutOrderBo syncOutOrderBo : outOrderMultipleBo.getOrders()) {
            if (syncOutOrderBo.getId() == null || syncOutOrderBo.getId().equals(0)) {
                OutOrderDestUpdDto destDto = syncOutOrderBo.getDestDto();
                //新建主订单
                OutOrderMainDto outOrderMainDto = new OutOrderMainDto();
                outOrderMainDto.setOrderFrom(outOrderMultipleBo.getOrderFrom()); //订单来源
                outOrderMainDto.setCustomerId(outOrderMultipleBo.getCustomerId()); //客户id
                outOrderMainDto.setCustomerNo(customerNo); //客户编号, 生成订单时用到
                outOrderMainDto.setInnerOrgId(innerOrgId); //内部组织id, 生成订单时用到
                outOrderMainDto.setEcPlatformCode(destDto.getEcPlatformCode()); //电商平台CODE
                outOrderMainDto.setReferenceSn(destDto.getReferenceSn()); //参考号
                outOrderMainDto.setStoreId(destDto.getStoreId()); //店铺id
                outOrderMainDto.setWhId(0l); //仓库id
                Long orderId = this.createMainOrder(outOrderMainDto);
                destDto.setOrderId(orderId);

                //保存目的地
                this.saveDestInfo(destDto, outOrderMultipleBo.getCustomerId(), 1);

                //保存商品
                //Integer orderStatus = outOrderCommodityItemService.saveItems(orderId, syncOutOrderBo.getCommodityItems());

                /*if (orderStatus.equals(2)) {
                    //有未找到的商品, 标识订单状态为 2创建异常
                    this.updStatus(orderId, 2, outOrderMultipleBo.getCustomerId());
                }*/
            }
        }

        // 状态  1等待同步  2正在同步  3已完成   4暂停  5异常  6取消
        //标识状态为 3已完成
        syncOutOrderService.updStatus(outOrderMultipleBo.getTrskId(), 3, outOrderMultipleBo.getCustomerId());
    }


    @Override
    public ResultUtil<Boolean> updStatus(Long id, Integer status, Long customerId) {

        List<Long> list = new ArrayList<>();
        list.add(id);

        Integer flag = baseMapper.updateOrderStatus(list, status, customerId);
        if (flag.equals(1)) {
            return ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS, true);
        }

        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_FAIL, false);
    }

    @Override
    public Integer batchUpdateOrderPullStatus(List<Long> orderIds, Integer status, Long customerId) {

        return baseMapper.batchUpdateOrderPullStatus(orderIds, status, customerId);
    }


    //删除订单
    @Override
    @Transactional
    public ResultUtil<Boolean> delById(Long orderId, Long customerId) {

        if (!exists(orderId, customerId)) {
            throw new AplException(OutOrderServiceCode.OUT_ORDER_NOT_EXIST.code, OutOrderServiceCode.OUT_ORDER_NOT_EXIST.msg, null);
        }

        //删除商品项目
        Boolean flag1 = outOrderCommodityItemService.delByOrderId(orderId);
        if (!flag1) {
            throw new AplException(CommonStatusCode.DEL_FAIL);
        }

        //删除订单附加信息表(物流信息)
        Boolean flag2 = outOrderDestService.delById(orderId, customerId);
        if (!flag2) {
            throw new AplException(CommonStatusCode.DEL_FAIL);
        }

        //删除主订单
        Boolean flag3 = removeById(orderId);
        if (!flag3) {
            throw new AplException(CommonStatusCode.DEL_FAIL);
        }

        return ResultUtil.APPRESULT(CommonStatusCode.DEL_SUCCESS, true);
    }


    //获取订单详细信息
    @Override
    public ResultUtil<Map> selectById(Long orderId, Long customerId) throws Exception {

        if (customerId == null) {
            customerId = CommonContextHolder.getSecurityUser(redisTemplate).getOuterOrgId();
        }

        //主订单信息
        OutOrderInfoVo orderVo = baseMapper.getById(orderId, customerId);
        if (null == orderVo) {
            throw new AplException(OutOrderServiceCode.OUT_ORDER_NOT_EXIST.code, OutOrderServiceCode.OUT_ORDER_NOT_EXIST.msg, null);
        }

        //关联 获取店铺名称
        JoinStore joinStore = new JoinStore(1, warehouseFeign, redisTemplate);
        StoreCacheBo entity = joinStore.getEntity(orderVo.getStoreId());
        //创建订单 没有保存店铺，直接返回查询订单，此字段为空
        if (entity != null) {
            orderVo.setStoreName(entity.getStoreName());
        }

        //关联客户名称
        JoinCustomer joinCustomer = new JoinCustomer(1, innerFeign, redisTemplate);
        CustomerCacheBo customerCacheBo = joinCustomer.getEntity(orderVo.getCustomerId());
        orderVo.setCustomerName(customerCacheBo.getCustomerName());

        //关联仓库名称
        if (orderVo.getWhId() != null && orderVo.getWhId() > 0) {
            JoinWarehouse joinWarehouse = new JoinWarehouse(1, warehouseFeign, redisTemplate);
            WarehouseCacheBo warehouseCacheBo = joinWarehouse.getEntity(orderVo.getWhId());
            if (null != warehouseCacheBo)
                orderVo.setWhName(warehouseCacheBo.getWhName());
        }

        //订单商品
        List<OutOrderCommodityItemInfoVo> commodityItems = outOrderCommodityItemService.getOrderItemsByOrderId(orderId);


        //关联商品图片
        JoinCommodity joinCommodity = new JoinCommodity(1, warehouseFeign, redisTemplate);

        //跨项目跨库关联表数组
        List<JoinBase> joinTabs = new ArrayList<>();
        if (null != joinCommodityFieldInfo) {
            joinCommodity.setJoinFieldInfo(joinCommodityFieldInfo);
        } else {
            joinCommodity.addField("commodityId", Long.class, "imgUrl", String.class);//添加缓存字段
            joinCommodity.addField("isCorrespondence", Integer.class);
            joinCommodityFieldInfo = joinCommodity.getJoinFieldInfo();
        }
        joinTabs.add(joinCommodity);
        //执行跨项目跨库关联商品图片
        JoinUtil.join(commodityItems, joinTabs);

        //目的地信息
        OutOrderDestVo destVo = outOrderDestService.selectById(orderId);

        //组装数据
        Map mapVo = new HashMap<>();
        mapVo.put("order", orderVo);
        mapVo.put("dest", destVo);
        mapVo.put("commodityItems", commodityItems);

        return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, mapVo);
    }

    @Override
    public ResultUtil<OrderItemListVo> getOrderPackMsg(Long orderId) throws Exception {

        OrderItemListVo orderItemListVo = baseMapper.getPackOrderMsg(orderId);

        if (orderItemListVo != null) {
            OrderCountVo order = new OrderCountVo();
            order.setId(orderItemListVo.getId());
            order.setOrderSn(orderItemListVo.getOrderSn());
            //主订单对应的子订单
            List<OutOrderCommodityItemInfoVo> commodityOrderItems = outOrderCommodityItemService.getOrderItemsByOrderId(orderId);

            List<OrderCountVo.OrderItem> orderItemList = new ArrayList<>();

            for (OutOrderCommodityItemInfoVo commodityOrderItem : commodityOrderItems) {

                OrderCountVo.OrderItem orderItem = new OrderCountVo.OrderItem();
                orderItem.setId(commodityOrderItem.getId());
                orderItem.setCommodityId(commodityOrderItem.getCommodityId());
                orderItem.setOrderQty(commodityOrderItem.getOrderQty());
                orderItemList.add(orderItem);
            }

            fullCommodityImg(commodityOrderItems);
            order.setOrderItems(orderItemList);

            orderItemListVo.setOrderItemInfos(commodityOrderItems);
            redisTemplate.opsForValue().set("packaging:" + orderId, order);
        }


        return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, orderItemListVo);
    }


    /**
     * 获取多个订单信息
     *
     * @param orderIds
     * @param orderStatus
     * @return
     * @throws Exception
     */
    @Override
    public ResultUtil<List<OrderItemListVo>> getMultiOrderMsg(List<Long> orderIds, Integer orderStatus) throws Exception {

        //获取订单列表 id SKU
        List<OrderItemListVo> orderListVos = baseMapper.selectOrderByIds(orderIds, orderStatus);

        JoinKeyValues joinKeyValues = JoinUtil.getLongKeys(orderIds);
        List<OutOrderCommodityItemInfoVo> orderItems = outOrderCommodityItemService.getOrderItemsByOrderIds(joinKeyValues.getSbKeys().toString());

        LinkedHashMap<String, List<OutOrderCommodityItemInfoVo>> outOrderCommodityMaps = JoinUtil.listGrouping(orderItems, "orderId");

        for (OrderItemListVo orderItemListVo : orderListVos) {

            //主订单对应的子订单
            List<OutOrderCommodityItemInfoVo> orderItem = outOrderCommodityMaps.get(orderItemListVo.getId().toString());
            //orderItem = outOrderCommodityItemService.getOrderItemsByOrderId(orderItemListVo.getId());

            //填充商品图片
            fullCommodityImg(orderItem);

            orderItemListVo.setOrderItemInfos(orderItem);
        }

        ResultUtil result = ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, orderListVos);

        return result;
    }


    /**
     * 分页查询订单详情
     *
     * @param pageDto 分页对象
     * @param keyDto  关键字对象
     */
    @Override
    public ResultUtil<OutOrderListResultVo> getList(PageDto pageDto, OutOrderKeyDto keyDto) throws Exception {

        //订单查询信息组装对象
        OutOrderListResultVo outOrderListResultVo = new OutOrderListResultVo();

        Page page = null;
        if (pageDto != null) {
            page = new Page();
            page.setCurrent(pageDto.getPageIndex());//默认1
            page.setSize(pageDto.getPageSize());//默认20
        }

        List<OutOrderListVo> list = null;
        JoinKeyValues joinKeyValues = null;
        if (!StringUtil.isEmpty(keyDto.getCommodityName())) {
            /* 按sku或商品名称查找，因为是按商品项目子表查找, 需要进行去重distinct, 查询字段越少, 去重比对速度越快
            故要分2步, 第1步:先只查找订单id，  第2步:根据id列表查找订单信息
             */
            //查找第一步，获取订单id列表
            List<Long> listIds = baseMapper.getOrderIds(page, keyDto);// , tspStart, tspEnd
            if (CollectionUtils.isEmpty(listIds)) {
                //没有找到订单, 返回空信息
                return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, outOrderListResultVo);
            }

            //查找第二步，用订单id列表查找订单
            joinKeyValues = JoinUtil.getLongKeys(listIds);
            list = baseMapper.getListByIds(joinKeyValues.getSbKeys().toString(), joinKeyValues.getMinKey(), joinKeyValues.getMaxKey());

        } else {
            //查找订单列表, 查找条件sku和商品名称为空，只用一步
            list = baseMapper.getList(page, keyDto);
            if (CollectionUtils.isEmpty(list)) {
                //没有找到订单, 返回空信息
                ResultUtil result = ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, outOrderListResultVo);
                return result;
            }

            joinKeyValues = JoinUtil.getKeys(list, "id", Long.class);
        }

        //根据订单查找多个订单商品项目总表
        List<OutOrderCommodityItemInfoVo> commodityItemVos = outOrderCommodityItemService.getOrderItemsByOrderIds(joinKeyValues.getSbKeys().toString());

        //订单商品项目总表，按订单id, 拆分成多个子表
        Map<String, List<OutOrderCommodityItemInfoVo>> commodityItemsMap = null;
        commodityItemsMap = JoinUtil.listGrouping(commodityItemVos, "orderId");
        outOrderListResultVo.setCommodityItems(commodityItemsMap);

        //跨项目跨库关联表数组
        List<JoinBase> joinTabs = new ArrayList<>();

        //关联商品图片
        JoinCommodity joinCommodity = new JoinCommodity(1, warehouseFeign, redisTemplate);
        if (null != joinCommodityFieldInfo) {
            joinCommodity.setJoinFieldInfo(joinCommodityFieldInfo);
        } else {
            joinCommodity.addField("commodityId", Long.class, "imgUrl", String.class);
            joinCommodityFieldInfo = joinCommodity.getJoinFieldInfo();
        }
        joinTabs.add(joinCommodity);
        //执行跨项目跨库关联商品图片
        JoinUtil.join(commodityItemVos, joinTabs);


        joinTabs = new ArrayList<>();
        //关联客户表字段信息
        JoinCustomer joinCustomer = new JoinCustomer(1, innerFeign, redisTemplate);
        if (null != joinCustomerFieldInfo) {
            joinCustomer.setJoinFieldInfo(joinCustomerFieldInfo);
        } else {
            joinCustomer.addField("customerId", Long.class, "customerName", String.class);
            //joinCustomer.addField("customerNameEn", String.class);
            joinCustomerFieldInfo = joinCustomer.getJoinFieldInfo();
        }
        joinTabs.add(joinCustomer);

        //关联仓库表字段信息
        JoinWarehouse joinWarehouse = new JoinWarehouse(1, warehouseFeign, redisTemplate);
        if (null != joinWarehouseInfo) {
            joinWarehouse.setJoinFieldInfo(joinWarehouseInfo);
        } else {
            joinWarehouse.addField("whId", Long.class, "whName", "whName", String.class);
            joinWarehouseInfo = joinWarehouse.getJoinFieldInfo();
        }
        joinTabs.add(joinWarehouse);


        //关联店铺表字段信息
        JoinStore joinStore = new JoinStore(1, warehouseFeign, redisTemplate);
        if (null != joinStoreInfo) {
            joinStore.setJoinFieldInfo(joinStoreInfo);
        } else {
            joinStore.addField("storeId", Long.class, "storeName", "storeName", String.class);
            joinStoreInfo = joinStore.getJoinFieldInfo();
        }
        joinTabs.add(joinStore);


        //执行跨项目跨库关联
        JoinUtil.join(list, joinTabs);

        //组装分页信息
        outOrderListResultVo.setOutOrders(list);
        if (page != null) {
            outOrderListResultVo.setCurrent(page.getCurrent());
            outOrderListResultVo.setSize(page.getSize());
            outOrderListResultVo.setTotal(page.getTotal());
        }

        return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, outOrderListResultVo);
    }


    /**
     * 获取问题订单
     *
     * @param pageDto
     * @param keyDto
     * @return
     * @throws Exception
     */
    @Override
    public ResultUtil<OutOrderListResultVo> listWrongOrder(PageDto pageDto, OutOrderKeyDto keyDto) throws Exception {

        //订单查询信息组装对象
        OutOrderListResultVo outOrderListResultVo = new OutOrderListResultVo();

        Page page = null;
        if (pageDto != null) {
            page = new Page();
            page.setCurrent(pageDto.getPageIndex());
            page.setSize(pageDto.getPageSize());
        }

        List<OutOrderListVo> list = baseMapper.listWrongOrder(page, keyDto, 2);

        if (CollectionUtils.isEmpty(list)) {
            return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, null);
        }

        JoinKeyValues joinKeyValues = JoinUtil.getKeys(list, "id", Long.class);

        //查找多个订单商品项目总表
        List<OutOrderCommodityItemInfoVo> commodityItemVos = outOrderCommodityItemService.getOrderItemsByOrderIds(joinKeyValues.getSbKeys().toString());

        //订单商品项目总表，按订单id, 拆分成多个子表
        Map<String, List<OutOrderCommodityItemInfoVo>> commodityItemsMap = null;
        commodityItemsMap = JoinUtil.listGrouping(commodityItemVos, "orderId");
        outOrderListResultVo.setCommodityItems(commodityItemsMap);

        //跨项目跨库关联表数组
        List<JoinBase> joinTabs = new ArrayList<>();

        //关联商品图片
        JoinCommodity joinCommodity = new JoinCommodity(1, warehouseFeign, redisTemplate);
        if (null != joinCommodityFieldInfo) {
            joinCommodity.setJoinFieldInfo(joinCommodityFieldInfo);
        } else {
            joinCommodity.addField("commodityId", Long.class, "imgUrl", String.class);
            joinCommodityFieldInfo = joinCommodity.getJoinFieldInfo();
        }
        joinTabs.add(joinCommodity);
        //执行跨项目跨库关联商品图片
        JoinUtil.join(commodityItemVos, joinTabs);


        joinTabs = new ArrayList<>();
        //关联客户表字段信息
        JoinCustomer joinCustomer = new JoinCustomer(1, innerFeign, redisTemplate);
        if (null != joinCustomerFieldInfo) {
            joinCustomer.setJoinFieldInfo(joinCustomerFieldInfo);
        } else {
            joinCustomer.addField("customerId", Long.class, "customerName", String.class);
            //joinCustomer.addField("customerNameEn", String.class);
            joinCustomerFieldInfo = joinCustomer.getJoinFieldInfo();
        }
        joinTabs.add(joinCustomer);

        //关联仓库表字段信息
        JoinWarehouse joinWarehouse = new JoinWarehouse(1, warehouseFeign, redisTemplate);
        if (null != joinWarehouseInfo) {
            joinWarehouse.setJoinFieldInfo(joinWarehouseInfo);
        } else {
            joinWarehouse.addField("whId", Long.class, "whName", "whName", String.class);
            joinWarehouseInfo = joinWarehouse.getJoinFieldInfo();
        }
        joinTabs.add(joinWarehouse);


        //关联店铺表字段信息
        JoinStore joinStore = new JoinStore(1, warehouseFeign, redisTemplate);
        if (null != joinStoreInfo) {
            joinStore.setJoinFieldInfo(joinStoreInfo);
        } else {
            joinStore.addField("storeId", Long.class, "storeName", "storeName", String.class);
            joinStoreInfo = joinStore.getJoinFieldInfo();
        }
        joinTabs.add(joinStore);


        //执行跨项目跨库关联
        JoinUtil.join(list, joinTabs);

        //组装分页信息
        outOrderListResultVo.setOutOrders(list);
        if (page != null) {
            outOrderListResultVo.setCurrent(page.getCurrent());
            outOrderListResultVo.setSize(page.getSize());
            outOrderListResultVo.setTotal(page.getTotal());
        }

        return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, outOrderListResultVo);
    }


    @Override
    public ResultUtil<List<OutOrderInfoVo>> listOperatorOrders() throws Exception {

        OperatorCacheBo operatorCacheBo = WmsWarehouseUtils.checkOperator(warehouseFeign, redisTemplate);

        List<OutOrderInfoVo> outOrderInfoVos = baseMapper.listOrderByOrderStatusPullStatusAndPullId(
                OutStorageOrderStatusEnum.CREATE.getStatus(),
                PullStatusType.ALREADY_ALLOCATION_PICKING_MEMBER.getStatus(),
                operatorCacheBo.getMemberId());


        ArrayList joinTabs = new ArrayList<>();
        //关联客户表字段信息
        JoinCustomer joinCustomer = new JoinCustomer(1, innerFeign, redisTemplate);
        if (null != joinCustomerFieldInfo) {
            joinCustomer.setJoinFieldInfo(joinCustomerFieldInfo);
        } else {
            joinCustomer.addField("customerId", Long.class, "customerName", String.class);
            joinCustomerFieldInfo = joinCustomer.getJoinFieldInfo();
        }
        joinTabs.add(joinCustomer);

        //执行跨项目跨库关联
        JoinUtil.join(outOrderInfoVos, joinTabs);

        return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, outOrderInfoVos);

    }


    @Override
    public ResultUtil<Page<OutOrderPickListVo>> queryOrderPickInfoByPage(PageDto pageDto, PullOrderKeyDto keyDto) throws Exception {

        List<OutOrderPickListVo> outOrderInfo;

        Page page = null;
        if (pageDto != null) {
            page = new Page();
            page.setCurrent(pageDto.getPageIndex());
            page.setSize(pageDto.getPageSize());
        }

        outOrderInfo = baseMapper.queryOrderPickInfoByPage(page, keyDto);

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

        //执行跨项目跨库关联
        JoinUtil.join(outOrderInfo, joinTabs);
        //填充仓库
//        fullOutOrderMsg(outOrderInfo);

        page.setRecords(outOrderInfo);

        ResultUtil result = ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, page);

        return result;
    }


    /**
     * 订单统计
     *
     * @param keyDto
     * @return
     */
    @Override
    public ResultUtil<List<StatisticsOrderVo>> statisticsOrder(PullOrderKeyDto keyDto) {

        List<StatisticsOrderVo> result = new ArrayList<>();

        List<OutOrderInfoVo> outOrderInfo;

        keyDto.setPullStatus(null);//拣货状态

        outOrderInfo = baseMapper.pageOrderPull(null, keyDto);

        //统计问题件
        List<OutOrderInfoVo> wrongOrder = outOrderInfo.stream()
                .filter((OutOrderInfoVo outOrderInfoVo) -> outOrderInfoVo.getIsWrong().equals(2))
                .collect(Collectors.toList());

        StatisticsOrderVo statisticsOrderVo = new StatisticsOrderVo();
        statisticsOrderVo.setOrderType(OutStorageOrderStatusEnum.WRONG.getStatus());
        statisticsOrderVo.setOrderCount(wrongOrder.size());

        result.add(statisticsOrderVo);
        //统计其他状态订单
        Map<Integer, List<OutOrderInfoVo>> map = outOrderInfo.stream().collect(Collectors.groupingBy(OutOrderInfoVo::getOrderStatus));

        for (Integer orderStatus : map.keySet()) {
            StatisticsOrderVo statisticsOrder = new StatisticsOrderVo();
            statisticsOrder.setOrderType(orderStatus);
            statisticsOrder.setOrderCount(map.get(orderStatus).size());
            result.add(statisticsOrder);
        }

        return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, result);
    }


    //分配拣货员
    @Override
    public ResultUtil<Boolean> allocationOperator(Long memberId, String orderIdList) {

        //查找订单 列表
        List<OutOrderListVo> findOutOrderList = baseMapper.getListByIds(orderIdList, null, null);
        List<OutOrderPo> orderList = new ArrayList<>();

        //循环判断订单是否已经分配给了对应的 拣货员
        for (OutOrderListVo findOutOrder : findOutOrderList) {

            //库位 没有锁定，不能够进行分配拣货员
            if (!findOutOrder.getPullStatus().equals(PullStatusType.ALREADY_ALLOCATION_STOCK.getStatus())) {
                throw new AplException(OutOrderServiceCode.STORAGE_LOCAL_NOT_LOCK.code, OutOrderServiceCode.STORAGE_LOCAL_NOT_LOCK.msg);
            }

            if (findOutOrder.getPullOperatorId() != null && findOutOrder.getPullOperatorId() > 0L) {
                throw new AplException(OutOrderServiceCode.ORDER_ALREADY_ALLOCATION.code, OutOrderServiceCode.ORDER_ALREADY_ALLOCATION.msg);
            }
            OutOrderPo outOrderPo = new OutOrderPo();
            outOrderPo.setPullOperatorId(memberId);
            outOrderPo.setId(findOutOrder.getId());
            outOrderPo.setPullStatus(PullStatusType.ALREADY_ALLOCATION_PICKING_MEMBER.getStatus());
            orderList.add(outOrderPo);

        }

        updateBatchById(orderList);

        return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, true);
    }

    @Override
    public ResultUtil<Boolean> cancelAllocationOperator(Long memberId, String orderIdList) {
        //查找订单 列表
        List<OutOrderListVo> findOutOrderList = baseMapper.getListByIds(orderIdList, null, null);
        List<OutOrderPo> orderList = new ArrayList<>();

        for (OutOrderListVo findOutOrder : findOutOrderList) {

            OutOrderPo outOrderPo = new OutOrderPo();
            outOrderPo.setPullOperatorId(0L);
            outOrderPo.setId(findOutOrder.getId());
            outOrderPo.setPullStatus(PullStatusType.ALREADY_ALLOCATION_STOCK.getStatus());
            orderList.add(outOrderPo);
        }
        // 更新订单拣货状态
        updateBatchById(orderList);

        return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, true);
    }


    /**
     * 取消订单
     *
     * @param orderIds
     * @return
     * @throws Exception
     */
    @Override
    public ResultUtil<String> cancelOrder(List<Long> orderIds) throws Exception {

        JoinKeyValues longKeys = JoinUtil.getLongKeys(orderIds);

        List<OutOrderInfoVo> orderInfoVoList = baseMapper.queryList(longKeys.getSbKeys().toString());

        if (orderInfoVoList.size() == 0) {

            ResultUtil.APPRESULT(OutOrderServiceCode.OUT_ORDER_NOT_EXIST.code, OutOrderServiceCode.OUT_ORDER_NOT_EXIST.msg, null);
        }

        List<Long> cancelStockOrderIds = new ArrayList<>();
        List<Long> cancelOrderIds = new ArrayList<>();
        StringBuilder canNotCancelOrders = new StringBuilder();

        for (OutOrderInfoVo vo : orderInfoVoList) {
            if (vo.getOrderStatus() == 3 && vo.getPullStatus() <= 3) {

                cancelOrderIds.add(vo.getId());

                if (vo.getPullStatus() == 3) {
                    cancelStockOrderIds.add(vo.getId());
                }
            } else {
                if (canNotCancelOrders.length() > 0) {
                    canNotCancelOrders.append(", ");
                }
                canNotCancelOrders.append(vo.getOrderSn());
            }
        }

        //批量将订单状态改为"6"取消状态
        Integer integer = baseMapper.updateOrderStatus(cancelOrderIds, 6, null);

        if (integer > 0 && cancelStockOrderIds.size() > 0) {
            pullAllocationItemService.cancelAllocationWarehouseForOrderQueueSend(cancelStockOrderIds);
        }

        if (canNotCancelOrders.length() > 0) {

            return ResultUtil.APPRESULT(OutOrderServiceCode.THESE_ORDER_CAN_NOT_BE_CANCEL.code,
                    OutOrderServiceCode.THESE_ORDER_CAN_NOT_BE_CANCEL.msg, canNotCancelOrders.toString());
        }

        return ResultUtil.APPRESULT(CommonStatusCode.SYSTEM_SUCCESS, null);
    }


    //判断订单是否存在
    private Boolean exists(Long orderId, Long customerId) {

        return baseMapper.exists(orderId, customerId) != null ? true : false;
    }

    /**
     * @Desc: 完成创建
     * @Author: CY
     * @Date: 2020/5/30 10:06
     */
    private Boolean cancel(Long id) {

        List<Long> idList = new ArrayList<>();
        idList.add(id);

        return baseMapper.updateOrderStatus(idList, OutStorageOrderStatusEnum.CANCEL.getStatus(), null) == 1 ? true : false;

    }


//    private void fullOutOrderMsg(List<OutOrderPickListVo> outOrderInfo) {
//
//        for (OutOrderPickListVo outOrderInfoVo : outOrderInfo) {
//
//            fullWh(outOrderInfoVo);
//            fullCustomer(outOrderInfoVo);
//            fullPullOperator(outOrderInfoVo);
//
//        }
//
//    }

//    private void fullWh(OutOrderPickListVo outOrderInfoVo) {
//        JoinWarehouse joinWarehouse = new JoinWarehouse(1, warehouseFeign, redisTemplate);
//        WarehouseCacheBo entity = joinWarehouse.getEntity(outOrderInfoVo.getWhId());
//        outOrderInfoVo.setWhName(entity.getWhName());
//    }
//
//    private void fullPullOperator(OutOrderPickListVo outOrderInfoVo) {
//        JoinOperator joinOperator = new JoinOperator(1, warehouseFeign, redisTemplate);
//        OperatorCacheBo entity = joinOperator.getEntity(outOrderInfoVo.getPullOperatorId());
//        if (entity != null) {
//            outOrderInfoVo.setPullOperatorName(entity.getMemberName());
//        }
//
//    }
//
//    private void fullCustomer(OutOrderPickListVo outOrderInfoVo) {
//        JoinCustomer joinCustomer = new JoinCustomer(1, innerFeign, redisTemplate);
//
//        CustomerCacheBo entity = joinCustomer.getEntity(outOrderInfoVo.getCustomerId());
//        outOrderInfoVo.setCustomerName(entity.getCustomerName());
//    }


    /**
     * @Desc: 更新主订单
     * @Author: CY
     * @Date: 2020/6/8 18:20
     */ //有可能是在现有的订单上增加保存商品, 有可能是新建的订单, 有创建订单操作和更新订单操作
    private Long updateMainOrder(OutOrderMainDto outOrderMainDto, Long orderId) {
        //新建主订单  默认值0
        if (orderId == 0) {
            orderId = createMainOrder(outOrderMainDto);
        } else {
            //如果订单不存在
            if (!this.exists(orderId, outOrderMainDto.getCustomerId())) {
                throw new AplException(OutOrderServiceCode.OUT_ORDER_NOT_EXIST.code, OutOrderServiceCode.OUT_ORDER_NOT_EXIST.msg);
            }
            //有订单id, 则更新主订单
            OutOrderPo outOrderPo = new OutOrderPo();
            BeanUtils.copyProperties(outOrderMainDto, outOrderPo);
            outOrderPo.setId(outOrderMainDto.getOrderId());
            outOrderPo.updateById();
        }
        return orderId;
    }


    /**
     * @Desc: 创建主订单
     * @Author: CY
     * @Date: 2020/6/8 18:20
     */
    Long createMainOrder(OutOrderMainDto outOrderMainDto) {

        OutOrderPo outOrderPo = new OutOrderPo();

        BeanUtils.copyProperties(outOrderMainDto, outOrderPo);

        // 订单状态  1创建中
        outOrderPo.setOrderStatus(OutStorageOrderStatusEnum.CREATE_ING.getStatus());

        // 拣货状态  1未分配库位
        outOrderPo.setPullStatus(PullStatusType.NOT_ALLOCATION_STOCK.getStatus());

        Timestamp crTime = new Timestamp(System.currentTimeMillis());
        outOrderPo.setCrTime(crTime); //创建时间

        //生成订单号
        String sn = orderSnGenUtils.createOutOrderSn(outOrderMainDto.getCustomerId(), outOrderMainDto.getCustomerNo(), outOrderMainDto.getInnerOrgId(), 1);
        outOrderPo.setOrderSn(sn);//出库订单号
        outOrderPo.setId(SnowflakeIdWorker.generateId());//订单id, 雪花算法
        outOrderPo.setIsWrong(1);

        baseMapper.insert(outOrderPo);
        return outOrderPo.getId();
    }

    /**
     * @Desc: 调用微服务，校验库存数量
     * @Author: CY
     * @Date: 2020/6/8 18:19
     */
    private void checkStockCount(PlatformOutOrderStockBo platformOutOrderStockBo) {
        ResultUtil<Boolean> checkResult = warehouseFeign.checkStockCount(platformOutOrderStockBo);

        //服务调用失败
        // SERVER_INVOKE_SUCCESS
        if (!checkResult.getCode().equals(CommonStatusCode.GET_SUCCESS.getCode())) {
            throw new AplException(checkResult.getCode(), checkResult.getMsg());
        }
    }


    /**
     * @Desc: 填充商品图片
     * @Author: CY
     * @Date: 2020/3/23 17:50
     */
    private void fullCommodityImg(List<OutOrderCommodityItemInfoVo> commodityItems) throws Exception {

        List<JoinBase> joinTabs = new ArrayList<>();
        JoinCommodity joinCommodity = new JoinCommodity(1, warehouseFeign, redisTemplate);

        joinCommodity.addField("commodityId", Long.class, "imgUrl", String.class);
        joinTabs.add(joinCommodity);

        JoinUtil.join(commodityItems, joinTabs);
    }


}