package com.weishuai.topic;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * PUB & SUB模型
 * 消息发送者
 * 发送一个字符串文本消息到ActiveMQ中
 * Created by WS on 2018/10/10.
 */
public class TopicProducer {

    public void sendTextMessage(String datas) {
        ConnectionFactory factory = null;
        Connection connection = null;
        Session session = null;
        Destination destination = null;
        MessageProducer producer = null;
        Message message = null;

        try {
            //创建工厂
            factory = new ActiveMQConnectionFactory("admin", "admin", "tcp://192.168.107.128:61616");
            //创建连接
            connection = factory.createConnection();
            //开启连接 建议启动；消息的发送者不必启动，消息的接收者必须启动
            connection.start();
            //创建会话
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            //创建目的地：topic
            destination = session.createTopic("test-topic");
            //创建消息发送者的producer
            producer = session.createProducer(destination);
            //创建文本消息
            message = session.createTextMessage(datas);
            //发送消息
            producer.send(message);
            System.out.println("消息已发送！");
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            if (producer != null) {//回收消息发送者
                try {
                    producer.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        TopicProducer producer = new TopicProducer();
        producer.sendTextMessage("测试ActiveMQ消息！");
    }
}
