package model;

import java.util.HashMap;
import java.util.Map;

public class ChannelsPool {
	private static final Map<String, ConsoleClient> channelsPool = new HashMap<>();

	public static void put(String name, ConsoleClient thread) {
		channelsPool.put(name, thread);
	}

	public static ConsoleClient get(String name) {
		return channelsPool.get(name);
	}

	private ChannelsPool(){

	}
}
