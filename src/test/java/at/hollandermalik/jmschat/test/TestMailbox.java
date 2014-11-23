package at.hollandermalik.jmschat.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.jms.JMSException;

import org.junit.Before;
import org.junit.Test;

import at.hollandermalik.jmschat.chat.ChatRoom;
import at.hollandermalik.jmschat.chat.JMSChat;
import at.hollandermalik.jmschat.chat.Mailbox;
import at.hollandermalik.jmschat.util.Util;

public class TestMailbox {

	JMSChat chat;
	Mailbox mail;
	ChatRoom room;

	@Before
	public void prepForTesting() {
		try {
			chat = new JMSChat(new URI("tcp://localhost:61616"), "nickname");
			chat.start();
			chat.joinChatroom("room1");
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
	public void MailboxSendingReceiving() {
		try {
			chat.getMailbox().sendMessageToQueue("nickname", "hallo");
			assertEquals("hallo", chat.getMailbox().getMessageQueue().get(0).getContent());
		} catch (JMSException | IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void MailboxClosing() {
		try {
			chat.getMailbox().close();
		} catch (IOException | JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
