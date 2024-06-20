package com.project.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class SingleProducer {

    private final static String QUEUE_NAME = "hello";

    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        //  这里还可以设置用户名密码和端口
        factory.setHost("localhost");
        //  建立连接
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        try {
            //  创建消息队列
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            String message = "Hello World";
            channel.basicPublish("", QUEUE_NAME, null, message.getBytes(StandardCharsets.UTF_8));
            System.out.println("[x] Sent:" + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
