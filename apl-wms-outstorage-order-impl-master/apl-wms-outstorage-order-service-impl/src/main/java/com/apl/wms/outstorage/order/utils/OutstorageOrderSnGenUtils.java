package com.apl.wms.outstorage.order.utils;

import com.apl.lib.exception.AplException;
import com.apl.lib.utils.RedisLock;
import com.apl.lib.utils.StringUtil;
import com.apl.sys.lib.cache.CustomerCacheBo;
import com.apl.sys.lib.cache.JoinCustomer;
import com.apl.sys.lib.feign.InnerFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Description 生成订单号
 * @Param
 * @Return
 * @Author arran
 * @Date 2020/1/9
 */
@Component
public class OutstorageOrderSnGenUtils {


    //状态code枚举
    enum SnUtilsCode {

        LOCK_ERROR("LOCK_ERROR", "加锁失败");

        private String code;
        private String msg;

        SnUtilsCode(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    NamedParameterJdbcTemplate jdbc;

    @Autowired
    InnerFeign innerFeign;

    @Value("${apl.wms.orderSnCacheTime:60}")
    public Integer orderSnCacheTime;

    // 分布式系统区号
    @Value("${apl.distributed-zone:12}")
    public Integer distributedZone;

    // 生成单号缓存时间(分钟)，默认1天
    @Value("${apl.sn-cache-time:1440}")
    public Integer snCacheTime;


    /**
     * @Desc: 生成出库订单号
     * @Author: CY
     * @Date: 2020/4/15 16:02
     */
    public String createOutOrderSn(Long customerId, String customerNo, Long innerOrgId, Integer len) {
        Long snNum = 0L;
        String cacheKey = "SN:OUT-ORDER-" + innerOrgId.toString() + "-" + customerId.toString();
        if (redisTemplate.hasKey(cacheKey))
            snNum = redisTemplate.opsForValue().increment(cacheKey, 1);
        if (null == snNum || snNum <= 1L) {
            //加锁
            try {
                if (RedisLock.lock(redisTemplate, "SN-OUT", 8l)) {

                    //从订单记录上找到这个客户的最大订单号，放到redis中  IN-PGS-AIR-0002
                    String sql = "SELECT order_sn FROM out_order WHERE id=(SELECT max(id) FROM out_order WHERE customer_id=" + customerId.toString() + " AND inner_org_id=" + innerOrgId.toString() + ")";
                    Map params = new HashMap<>();
                    List<String> list = jdbc.queryForList(sql, params, String.class);
                    if (list.size() > 0) {
                        String str = list.get(0);
                        int p = str.lastIndexOf("-");
                        String str2 = str.substring(p + 1);
                        snNum = Long.parseLong(str2) + 1L;
                    }
                    if (null == snNum || snNum < 1L)
                        snNum = 1L;
                    redisTemplate.opsForValue().set(cacheKey, snNum);
                }
            } catch (Exception exception) {
                throw new AplException("LOCK_ERROR", "加锁失败");
            } finally {
                RedisLock.unlock(redisTemplate, "SN-OUT");
            }
        }

        redisTemplate.expire(cacheKey, orderSnCacheTime, TimeUnit.MINUTES); //登陆用户展期

        if (StringUtil.isEmpty(customerNo)) {
            JoinCustomer joinCustomer = new JoinCustomer(1, innerFeign, redisTemplate);
            CustomerCacheBo customerCacheBo = joinCustomer.getEntity(customerId);
            customerNo = customerCacheBo.getCustomerNo();
        }
        String sn = "OUT-" + customerNo.toUpperCase() + "-" + String.format("%0" + len.toString() + "d", snNum);

        return sn;
    }




}

