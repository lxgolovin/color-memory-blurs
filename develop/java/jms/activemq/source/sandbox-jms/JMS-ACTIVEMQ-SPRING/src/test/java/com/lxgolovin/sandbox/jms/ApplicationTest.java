package com.lxgolovin.sandbox.jms;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { Application.class }, loader = AnnotationConfigContextLoader.class)
//@SpringBootTest
@EnableConfigurationProperties
@PropertySource("classpath:application.properties")
public class ApplicationTest {

    @Autowired
    JmsTemplate jmsTemplate;

    private Email email;

    @BeforeEach
    void setUp() {
        final Date date = Date.from(LocalDate.now().atTime(LocalTime.NOON).atZone(ZoneId.systemDefault()).toInstant());
        final List<Person> personList = Arrays.asList(
            Person.builder().age(10).name("Mike again").build(),
            Person.builder().age(12).name("Mike1").build(),
            Person.builder().age(13).name("Mike2").build(),
            Person.builder().age(14).name("Mike3").build(),
            Person.builder().age(15).name("Mike4").build()
        );
        email = Email.builder()
            .date(date)
            .name("Nick")
            .to("Alex")
            .persons(personList)
            .build();
    }

    @Test
    public void testJacksonConvert() throws JMSException {
        String messageId = UUID.randomUUID().toString();
        jmsTemplate.convertAndSend("ACCOUNTS.QUEUE", email, m -> {
            m.setJMSCorrelationID(messageId);
            m.setJMSPriority(Message.DEFAULT_PRIORITY);
            m.setJMSTimestamp(System.nanoTime());
            m.setJMSType("type");

            m.setStringProperty("jms-custom-header", "this is a custom jms property");
            m.setBooleanProperty("jms-custom-property", true);
            m.setDoubleProperty("jms-custom-property-price", 0.0);

            return m;
        });

        Message message = jmsTemplate.receive("ACCOUNTS.QUEUE.RESP");
        assertNotNull(message);

        assertTrue(true);
        assertEquals(messageId , message.getJMSCorrelationID());
    }

    @Test
    public void testGsonConvert() {
        Gson gson = new GsonBuilder().setDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").create();
        String messageText = gson.toJson(email);
        jmsTemplate.send("ACCOUNTS.QUEUE",
            session -> {
            TextMessage textMessage = session.createTextMessage(messageText);
            textMessage.setStringProperty("_type", Email.class.getName());
            return textMessage;
        });

        Message message = jmsTemplate.receive("ACCOUNTS.QUEUE.RESP");
        assertNotNull(message);
    }
}