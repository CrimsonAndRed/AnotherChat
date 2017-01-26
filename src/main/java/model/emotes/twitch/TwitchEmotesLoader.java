package model.emotes.twitch;

import com.google.gson.JsonObject;
import model.emotes.EmotesLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.JsonLoader;
import web.CustomUrlBuilder;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.concurrent.TimeUnit;

/*
curl -H 'Accept: application/vnd.twitchtv.v5+json' \
-H 'Client-ID: uo6dggojyb8d6soh92zknwmi5ej1q2' \
-X GET 'https://api.twitch.tv/kraken/chat/emoticons'
 */
public class TwitchEmotesLoader implements EmotesLoader {

	private static final Logger logger = LogManager.getLogger();

	public JsonObject getEmotesAndSave(String filepath) {

		File file = new File(filepath);
		JsonLoader loader = new JsonLoader();
		if (file.exists()) {
			logger.info("File " + filepath + " exists");
			try {
				long days = checkEmotes(filepath);
				logger.info("Emotes were updated " + days  + " days ago");
				if (days > 7) {
					logger.info("Reloading new file from Twitch");
				} else {
					logger.info("Keeping cached file");
					return loader.getJsonFromStream(new InputStreamReader(new FileInputStream(file)));
				}
			} catch (IOException e) {
				final String errorMsg = "Cant read modification date of file " + file.getAbsolutePath();
				logger.error(errorMsg);
				throw new InputMismatchException(errorMsg);
			}
		} else {
			logger.info("File " + filepath + " does not exist");
		}
		JsonObject obj = getEmotes();
		saveEmotes(obj, filepath);
		return obj;
	}

	@Override
	public JsonObject getEmotes() {
		JsonLoader loader = new JsonLoader();
		InputStreamReader stream;
		final String url = "https://api.twitch.tv/kraken/chat/emoticons";
		try {
			stream = new CustomUrlBuilder(new URL(url))
					.setAttribute("Accept", "application/vnd.twitchtv.v5+json")
					//TODO remove hardcoded client-id
					.setAttribute("Client-ID", "mhxmew00o9rho407thdtweysipi7q2")
					.getStream();
		} catch (IOException e) {
			final String errorMsg = "Wrong URL or service is down on url: " + url;
			logger.error(errorMsg);
			throw new InputMismatchException(errorMsg);
		}
		JsonObject obj = loader.getJsonFromStream(stream);
		logger.info("Emotes are loaded");
		return obj;
	}

	@Override
	public void saveEmotes(JsonObject obj, String filepath) {
		File file = new File(filepath);
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(file, false);
			fileOutputStream.write(obj.toString().getBytes());
			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (IOException e) {
			final String errorMsg = "File writing exception on file: " + file.getAbsolutePath();
			logger.error(errorMsg);
			throw new InputMismatchException(errorMsg);
		}
	}

	@Override
	public long checkEmotes(String filepath) throws IOException{
		return  TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()) - Files.getLastModifiedTime(Paths.get(filepath)).to(TimeUnit.DAYS);
	}
}
