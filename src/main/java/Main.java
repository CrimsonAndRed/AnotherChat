import model.ChannelsPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.ConsoleWriter;

import java.io.IOException;

public class Main {
	private static final Logger logger = LogManager.getLogger();
	//TODO human-usable login interface
	//TODO json -> preferences or .properties
	//TODO Once system didnt respond. Reproduce and make something like retry connection
	//TODO twitch.tv/tags to add capitalization of twitch nicknames
	//TODO exception management
	/*
		To run this:
		1. Rename "settingsStub.json" to "settings.json"
		2. Add your info
	 */
	public static void main(String[] args)  {
		logger.info("Starting app!");

		Thread inp = new Thread(new ConsoleWriter());
		inp.start();

		try {
			inp.join();
		} catch (InterruptedException e) {
			logger.error(e);
		}
		logger.info("Shutting down");
		try {
			ChannelsPool.clear();
			logger.info("All channels terminated");
		} catch (IOException e) {
			logger.error(e);
		}
	}
}
