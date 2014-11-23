package at.hollandermalik.jmschat.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.jms.JMSException;

import org.junit.Before;
import org.junit.Test;

import at.hollandermalik.jmschat.chat.JMSChat;
import at.hollandermalik.jmschat.chat.Mailbox;

/**
 * The testclass for JMSChat
 * 
 * @author Patrick Malik
 * @version 141123
 */
public class TestJMSChat {

	JMSChat chat;
	Mailbox mail;

	/**
	 * Creates a standard JMSChat to use for the testcases;
	 */
	@Before
	public void createJMSChat() {
		try {
			chat = new JMSChat(new URI("tcp://localhost:61616"), "nickname");

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Testing if the constructor sets the right brokerURI.
	 */
	@Test
	public void JMSChatConstructorBrokerURI() {
		try {
			assertEquals(new URI("tcp://localhost:61616"), chat.getBrokerUri());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Testing if the constructor sets the right nickname.
	 */
	@Test
	public void JMSChatConstructorNickname() {
		assertEquals("nickname", chat.getNickname());
	}

	/**
	 * Tests if the right IP is set at while starting.
	 */
	@Test
	public void JMSChatStartMyIP() {
		try {
			chat.start();
			URL whatismyip = new URL("http://checkip.amazonaws.com");
			BufferedReader in = new BufferedReader(new InputStreamReader(whatismyip.openStream()));
			InetAddress ip = InetAddress.getByName(in.readLine());
			assertEquals(chat.getMyIp(), ip);
		} catch (IOException | JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Tests if the right chatroom is set, when there's no chatroom set yet.
	 */
	@Test
	public void JMSChatJoinChatroomClosed() {
		try {
			chat.start();
			chat.joinChatroom("test1");
			assertEquals("test1", chat.getCurrentChatRoom().getTopicName());
		} catch (IOException | JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Testing if the chatroom changes when there is already one set.
	 */
	@Test
	public void JMSChatJoinChatroomOpen() {
		try {
			chat.start();
			chat.joinChatroom("test1");
			chat.joinChatroom("test2");
			assertEquals("test2", chat.getCurrentChatRoom().getTopicName());
		} catch (IOException | JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Testing if the mailbox is set correctly
	 */
	@Test
	public void JMSChatGetMailbox() {
		try {
			chat.start();
			mail = chat.getMailbox();
			assertEquals(mail, chat.getMailbox());
		} catch (JMSException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Testing if the JMSChat closes correctly
	 */
	@Test
	public void JMSChatClose() {
		try {
			chat.start();
			chat.joinChatroom("room");
			chat.getMailbox();
			chat.close();
		} catch (IOException | JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	/**
	 * Testing if the JMSChat closes correctly, also if there's no mailbox and chatroom set.
	 */
	@Test
	public void JMSChatCloseWithout() {

		try {
			chat.start();
			chat.close();
		} catch (IOException | JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
