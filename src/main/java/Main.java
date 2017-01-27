import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.emotes.EmotesContainer;
import model.emotes.twitch.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.JsonPropertiesReader;
import web.IrcClient;

import java.util.InputMismatchException;

public class Main {
	private static final Logger logger = LogManager.getLogger();
	//TODO human-usable login interface
	//TODO json -> preferences or .properties
	//TODO Once system didnt respond. Reproduce and make something like retry connection
	/*
		To run this:
		1. Rename "settingsStub.json" to "settings.json"
		2. Add your info
	 */
	public static void main(String[] args) {
		logger.info("Starting app!");
		JsonPropertiesReader settingsReader = null;
		try {
			settingsReader = new JsonPropertiesReader("settings.json");
		} catch (InputMismatchException e) {
			logger.error(e);
			return;
		}
		final String address = "irc.chat.twitch.tv";
		final int port = 6667;
		final String name = settingsReader.readProperty("nickname").toLowerCase();
		final String oauth = "oauth:" + settingsReader.readProperty("oauth");
		logger.info("Got properties");

		logger.info("Getting twitch emotes");
		final String pathToEmotes = "temp/TwitchEmotes.json";
		JsonObject obj = new TwitchEmotesLoader().getEmotesAndSave(pathToEmotes);
		logger.info("Got twitch emotes");
		logger.info("Trying to parse twitch emotions");
		JsonArray arr = obj.getAsJsonArray("emoticons");
		logger.info("Got " + arr.size() + " twitch emotions. Is it possible?");

		Gson gson = new Gson();
		//TODO put this in method or class
		//TODO add this https://api.betterttv.net/emotes
		EmotesContainer emotes = TwitchEmotesContainer.setInstance(arr.size());
		for (int i = 0; i < arr.size(); i++) {
			TwitchJsonEmotion emotion = gson.fromJson(arr.get(i), TwitchJsonEmotion.class);
			emotes.getMap().put(emotion.getRegex(), emotion.getImages().get(0).getUrl());
		}
		logger.info("Parsed " + emotes.getMap().size() + " twitch emotions in base. Must be equal to " + arr.size());
		logger.info("Getting betterTv emotes");

		final String pathToBetterTv = "temp/BetterTvEmotes.json";
		obj = new BetterTvEmotesLoader().getEmotesAndSave(pathToBetterTv);
		logger.info("Got betterTv emotes");
		logger.info("Trying to parse betterTv emotions");
		JsonArray array = obj.getAsJsonArray("emotes");
		logger.info("Got " + array.size() + " betterTv emotions. Is it possible?");
		//TODO RageFace seems to be in Twitch.tv and BetterTv emotes
		//TODO Replace Twitch emote with Better emote forever
		for (int i = 0; i < array.size(); i++) {
			BetterTvJsonEmotion emotion = gson.fromJson(array.get(i), BetterTvJsonEmotion.class);
			emotes.getMap().put(emotion.getRegex(), emotion.getUrl());
		}
		logger.info("Now twitch has " + emotes.getMap().size() + " emotes in base. Must be equal to " + (array.size() + arr.size()));
		logger.info("Starting IRC connection...");

		IrcClient client = new IrcClient(address, port, name, oauth);
		client.tryConnect();
	}
}
