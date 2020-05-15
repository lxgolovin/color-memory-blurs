# Message Selectors

* Used by receivers and subscribers.
* Subset of the SQL WHERE clause
* Criteria is based on the header properties
    * JMSPriority > 4
    * iban = ‘DE443794221391961944346’ AND amount > 50
* Exact match (case sensitive) of header property

```java
message.setStringProperty("iban", "DE443794221391961944346");
```

## Operators

* Logical operators: `and`, `or`
* Algebraic operators: `=`, `<`, `>`, `<=`, `>=`, `<>`
* Arithmetic operators: `+`, `-`, `*`, `/`
* Comparison operators: like, between, in, not, is null, is not null

## Message selectors examples

* JMSPriority in (5, 6, 7)
* iban like ‘DE%’
* amount > 50
* amount between 10 and 20

## Setting message selectors

* On the message producer side:
```java
message.setStringProperty("iban", "DE443794221391961944346");
```
* On the message consumer side:
```java
session.createConsumer(queue, "iban = 'DE443794221391961944346'");
```

## Example:

Sender
```java
public class JMSSender {

	public static void main(String[] args) throws JMSException, InterruptedException {

		Logger logger = LogManager.getLogger(JMSSender.class);

		String[] ibans = new String[]{"DE443794221391961944346", "FR143841010373619987209"};

		ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61616");
		Connection connection = cf.createConnection();
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Queue queue = session.createQueue("ACCOUNTS.QUEUE");

		MessageProducer sender = session.createProducer(queue);

		for (int i = 0; i < 5; i++) {
			int amount = (int) (100 * Math.random());
			TextMessage message = session.createTextMessage("DEPOSIT " + amount + " EUR into account " + ibans[i%2]);

			message.setStringProperty("iban", ibans[i%2]);
			message.setIntProperty("amount", amount);

			sender.send(message);
			logger.info("Message sent: {}", message.getText());
			Thread.sleep(2000);
		}

		connection.close();
	}
}
```

Two receivers
```java
public class JMSAsyncReceiver {

	private Logger logger = LogManager.getLogger(JMSAsyncReceiver.class);

	public JMSAsyncReceiver() {
		try {
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
			Connection connection = connectionFactory.createConnection();
			connection.start();

			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue queue = session.createQueue("ACCOUNTS.QUEUE");
			MessageConsumer receiver = session.createConsumer(queue, "iban like 'DE%' and amount > 20");
			MessageConsumer receiverOther = session.createConsumer(queue, "iban not like 'DE%'");
//			MessageConsumer receiverOther1 = session.createConsumer(queue, "iban like 'DE%' and amount <= 20");
			receiver.setMessageListener(message -> {
				try {
					logger.info(((TextMessage) message).getText());
				} catch (JMSException jmsException) {
					jmsException.printStackTrace();
				}
			});
			receiverOther.setMessageListener(message -> {
				try {
					logger.info(((TextMessage) message).getText());
				} catch (JMSException jmsException) {
					jmsException.printStackTrace();
				}
			});
			logger.info("Waiting on messages fulfilling the condition: {}", receiver.getMessageSelector());
		} catch (JMSException jmsException) {
			jmsException.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Thread() {
			public void run() {
				new JMSAsyncReceiver();
			}
		}.start();
	}

}
```
