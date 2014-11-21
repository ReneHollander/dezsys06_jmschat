package at.hollandermalik.jmschat;

import java.util.ArrayList;
import java.util.List;

import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;

public class Mailbox {

	private JMSChat chat;

	public Mailbox(JMSChat chat) {
		this.chat = chat;
	}

	public List<ChatMessage> getMessageQueue() throws JMSException {
		Destination destination = this.getChat().getSession().createQueue(this.getChat().getNickname());
		MessageConsumer consumer = this.getChat().getSession().createConsumer(destination);
		List<ChatMessage> messageQueue = new ArrayList<ChatMessage>();

		Message message;
		while ((message = consumer.receiveNoWait()) != null) {
			messageQueue.add(MessageUtil.deserializeMessage(message));
		}
		consumer.close();
		return messageQueue;
	}

	public void sendMessageToQueue(String recieverNickname, String content) throws JMSException {
		Destination destination = this.getChat().getSession().createQueue(recieverNickname);
		MessageProducer producer = this.getChat().getSession().createProducer(destination);
		producer.setDeliveryMode(DeliveryMode.PERSISTENT);
		producer.send(MessageUtil.serializeMessage(this.getChat().getSession(), new ChatMessage(this.getChat().getNickname(), content)));
		producer.close();
	}

	public JMSChat getChat() {
		return chat;
	}

}
