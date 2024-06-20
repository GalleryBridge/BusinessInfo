package com.project.springbootinit.bizmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 用于创建测试程序用到的交换机和队列
 * 只用启动一次
 */
public class BiInitMain {
    public static void main(String[] args) {

        try {
            ConnectionFactory connectionFactory = new ConnectionFactory();
            connectionFactory.setHost("localhost");
            Connection connection = connectionFactory.newConnection();
            Channel channel = connection.createChannel();

            String EXCHANGE_NAME = "bi_exchange";
            channel.exchangeDeclare(EXCHANGE_NAME, "direct");

            String queueName = "bi_queue";
            channel.queueDeclare(queueName, true, false, false, null);
            channel.queueBind(queueName, EXCHANGE_NAME, "bi_routingKey");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
