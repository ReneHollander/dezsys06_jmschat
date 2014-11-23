package at.hollandermalik.jmschat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.jms.JMSException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import at.hollandermalik.jmschat.chat.JMSChat;
import at.hollandermalik.jmschat.chat.Mailbox;
import at.hollandermalik.jmschat.message.ChatMessage;

public class Main {

	private static final Logger LOGGER = LogManager.getLogger(Main.class);

	private JMSChat chat;
	private Mailbox mailbox;

	public Main(JMSChat chat) {
		setChat(chat);
	}

	public void startCliClient() {
		boolean tryAgain = true;

		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		LOGGER.info("Welcome!");

		System.out.println("Welcome!");
		System.out.println("Enter \"HELP\" for help and \"EXIT\" if you want to leave.");

		// while (tryAgain) {

		// }

		while (true) {

			// TODO please use the scanner...

			String input = null;
			try {
				input = stdIn.readLine();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (input.equals("HELP")) {
				System.out.println("Help");
				this.help();
			} else if (input.equals("EXIT")) {
				System.out.println("exit");
				return;
			} else if (input.equals("MAILBOX")) {
				System.out.println("mailbox");
				this.mailbox();
			} else {
				String s1, s2;
				try {
					s1 = input.substring(0, input.indexOf(" "));
					s2 = input.substring(input.indexOf(" ") + 1);
				} catch (StringIndexOutOfBoundsException e1) {
					s1 = "";
					s2 = "";
				}
				if (s1.equals("MAIL")) {
					System.out.println("mail");
					try {
						s1 = s2.substring(0, s2.indexOf(" "));
						s2 = s2.substring(s2.indexOf(" ") + 1);
					} catch (StringIndexOutOfBoundsException e1) {
						s1 = "";
						s2 = "";
					}
					// TODO WE ONLY USE THE USERNAME TO SPECIFY A MAILBOX!!!
					this.mail(s1, s2);
				} else if (!s1.equals("")) {
					System.out.println("send");
					this.send(input);
				}
			}

		}
	}

	public void help() {

		System.out.println("EXIT\texit the program");
		System.out.println("MAIL <userIP> <message>\tsends a mail to the mailbox of the given user");
		System.out.println("MAILBOX\tcall up your mailbox");

	}

	public void send(String msg) {
		try {
			getChat().getCurrentChatRoom().sendMessage(msg);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void mail(String recvNickname, String content) {
		try {
			getMailbox().sendMessageToQueue(recvNickname, content);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void mailbox() {
		try {
			for (ChatMessage m : getMailbox().getMessageQueue()) {
				System.out.println("Sender: " + m.getNickname() + " (" + m.getSenderIp() + ")");
				System.out.println("Message:\n" + m.getContent());
			}
		} catch (JMSException e) {
			e.printStackTrace();
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
		Main cli = new Main(new JMSChat(null, null));
		cli.startCliClient();
	}

}
