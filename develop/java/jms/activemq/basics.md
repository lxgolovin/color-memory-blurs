# What is messaging

## Usage Basics

* Messaging - a method of communication between software components or applications.
* A messaging client can send messages to, and receive messages from any other client.
* Each client connects to a messaging agent
* A messaging agent provides facilities for creating, sending, receiving, and reading messages.
* Messaging enables loosely coupled distributed communication.
* A component sends a message to a destination, the recipient can retrieve the message from the destination.
* The sender and the receiver do not have to be available at the same time in order to communicate

## What do sender and receiver need to know?

* The sender does not need to know anything about the receiver.
* The receiver does not need to know anything about the sender.
* The sender and the receiver need to know only which message format and which destination to use.

## What is the JMS API?

* JMS - a Java API that allows applications to create, send, receive, and read messages.
* JMS API defines a common set of interfaces that allow Java programs to communicate.
* The JMS API enables communication that is:
    * Loosely coupled
    * Asynchronous: A JMS provider can deliver messages to a client as they arrive; a client does not have to request messages in order to receive them.
    * Reliable: The JMS API can ensure that a message is delivered once and only once.
* JMS provider (broker) - a messaging system that implements the JMS interfaces and provides administrative features.
* JMS clients - Java programs or components that produce and consume messages.
* Messages - objects that communicate information between JMS clients.

## Point-to-Point messaging - fire and forget

* Point-to-Point messaging uses queues, senders and receivers.
* The message is delivered to one single receiver.
* Most providers do some round-robin algorithm to balance the receivers.

![Point to point, fire and forget](./img/point-to-point-jms-fire-forget.png)

## Point-to-Point messaging - request - reply

* The sender needs to wait for the response.
* Sender cannot advance until receiving a response – blocking wait. Example: the sender places some information about a client into a queue and needs to get some confirmation that the client has been received.
* Point to point messaging supports load balancing of receivers.

![Point to point, request - reply](./img/point-to-point-jms-request-reply.png)

## Publish - subscribe messaging

* Publish-Subscribe messaging uses topics, publishers, and subscribers.
* The message is delivered to each subscriber.
* Fire and forget broadcasting.
* Publish-Subscribe messaging supports load balancing of subscribers starting with JMS 2.0.
* Subscribers are not known by the publisher.

![Publish - subscribe messaging](./img/publish-subscribe-jms.png)

## Get ActiveMQ

* Download ActiveMQ http://activemq.apache.org/download.html
* Unzip the archive, you will get an ${ACTIVEMQ_HOME} folder
* Start up ActiveMQ  ${ACTIVEMQ_HOME}/bin/activemq start – default start on port 61616 (admin:admin)
* Start up producer ${ACTIVEMQ_HOME}/bin/activemq producer
* Start up consumer ${ACTIVEMQ_HOME}/bin/activemq consumer

## Usefull links

* [My Jms tryings](./source/)
* [Building an Application with Spring Boot](https://spring.io/guides/gs/spring-boot/)
* [Spring Boot Embedded ActiveMQ Configuration Example](https://memorynotfound.com/spring-boot-embedded-activemq-configuration-example/)
* [Setting and Reading Spring JMS Message Header Properties Example](https://memorynotfound.com/spring-jms-setting-reading-header-properties-example/)
* [Spring Jms ActiveMQ – How to create a SpringBoot ActiveMQ Response Management application by @SendTo annotation](https://grokonez.com/spring-framework/spring-jms/activemq-create-springboot-activemq-response-management-application-sendto-annotation)
* [Gson – How to convert Java object to / from JSON](https://mkyong.com/java/how-do-convert-java-object-to-from-json-format-gson-api/)
* [Spring Boot Jms ActiveMQ Example](https://www.devglan.com/spring-boot/spring-boot-jms-activemq-example)
* [Spring JMS Example + ActiveMQ + Annotation/ JavaConfig](https://www.devglan.com/spring-mvc/spring-jms-activemq-integration-example)
* [spring-guides/gs-messaging-jms](https://github.com/spring-guides/gs-messaging-jms)
* [ibm-messaging/mq-jms-spring](https://github.com/ibm-messaging/mq-jms-spring)
* [Messaging with JMS](https://spring.io/guides/gs/messaging-jms/)
* [MQ JMS application development with Spring Boot](https://developer.ibm.com/technologies/java/tutorials/mq-jms-application-development-with-spring-boot/)
* [Getting Started with Spring JMS](https://www.baeldung.com/spring-jms)
* [Разработка MQ JMS приложения на Spring Boot](https://habr.com/ru/post/479232/)
