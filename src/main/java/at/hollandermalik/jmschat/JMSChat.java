package at.hollandermalik.jmschat;

import java.io.Closeable;
import java.io.IOException;
import java.net.URI;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class JMSChat implements Closeable {

	private URI brokerUri;
	private String nickname;

	private ActiveMQConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;

	private ChatRoom currentChatRoom;

	public JMSChat(URI brokerUri, String nickname) {
		this.brokerUri = brokerUri;
		this.nickname = nickname;
	}

	public void start() throws JMSException {
		// Create a ConnectionFactory
		this.connectionFactory = new ActiveMQConnectionFactory(this.brokerUri);

		// Create a Connection
		this.connection = connectionFactory.createConnection();
		this.connection.start();

		// Create a Session
		this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		/*
		 * // Create the destination (Topic or Queue) this.currentDestination =
		 * this.session.createQueue("TEST.FOO");
		 * 
		 * // Create a MessageProducer from the Session to the Topic or Queue
		 * MessageProducer producer = session.createProducer(destination);
		 * producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		 * 
		 * // Create a messages String text = "Hello world! From: " +
		 * Thread.currentThread().getName() + " : " + this.hashCode();
		 * TextMessage message = session.createTextMessage(text);
		 * 
		 * // Tell the producer to send the message
		 * System.out.println("Sent message: " + message.hashCode() + " : " +
		 * Thread.currentThread().getName()); producer.send(message);
		 * 
		 * // Create a MessageConsumer from the Session to the Topic or Queue
		 * MessageConsumer consumer = session.createConsumer(destination);
		 * 
		 * // Wait for a message Message recvmessage = consumer.receive(1000);
		 * 
		 * if (recvmessage instanceof TextMessage) { TextMessage textMessage =
		 * (TextMessage) recvmessage; String recvtext = textMessage.getText();
		 * System.out.println("Received: " + text); } else {
		 * System.out.println("Received: " + recvmessage); } consumer.close();
		 * 
		 * // Clean up session.close(); connection.close();
		 */
	}

	public ChatRoom joinChatroom(String topicName) throws IOException, JMSException {
		if (this.getCurrentChatRoom() != null) {
			this.currentChatRoom.close();
			this.currentChatRoom = null;
		}
		this.currentChatRoom = new ChatRoom(this, topicName);
		this.currentChatRoom.join();
		return this.currentChatRoom;
	}

	public URI getBrokerUri() {
		return brokerUri;
	}

	public String getNickname() {
		return nickname;
	}

	public ActiveMQConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	public Connection getConnection() {
		return connection;
	}

	public Session getSession() {
		return session;
	}

	public ChatRoom getCurrentChatRoom() {
		return currentChatRoom;
	}

	@Override
	public void close() throws IOException {
		if (this.getCurrentChatRoom() != null) {
			this.getCurrentChatRoom().close();
		}
		try {
			this.getConnection().close();
			this.getSession().close();
		} catch (JMSException e) {
			throw new IOException(e);
		}
	}
}
