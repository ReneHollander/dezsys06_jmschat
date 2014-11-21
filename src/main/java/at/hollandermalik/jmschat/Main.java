package at.hollandermalik.jmschat;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.jms.JMSException;

public class Main {

	public static void main(String[] args) throws JMSException, URISyntaxException, IOException {
		JMSChat chat = new JMSChat(new URI("tcp://localhost:61616/"), "Rene8888");
		chat.start();
		chat.joinChatroom("test");
		chat.getCurrentChatRoom().setMessageHandler(message -> {
			System.out.println(message);
			try {
				chat.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		chat.getCurrentChatRoom().sendMessage("test");
	}
}
