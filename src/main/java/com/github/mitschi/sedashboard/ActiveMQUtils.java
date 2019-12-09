package com.github.mitschi.sedashboard;

import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.Serializable;

public class ActiveMQUtils {

    private MessageProducer producer;
    private  ActiveMQConnectionFactory connectionFactory;
    private Connection connection;
    private Session session;
    private Queue destination;

    public  void send(Serializable content) {
        try {
            if(producer==null) {
                init("testQUEUE","localhost");
            }
            ObjectMessage message = session.createObjectMessage(content);
            producer.send(message);
        }
        catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public  void init(String queueName,String hostname) throws JMSException {
        // Create a ConnectionFactory
        connectionFactory = new ActiveMQConnectionFactory("tcp://"+hostname+":61616");
//        connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.0.3:61616");
//        connectionFactory.setTrustedPackages(Arrays.asList(new String[]{"at.aau"}));
        connectionFactory.setTrustAllPackages(true);
        // Create a Connection
        connection = connectionFactory.createConnection();
        connection.start();

        // Create a Session
        session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create the destination (Topic or Queue)
        destination = session.createQueue(queueName);

        // Create a MessageProducer from the Session to the Topic or Queue
        producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);

    }

    public  void close() throws JMSException {
        // Clean up
        session.close();
        connection.close();
    }
}
