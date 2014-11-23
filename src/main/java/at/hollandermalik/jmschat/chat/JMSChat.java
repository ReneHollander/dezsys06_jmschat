package at.hollandermalik.jmschat.chat;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

import at.hollandermalik.jmschat.message.ChatMessage;
import at.hollandermalik.jmschat.util.Handler;
import at.hollandermalik.jmschat.util.Util;

/**
 * A JMS Message client. You can join ChatRooms and send Messages to a users
 * mailbox.
 * 
 * @author Rene Hollander
 */
public class JMSChat implements Closeable {

	private URI brokerUri;

	private InetAddress myIp;
	private String nickname;

	private ActiveMQConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;

	private ChatRoom currentChatRoom;
	private Mailbox mailbox;

	private Handler<ChatMessage> messageHandler;

	/**
	 * Construct but not start a new JMSChat. Use start() to actually start the
	 * Chat. You don't need an internet connection using the constructor.
	 * 
	 * @param brokerUri
	 *            URI of the broker
	 * @param nickname
	 *            Nickname of the user
	 */
	public JMSChat(URI brokerUri, String nickname) {
		this.brokerUri = brokerUri;

		this.nickname = nickname;
	}

	/**
	 * Start the JMSChat. Here the first connection attempt to the broker is
	 * made.
	 * 
	 * @throws IOException
	 * @throws JMSException
	 */
	public void start() throws IOException, JMSException {
		this.myIp = Util.getIp();

		// Create a ConnectionFactory
		this.connectionFactory = new ActiveMQConnectionFactory(this.brokerUri);

		// Create a Connection
		this.connection = connectionFactory.createConnection();
		this.connection.start();

		// Create a Session
		this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
	}

	/**
	 * Join a chatroom. If there is another ChatRoom open the room gets closed.
	 * 
	 * @param topicName
	 *            Name of the chatroom
	 * @return Chatroom that we connected to
	 * @throws IOException
	 * @throws JMSException
	 */
	public ChatRoom joinChatroom(String topicName) throws IOException, JMSException {
		if (this.getCurrentChatRoom() != null) {
			this.currentChatRoom.close();
			this.currentChatRoom = null;
		}
		this.currentChatRoom = new ChatRoom(this, topicName);
		this.currentChatRoom.join();
		return this.currentChatRoom;
	}

	/**
	 * Get the mailbox of the current user. Also needed to send messages to
	 * another users mailbox.
	 * 
	 * @return The mailbox we connected to
	 * @throws JMSException
	 */
	public Mailbox getMailbox() throws JMSException {
		if (this.mailbox == null) {
			this.mailbox = new Mailbox(this);
			this.mailbox.join();
		}
		return this.mailbox;
	}

	/**
	 * Gets the uri of the used broker
	 * 
	 * @return URI of the broker
	 */
	public URI getBrokerUri() {
		return brokerUri;
	}

	/**
	 * Gets the ip the user had on calling start()
	 * 
	 * @return IP of the user
	 */
	public InetAddress getMyIp() {
		return myIp;
	}

	/**
	 * Gets the Nickname of the user
	 * 
	 * @return Nickname of the user
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * Gets the ConnectionFactory associated with the broker uri
	 * 
	 * @return Used connection factory
	 */
	public ActiveMQConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}

	/**
	 * Gets the Connection used for the Chat and Mailbox with the JMS Broker
	 * 
	 * @return Connection with the JMS Broker
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Gets the Session used for the Chat and Mailbox
	 * 
	 * @return Session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Gets the Chatroom that we are currently in
	 * 
	 * @return Current ChatRoom
	 */
	public ChatRoom getCurrentChatRoom() {
		return currentChatRoom;
	}

	/**
	 * Sets the message handler to handle messages comming from the ChatRoom.
	 * 
	 * @param handler
	 */
	public void setMessageHandler(Handler<ChatMessage> handler) {
		this.messageHandler = handler;
	}

	/**
	 * Gets the message handler
	 * 
	 * @return MessageHandler
	 */
	public Handler<ChatMessage> getMessageHandler() {
		return this.messageHandler;
	}

	@Override
	public void close() throws IOException {
		try {
			if (this.getCurrentChatRoom() != null) {
				this.getCurrentChatRoom().close();
			}
			if (this.getMailbox() != null) {
				this.getMailbox().close();
			}
			this.getConnection().close();
			this.getSession().close();
		} catch (JMSException e) {
			throw new IOException(e);
		}
	}
}
