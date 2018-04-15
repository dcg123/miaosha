package com.czmec.miaosha.rabbitmq;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author dcg
 * Created by user on 2018/4/15.
 */
@Configuration
public class MQConfig {
    public static final String QUEUE="queue";
    @Bean
    public Queue queue(){
        return new Queue(QUEUE,true);
    }
}
