package at.hollandermalik.jmschat;

import java.io.Closeable;
import java.io.IOException;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;

public class ChatRoom implements Closeable {

	private JMSChat chat;
	private String topicName;

	private Handler<ChatMessage> messageHandler;

	private Destination destination;
	private MessageProducer producer;
	private MessageConsumer consumer;

	private MessageReciever messageReciever;

	public ChatRoom(JMSChat chat, String topicName) {
		this.chat = chat;
		this.topicName = topicName;
	}

	public void join() throws JMSException {
		this.destination = this.getChat().getSession().createTopic(this.getTopicName());

		this.producer = this.getChat().getSession().createProducer(this.getDestination());
		this.getProducer().setDeliveryMode(DeliveryMode.NON_PERSISTENT);

		this.consumer = this.getChat().getSession().createConsumer(this.getDestination());

		this.messageReciever = new MessageReciever(this);
	}

	public Handler<ChatMessage> getMessageHandler() {
		return messageHandler;
	}

	public void setMessageHandler(Handler<ChatMessage> messageHandler) {
		this.messageHandler = messageHandler;
	}

	public void sendMessage(String message) throws JMSException {
		this.getProducer().send(MessageUtil.serializeMessage(this.getChat().getSession(), new ChatMessage(this.getChat().getNickname(), message)));
	}

	public JMSChat getChat() {
		return this.chat;
	}

	public String getTopicName() {
		return this.topicName;
	}

	public Destination getDestination() {
		return this.destination;
	}

	public MessageProducer getProducer() {
		return this.producer;
	}

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
					if (this.chatRoom.getMessageHandler() != null) {
						ChatMessage chatMessage = MessageUtil.deserializeMessage(message);
						this.chatRoom.getMessageHandler().handle(chatMessage);
					}
				} catch (JMSException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void close() throws IOException {
			this.running = false;
		}
	}
}
