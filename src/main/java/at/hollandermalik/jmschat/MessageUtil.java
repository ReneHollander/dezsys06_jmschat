package at.hollandermalik.jmschat;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

public class MessageUtil {

	private static final String NICKNAME_PROPERTY_KEY = "nickname";
	private static final String CONTENT_PROPERTY_KEY = "content";

	public static Message serializeMessage(Session session, ChatMessage chatMessage) throws JMSException {
		Message message = session.createBytesMessage();

		message.setStringProperty(NICKNAME_PROPERTY_KEY, chatMessage.getNickname());
		message.setStringProperty(CONTENT_PROPERTY_KEY, chatMessage.getContent());

		return message;
	}

	public static ChatMessage deserializeMessage(Message message) throws JMSException {
		String nickname = message.getStringProperty(NICKNAME_PROPERTY_KEY);
		String content = message.getStringProperty(CONTENT_PROPERTY_KEY);

		return new ChatMessage(nickname, content);
	}

}
