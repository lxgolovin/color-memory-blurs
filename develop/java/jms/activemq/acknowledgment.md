# Acknowledgment Modes

* Auto acknowledgment - Automatically marks the message as delivered when received by the consumer.
* Client acknowledgment - Marks the message as delivered only when manually acknowledged by the consumer.
* Dups OK acknowledgment

## Auto acknowledgment

```java
connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
```

As the message is delivered, the onMessage() method starts. It immediately sends back an acknowledgment o the JMS provider saying that it has been delivered.
In parallel: processing of the message on consumer side and the JMS provider has marked the message as acknowledged and removed.

## Client acknowledgment

```java
connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
```

```java
// in IMS Receiver:
message.acknowledge();
```
As the message is delivered, the onMessage() method starts. It immediately sends back an acknowledgment o the JMS provider saying that it has been delivered. The JMS provider marks the message as blocked, not delivering it to any other consumer.
The message consumer provides the acknowledge. The JMS provider receives it and, in parallel, the consumer completes the processing and the JMS provider removes the message.

## Dups OK acknowledgment

```java
connection.createSession(false, Session.DUPS_OK_ACKNOWLEDGE);
```

As it does not block the message, there is the possibility that the message is also received by another consumer – it is OK if there are duplicates. There is a time-frame window where there is a chance that another consumer receives the same message.

As the message is delivered, the onMessage() method starts. The consumer immediately starts to process.  The JMS provider lazily marks the message as delivered. The purpose is to speed-up the processing on the JMS provider side. Nowadays, there is little difference between “auto acknowledge” and “dups OK acknowledge”.
