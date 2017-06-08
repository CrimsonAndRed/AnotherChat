package model.emotes;

import model.Origin;

import java.util.Map;

public interface EmotesContainer {
	Origin getOrigin();
	String getEmote(String emote);
	Map<String, String> getMap();
}
