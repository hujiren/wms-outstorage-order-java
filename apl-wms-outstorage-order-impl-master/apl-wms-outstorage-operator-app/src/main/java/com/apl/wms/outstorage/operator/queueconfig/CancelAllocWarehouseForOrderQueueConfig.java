package com.apl.wms.outstorage.operator.queueconfig;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hjr start
 * @date 2020/7/8 - 16:41
 */
@Component
public class CancelAllocWarehouseForOrderQueueConfig {
    @Bean
    public Queue cancelAllocWarehouseForOrderQueue(){

        Map<String, Object> args = new HashMap<>();
        return new Queue("cancelAllocWarehouseForOrderQueue",false,false,false,args);
    }

    @Bean
    public Exchange cancelAllocWarehouseForOrderQueueExchange(){
        return new DirectExchange("cancelAllocWarehouseForOrderQueueExchange");
    }

    @Bean
    public Binding cancelAllocWarehouseForOrderQueueBinding(Queue cancelAllocWarehouseForOrderQueue, Exchange cancelAllocWarehouseForOrderQueueExchange){

        return BindingBuilder.bind(cancelAllocWarehouseForOrderQueue).to(cancelAllocWarehouseForOrderQueueExchange).with("cancelAllocWarehouseForOrderQueue").noargs();
    }
}
