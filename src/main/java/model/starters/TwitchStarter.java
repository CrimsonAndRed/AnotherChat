package model.starters;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.ConsoleClient;
import model.exceptions.FileSystemException;
import model.exceptions.InternetException;
import model.emotes.EmotesContainer;
import model.emotes.twitch.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.JsonPropertiesReader;

import java.io.*;
import java.net.Socket;
import java.util.InputMismatchException;

import static web.StringSender.sendString;

public class TwitchStarter implements Starter {

	private final static Logger logger = LogManager.getLogger();
	private final String ADDRESS = "irc.chat.twitch.tv";
	private final int PORT = 6667;
	private String name;
	private String oauth;

	public Socket getSocket() {
		return socket;
	}

	private Socket socket;
	public BufferedWriter bwriter;
	public BufferedReader breader;

	@Override
	public synchronized ConsoleClient prepareConnection(String channelName) throws InternetException, FileSystemException {
		JsonPropertiesReader settingsReader = null;
		try {
			settingsReader = new JsonPropertiesReader("settings.json");
		} catch (InputMismatchException e) {
			logger.error(e);
			return null;
		}
		name = settingsReader.readProperty("nickname").toLowerCase();
		oauth = "oauth:" + settingsReader.readProperty("oauth");
		logger.info("Got properties");
		loadEmotes();

		try {
			socket = new Socket(ADDRESS, PORT);
			bwriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			breader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			throw new InternetException();
		}
		tryAuthenticate();
		sendString(bwriter, "JOIN #" + channelName);
		//TODO maybe sort of resolve, this is stupid
		return new ConsoleClient(socket, bwriter, breader);
	}

	private void loadEmotes() throws FileSystemException {

		logger.info("Getting twitch emotes");

		if (TwitchEmotesContainer.getInstance() == null) {

			final String pathToEmotes = "temp/TwitchEmotes.json";
			JsonObject obj = new TwitchEmotesLoader().getEmotesAndSave(pathToEmotes);
			logger.info("Got twitch emotes");
			logger.info("Trying to parse twitch emotions");
			JsonArray arr = obj.getAsJsonArray("emoticons");
			logger.info("Got " + arr.size() + " twitch emotions. Is it possible?");

			Gson gson = new Gson();
			//TODO put this in method or class
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
			// -1 because or RageFace
			logger.info("Now twitch has " + emotes.getMap().size() + " emotes in base. Must be equal to " + (array.size() + arr.size() - 1));
		} else {
			logger.info("Twitch emotes are already loaded");
		}
		logger.info("Starting IRC connection...");
	}

	private void tryAuthenticate() throws InternetException {
		sendString(bwriter, "PASS " + oauth);
		sendString(bwriter, "NICK " + name);
		String string = null;
		try {
			string = breader.readLine();
			if (string.equals(":tmi.twitch.tv NOTICE * :Improperly formatted auth") ||
					string.equals(":tmi.twitch.tv NOTICE * :Login authentication failed") ||
					string.endsWith("Unknown command")) {
				logger.error("Authentication error");
				logger.info("System responded:\n" + string);
				throw new InternetException();
			}
			// 7 messages as twitch irc respond
			logger.debug(string);
			logger.debug(breader.readLine());
			logger.debug(breader.readLine());
			logger.debug(breader.readLine());
			logger.debug(breader.readLine());
			logger.debug(breader.readLine());
			logger.debug(breader.readLine());
			logger.info("Authentication completed");

		} catch (IOException e) {
			throw new InternetException();
		}
	}
}
