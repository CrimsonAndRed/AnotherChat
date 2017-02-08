package model.clients;

import model.exceptions.InternetException;
import model.messages.TwitchUserMessage;
import model.messages.UserMessage;
import model.responses.Checker;
import model.responses.ResponseType;
import model.responses.TwitchChecker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.net.Socket;

import static web.StringSender.sendString;

public class TwitchConsoleClient extends ConsoleClient {
	private static final Logger logger = LogManager.getLogger();
	private final Checker checker = new TwitchChecker();

	public TwitchConsoleClient(Socket socket, BufferedWriter bw, BufferedReader br) {
		super(socket, bw, br);
	}

	@Override
	protected Logger getLogger() {
		return logger;
	}

	@Override
	protected void parseInput(String line) throws InternetException {
		ResponseType type = null;
		type = checker.checkType(line);
		switch (type) {
			case MSG:
				UserMessage message = new TwitchUserMessage(line);
				System.out.println("Sender " + message.getSender()
						+ " in channel " + message.getChannel()
						+ " said:" + message.getMessage());
				break;
			case PING:
				sendString(bw, "PONG :tmi.twitch.tv");
				break;
			case SYSMSG:
				System.out.println("System said:\n" + line);
				break;
			case UNKNOWN:
				System.out.println("Unknown message:\n" + line);
				break;
			default:
				break;
		}
	}
}
