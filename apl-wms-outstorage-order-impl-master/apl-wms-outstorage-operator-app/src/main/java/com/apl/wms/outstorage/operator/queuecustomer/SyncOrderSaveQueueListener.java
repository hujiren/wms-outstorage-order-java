package com.apl.wms.outstorage.operator.queuecustomer;

import com.apl.db.datasource.DataSourceContextHolder;

import com.apl.lib.security.SecurityUser;
import com.apl.lib.utils.CommonContextHolder;
import com.apl.lib.utils.StringUtil;
import com.apl.db.mybatis.MyBatisPlusConfig;
import com.apl.wms.outstorage.order.lib.pojo.bo.OutOrderMultipleBo;
import com.apl.wms.outstorage.order.service.OutOrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SyncOrderSaveQueueListener {

    @Autowired
    OutOrderService outOrderService;

    @Autowired
    RedisTemplate redisTemplate;

    @RabbitHandler
    @RabbitListener(queues = "syncOrderSaveQueue")
    public void onMessage(Message message, Channel channel)  throws Exception{

        try {

            OutOrderMultipleBo outOrderMultipleBo = (OutOrderMultipleBo) StringUtil.getObjectFromBytes(message.getBody());
            
            SecurityUser securityUser  = outOrderMultipleBo.getSecurityUser();

            //创建临时token，并把securityUser放入redis中，供微服务调用
            String token = CommonContextHolder.setSecurityUser(redisTemplate, securityUser);

            //把临时token放入线程安全变量中, feign会用到
            CommonContextHolder.tokenContextHolder.set(token);

            //多数据源切换
            DataSourceContextHolder.set(securityUser.getTenantGroup(), securityUser.getInnerOrgCode(), securityUser.getInnerOrgId());

            // 多租户ID值
            MyBatisPlusConfig.tenantIdContextHolder.set(securityUser.getInnerOrgId());

            //订单来源  1自动同步平台订单
            outOrderMultipleBo.setOrderFrom(1);
            outOrderService.saveOrders(outOrderMultipleBo);

            //删除临时token
            redisTemplate.delete(token);
            CommonContextHolder.tokenContextHolder.remove();
            CommonContextHolder.securityUserContextHolder.remove();

            //手工ACK
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (Exception e) {
            log.error(this.getClass().getName()+", ERROR：{}", e.getMessage());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
        }
    }


    public static void main(String[] args)  {

        try {


        }
        catch (Exception e){
        }

    }
}
