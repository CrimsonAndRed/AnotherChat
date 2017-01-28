package web;

import model.ConsoleClient;
import util.ConsoleWriter;

import java.io.*;
import java.net.Socket;
import java.util.InputMismatchException;

import static web.StringSender.sendString;

public class IrcClient {

	private final String address;
	private final int port;
	private final String name;
	private final String oauth;

	public IrcClient(String address, int port, String name, String oauth) {
		this.address = address;
		this.port = port;
		this.name = name;
		this.oauth = oauth;
	}

	public void tryConnect() {

		try (Socket socket = new Socket(address, port);
		     BufferedWriter bwriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		     BufferedReader breader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

			sendString(bwriter, "PASS " + oauth);
			sendString(bwriter, "NICK " + name);


			//Yes it is threadsafe
			ConsoleClient thread = new ConsoleClient(breader, bwriter);
			thread.start();

			Thread inp = new Thread(new ConsoleWriter(bwriter));
			inp.start();

			try {
				inp.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			//TODO quitting without PART channels might produce NPE under high workload
			//bwriter.close();
			//breader.close();
			socket.shutdownInput();
			socket.shutdownOutput();

		} catch (IOException e) {
			//TODO Dafuq
			throw new InputMismatchException();
		}
	}
}
