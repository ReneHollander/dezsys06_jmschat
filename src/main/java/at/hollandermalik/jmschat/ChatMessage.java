package at.hollandermalik.jmschat;

public class ChatMessage {

	private String nickname;
	private String content;

	public ChatMessage(String nickname, String content) {
		this.nickname = nickname;
		this.content = content;
	}

	public String getNickname() {
		return nickname;
	}

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
