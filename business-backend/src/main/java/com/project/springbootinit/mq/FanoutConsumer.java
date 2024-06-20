package com.project.springbootinit.mq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class FanoutConsumer {

    public static final String EXCHANGE_NAME = "fanout_exchange";

    public static void main(String[] args) throws Exception {
        //  创建链接
        ConnectionFactory factory = new ConnectionFactory();
        //  这里还可以设置用户名密码和端口
        factory.setHost("localhost");
        //  建立连接
        Connection connection = factory.newConnection();
        Channel channel1 = connection.createChannel();
        Channel channel2 = connection.createChannel();
        //  声明交换机
        channel1.exchangeDeclare(EXCHANGE_NAME, "fanout");
        //  创建队列 随机分配队列名称
        String queueName1 = "小王";
        channel1.queueDeclare(queueName1, true, false, false, null);
        channel1.queueBind(queueName1, EXCHANGE_NAME, "");

        String queueName2 = "小李";
        channel2.queueDeclare(queueName2, true, false, false, null);
        channel2.queueBind(queueName2, EXCHANGE_NAME, "");

        DeliverCallback deliverCallback1 = (consumerTag, delivery) -> {
            String string = new String(delivery.getBody(), "UTF-8");
            System.out.println("[小李]Received" + string);
        };
        DeliverCallback deliverCallback2 = (consumerTag, delivery) -> {
            String string = new String(delivery.getBody(), "UTF-8");
            System.out.println("[小王]Received" + string);
        };
        channel1.basicConsume(queueName1, true, deliverCallback1, consumerTag -> {});
        channel2.basicConsume(queueName2, true, deliverCallback2, consumerTag -> {});
    }
}
