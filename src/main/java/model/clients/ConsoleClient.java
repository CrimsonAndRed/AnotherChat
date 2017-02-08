package model.clients;

import model.exceptions.InternetException;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

public abstract class ConsoleClient extends Thread {

	private final Logger logger = getLogger();

	protected BufferedReader br;
	protected BufferedWriter bw;
	protected Socket socket;

	public ConsoleClient(Socket socket, BufferedWriter bw, BufferedReader br) {
		this.socket = socket;
		this.br = br;
		this.bw = bw;
	}

	@Override
	public void run() {
		try {
			String line = null;
			while ((line = br.readLine()) != null) {
				// Logic goes here
				parseInput(line);
			}
		} catch (InternetException | IOException e) {
			logger.error("Internet seems to be down. Trying to reconnect..");
			try {
				//TODO real reconnect
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

	protected abstract Logger getLogger();
	protected abstract void parseInput(String line) throws InternetException;
}
