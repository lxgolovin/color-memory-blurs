package com.lxgolovin.sandbox.jms;

import com.google.gson.Gson;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.support.JmsMessageHeaderAccessor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Listener {

//    @JmsListener(destination = "ACCOUNTS.QUEUE")
//    @SendTo("ACCOUNTS.QUEUE.RESP")
    // This is a listener with basic gson
    public Message<Email> receiveMessage(final javax.jms.Message jsonMessage) throws JMSException {
        String messageData = null;
        Email email = new Email(new Date(), "Nick", "Alex");
        if(jsonMessage instanceof TextMessage) {
            log.info("Text message");
            TextMessage textMessage = (TextMessage)jsonMessage;
            messageData = textMessage.getText();
            email = new Gson().fromJson(messageData, Email.class);
            log.info("name {}", email.getName());
            log.info("to {}", email.getTo());
        }
        log.info(messageData);


        MessageHeaderAccessor messageHeaderAccessor = new MessageHeaderAccessor();
        messageHeaderAccessor.copyHeaders(new HashMap<String, Object>(){{
            put("jms-custom-property-bool", true);
            put("jms-custom-property-price", 5);
        }});

        return MessageBuilder
            .withPayload(email)
            .setHeaders(messageHeaderAccessor)
            .setHeader("sadfsdfsdfdsf", 33)
            .build();
    }



    @JmsListener(destination = "ACCOUNTS.QUEUE")
    @SendTo("ACCOUNTS.QUEUE.RESP")
    public Message<Email> receiveMessage(@Payload Email email,
        @Header(name = "jms-header-not-exists", defaultValue = "default") String nonExistingHeader,
        @Headers Map<String, Object> headers,
        MessageHeaders messageHeaders,
        JmsMessageHeaderAccessor jmsMessageHeaderAccessor) {

        log.info("received <" + email + ">");

        log.info("\n# Spring JMS accessing single header property");
        log.info("- jms-header-not-exists=" + nonExistingHeader);

        log.info("\n# Spring JMS retrieving all header properties using Map<String, Object>");
        log.info("- jms-custom-header=" + headers.get("jms-custom-property"));

        log.info("\n# Spring JMS retrieving all header properties MessageHeaders");
        log.info("- jms-custom-property-price=" + messageHeaders.get("jms-custom-property-price", Double.class));

        log.info("\n# Spring JMS retrieving all header properties JmsMessageHeaderAccessor");
        log.info("- jms_destination=" + jmsMessageHeaderAccessor.getDestination());
        log.info("- jms_priority=" + jmsMessageHeaderAccessor.getPriority());
        log.info("- jms_timestamp=" + jmsMessageHeaderAccessor.getTimestamp());
        log.info("- jms_type=" + jmsMessageHeaderAccessor.getType());
        log.info("- jms_redelivered=" + jmsMessageHeaderAccessor.getRedelivered());
        log.info("- jms_replyTo=" + jmsMessageHeaderAccessor.getReplyTo());
        log.info("- jms_correlationId=" + jmsMessageHeaderAccessor.getCorrelationId());
        log.info("- jms_contentType=" + jmsMessageHeaderAccessor.getContentType());
        log.info("- jms_expiration=" + jmsMessageHeaderAccessor.getExpiration());
        log.info("- jms_messageId=" + jmsMessageHeaderAccessor.getMessageId());
        log.info("- jms_deliveryMode=" + jmsMessageHeaderAccessor.getDeliveryMode() + "\n");


        MessageHeaderAccessor messageHeaderAccessor = new MessageHeaderAccessor();
        messageHeaderAccessor.copyHeaders(new HashMap<String, Object>(){{
            put("jms-custom-property-bool", true);
            put("jms-custom-property-price", 5);
        }});

        return MessageBuilder
            .withPayload(email)
            .setHeaders(messageHeaderAccessor)
            .setHeader("sadfsdfsdfdsf", 33)
            .build();
    }
}
