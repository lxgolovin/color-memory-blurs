package com.lxgolovin.sandbox.jms;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.UUID;
import java.util.stream.Stream;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.params.provider.MethodSource;

public class PropertiesSetUpTest {

    private Connection connection;

    private static Stream<Arguments> provideStringProperties() {
        return Stream.of(
            Arguments.of("JMSXUserID", UUID.randomUUID().toString()),
            Arguments.of("JMSXAppID", UUID.randomUUID().toString()),
            Arguments.of("JMSXProducerID", UUID.randomUUID().toString()),
            Arguments.of("JMSXConsumerID", UUID.randomUUID().toString()),
            Arguments.of("JMSXGroupID", UUID.randomUUID().toString())
        );
    }

    @BeforeEach
    public void initAll() throws JMSException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        connection = connectionFactory.createConnection();
        connection.start();
    }

    @ParameterizedTest(name = "#{index} - set String properties {0}")
    @MethodSource("provideStringProperties")
    void setProperties(String property, String value) throws JMSException {
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Queue queue = session.createQueue("ACCOUNTS.QUEUE");
        String messageString = UUID.randomUUID().toString();

        MessageProducer producer = session.createProducer(queue);
        MessageConsumer consumer = session.createConsumer(queue);
        TextMessage message = session.createTextMessage(messageString);

        message.setStringProperty(property, value);

        producer.send(message);
        TextMessage messageReceived = (TextMessage) consumer.receive(JmsOperations.TIMEOUT);

        assertEquals(messageString, messageReceived.getText());
        assertEquals(value, messageReceived.getStringProperty(property));
    }

    @AfterEach
    public void shutdownAll() throws JMSException {
        connection.close();
    }
}
