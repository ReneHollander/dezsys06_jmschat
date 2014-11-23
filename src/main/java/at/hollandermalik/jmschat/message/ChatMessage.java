package at.hollandermalik.jmschat.message;

import java.net.InetAddress;

/**
 * A Message to send over the network. Contains IP, InetAddress of the sender
 * and the actual message.
 * 
 * @author Rene Hollander
 */
public class ChatMessage {

	private InetAddress senderIp;
	private String nickname;
	private String content;

	/**
	 * Construct a new ChatMessage with the given Parameters
	 * 
	 * @param senderIp
	 *            IP of the sender of the message
	 * @param nickname
	 *            Nickname of the sender of the message
	 * @param content
	 *            String content of the message
	 */
	public ChatMessage(InetAddress senderIp, String nickname, String content) {
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
		return "ChatMessage [nickname=" + nickname + ", content=" + content + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((nickname == null) ? 0 : nickname.hashCode());
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
		return true;
	}

}
