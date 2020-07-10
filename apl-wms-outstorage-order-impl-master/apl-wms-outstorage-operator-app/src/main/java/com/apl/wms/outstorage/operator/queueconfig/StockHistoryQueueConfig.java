package com.apl.wms.outstorage.operator.queueconfig;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Desc: 库存历史变更
 * @Author: CY
 * @Date: 2020/1/8 9:48
 */
@Component
public class StockHistoryQueueConfig {

    private String stockHistoryQueue = "stockHistoryQueue";

    private String stockHistoryExchange = "stockHistoryExchange";

    @Bean
    public Queue stockHistoryQueue(){

        Map<String, Object> args = new HashMap<>();
        return new Queue(stockHistoryQueue,false,false,false,args);
    }


    @Bean
    public Exchange stockHistoryExchange(){

        return new FanoutExchange(stockHistoryExchange);
    }


    @Bean
    public Binding putAwayBinding(Queue stockHistoryQueue, Exchange stockHistoryExchange){

        return BindingBuilder.bind(stockHistoryQueue).to(stockHistoryExchange).with("stockHistoryQueue").noargs();
    }
}
