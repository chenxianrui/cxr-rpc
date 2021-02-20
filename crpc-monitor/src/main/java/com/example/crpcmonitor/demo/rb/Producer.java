package com.example.crpcmonitor.demo.rb;

/**
 * @Author cxr
 * @Date 2021/2/20 20:24
 */
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by wzj on 2018/6/10.
 */
public class Producer
{
    public final static String QUEUE_NAME = "test";

    public static void main(String[] args) throws IOException, TimeoutException
    {
        //创建连接工厂
        ConnectionFactory factory = new ConnectionFactory();

        //设置RabbitMQ相关信息
        factory.setHost("121.196.104.251");
        factory.setVirtualHost("/");
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setPort(5673);

        //创建一个新的连接
        Connection connection = factory.newConnection();

        //创建一个通道
        Channel channel = connection.createChannel();

        // 声明一个队列
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);

        //发送消息到队列中
        String message = "Hello RabbitMQ";
        channel.basicPublish("", QUEUE_NAME, null, message.getBytes("UTF-8"));
        System.out.println("Producer Send +'" + message + "'");

        //关闭通道和连接
        channel.close();
        connection.close();
    }
}
