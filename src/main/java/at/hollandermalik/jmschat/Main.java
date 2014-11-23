package at.hollandermalik.jmschat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

import javax.jms.JMSException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.hollandermalik.jmschat.chat.JMSChat;
import at.hollandermalik.jmschat.chat.Mailbox;
import at.hollandermalik.jmschat.message.ChatMessage;
import at.hollandermalik.jmschat.util.Handler;

public class Main {

	// TODO I never set the message handler so this is probably the problem, but
	// i have no idea what I should set as Messagehandler

	private static final Logger LOGGER = LogManager.getLogger(Main.class);

	private JMSChat chat;
	private Mailbox mailbox;

	public Main(JMSChat chat) {
		setChat(chat);

	}

	public void startCliClient() {

		Scanner scanner = null;
		// BufferedReader stdIn = new BufferedReader(new
		// InputStreamReader(System.in));

		LOGGER.info("Welcome!");

		System.out.println("Welcome!");
		System.out.println("Enter \"HELP\" for help and \"EXIT\" if you want to leave.");

		BufferedReader a;
		String z;

		while (true) {

			a = new BufferedReader(new InputStreamReader(System.in));
			try {
				scanner = new Scanner(a.readLine());
			} catch (IOException e1) {
				LOGGER.error("An Error occured while initialising of the scanner", e1);
			}
			switch (z = scanner.next()) {
				case "HELP" :
					this.help();
					break;
				case "EXIT" :
					System.out.println("exiting");
					return;
				case "MAILBOX" :
					this.mailbox();
					break;
				case "MAIL" :
					if (scanner.hasNext()) {
						String username = scanner.next();
						String content = "";
						while (scanner.hasNext()) {
							content += " " + scanner.next();
						}
						System.out.println(username + " " + content);
						try {
							getMailbox().sendMessageToQueue(username, content);
						} catch (JMSException | IOException e) {
							LOGGER.error("An Error occured while sending to MessageQueue", e);
						}
					} else {
						System.out.println("Please enter a valid username.");
					}
					break;
				case "CHATROOM" :
					if (scanner.hasNext()) {
						try {
							getChat().joinChatroom(scanner.next());
						} catch (IOException | JMSException e) {
							LOGGER.error("An Error occured while entering chatroom", e);
						}
					} else {
						System.out.println("Please enter a valid chatroomname");
					}
					break;
				default :
					String content = z;
					while (scanner.hasNext()) {
						content += " " + scanner.next();
					}
					send(content);
					System.out.println(content);
			}
			scanner.close();
		}
	}
	public void help() {

		System.out.println("EXIT\texit the program");
		System.out.println("MAIL <nickname> <message>\tsends a mail to the mailbox of the given user");
		System.out.println("MAILBOX\tcall up your mailbox");
		System.out.println("CHATROOM <chatroomname>\tEnters the given chatroom");

	}

	public void send(String msg) {
		try {
			getChat().getCurrentChatRoom().sendMessage(msg);
		} catch (JMSException | IOException e) {
			LOGGER.error("An Error occured while trying to send messages", e);
		}
	}

	public void mail(String recvNickname, String content) {
		try {
			getMailbox().sendMessageToQueue(recvNickname, content);
		} catch (JMSException | IOException e) {
			LOGGER.error("An Error occured while trying to send mails", e);
		}
	}

	public void mailbox() {
		try {
			for (ChatMessage m : getMailbox().getMessageQueue()) {
				System.out.println("Sender: " + m.getNickname() + " (" + m.getSenderIp() + ")");
				System.out.println("Message:\n" + m.getContent());
			}
		} catch (JMSException | ClassNotFoundException | IOException e) {
			LOGGER.error("An Error occured while requesting for mails", e);
		}
	}

	public JMSChat getChat() {
		return chat;
	}

	public void setChat(JMSChat chat) {
		this.chat = chat;
	}

	public Mailbox getMailbox() {
		return mailbox;
	}

	public void setMailbox(Mailbox mailbox) {
		this.mailbox = mailbox;
	}

	public static void main(String[] args) {
		Main cli = null;
		try {
			cli = new Main(new JMSChat(new URI(args[0]), args[1]));
			cli.getChat().start();
			try {
				if (args[2] != null) {
					System.out.println(args[2]);
					cli.getChat().joinChatroom(args[2]);
				}
			} catch (ArrayIndexOutOfBoundsException e1) {
				System.out.println("You still have to join a chatroom");
			}
		} catch (URISyntaxException e) {
			System.out.println("Wrong broker-URI it should look like the following: tcp://ip:port");
		} catch (IOException | JMSException e) {
			LOGGER.error("An Error occured while creating a new Main", e);
		}
		cli.startCliClient();
	}

}
