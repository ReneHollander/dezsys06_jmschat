package at.hollandermalik.jmschat.test;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.jms.JMSException;

import org.junit.Before;
import org.junit.Test;

import at.hollandermalik.jmschat.Main;
import at.hollandermalik.jmschat.chat.ChatRoom;
import at.hollandermalik.jmschat.chat.JMSChat;
import at.hollandermalik.jmschat.chat.Mailbox;
import at.hollandermalik.jmschat.util.Util;

public class TestMain {

	Main main;
	JMSChat chat;
	ChatRoom room;
	Mailbox mail;

	@Before
	public void prepForTesting() {

		try {
			chat = new JMSChat(new URI("tcp://localhost:61616"), "nickname");
			chat.start();
			room = chat.joinChatroom("room1");
			chat.setMessageHandler(message -> {
				System.out.println(Util.stringifyMessage(message));
			});
			mail = chat.getMailbox();
		} catch (URISyntaxException | JMSException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testMain() {
		Main main = new Main(chat);
		assertEquals(chat.getBrokerUri(), main.getChat().getBrokerUri());
	}

	/*
	 * @Test public void startCliClientEXIT(){ Main main = new Main(chat);
	 * ByteArrayInputStream in = new ByteArrayInputStream("EXIT".getBytes());
	 * System.setIn(in); main.startCliClient();
	 * 
	 * 
	 * }
	 * 
	 * @Test public void startCliHELP(){ Main main = new Main(chat);
	 * ByteArrayInputStream in = new ByteArrayInputStream("HELP".getBytes());
	 * System.setIn(in); main.startCliClient();
	 * 
	 * 
	 * }
	 * 
	 * @Test public void testMainmain() { Main main = new Main(chat);
	 * ByteArrayInputStream in = new ByteArrayInputStream("EXIT".getBytes());
	 * System.setIn(in); Main.main(new String[] { "tcp://localhost:61616",
	 * "nickname", "testroom" }); }
	 */

}
