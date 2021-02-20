package com.example.crpcmonitor.rbmq.consumer;

import com.example.crpcmonitor.rbmq.config.RabbitConfig;
import com.example.crpcmonitor.rbmq.endpoint.WebSocketServerEndpoint;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;

/**
 * @ClassName RabbitReceive
 * @Description RabbitMQ 定时消息队列 消费监听回调
 * @Date 2019-07-20 12:09
 * @Version 1.0.0
 **/
@Slf4j
@Component
public class RabbitConsumer {

    private static RabbitConsumer rabbitConsumer;
    @Resource
    private WebSocketServerEndpoint webSocketServerEndpoint; //引入WebSocket

    /**
     * 构造方法注入rabbitTemplate
     */
    @PostConstruct
    public void init() {
        rabbitConsumer = this;
        rabbitConsumer.webSocketServerEndpoint = webSocketServerEndpoint;
    }

    @RabbitListener(queues = RabbitConfig.msg_queue) //监听队列
    public void msgReceive(String content, Message message, Channel channel) throws IOException {
        log.info("----------------接收到消息--------------------"+content);
        //发送给WebSocket 由WebSocket推送给前端
        rabbitConsumer.webSocketServerEndpoint.sendMessageOnline(content);
        // 确认消息已接收
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}


