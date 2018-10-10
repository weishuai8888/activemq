package com.weishuai.topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * PUB & SUB模型
 * 消息消费者
 * Created by WS on 2018/10/10.
 */
public class TopicConsumer {

    public String receiveMessage() {
        String resultCode = "";
        ConnectionFactory factory = null;
        Connection connection = null;
        Session session = null;
        Destination destination = null;
        MessageConsumer consumer = null;
        Message message = null;

        try {
            factory = new ActiveMQConnectionFactory("admin", "admin", "tcp://192.168.107.128:61616");
            connection = factory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            destination = session.createTopic("test-topic");
            consumer = session.createConsumer(destination);
            message = consumer.receive();
            resultCode = ((TextMessage) message).getText();
        } catch (JMSException e) {
            e.printStackTrace();
        }finally {
            if (consumer != null){
                try {
                    consumer.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
            if (session != null){
                try {
                    session.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null){
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultCode;
    }

    public static void main(String[] args) {
        TopicConsumer consumer = new TopicConsumer();
        String str = consumer.receiveMessage();
        System.out.println("消息是：" + str);
    }
}
