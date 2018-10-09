package com.weishuai.listener;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.Serializable;

/**
 * PTP - 观察者消费(消息接收者-监听器)
 * 使用监听器的方式实现消息的处理
 * Created by WS on 2018/10/10.
 */
public class ConsumerListener {
    //处理消息
    public void consumMessage() {
        ConnectionFactory factory = null;
        Connection connection = null;
        Session session = null;
        Destination destination = null;
        MessageConsumer consumer = null;
        try {
            //创建工厂
            factory = new ActiveMQConnectionFactory("admin", "admin", "tcp://192.168.107.128:61616");
            //创建连接
            connection = factory.createConnection();
            //开启连接
            connection.start();
            //创建会话
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            //创建会话
            destination = session.createQueue("test-listener");
            //创建消费者对象
            consumer = session.createConsumer(destination);
            //注册监听器。注册成功后，队列中的消息变化会自动触发监听器代码，接收消息并处理
            consumer.setMessageListener(new MessageListener() {
                /*
                * 监听器一旦注册永久有效；
                * 永久有效的前提是：consumer线程不关闭
                * 处理消息的方式：只要消息未处理，就会自动调用onMessage方法，处理消息。
                * 监听器可以注册若干个。注册多个相当于集群。
                * ActiveMQ自动的循环调用多个监听器，处理队列中的消息，实现并行处理。
                *
                * 处理消息的方法就是监听方法
                * 坚挺的事件是消息，消息未处理。
                * 要处理的具体内容：消息处理
                * @param message - 未处理的消息
                * */
                @Override
                public void onMessage(Message message) {
                    try {
                        //acknowledge方法就是确认方法，代表consumer已经收到消息。确定后MQ删除对应的消息
                        //若果没有此方法，可能会出现重复消息的问题
                        message.acknowledge();
                        Object data = ((ObjectMessage) message).getObject();
                        System.out.println(data);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });

            //阻塞当前代码，保证listenner代码未结束，如果代码结束了，监听器自动关闭。
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (consumer != null) {
                try {
                    consumer.close();
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
        ConsumerListener listener = new ConsumerListener();
        listener.consumMessage();
    }
}
