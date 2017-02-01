package model.emotes.twitch;

import model.emotes.EmotesContainer;
import model.messages.Origins;

import java.util.HashMap;
import java.util.Map;

public class TwitchEmotesContainer implements EmotesContainer {

	private static TwitchEmotesContainer singleton = null;
	private Origins origin = Origins.TWITCHTV;
	private Map<String, String> map;

	private TwitchEmotesContainer(int capacity) {
		// Should it be concurrent??
		//TODO decide later
		//JavaDoc says that:
		//If multiple threads access a hash map concurrently, and at least one of the threads modifies the map structurally, it must be synchronized externally.
		map = new HashMap<>(capacity);
		//	map = new ConcurrentHashMap<>(capacity);
	}

	public static TwitchEmotesContainer getInstance() {
		return singleton;
	}


	// Dont need double check lock here
	// setInstance is invoked only once
	public static synchronized TwitchEmotesContainer setInstance(int capacity) {
		if (singleton == null) {
			singleton = new TwitchEmotesContainer(capacity);
		}
		return singleton;
	}

	@Override
	public Origins getOrigin() {
		return origin;
	}

	@Override
	public String getEmote(String emote) {
		return map.get(emote);
	}

	@Override
	public Map<String, String> getMap() {
		return map;
	}

}
