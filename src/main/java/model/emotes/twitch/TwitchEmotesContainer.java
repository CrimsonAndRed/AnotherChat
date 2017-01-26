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
		//	map = new ConcurrentHashMap<>(capacity);
		map = new HashMap<>(capacity);
	}

	public static TwitchEmotesContainer getInstance() {
		return singleton;
	}

	public static TwitchEmotesContainer setInstance(int capacity) {
		singleton = new TwitchEmotesContainer(capacity);
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
