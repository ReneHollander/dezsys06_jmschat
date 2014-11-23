package at.hollandermalik.jmschat.test;

import static org.junit.Assert.assertEquals;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

import org.junit.Test;

import at.hollandermalik.jmschat.message.ChatMessage;

public class TestChatMessage {

	@Test
	public void ChatMessageToString() {
		LocalDateTime t = LocalDateTime.now();
		ChatMessage message;
		try {

			message = new ChatMessage(InetAddress.getByName("0.0.0.0"), t, "nickname", "content");
			assertEquals("ChatMessage [senderIp=/0.0.0.0, timestamp=" + t + ", nickname=nickname, content=content]", message.toString());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
