package model.pools;

import model.clients.Client;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChannelsPool {
	private static final Logger logger = LogManager.getLogger();
	private static final Map<String, Client> channelsPool = new HashMap<>();

	private ChannelsPool(){
	}

	public static synchronized Client remove(String name) {
		return channelsPool.remove(name);
	}

	public static synchronized void put(String name, Client thread) {

		if (channelsPool.putIfAbsent(name, thread) != null) {
			throw new IllegalArgumentException("Channel already exist in base!");
		}
	}

	public static Client get(String name) {
		return channelsPool.get(name);
	}

	public static void clear() throws IOException{
		logger.info("Totally there are " + channelsPool.size() + " channels up. Trying to close them");
		for (Map.Entry<String, Client> entry : channelsPool.entrySet()) {
			entry.getValue().terminate();
		}
		channelsPool.clear();
	}
}
