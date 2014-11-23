package at.hollandermalik.jmschat.chat;

import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;

import at.hollandermalik.jmschat.message.ChatMessage;
import at.hollandermalik.jmschat.message.MessageUtil;

/**
 * The Users Mailbox. Send and retrieve messages from a JMS Message Queue.
 * 
 * @author Rene Hollander
 */
public class Mailbox implements Closeable {

	private JMSChat chat;

	private Destination destination;
	private MessageConsumer consumer;

	/**
	 * Construct but not join the Mailbox
	 * 
	 * @param chat
	 *            JMSChat parent
	 */
	public Mailbox(JMSChat chat) {
		this.chat = chat;
	}

	/**
	 * Join the mailbox. You don't need to join the mailbox if you just want to
	 * send messages.
	 * 
	 * @throws JMSException
	 */
	public void join() throws JMSException {
		this.destination = this.getChat().getSession().createQueue(this.getChat().getNickname());
		this.consumer = this.getChat().getSession().createConsumer(destination);
	}

	/**
	 * Get the Messages in the users mailbox (JMS Queue)
	 * 
	 * @return Messages from the mailbox
	 * @throws JMSException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public List<ChatMessage> getMessageQueue() throws JMSException, ClassNotFoundException, IOException {
		List<ChatMessage> messageQueue = new ArrayList<ChatMessage>();

		Message message;
		while ((message = this.consumer.receiveNoWait()) != null) {
			messageQueue.add(MessageUtil.deserializeMessage(message));
		}
		return messageQueue;
	}

	/**
	 * Sends a message to a user mailbox
	 * 
	 * @param recieverNickname
	 *            Nickname of the message reciever
	 * @param content
	 *            Content of the message
	 * @throws JMSException
	 * @throws IOException
	 */
	public void sendMessageToQueue(String recieverNickname, String content) throws JMSException, IOException {
		Destination destination = this.getChat().getSession().createQueue(recieverNickname);
		MessageProducer producer = this.getChat().getSession().createProducer(destination);
		producer.setDeliveryMode(DeliveryMode.PERSISTENT);
		producer.send(MessageUtil.serializeMessage(this.getChat().getSession(), new ChatMessage(this.getChat().getMyIp(), LocalDateTime.now(), this.getChat().getNickname(), content)));
		producer.close();
	}

	/**
	 * Gets the parent JMSChat associated with this chatroom
	 * 
	 * @return Parent JMSChat
	 */
	public JMSChat getChat() {
		return chat;
	}

	@Override
	public void close() throws IOException {
		try {
			this.consumer.close();
		} catch (JMSException e) {
			throw new RuntimeException(e);
		}
	}
}
