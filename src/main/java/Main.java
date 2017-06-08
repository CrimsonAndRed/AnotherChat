import model.core.console.ConsoleCore;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
	private static final Logger logger = LogManager.getLogger();
	//TODO human-usable login interface
	//TODO json -> preferences or .properties
	//TODO Once system didnt respond. Reproduce and make something like retry connection
	//TODO twitch.tv/tags to add capitalization of twitch nicknames
	//TODO exception management
	/*
		To run this:
		You cannot run it unless you know client secret
	 */
	public static void main(String[] args)  {
		logger.info("Starting app!");
		Thread core = new Thread(new ConsoleCore());
		core.start();

		try {
			core.join();
		} catch (InterruptedException e) {
			logger.error(e);
		}
	}
}
