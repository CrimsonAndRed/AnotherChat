package model.emotes;

import com.google.gson.JsonObject;

import java.io.IOException;


public interface EmotesLoader {
	JsonObject getEmotesAndSave(String filepath);
	JsonObject getEmotes();
	void saveEmotes(JsonObject obj, String filepath);
	long checkEmotes(String filepath) throws IOException;

}
