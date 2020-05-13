# JMS Properties

## JMS Message Properties

Message properties are set through:
* setStringProperty()
* setIntProperty()
* setLongProperty()

Here are the properties:
* JMSXUserID - String
* JMSXAppID - String
* JMSXProducerID - String
* JMSXConsumerID - String
* JMSXRcvTimestamp - int
* JMSXDeliveryCount - int
* JMSXState - int
* JMSXGroupID - String
* JMSXGroupSeq - int

## JMSXUserID

* Contains a String value representing the user id of the messaging client.
* Optionally set on incoming messages by the JMS application.
* Not set on outgoing messages.

## JMSXAppID

* Contains a String value representing the application id of the messaging client.
* Optionally set on incoming messages by the JMS application.
* Not set on outgoing messages.

## JMSXProducerID

* Contains the transaction identifier of the transaction where this message has been produced.
* Optionally set on incoming messages by the JMS application.
* Not set on outgoing messages.

## JMSXConsumerID

* Contains the transaction identifier of the transaction where this message has been consumed.
* Optionally set on incoming messages by the JMS application.
* Not set on outgoing messages.

## JMSXRcvTimestamp

* Contains the time (in milliseconds) when the message has been delivered to the consumer.
* Optionally set on incoming messages by the JMS application.
* Not set on outgoing messages.

## JMSXDeliveryCount

* Contains an int indicating the number of delivery attempts for this message.
* Set by the message broker.

## JMSXState

Contains an int value optionally set by the message broker indicating the internal state of the message
* 1 = waiting
* 2 = ready
* 3 = expired
* 4 = retained

## JMSXGroupID

* Contains an String value identifying the group the message is part of.
* All messages of the same group are sent to the same consumer
* The developer can set this property on outgoing messages to group messages into a numbered sequence.

## JMSXGroupSeq

* Contains an int value identifying the sequence number of the message in its group.
* The developer can set this property on outgoing messages to group messages into a numbered sequence.

## JMS Application Header Properties

Application header properties are set through:
* setStringProperty()
* setIntProperty()
* setLongProperty()
* setObjectProperty() - can only contain String objects and primitive wrapper classes

```java
MessageProducer sender = session.createProducer(queue);
TextMessage message = session.createTextMessage("DEPOSIT 100 EUR");
message.setStringProperty("ApplicationStringHeader", "1234567890");
message.setIntProperty("ApplicationIntHeader", 12345);
message.setLongProperty("ApplicationLongHeader", 1234567890123456789L);
message.setObjectProperty("ApplicationObjectHeader", new Double(1000));
```
