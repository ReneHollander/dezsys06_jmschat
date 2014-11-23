package at.hollandermalik.jmschat.message;

import java.io.IOException;

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
		message.writeBytes(Util.serializeToByteArray(chatMessage));
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
		return (ChatMessage) Util.deserilizeFromByteArray(data);
	}
}
