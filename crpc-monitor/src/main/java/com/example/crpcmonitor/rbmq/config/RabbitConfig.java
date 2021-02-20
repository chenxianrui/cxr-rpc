package com.example.crpcmonitor.rbmq.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


/**
 * @ClassName RabbitConfig
 * @Description RabbitMQ 配置类
 * @Date 2019-07-20 11:44
 * @Version 1.0.0
 **/
@Configuration
public class RabbitConfig {

    //websocket 消息队列
    public static final String msg_queue = "msg_queue";

    //消息交换机
    public static final String msg_exchang = "msg_exchang";

    //消息路由键
    public static final String msg_routing_key = "msg_routing_key";

    /**
     * 消息队列
     * @return
     */
    @Bean
    public Queue msgQueue(){
        return new Queue(msg_queue);
    }

    @Bean
    public DirectExchange directExchange(){
        return new DirectExchange(msg_exchang);
    }

    /**
     * 消息队列绑定消息交换机
     * @return
     */
    @Bean
    public Binding msgBinding(){
        return BindingBuilder.bind(msgQueue()).to(directExchange()).with(msg_routing_key);
    }
}


