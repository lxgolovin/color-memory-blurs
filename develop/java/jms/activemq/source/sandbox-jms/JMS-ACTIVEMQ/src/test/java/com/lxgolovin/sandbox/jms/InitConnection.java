package com.lxgolovin.sandbox.jms;

import java.util.Optional;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.ActiveMQConnectionFactory;

@Slf4j
@NoArgsConstructor
public class InitConnection implements AutoCloseable {

    private Connection connection;

    @Getter
    private String queueName = "ACCOUNTS.QUEUE";

    public Session start() throws JMSException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
        this.connection = connectionFactory.createConnection();

        connection.start();

        log.info("Connection started");
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    public Session startJndiConnection() throws JMSException, NamingException {
        Context context = new InitialContext();

        Queue queue = (Queue) context.lookup("ACCOUNTS.QUEUE");
        this.queueName = queue.getQueueName();

        ConnectionFactory connectionFactory = (ConnectionFactory) context.lookup("ConnectionFactory");
        this.connection = connectionFactory.createConnection();
        connection.start();

        log.info("Jndi connection started");
        return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
    }

    @Override
    public void close() {
        Optional.ofNullable(connection).ifPresent(this::closeConnection);
    }

    private void closeConnection(Connection con) {
        try {
            con.close();
            log.info("Connection closed");
        } catch (JMSException e) {
            log.error("Cannot close connection", e);
        }
    }
}
