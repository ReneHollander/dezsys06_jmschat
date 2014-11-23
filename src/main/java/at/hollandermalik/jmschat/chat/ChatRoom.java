package at.hollandermalik.jmschat.chat;

import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDateTime;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.hollandermalik.jmschat.message.ChatMessage;
import at.hollandermalik.jmschat.message.MessageUtil;

/**
 * A ChatRoom based on a JMS Topic.
 * 
 * @author Rene Hollander
 */
public class ChatRoom implements Closeable {

	private static final Logger LOGGER = LogManager.getLogger(ChatRoom.class);

	private JMSChat chat;
	private String topicName;

	private Destination destination;
	private MessageProducer producer;
	private MessageConsumer consumer;

	private MessageReciever messageReciever;

	/**
	 * Construct but not join a new ChatRoom
	 * 
	 * @param chat
	 *            JMSChat parent
	 * @param topicName
	 *            Name of the Topic to join (Chatroom)
	 */
	public ChatRoom(JMSChat chat, String topicName) {
		this.chat = chat;
		this.topicName = topicName;
	}

	/**
	 * Actually join the ChatRoom
	 * 
	 * @throws JMSException
	 *             Throws JMS Exception on JMS Error
	 */
	public void join() throws JMSException {
		this.destination = this.getChat().getSession().createTopic(this.getTopicName());

		this.producer = this.getChat().getSession().createProducer(this.getDestination());
		this.getProducer().setDeliveryMode(DeliveryMode.NON_PERSISTENT);

		this.consumer = this.getChat().getSession().createConsumer(this.getDestination());

		this.messageReciever = new MessageReciever(this);
	}

	/**
	 * Send a message to the chatroom
	 * 
	 * @param message
	 *            String content of the message
	 * @throws JMSException
	 *             Throws JMS Exception on JMS Error
	 * @throws IOException
	 *             Throws an Exception if there was an error serializing the
	 *             message
	 */
	public void sendMessage(String message) throws JMSException, IOException {
		this.getProducer().send(MessageUtil.serializeMessage(this.getChat().getSession(), new ChatMessage(this.getChat().getMyIp(), LocalDateTime.now(), this.getChat().getNickname(), message)));
	}

	/**
	 * Gets the parent JMSChat associated with this chatroom
	 * 
	 * @return Parent JMSChat
	 */
	public JMSChat getChat() {
		return this.chat;
	}

	/**
	 * Gets the Topic of the current chatroom
	 * 
	 * @return Topic
	 */
	public String getTopicName() {
		return this.topicName;
	}

	/**
	 * Gets the Desination of the chatroom
	 * 
	 * @return Destination
	 */
	public Destination getDestination() {
		return this.destination;
	}

	/**
	 * Gets the MessageProducer of the chatroom
	 * 
	 * @return MessageProducer
	 */
	public MessageProducer getProducer() {
		return this.producer;
	}

	/**
	 * Gets the MessageConsumer of the chatroom
	 * 
	 * @return MessageConsumer
	 */
	public MessageConsumer getConsumer() {
		return this.consumer;
	}

	@Override
	public void close() throws IOException {
		try {
			this.messageReciever.close();
			this.getProducer().close();
			this.getConsumer().close();
		} catch (JMSException e) {
			throw new IOException(e);
		}
	}

	private class MessageReciever implements Runnable, Closeable {

		private ChatRoom chatRoom;

		private boolean running;

		public MessageReciever(ChatRoom chatRoom) {
			this.chatRoom = chatRoom;

			this.running = true;
			Thread thread = new Thread(this);
			thread.setDaemon(true);
			thread.start();
		}

		@Override
		public void run() {
			while (this.running) {
				try {
					Message message = this.chatRoom.consumer.receive();
					if (message == null) {
						this.close();
						break;
					}
					if (this.chatRoom.getChat().getMessageHandler() != null) {
						ChatMessage chatMessage = MessageUtil.deserializeMessage(message);
						this.chatRoom.getChat().getMessageHandler().handle(chatMessage);
					}
				} catch (Exception e) {
					LOGGER.error("An Error occured while trying to read messages", e);
				}
			}
		}

		@Override
		public void close() throws IOException {
			this.running = false;
		}
	}
}