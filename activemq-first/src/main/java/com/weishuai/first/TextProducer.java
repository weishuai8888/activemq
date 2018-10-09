package com.weishuai.first;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

/**
 * 发送一个字符串文本消息到ActiveMQ中
 * Created by WS on 2018/10/9.
 */
public class TextProducer {

    /**
     * 发送消息到activemq中，具体的消息内容为参数信息
     * 开发JMS相关代码的过程中，使用的接口类型都是javax.jms包下的类型
     *
     * @param datas - 消息内容
     */
    public void sendTextMessage(String datas) {
        //连接工厂
        ConnectionFactory factory = null;
        //连接
        Connection connection = null;
        //目的地
        Destination destination = null;
        //会话
        Session session = null;
        //消息的发送者
        MessageProducer producer = null;
        //消息对象
        Message message = null;
        try {
            //创建连接工厂；连接ActiveMQ服务的连接工厂
            //创建工厂；构造方法有三个参数：用户名、密码、连接地址
            //无参构造；有默认的连接地址。本地连接：tcp://localhost:61616。
            //单参数构造：无验证模式的（任何人都可以发送、接收消息）
            //三参数构造：有认证 + 指定地址。默认端口：61616.可以从ActiveMQ的conf/activemq.xml配置文件中查看。
            factory = new ActiveMQConnectionFactory("guest", "guest", "tcp://192.168.107.128:61616");

            //通过工厂，创建连接对象
            //创建连接的方法有重载，其中creatConnection(String userName, String password)
            //可以在创建连接和工厂时，只传递链接地址，不传递用户信息（用构建工厂时，创建的用户名和密码进行连接）；
            connection = factory.createConnection();
            //启动连接。消息的发送者不是必须启动连接，消息的消费者必须启动连接
            //建议启动连接。
            //producer在发送消息的时候会检查是否启动了连接。如果未启动，自动启动。
            //如果有特殊的配置，建议配置完毕后再启动连接
            connection.start();

            //通过连接对象，创建会话对象。
            /*
            * 创建会话的时候必须传两个参数：一个是是否支持事务（消息的发送）；一个是如何确认消息处理（消息的接收）
            * transacted - 是否支持事务（消息的发送），数据类型Boolean： true - 支持、false - 不支持。
            * true:支持事务。第二个参数对producer来说默认无效（consumer无事务），建议传递的参数时Session.SESSION_TRANSACTED（消息可能会批量发送，建议使用事务。事务会提供一个缓冲区）
            * false:不支持事务。常用参数，第二个参数必须传递。
            *
            * acknowledgeMode - 如何确认消息的处理（消息的接收）， 使用确认机制实现的。
            * AUTO_ACKNOWLEDGE - 自动确认消息。消息的消费者处理消息后，自动确认。常用（商业开发不推荐）。
            * CLIENT_ACKNOWLEDGE - 客户端手动确认，消息的消费者处理后，必须手工确认（不确认消息，消息等同于没有消费）。
            * DUPS_OK_ACKNOWLEDGE - 有副本的客户端手动确认
            *   一个消息可以多次处理；
            *   可以降低session的消耗，在可以容忍消息可以重复的时候使用（不推荐）
            * */
            session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            //创建目的地。参数是目的地名称，即目的地的唯一标记。
            destination = session.createQueue("first-mq");

            //通过会话对象，创建消息的发送者producer
            //创建的消息发送者，发送的消息一定到指定的目的地中。
            //创建producer的时候，可以不提供目的地，在发送消息的时候指定目的地（如： producer = session.createProducer(null);）。
            producer = session.createProducer(destination);

            //创建文本消息对象，作为具体数据内容的载体。
            message = session.createTextMessage(datas);

            //使用producer发送消息到ActiveMQ的目的地中。如果消息发送失败，抛出异常。
            //如果消息发送太多，堆积到了MQ中，可能会造成内存的压力问题，造成MQ崩溃。原因是代码设计问题。可能是consumer太少了或者没有限流。
            producer.send(message);

            System.out.println("消息已发送成功！");
        } catch (JMSException e) {
            e.printStackTrace();
        } finally {
            //回收资源
            if (producer != null) {//回收发送者
                try {
                    producer.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
            if (session != null){//回收会话对象
                try {
                    session.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null){//回收连接对象
                try {
                    connection.close();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        TextProducer producer = new TextProducer();
        producer.sendTextMessage("测试ActiveMQ");
    }
}
