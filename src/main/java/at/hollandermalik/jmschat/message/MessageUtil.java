package at.hollandermalik.jmschat.message;

import java.net.InetAddress;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * Util for the ChatMessage
 * 
 * @author Rene Hollander
 */
public class MessageUtil {

	private static final String SENDERIP_PROPERTY_KEY = "senderip";
	private static final String NICKNAME_PROPERTY_KEY = "nickname";
	private static final String CONTENT_PROPERTY_KEY = "content";

	/**
	 * Serialize a ChatMessage into a JMS Message
	 * 
	 * @param session
	 *            Session to generate the Message from
	 * @param chatMessage
	 *            Message to serialize
	 * @return JMS Message
	 * @throws JMSException
	 */
	public static Message serializeMessage(Session session, ChatMessage chatMessage) throws JMSException {
		Message message = session.createBytesMessage();

		message.setObjectProperty(SENDERIP_PROPERTY_KEY, chatMessage.getSenderIp());
		message.setStringProperty(NICKNAME_PROPERTY_KEY, chatMessage.getNickname());
		message.setStringProperty(CONTENT_PROPERTY_KEY, chatMessage.getContent());

		return message;
	}

	/**
	 * Deserialize a JMSMessage into a ChatMessage
	 * 
	 * @param message
	 *            JMS Message to desrialize
	 * @return ChatMessage from the JMS Message
	 * @throws JMSException
	 */
	public static ChatMessage deserializeMessage(Message message) throws JMSException {
		InetAddress senderIp = (InetAddress) message.getObjectProperty(SENDERIP_PROPERTY_KEY);
		String nickname = message.getStringProperty(NICKNAME_PROPERTY_KEY);
		String content = message.getStringProperty(CONTENT_PROPERTY_KEY);

		return new ChatMessage(senderIp, nickname, content);
	}

}
