package model.emotes;

import com.google.gson.JsonObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import util.JsonLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.InputMismatchException;
import java.util.concurrent.TimeUnit;

//TODO replace logger with abstract getLogger();
public abstract class EmotesLoader {

	private static final Logger logger = LogManager.getLogger();

	public JsonObject getEmotesAndSave(String filepath) {
		File file = new File(filepath);
		JsonLoader loader = new JsonLoader();
		if (file.exists()) {
			logger.info("File " + filepath + " exists");
			try {
				long days = checkEmotes(filepath);
				logger.info("Emotes were updated " + days + " days ago");
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

	public long checkEmotes(String filepath) throws IOException {
		return TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis()) - Files.getLastModifiedTime(Paths.get(filepath)).to(TimeUnit.DAYS);
	}

	public JsonObject getEmotes() {
		JsonLoader loader = new JsonLoader();
		InputStreamReader stream;
		JsonObject obj;
		try {
			stream = getStream();
			obj = loader.getJsonFromStream(stream);
			stream.close();
		} catch (IOException e) {
			final String errorMsg = "Wrong URL or service is down on url: " + getUrl();
			logger.error(errorMsg);
			throw new InputMismatchException(errorMsg);
		}
		logger.info("Emotes are loaded");
		return obj;
	}

	protected abstract InputStreamReader getStream() throws IOException;
	protected abstract String getUrl();
}
