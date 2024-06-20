package com.project.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class SingleConsumer {

    public static final String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        //  创建链接
        ConnectionFactory factory = new ConnectionFactory();
        //  这里还可以设置用户名密码和端口
        factory.setHost("localhost");
        //  建立连接
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        //  声明队列 这里参数必须和发送者一致
        channel.queueDeclare(QUEUE_NAME, false, false, false, null);
        System.out.println("[*] Waiting for message. To exit press CTRL + C");
        //  定义如何处理消息
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
            System.out.println("[x] Sent:" + message);
        };
        //  消费消息 会持续阻塞
        channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});

    }
}
