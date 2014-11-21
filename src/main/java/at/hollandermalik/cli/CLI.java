package at.hollandermalik.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream.GetField;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.jms.JMSException;

import at.hollandermalik.jmschat.ChatMessage;
import at.hollandermalik.jmschat.JMSChat;
import at.hollandermalik.jmschat.Mailbox;

public class CLI {

	JMSChat chat;
	Mailbox mailbox;

	public CLI(JMSChat chat) {
		setChat(chat);
	}

	public static void main(String[] args) {

		// TODO just not sure if from the cmd or as sysin
		CLI cli = new CLI(new JMSChat(null, null));

		InetAddress recv;

		boolean tryAgain = true;

		BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

		System.out.println("Welcome!");
		System.out.println("Enter \"HELP\" for help and \"EXIT\" if you want to leave.");

		// while (tryAgain) {

		// }

		while (true) {

			String input = null;
			try {
				input = stdIn.readLine();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			if (input.equals("HELP")) {
				System.out.println("Help");
				cli.help();
			} else if (input.equals("EXIT")) {
				System.out.println("exit");
				return;
			} else if (input.equals("MAILBOX")) {
				System.out.println("mailbox");
				cli.mailbox();
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
					try {
						recv = InetAddress.getByName(s1);
						// TODO replace null with the IPAddress
						cli.mail(null, s2);
					} catch (UnknownHostException e1) {
					}

				} else if (!s1.equals("")) {
					System.out.println("send");
					cli.send(input);
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
			//TODO add the IPAddress
			getChat().getCurrentChatRoom().sendMessage(getChat().getNickname() + ": " + msg);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void mail(String recvNickname, String content) {
		try {
			content = getChat().getNickname() + ": " + content;
			getMailbox().sendMessageToQueue(recvNickname, content);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	public void mailbox() {
		try {
			for (ChatMessage m : getMailbox().getMessageQueue())
				System.out.println(m.getContent());

		} catch (JMSException e) {
			// TODO Auto-generated catch block
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

}
