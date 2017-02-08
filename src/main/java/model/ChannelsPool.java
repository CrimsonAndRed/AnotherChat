package model;

import model.clients.ConsoleClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChannelsPool {
	private static final Logger logger = LogManager.getLogger();
	private static final Map<String, ConsoleClient> channelsPool = new HashMap<>();

	private ChannelsPool(){

	}

	public static synchronized ConsoleClient remove(String name) {
		return channelsPool.remove(name);
	}

	public static synchronized void put(String name, ConsoleClient thread) {

		if (channelsPool.putIfAbsent(name, thread) != null) {
			throw new IllegalArgumentException("Channel already exist in base!");
		}
	}

	public static ConsoleClient get(String name) {
		return channelsPool.get(name);
	}

	public static void clear() throws IOException{
		for (Map.Entry<String, ConsoleClient> entry : channelsPool.entrySet()) {
			entry.getValue().terminate();
		}
		channelsPool.clear();
	}
}
