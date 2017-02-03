package model;

import model.exceptions.InternetException;
import model.messages.TwitchUserMessage;
import model.messages.UserMessage;
import model.responses.Checker;
import model.responses.ResponseType;
import model.responses.TwitchChecker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

import static web.StringSender.sendString;

public class ConsoleClient extends Thread {

	private static final Logger logger = LogManager.getLogger();

	private BufferedReader br;
	private BufferedWriter bw;
	private Socket socket;
	//TODO how about DI?
	Checker checker = new TwitchChecker();

	public ConsoleClient(Socket socket, BufferedWriter bw, BufferedReader br) {
		this.socket = socket;
		this.br = br;
		this.bw = bw;
	}

	@Override
	public void run() {
		try {
			ResponseType type = null;
			String line = null;
			while ((line = br.readLine()) != null) {
				// Logic goes here
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
		} catch (InternetException | IOException e) {
			logger.error("Internet seems to be down. Trying to reconnect..");
			try {
				Thread.sleep(3000L);
			} catch (InterruptedException e1) {
				logger.error(e1);
			}

		}
	}

	public void terminate() throws IOException {
		socket.shutdownInput();
		socket.shutdownOutput();
	}
}
