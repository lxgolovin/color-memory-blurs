package com.lxgolovin.sandbox.jms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.UUID;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueRequestor;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
public class JmsReceiveReplyTest {

    private static final String QUEUE_NAME = "ACCOUNTS.QUEUE";

    private static final String RESP_QUEUE_NAME = "ACCOUNTS.QUEUE.RESP";

    private final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();

    private Connection connection;

    @BeforeEach
    public void initAll() throws JMSException {
        connection = connectionFactory.createConnection();
        connection.start();
    }

    @Test
    void requestReplyBlocking() throws JMSException {
        String messageString = UUID.randomUUID().toString();
        String confirmationMessageString = UUID.randomUUID().toString();

        // create a receiver
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(QUEUE_NAME);
        MessageConsumer consumer = session.createConsumer(queue);
        consumer.setMessageListener(messageReceived -> {
            try {
                log.info("Message received {}", messageReceived.getJMSMessageID());
                assertEquals(messageString, ((TextMessage) messageReceived).getText());
                Thread.sleep(2000);

                TextMessage confirmationMessage = session.createTextMessage(confirmationMessageString);
                MessageProducer confirmationProducer = session.createProducer(messageReceived.getJMSReplyTo());
                confirmationProducer.send(confirmationMessage);
            } catch (JMSException | InterruptedException e) {
                fail();
            }
        });

        QueueConnection queueConnection = connectionFactory.createQueueConnection();
        queueConnection.start();
        QueueSession queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
        TextMessage message = queueSession.createTextMessage(messageString);
        QueueRequestor queueRequestor = new QueueRequestor(queueSession, queue);

        log.info("Block on waiting");
        TextMessage confirmationMessage = (TextMessage) queueRequestor.request(message);
        assertEquals(confirmationMessageString, confirmationMessage.getText());
        log.info("Confirmation Message received");
    }

    @Test
    void requestReplyAsync() throws JMSException {
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue(QUEUE_NAME);
        Queue queueReply = session.createQueue(RESP_QUEUE_NAME);
        String messageString = UUID.randomUUID().toString();
        String confirmationMessageString = UUID.randomUUID().toString();

        MessageProducer producer = session.createProducer(queue);
        MessageConsumer consumer = session.createConsumer(queue);
        TextMessage message = session.createTextMessage(messageString);
        message.setJMSReplyTo(queueReply);
        producer.send(message);
        String messageId = message.getJMSMessageID();

        // Getting message and sending confirmation
        consumer.setMessageListener(messageReceived -> {
            try {
                log.info("Message received {}", messageReceived.getJMSMessageID());
                assertEquals(messageString, ((TextMessage) messageReceived).getText());
                Thread.sleep(2000);

                TextMessage confirmationMessage = session.createTextMessage(confirmationMessageString);
                confirmationMessage.setJMSCorrelationID(messageReceived.getJMSMessageID());
                MessageProducer confirmationProducer = session.createProducer(messageReceived.getJMSReplyTo());
                confirmationProducer.send(confirmationMessage);
            } catch (JMSException | InterruptedException e) {
                fail();
            }
        });

        // getting confirmation message
        String filter = "JMSCorrelationID = '" + message.getJMSMessageID() + "'";
        MessageConsumer confirmationReceiver = session.createConsumer(queueReply, filter);
//        MessageConsumer confirmationReceiver = session.createConsumer(queueReply);
        confirmationReceiver.setMessageListener(confirmationMessageReceived -> {
            try {
                Thread.sleep(2000);
                assertEquals(confirmationMessageString, ((TextMessage) confirmationMessageReceived).getText());
                assertEquals(messageId, confirmationMessageReceived.getJMSCorrelationID());
                log.info("Confirmed {}", confirmationMessageReceived.getJMSCorrelationID());
            } catch (JMSException | InterruptedException e) {
                fail();
            }
        });

        log.info("Waiting for confirmation {}", messageId);
    }

    @AfterEach
    public void shutdownAll() throws JMSException {
        connection.close();
    }
}
