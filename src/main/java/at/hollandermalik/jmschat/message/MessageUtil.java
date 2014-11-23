package at.hollandermalik.jmschat.message;

import java.io.IOException;
import java.net.InetAddress;
import java.time.LocalDateTime;
import java.util.HashMap;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import at.hollandermalik.jmschat.util.Util;

/**
 * Util for the ChatMessage
 * 
 * @author Rene Hollander
 */
public class MessageUtil {

	private static final String SENDERIP_PROPERTY_KEY = "senderip";
	private static final String TIMESTAMP_PROPERTY_KEY = "timestamp";
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
	 *             Throws JMS Exception on JMS Error
	 * @throws IOException
	 *             If there was an error serializing the object
	 */
	public static Message serializeMessage(Session session, ChatMessage chatMessage) throws JMSException, IOException {
		BytesMessage message = session.createBytesMessage();

		HashMap<String, Object> map = new HashMap<String, Object>();

		map.put(SENDERIP_PROPERTY_KEY, chatMessage.getSenderIp());
		map.put(TIMESTAMP_PROPERTY_KEY, chatMessage.getTimestamp());
		map.put(NICKNAME_PROPERTY_KEY, chatMessage.getNickname());
		map.put(CONTENT_PROPERTY_KEY, chatMessage.getContent());

		message.writeBytes(Util.serializeToByteArray(map));

		return message;
	}

	/**
	 * Deserialize a JMSMessage into a ChatMessage
	 * 
	 * @param inMsg
	 *            JMS Message to desrialize
	 * @return ChatMessage from the JMS Message
	 * @throws JMSException
	 *             Throws JMS Exception on JMS Error
	 * @throws IOException
	 *             If there was an error deserializing the byte data
	 * @throws ClassNotFoundException
	 *             If there was an error deserializing byte data
	 */
	public static ChatMessage deserializeMessage(Message inMsg) throws JMSException, ClassNotFoundException, IOException {
		BytesMessage message = (BytesMessage) inMsg;

		byte[] data = new byte[(int) message.getBodyLength()];
		message.readBytes(data);
		@SuppressWarnings("unchecked")
		HashMap<String, Object> map = (HashMap<String, Object>) Util.deserilizeFromByteArray(data);

		InetAddress senderIp = (InetAddress) map.get(SENDERIP_PROPERTY_KEY);
		LocalDateTime timestamp = (LocalDateTime) map.get(TIMESTAMP_PROPERTY_KEY);
		String nickname = (String) map.get(NICKNAME_PROPERTY_KEY);
		String content = (String) map.get(CONTENT_PROPERTY_KEY);

		return new ChatMessage(senderIp, timestamp, nickname, content);
	}
}
