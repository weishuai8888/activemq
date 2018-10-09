package com.weishuai.first;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * PTP - 主动消费（消息的消费者）
 * 消息的接收者
 * Created by WS on 2018/10/9.
 */
public class TextConsumer {

    public String receiveTextMessage() {
        String resultCode = "";
        ConnectionFactory factory = null;
        Connection connection = null;
        Session session = null;
        Destination destination = null;
        MessageConsumer consumer = null;
        Message message = null;
        try {
            //创建工厂
            factory = new ActiveMQConnectionFactory("admin", "admin", "tcp://192.168.107.128:61616");
            //创建连接
            connection = factory.createConnection();
            //消息的接收者必须开启连接，否则无法处理消息。
            connection.start();
            //创建会话对象
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //创建目的地
            destination = session.createQueue("first-mq");
            //创建消息消费者对象，在指定目的地中获取消息(必须指定目的地)
            consumer = session.createConsumer(destination);
            //获取队列中的消息。receive()是一个主动获取消息的方法。执行一次，拉取一个消息。学习时使用，开发少用。
            message = consumer.receive();
            //处理文本消息
            resultCode = ((TextMessage)message).getText();
        } catch (JMSException e) {
            e.printStackTrace();
        }finally {
            //回收资源
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
        TextConsumer consumer = new TextConsumer();
        String str = consumer.receiveTextMessage();
        System.out.println(str);
    }
}
