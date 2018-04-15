package com.czmec.miaosha.rabbitmq;

import com.czmec.miaosha.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author dcg
 * Created by user on 2018/4/15.
 */
@Service
public class MQSender {
    private static Logger log=LoggerFactory.getLogger(MQReceiver.class);
    @Autowired
    AmqpTemplate amqpTemplate;
    public void send(Object message){
        String msg=RedisService.beanToString(message);
        log.info("send:"+msg);
        amqpTemplate.convertAndSend(MQConfig.QUEUE,msg);
    }
}
