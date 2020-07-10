package com.apl.wms.outstorage.operator.queueconfig;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

@Component
public class SyncOrderSaveQueueConfig {

    @Bean
    public Queue syncOrderSaveQueue(){

        Map<String, Object> args = new HashMap<>();
        return new Queue("syncOrderSaveQueue",false,false,false,args);
    }

    @Bean
    public Exchange syncOrderSaveQueueExchange(){

        return new DirectExchange("apl.ec.api.syncOrderSaveExchange");
    }

    @Bean
    public Binding syncOrderSaveQueueBinding(Queue syncOrderSaveQueue, Exchange syncOrderSaveQueueExchange){

        return BindingBuilder.bind(syncOrderSaveQueue).to(syncOrderSaveQueueExchange).with("syncOrderSaveQueue").noargs();
    }

}
