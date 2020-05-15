package com.lxgolovin.sandbox.jms;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import java.time.LocalDate;
import java.util.UUID;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.naming.NamingException;
import org.junit.jupiter.api.Test;

class JmsOperationsTest {

//    @Test
    void basicSend() throws JMSException {
        String messageText = new Gson().toJson(new Email(LocalDate.now(), "Nick", "Alex"));

        try (InitConnection connection = new InitConnection()) {
            Session session = connection.start();
            JmsOperations operations = new JmsOperations(session, connection.getQueueName());
            assertTrue(operations.send(messageText));
        }
    }

    @Test
    void basicSendReceiveWithTimeOut() throws JMSException {
        String messageText = UUID.randomUUID().toString();

        try (InitConnection connection = new InitConnection()) {
            Session session = connection.start();
            JmsOperations operations = new JmsOperations(session, connection.getQueueName());
            assertTrue(operations.send(messageText));

            String messageTextReply = operations.receive();
            assertNotNull(messageTextReply);
            assertEquals(messageText, messageTextReply);
        }
    }

    @Test
    void basicSendReceiveWithTimeOutUsingJndi() throws JMSException, NamingException {
        String messageText = UUID.randomUUID().toString();

        try (InitConnection connection = new InitConnection()) {
            Session session = connection.startJndiConnection();
            JmsOperations operations = new JmsOperations(session, connection.getQueueName());
            assertTrue(operations.send(messageText));

            String messageTextReply = operations.receive();
            assertNotNull(messageTextReply);
            assertEquals(messageText, messageTextReply);
        }
    }
}