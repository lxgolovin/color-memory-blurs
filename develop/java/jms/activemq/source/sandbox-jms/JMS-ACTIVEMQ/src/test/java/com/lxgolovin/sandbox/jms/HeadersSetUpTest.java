package com.lxgolovin.sandbox.jms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.Date;
import java.util.UUID;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HeadersSetUpTest {

    private static final String MESSAGE_ID = "ID:0123456789";
    private static final int DEFAULT_JMS_EXPIRATION = 0;
    private static final int DEFAULT_JMS_PRIORITY = 4;

    private Connection connection;

    @BeforeEach
    public void initAll() throws JMSException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        connection = connectionFactory.createConnection();
        connection.start();
    }

    /**
     * Setting header with message is ignored. So all settings will fail
     */
    @Test
    void failedToSetUpHeaders() throws JMSException {
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("ACCOUNTS.QUEUE");
        String messageString = UUID.randomUUID().toString();

        MessageProducer producer = session.createProducer(queue);
        MessageConsumer consumer = session.createConsumer(queue);

        TextMessage message = session.createTextMessage(messageString);

        long messageTimeStamp = new Date().getTime() + 5000;
        message.setJMSMessageID(MESSAGE_ID);
        message.setJMSDeliveryMode(DeliveryMode.NON_PERSISTENT);
        message.setJMSTimestamp(messageTimeStamp);
        message.setJMSExpiration(new Date().getTime() + 10000);
        message.setJMSPriority(9);
        message.setJMSRedelivered(true);

        producer.send(message);
        TextMessage messageReceived = (TextMessage) consumer.receive(JmsOperations.TIMEOUT);

        assertEquals(messageString, messageReceived.getText());
        assertNotEquals(MESSAGE_ID, messageReceived.getJMSMessageID());
        assertNotEquals(DeliveryMode.NON_PERSISTENT, messageReceived.getJMSDeliveryMode());
        assertNotEquals(messageTimeStamp, messageReceived.getJMSTimestamp());
        assertEquals(DEFAULT_JMS_EXPIRATION, messageReceived.getJMSExpiration());
        assertEquals(DEFAULT_JMS_PRIORITY, messageReceived.getJMSPriority());
        assertFalse(messageReceived.getJMSRedelivered());
    }

    /**
     * Setting header with producer is not ignored. So all settings will accept
     */
    @Test
    void acceptToSetUpHeaders() throws JMSException {
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("ACCOUNTS.QUEUE");
        String messageString = UUID.randomUUID().toString();

        MessageProducer producer = session.createProducer(queue);
        MessageConsumer consumer = session.createConsumer(queue);
        TextMessage message = session.createTextMessage(messageString);

        long messageTimeStamp = new Date().getTime() + 5000;

        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        producer.setTimeToLive(messageTimeStamp);
        producer.setPriority(9);
        producer.send(message);

        TextMessage messageReceived = (TextMessage) consumer.receive(JmsOperations.TIMEOUT);

        assertEquals(messageString, messageReceived.getText());
        assertEquals(DeliveryMode.NON_PERSISTENT, messageReceived.getJMSDeliveryMode());
        assertNotEquals(DEFAULT_JMS_EXPIRATION, messageReceived.getJMSExpiration());
        assertNotEquals(DEFAULT_JMS_PRIORITY, messageReceived.getJMSPriority());
    }

    @AfterEach
    public void shutdownAll() throws JMSException {
        connection.close();
    }
}
