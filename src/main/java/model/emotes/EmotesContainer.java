package model.emotes;

import model.messages.Origins;

import java.util.Map;

public interface EmotesContainer {
	Origins getOrigin();
	String getEmote(String emote);
	Map<String, String> getMap();
}
