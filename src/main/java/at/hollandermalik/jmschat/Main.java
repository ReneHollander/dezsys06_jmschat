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
import at.hollandermalik.jmschat.message.ChatMessage;
import at.hollandermalik.jmschat.util.Util;

/**
 * The CLI for the chat, provides all the commands and starts the chatclient
 * 
 * @author Patrick Malik
 * @version 141123
 */
public class Main {

	private static final Logger LOGGER = LogManager.getLogger(Main.class);

	private JMSChat chat;

	/**
	 * Constructs Main and sets the chat
	 * 
	 * @param chat
	 *            the given chat
	 */
	public Main(JMSChat chat) {
		setChat(chat);

	}

	/**
	 * Starts the CLI-Client, checks for the input and sets the required
	 * attributes.
	 */
	public void startCliClient() {

		this.getChat().setMessageHandler(message -> {
			System.out.println(Util.stringifyMessage(message));
		});

		LOGGER.info("Welcome!");
		LOGGER.info("Enter \"HELP\" for help and \"EXIT\" if you want to leave.");

		BufferedReader sysin = new BufferedReader(new InputStreamReader(System.in));

		String line = null;
		try {
			while ((line = sysin.readLine()) != null) {

				Scanner scanner = new Scanner(line);
				String begin = scanner.next();

				switch (begin) {
				case "HELP":
					help();
					break;
				case "EXIT":
					LOGGER.info("exiting");
					try {
						this.getChat().close();
					} catch (IOException e1) {
					}
					System.exit(0);
				case "MAILBOX":
					try {
						for (ChatMessage m : this.getChat().getMailbox().getMessageQueue()) {
							System.out.println(Util.stringifyMessage(m));
						}
					} catch (JMSException | ClassNotFoundException | IOException e) {
						LOGGER.error("An Error occured while requesting for mails", e);
					}
					break;
				case "MAIL":
					if (scanner.hasNext()) {
						String username = scanner.next();
						String content = "";
						while (scanner.hasNext()) {
							if (content.length() == 0) {
								content = scanner.next();
							} else {
								content += " " + scanner.next();
							}
						}
						try {
							this.getChat().getMailbox().sendMessageToQueue(username, content);
							LOGGER.info("Successfully sent message to user: " + username);
						} catch (JMSException | IOException e) {
							LOGGER.error("An Error occured while sending to MessageQueue", e);
						}
					} else {
						LOGGER.info("Please enter a valid username.");
					}
					break;
				case "CHATROOM":
					if (scanner.hasNext()) {
						try {
							getChat().joinChatroom(scanner.next());
							LOGGER.info("You entered chatroom: " + getChat().getCurrentChatRoom().getTopicName());
						} catch (IOException | JMSException e) {
							LOGGER.error("An Error occured while entering chatroom", e);
						}
					} else {
						LOGGER.info("Please enter a valid chatroomname");
					}
					break;
				default:
					if (getChat().getCurrentChatRoom() != null) {
						String content = begin;
						while (scanner.hasNext()) {
							content += " " + scanner.next();
						}
						try {
							getChat().getCurrentChatRoom().sendMessage(content);
						} catch (JMSException | IOException e) {
							LOGGER.error("An Error occured while trying to send messages", e);
						}
						// LOGGER.info(content);
					} else {
						LOGGER.info("You have to join a chatroom use: CHATROOM <chatroomname>");
					}
				}
				scanner.close();
			}
		} catch (IOException e) {
			LOGGER.error("Error reading from sysin", e);
		}
	}

	/**
	 * Gets the parent JMSChat associated with this chatroom
	 * 
	 * @return Parent JMSChat
	 */
	public JMSChat getChat() {
		return chat;
	}

	/**
	 * Sets the parent JMSChat associated with this chatroom
	 * 
	 * @param chat
	 *            parent JMSChat
	 */
	public void setChat(JMSChat chat) {
		this.chat = chat;
	}

	/**
	 * Receives the arguments, starts the CLI and handles the exceptions.
	 * 
	 * @param args
	 *            CLI Arguments
	 */
	public static void main(String[] args) {
		try {
			if (args.length >= 2) {
				Main cli = new Main(new JMSChat(new URI(args[0]), args[1]));
				cli.getChat().start();
				if (args.length == 3) {
					LOGGER.info(args[2]);
					cli.getChat().joinChatroom(args[2]);
				} else {
					LOGGER.info("You still have to join a chatroom");
				}
				cli.startCliClient();
			} else {
				LOGGER.info("Not enough arguments");
				LOGGER.info("Usage: java -jar dezsys06_jmschat.jar <ip_of_the_message-broker> <nickname> [chatroom_name]");
				LOGGER.info("For example: java -jar 0.0.0.0:61616 testuser testchatroom");
			}
		} catch (URISyntaxException e) {
			LOGGER.info("Wrong broker-URI it should look like the following: tcp://ip:port");
		} catch (IOException | JMSException e) {
			LOGGER.error("An Error occured while creating a new Main", e);
		}
	}

	/**
	 * displays the help-menu
	 */
	private static void help() {
		LOGGER.info("EXIT\t\t\t\texit the program");
		LOGGER.info("MAIL <nickname> <message>\tsends a mail to the mailbox of the given user");
		LOGGER.info("MAILBOX\t\t\tcall up your mailbox");
		LOGGER.info("CHATROOM <chatroomname>\tEnters the given chatroom");
	}
}
