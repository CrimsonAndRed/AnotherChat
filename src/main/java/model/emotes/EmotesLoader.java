package model.emotes;

import com.google.gson.JsonObject;

import java.io.IOException;

/*
curl -H 'Accept: application/vnd.twitchtv.v5+json' \
-H 'Client-ID: uo6dggojyb8d6soh92zknwmi5ej1q2' \
-X GET 'https://api.twitch.tv/kraken/chat/emoticons'
 */
public interface EmotesLoader {
	JsonObject getEmotesAndSave(String filepath);
	JsonObject getEmotes();
	void saveEmotes(JsonObject obj, String filepath);
	long checkEmotes(String filepath) throws IOException;

}
