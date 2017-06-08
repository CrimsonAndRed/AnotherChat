package model.clients;

import model.exceptions.InternetException;
import model.messages.TwitchUserMessage;
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
	protected void simpleParseInput(String line) throws InternetException {
		ResponseType type = null;
		type = checker.checkType(line);

		if (type == ResponseType.PING) {
			//TODO This is synchronous!!
			logger.debug("Received PONG twitch message");
			sendString(bw, "PONG :tmi.twitch.tv");
			logger.debug("Answered PING to twitch server");
		} else {
			writer.queueMessage(new TwitchUserMessage(type, line));
		}
	}
}
