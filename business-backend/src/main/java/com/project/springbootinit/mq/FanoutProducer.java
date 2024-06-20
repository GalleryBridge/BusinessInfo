package com.project.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class FanoutProducer {

    public static final String EXCHANGE_NAME = "fanout_exchange";

    public static void main(String[] args) throws Exception {
        //  创建链接
        ConnectionFactory factory = new ConnectionFactory();
        //  这里还可以设置用户名密码和端口
        factory.setHost("localhost");
        //  建立连接
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            //  创建交换机
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout");

            Scanner scanner = new Scanner(System.in);
            while (scanner.hasNext()) {
                String message = scanner.nextLine();
                channel.basicPublish(EXCHANGE_NAME, "", null, message.getBytes("UTF-8"));
                System.out.println("[x] Sent:" + message);
            }
        }
    }
}