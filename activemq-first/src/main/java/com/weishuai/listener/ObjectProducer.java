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
            factory = new ActiveMQConnectionFactory("admin", "admin", "tcp://192.168.107.128:61616");
            connection = factory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            destination = session.createQueue("test-listener");
            producer = session.createProducer(destination);
            for (int i = 0; i < 100; i++){
                message = session.createObjectMessage(i);
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
