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
public class AllocationWarehouseForOrderQueueConfig {
    @Bean
    public Queue allocationWarehouseForOrderQueue(){

        Map<String, Object> args = new HashMap<>();
        return new Queue("allocationWarehouseForOrderQueue",false,false,false,args);
    }

    @Bean
    public Exchange allocationWarehouseForOrderQueueExchange(){
        return new DirectExchange("allocationWarehouseForOrderQueueExchange");
    }

    @Bean
    public Binding allocationWarehouseForOrderQueueBinding(Queue allocationWarehouseForOrderQueue, Exchange allocationWarehouseForOrderQueueExchange){

        return BindingBuilder.bind(allocationWarehouseForOrderQueue).to(allocationWarehouseForOrderQueueExchange).with("allocationWarehouseForOrderQueue").noargs();
    }
}
