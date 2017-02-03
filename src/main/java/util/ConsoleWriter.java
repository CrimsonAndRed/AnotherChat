package util;

import model.ChannelsPool;
import model.ConsoleClient;
import model.exceptions.FileSystemException;
import model.exceptions.InternetException;
import model.starters.TwitchStarter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Scanner;

public class ConsoleWriter implements Runnable {
	private static final Logger logger = LogManager.getLogger();

	@Override
	public void run() {
		Scanner sc = new Scanner(System.in);
		String string = null;
		while (true) {
			string = sc.nextLine();
			if ("quit".equals(string)) {
				break;
			}
			checkInput(string);
			//sendString(br, string);
		}
	}

	private void checkInput(String string) {
		if (string.startsWith("join ")) {
			TwitchStarter starter = new TwitchStarter();
			ConsoleClient c1 = null;
			String name = string.substring(5);
			try {
				c1 = starter.prepareConnection(name);
				ChannelsPool.put(name, c1);
				c1.start();
			} catch (InternetException e) {
				e.printStackTrace();
			} catch (FileSystemException e) {
				logger.error(e);
			}

		} else if (string.startsWith("part ")) {
			String name = string.substring(5);
			ConsoleClient consoleClient = ChannelsPool.get(name);
			if (consoleClient == null) {
				logger.warn("Channel " + name + " does not exist");
			} else {
				try {
					consoleClient.terminate();
					logger.info("Successfully terminated connection to " + name);
				} catch (IOException e) {
					logger.error(e);
				}
			}
		} else {
			logger.warn("Wrong command: " + string);
		}

	}
}
