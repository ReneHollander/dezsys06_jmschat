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

		this.getChat().setMessageHandler(new Handler<ChatMessage>() {

			@Override
			public void handle(ChatMessage param) {
				LOGGER.info(param.getNickname() + " [" + param.getSenderIp() + "]: " + param.getContent());
			}
		});

		LOGGER.info("Welcome!");
		LOGGER.info("Enter \"HELP\" for help and \"EXIT\" if you want to leave.");

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
					help();
					break;
				case "EXIT" :
					LOGGER.info("exiting");
					try {
						this.getChat().close();
					} catch (IOException e1) {
					}
					System.exit(0);
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
						LOGGER.info(username + " " + content);
						try {
							this.getChat().getMailbox().sendMessageToQueue(username, content);
						} catch (JMSException | IOException e) {
							LOGGER.error("An Error occured while sending to MessageQueue", e);
						}
					} else {
						LOGGER.info("Please enter a valid username.");
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
						LOGGER.info("Please enter a valid chatroomname");
					}
					break;
				default :
					String content = z;
					while (scanner.hasNext()) {
						content += " " + scanner.next();
					}
					send(content);
					LOGGER.info(content);
			}
			scanner.close();
		}
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
			this.getChat().getMailbox().sendMessageToQueue(recvNickname, content);
		} catch (JMSException | IOException e) {
			LOGGER.error("An Error occured while trying to send mails", e);
		}
	}

	public void mailbox() {
		try {
			for (ChatMessage m : this.getChat().getMailbox().getMessageQueue()) {
				LOGGER.info("Sender: " + m.getNickname() + " (" + m.getSenderIp() + ")");
				LOGGER.info("Message:\n" + m.getContent());
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

	public static void main(String[] args) {
		try {
			if (args.length >= 2) {
				Main cli = new Main(new JMSChat(new URI(args[0]), args[1]));
				cli.getChat().start();
				if (args.length != 3) {
					LOGGER.info(args[2]);
					cli.getChat().joinChatroom(args[2]);
				} else {
					LOGGER.info("You still have to join a chatroom");
				}
				cli.startCliClient();
			} else {
				// TODO not enough arguments, display help
				LOGGER.info("");
			}
		} catch (URISyntaxException e) {
			LOGGER.info("Wrong broker-URI it should look like the following: tcp://ip:port");
		} catch (IOException | JMSException e) {
			LOGGER.error("An Error occured while creating a new Main", e);
		}
	}

	private static void help() {
		LOGGER.info("EXIT\texit the program");
		LOGGER.info("MAIL <nickname> <message>\tsends a mail to the mailbox of the given user");
		LOGGER.info("MAILBOX\tcall up your mailbox");
		LOGGER.info("CHATROOM <chatroomname>\tEnters the given chatroom");
	}
}
