package at.hollandermalik.jmschat;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;

public class JMSChat {

	public JMSChat() throws JMSException {
		// Create a ConnectionFactory
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");

		// Create a Connection
		Connection connection = connectionFactory.createConnection();
		connection.start();

		// Create a Session
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		// Create the destination (Topic or Queue)
		Destination destination = session.createQueue("TEST.FOO");

		// Create a MessageProducer from the Session to the Topic or Queue
		MessageProducer producer = session.createProducer(destination);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

		// Create a messages
		String text = "Hello world! From: " + Thread.currentThread().getName() + " : " + this.hashCode();
		TextMessage message = session.createTextMessage(text);

		// Tell the producer to send the message
		System.out.println("Sent message: " + message.hashCode() + " : " + Thread.currentThread().getName());
		producer.send(message);

		// Create a MessageConsumer from the Session to the Topic or Queue
		MessageConsumer consumer = session.createConsumer(destination);

		// Wait for a message
		Message recvmessage = consumer.receive(1000);

		if (recvmessage instanceof TextMessage) {
			TextMessage textMessage = (TextMessage) recvmessage;
			String recvtext = textMessage.getText();
			System.out.println("Received: " + text);
		} else {
			System.out.println("Received: " + recvmessage);
		}
		consumer.close();

		// Clean up
		session.close();
		connection.close();
	}

	public static void main(String[] args) throws JMSException {
		new JMSChat();
	}

}
