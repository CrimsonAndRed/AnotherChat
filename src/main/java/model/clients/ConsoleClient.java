package model.clients;

import model.core.console.Writer;
import model.exceptions.InternetException;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

public abstract class ConsoleClient implements Client {

	private final Logger logger = getLogger();

	protected Writer writer;
	protected BufferedReader br;
	protected BufferedWriter bw;
	protected Socket socket;

	// Because bw and br were opened before
	public ConsoleClient(Socket socket, BufferedWriter bw, BufferedReader br) {
		this.socket = socket;
		this.br = br;
		this.bw = bw;
	}

	@Override
	public void registerWriter(Writer writer) {
		this.writer = writer;
	}

	@Override
	public Writer getWriter() {
		return writer;
	}

	@Override
	public void run() {
		try {
			String line = null;
			while ((line = br.readLine()) != null) {
				// Logic goes here
				simpleParseInput(line);
			}
		} catch (InternetException | IOException e) {
			getLogger().error("Internet seems to be down. Trying to reconnect..");
			try {
				//TODO real reconnect
				Thread.sleep(3000L);
			} catch (InterruptedException e1) {
				getLogger().error(e1);
			}

		}
	}

	@Override
	public void terminate() throws IOException {
		socket.shutdownInput();
		socket.shutdownOutput();
	}

	protected abstract Logger getLogger();
	/**
	 * This method is synchronous with reading from irc.
	 * So it should be as fast as possible.
	 * Detailed message parsing is Writer responsibility.
	 */
	protected abstract void simpleParseInput(String line) throws InternetException;
}
