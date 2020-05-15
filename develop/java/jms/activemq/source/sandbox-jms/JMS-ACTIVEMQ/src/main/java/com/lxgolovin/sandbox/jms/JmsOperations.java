package com.lxgolovin.sandbox.jms;

import java.util.Optional;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class JmsOperations {

    static final long TIMEOUT = 1000;

    private final Session session;

    private final String queueName;

    public boolean send(String messageText) {
        return Optional.ofNullable(messageText)
            .map(this::sendMessage)
            .orElse(false);
    }

    public String receive() {
        String messageText = null;
        try{
            Queue queue = session.createQueue(queueName);
            MessageConsumer consumer = session.createConsumer(queue);
            TextMessage message = (TextMessage) consumer.receive(TIMEOUT);
            messageText = message.getText();
            log.info("Message {} was received", messageText);
        } catch (JMSException e) {
            log.error("Message was not received", e);
        }

        return messageText;
    }

    private boolean sendMessage(String messageText) {
        try{
            Queue queue = session.createQueue(queueName);
            MessageProducer producer = session.createProducer(queue);
            TextMessage message = session.createTextMessage(messageText);
            producer.send(message);
            log.info("Message {} was sent", messageText);
            return true;
        } catch (JMSException e) {
            log.error("Message {} was not sent", messageText, e);
            return false;
        }
    }
}
