package com.weishuai.listener;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * PTP - 观察者消费(消息发送者)
 * Created by WS on 2018/10/10.
 */
public class ObjectProducer {

    public void sendMessage(Object obj) {
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
            //开启连接
            connection.start();
            //创建会话
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            //创建目的地
            destination = session.createQueue("test-listener");
            //消息发送者
            producer = session.createProducer(destination);
            for (int i = 0; i < 100; i++){
                //创建消息文本对象
                message = session.createObjectMessage(i);
                //发送消息
                producer.send(message);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }finally {
            if (producer != null){
                try {
                    producer.close();
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
    }

    public static void main(String[] args) {
        ObjectProducer producer = new ObjectProducer();
        producer.sendMessage(null);
    }
}
