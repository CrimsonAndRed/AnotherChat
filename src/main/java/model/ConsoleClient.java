package model;

import model.messages.TwitchUserMessage;
import model.messages.UserMessage;
import model.responses.Checker;
import model.responses.ResponseType;
import model.responses.TwitchChecker;

import java.io.*;
import java.net.Socket;

import static web.StringSender.sendString;

public class ConsoleClient extends Thread {

	private BufferedReader br;
	private BufferedWriter bw;
	private Socket socket;

	public ConsoleClient(Socket socket, BufferedWriter bw, BufferedReader br) {
		this.socket = socket;
		this.br = br;
		this.bw = bw;
	}

	@Override
	public void run() {
		try {
			//TODO how about DI?
			Checker checker = new TwitchChecker();
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
								+ " said: " + message.getMessage());
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void terminate() throws IOException {
		socket.shutdownInput();
		socket.shutdownOutput();
	}
}
