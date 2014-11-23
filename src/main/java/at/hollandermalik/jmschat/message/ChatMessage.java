package at.hollandermalik.jmschat.message;

import java.net.InetAddress;
import java.time.LocalDateTime;

/**
 * A Message to send over the network. Contains IP, InetAddress of the sender
 * and the actual message.
 * 
 * @author Rene Hollander
 */
public class ChatMessage {

	private InetAddress senderIp;
	private LocalDateTime timestamp;
	private String nickname;
	private String content;

	/**
	 * Construct a new ChatMessage with the given Parameters
	 * 
	 * @param senderIp
	 *            IP of the sender of the message
	 * @param timestamp
	 *            Timestamp of the sent message
	 * @param nickname
	 *            Nickname of the sender of the message
	 * @param content
	 *            String content of the message
	 */
	public ChatMessage(InetAddress senderIp, LocalDateTime timestamp, String nickname, String content) {
		this.senderIp = senderIp;
		this.timestamp = timestamp;
		this.nickname = nickname;
		this.content = content;
	}

	/**
	 * Gets the IP of the sender of the message
	 * 
	 * @return IP of the sender
	 */
	public InetAddress getSenderIp() {
		return this.senderIp;
	}

	/**
	 * Gets the Timestamp of the message
	 * 
	 * @return Timestamp
	 */
	public LocalDateTime getTimestamp() {
		return this.timestamp;
	}

	/**
	 * Gets the Nickname of the sender of the message
	 * 
	 * @return Nickname of the sender
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * Gets the Content of the message
	 * 
	 * @return Content of the sender
	 */
	public String getContent() {
		return content;
	}

	@Override
	public String toString() {
		return "ChatMessage [senderIp=" + senderIp + ", timestamp=" + timestamp + ", nickname=" + nickname + ", content=" + content + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((nickname == null) ? 0 : nickname.hashCode());
		result = prime * result + ((senderIp == null) ? 0 : senderIp.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChatMessage other = (ChatMessage) obj;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (nickname == null) {
			if (other.nickname != null)
				return false;
		} else if (!nickname.equals(other.nickname))
			return false;
		if (senderIp == null) {
			if (other.senderIp != null)
				return false;
		} else if (!senderIp.equals(other.senderIp))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}

}
