package com.apl.wms.outstorage.order.service.impl;

import com.apl.amqp.ChannelShell;
import com.apl.amqp.RabbitMqUtil;
import com.apl.amqp.RabbitSender;
import com.apl.cache.AplCacheUtil;
import com.apl.lib.constants.CommonStatusCode;
import com.apl.lib.exception.AplException;
import com.apl.lib.join.JoinBase;
import com.apl.lib.join.JoinFieldInfo;
import com.apl.lib.join.JoinUtil;
import com.apl.lib.pojo.dto.PageDto;
import com.apl.lib.security.SecurityUser;
import com.apl.lib.utils.CommonContextHolder;
import com.apl.lib.utils.ResultUtil;
import com.apl.lib.utils.SnowflakeIdWorker;
import com.apl.lib.utils.StringUtil;
import com.apl.sys.lib.cache.CustomerCacheBo;
import com.apl.sys.lib.cache.JoinCustomer;
import com.apl.sys.lib.feign.InnerFeign;
import com.apl.wms.outstorage.order.lib.cache.JoinStore;
import com.apl.wms.outstorage.order.lib.pojo.bo.SyncOutOrderTaskBo;
import com.apl.wms.outstorage.order.dao.SyncOrderMapper;
import com.apl.wms.outstorage.order.pojo.dto.SyncOutOrderKeyDto;
import com.apl.wms.outstorage.order.pojo.dto.SyncOutOrderSaveDto;
import com.apl.wms.outstorage.order.pojo.po.SyncOutOrderPo;
import com.apl.wms.outstorage.order.pojo.vo.SyncOutOrderInfoVo;
import com.apl.wms.outstorage.order.pojo.vo.SyncOutOrderListVo;
import com.apl.wms.outstorage.order.service.SyncOutOrderService;
import com.apl.wms.warehouse.lib.cache.StoreCacheBo;
import com.apl.wms.warehouse.lib.feign.WarehouseFeign;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * 同步平台订单 service实现类
 * </p>
 *
 * @author arran
 * @since 2019-12-25
 */
@Service
@Slf4j
public class SyncOutOrderServiceImpl extends ServiceImpl<SyncOrderMapper, SyncOutOrderPo> implements SyncOutOrderService {

    //状态code枚举
    enum SyncOrderServiceCode {

        TASK_ALREADY_BOOT("TASK_ALREADY_BOOT", "启动任务已启动"),
        GET_STORE_API_CONFIG_FAIL("GET_STORE_API_CONFIG_FAIL", "获取店铺API配置失败"),
        STORE_NOT_CONFIG_API("GET_STORE_API_CONFIG_FAIL", "店铺没有配置API"),
        ;

        private String code;
        private String msg;

        SyncOrderServiceCode(String code, String msg) {
             this.code = code;
             this.msg = msg;
        }
    }


    @Autowired
    InnerFeign innerFeign;

    @Autowired
    WarehouseFeign warehouseFeign;

    @Autowired
    AplCacheUtil redisTemplate;

    @Autowired
    RabbitSender rabbitSender;

    @Autowired
    RabbitMqUtil rabbitMqUtil;

    static JoinFieldInfo joinCustomerFieldInfo = null; //缓存联客户表反射字段
    static JoinFieldInfo joinStoreInfo = null; //跨项目跨库关联 店铺表 反射字段缓存

    @Override
    public ResultUtil<Integer> add(SyncOutOrderSaveDto syncOrder){

        ResultUtil<String> feignResult =  warehouseFeign.getStoreApiConfigStrVal(syncOrder.getStoreId());
        if(feignResult==null  || feignResult.getCode()==null || !feignResult.getCode().equals("SYSTEM_SUCCESS") ) {
            throw new AplException(SyncOrderServiceCode.GET_STORE_API_CONFIG_FAIL.code, SyncOrderServiceCode.GET_STORE_API_CONFIG_FAIL.msg);
        }
        if(feignResult==null  || StringUtil.isEmpty(feignResult.getData())) {
            throw new AplException(SyncOrderServiceCode.STORE_NOT_CONFIG_API.code, SyncOrderServiceCode.STORE_NOT_CONFIG_API.msg);
        }

        if(syncOrder.getOrderStartTimeMillis()<=1){
            SyncOutOrderInfoVo syncOutOrderInfoVo =  baseMapper.getLastSync(syncOrder.getStoreId());
            if(syncOutOrderInfoVo!=null)
                syncOrder.setOrderStartTime(syncOutOrderInfoVo.getOrderStartTime().getTime());
            else
                syncOrder.setOrderStartTime(System.currentTimeMillis());
        }
        if(syncOrder.getOrderEndTimeMillis()<=1){
            syncOrder.setOrderEndTime(System.currentTimeMillis());
        }

        SyncOutOrderPo syncOrderPo = new SyncOutOrderPo();
        BeanUtils.copyProperties(syncOrder, syncOrderPo);
        syncOrderPo.setStatus(1);
        syncOrderPo.setId(SnowflakeIdWorker.generateId());

        this.exists(0l, syncOrder.getStoreId(), syncOrderPo.getOrderStartTime(), syncOrderPo.getOrderEndTime());

        Integer flag = baseMapper.insert(syncOrderPo);
        if(flag.equals(1)){
            return ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS , syncOrder.getId());
        }

        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_FAIL , null);
    }


    @Override
    public ResultUtil<Boolean> updById(SyncOutOrderSaveDto syncOrder, Long customerId){

        SyncOutOrderPo syncOrderPo = new SyncOutOrderPo();
        BeanUtils.copyProperties(syncOrder , syncOrderPo);

        Integer flag = baseMapper.updateById(syncOrderPo);
        if(flag.equals(1)){
            return ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS , true);
        }

        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_FAIL , false);
    }


    @Override
    public ResultUtil<Boolean> updStatus(Long id, Integer status, Long customerId){

        Integer flag = baseMapper.updStatus(id, status, customerId);
        if(flag.equals(1)){
            return ResultUtil.APPRESULT(CommonStatusCode.SAVE_SUCCESS , true);
        }

        return ResultUtil.APPRESULT(CommonStatusCode.SAVE_FAIL , false);
    }


    @Override
    public ResultUtil<Boolean> delById(Long id, Long customerId){

        boolean flag = removeById(id);
        if(flag){
            return ResultUtil.APPRESULT(CommonStatusCode.DEL_SUCCESS , true);
        }

        return ResultUtil.APPRESULT(CommonStatusCode.DEL_FAIL , false);
    }


    @Override
    public ResultUtil<SyncOutOrderInfoVo> selectById(Long id, Long customerId, Integer isShowCustomer) {

        SyncOutOrderInfoVo syncOrderInfoVo = baseMapper.getById(id);
        if(syncOrderInfoVo==null){
            return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, null);
        }

        //关联客户名称
        JoinCustomer joinCustomer = new JoinCustomer(1, innerFeign, redisTemplate);
        CustomerCacheBo customerCacheBo = joinCustomer.getEntity(syncOrderInfoVo.getCustomerId());
        syncOrderInfoVo.setCustomerName(customerCacheBo.getCustomerName());

        //关联店铺名称
        JoinStore joinStore = new JoinStore(1, warehouseFeign, redisTemplate);
        StoreCacheBo storeCacheBo = joinStore.getEntity(syncOrderInfoVo.getStoreId());
        syncOrderInfoVo.setStoreName(storeCacheBo.getStoreName());
        syncOrderInfoVo.setStoreNameEn(storeCacheBo.getStoreNameEn());

        return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, syncOrderInfoVo);
    }


    @Override
    public ResultUtil<Page<SyncOutOrderListVo>> getList(PageDto pageDto, SyncOutOrderKeyDto keyDto, Integer isShowCustomer) throws Exception{

        Page<SyncOutOrderListVo> page = new Page();
        page.setCurrent(pageDto.getPageIndex());
        page.setSize(pageDto.getPageSize());

        List<SyncOutOrderListVo> list = baseMapper.getList(page , keyDto);
        if(list==null || list.size()==0) {
            page.setRecords(list);
            return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, page);
        }

        SecurityUser securityUser = CommonContextHolder.getSecurityUser();

        List<JoinBase> joinTabs = new ArrayList<>();

        //关联客户名称
        if(isShowCustomer.equals(1)){
            JoinCustomer joinCustomer = new JoinCustomer(1, innerFeign, redisTemplate);
            if(null!=joinCustomerFieldInfo) {
                //已经缓存客户反射字段
                joinCustomer.setJoinFieldInfo(joinCustomerFieldInfo);
            }
            else{
                //缓存客户反射字段，此代码只执行一次
                joinCustomer.addField("customerId",  Long.class, "customerName",  String.class);
                joinCustomerFieldInfo = joinCustomer.getJoinFieldInfo();
            }
            joinTabs.add(joinCustomer);
        }

        //关联店铺名称
        JoinStore joinStore = new JoinStore(1, warehouseFeign, redisTemplate);
        if (null != joinStoreInfo) {
            joinStore.setJoinFieldInfo(joinStoreInfo);
        } else {
            joinStore.addField("storeId", Long.class, "storeName", String.class); //店铺名称
            joinStore.addField("storeNameEn",  String.class);//店铺英文名称
            joinStoreInfo = joinStore.getJoinFieldInfo();
        }
        joinTabs.add(joinStore);

        JoinUtil.join(list, joinTabs);
        page.setRecords(list);

        String key;
        Map<String, Integer> maps = new HashMap<>();
        for (SyncOutOrderListVo syncOutOrderListVo : list) {
            // 状态  1等待同步  2正在同步  3已完成同步   4同步异常   5暂停同步   6作废
            if(syncOutOrderListVo.getStatus().equals(2)) {
                key = "TASK_STATUS:" + securityUser.getInnerOrgId().toString() + "_" + syncOutOrderListVo.getId().toString();
                maps.put(key, 2);
            }
        }
        redisTemplate.opsForValue().multiSet(maps);

        return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, page);
    }


    void exists(Long id,  Long storeId,  Timestamp tspOrderStartTime,   Timestamp tspOrderEndTime) {

        List<Integer> list = baseMapper.exists(id, storeId,  tspOrderStartTime,  tspOrderEndTime );
        if (!CollectionUtils.isEmpty(list)) {
            throw new AplException("SYNC_TIME_EXIST", "此店铺已有相同时间的同步任务");
        }
    }

    @Transactional
    public ResultUtil<Boolean> bootTask(Long id, Long customerId) throws Exception {

        SyncOutOrderTaskBo byncOutOrderTaskBo = baseMapper.bootTask(id, customerId);
        if (null==byncOutOrderTaskBo) {
            throw new AplException("TASK_NOT_EXIST", "任务不存在");
        }

        ResultUtil<String> apiConfigByFeign =  warehouseFeign.getStoreApiConfigStrVal(byncOutOrderTaskBo.getStoreId());
        if(apiConfigByFeign==null  || apiConfigByFeign.getCode()==null || !apiConfigByFeign.getCode().equals("SYSTEM_SUCCESS") ) {
            throw new AplException(SyncOrderServiceCode.GET_STORE_API_CONFIG_FAIL.code, SyncOrderServiceCode.GET_STORE_API_CONFIG_FAIL.msg);
        }
        if(apiConfigByFeign==null  || StringUtil.isEmpty(apiConfigByFeign.getData())) {
            throw new AplException(SyncOrderServiceCode.STORE_NOT_CONFIG_API.code, SyncOrderServiceCode.STORE_NOT_CONFIG_API.msg);
        }

        SecurityUser securityUser = CommonContextHolder.getSecurityUser();
        byncOutOrderTaskBo.setSecurityUser(securityUser);
        byncOutOrderTaskBo.setApiConfig(apiConfigByFeign.getData());

        if(byncOutOrderTaskBo.getStatus()==1) {

            // 状态  1等待同步  2正在同步  3已完成同步   4同步异常   5暂停同步   6作废
            // 标识状态为 2正在同步
            baseMapper.updStatus(id, 2, customerId);

            //rabbitSender.send("apl.ec.api.syncOrderShopifyExchange", "syncOrderShopifyQueue", byncOutOrderTaskBo);
            ChannelShell channel = rabbitMqUtil.createChannel("1", false);
            rabbitMqUtil.send(channel, "syncOrderShopifyQueue", byncOutOrderTaskBo);

            String key = "TASK_STATUS:" + securityUser.getInnerOrgId().toString() + "_" + id.toString();
            redisTemplate.opsForValue().set(key, 2);

            channel.close();
        }

        return ResultUtil.APPRESULT(SyncOrderServiceCode.TASK_ALREADY_BOOT.code, SyncOrderServiceCode.TASK_ALREADY_BOOT.msg, true);
    }


    public ResultUtil<Integer> getStatus(Long id){

        SecurityUser securityUser = CommonContextHolder.getSecurityUser();
        String key = "TASK_STATUS:" + securityUser.getInnerOrgId().toString() + "_" + id.toString();
        Integer status = (Integer) redisTemplate.opsForValue().get(key);
        if(status!=null &&  status.equals(3)){
            //同步完成, 清空缓存
            redisTemplate.delete(key);
            return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, status);
        }

        if(status==null){
            status = baseMapper.getStatus(id);
            if(status!=null &&  status.equals(2)){
                //2 正在同步
                redisTemplate.opsForValue().set(key, status);
            }
        }

        return ResultUtil.APPRESULT(CommonStatusCode.GET_SUCCESS, status);
    }
}
