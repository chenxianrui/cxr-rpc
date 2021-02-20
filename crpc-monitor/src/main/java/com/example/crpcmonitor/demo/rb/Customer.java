package com.example.crpcmonitor.demo.rb;

/**
 * @Author cxr
 * @Date 2021/2/20 20:24
 */
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by wzj on 2018/6/10.
 */
public class Customer
{
    private final static String QUEUE_NAME = "test";

    public static void main(String[] args) throws IOException, TimeoutException
    {
        // 创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();

        //设置RabbitMQ地址
        factory.setHost("121.196.104.251");
        factory.setVirtualHost("/");
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setPort(5673);
        //创建一个新的连接
        Connection connection = factory.newConnection();

        //创建一个通道
        Channel channel = connection.createChannel();

        //声明要关注的队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println("Customer Waiting Received messages");

        // 告诉服务器我们需要那个频道的消息，如果频道中有消息，就会执行回调函数handleDelivery
        Consumer consumer = new DefaultConsumer(channel)
        {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException
            {
                String message = new String(body, "UTF-8");
                System.out.println("Customer Received '" + message + "'");
            }
        };
        //自动回复队列应答 -- RabbitMQ中的消息确认机制
        channel.basicConsume(QUEUE_NAME, true, consumer);
    }
}
