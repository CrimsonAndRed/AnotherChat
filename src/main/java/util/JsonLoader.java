package util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.InputMismatchException;

public class JsonLoader {

	private JsonParser parser = new JsonParser();

	public JsonObject getJsonByString(String stringUrl) {
		URL url;
		JsonObject obj;
		try {
			url = new URL(stringUrl);
			obj = getJsonFromStream(new InputStreamReader(url.openStream()));

		} catch (IOException e) {
			throw new InputMismatchException("Wrong address or service down");
		}
		return obj;
	}

	public JsonObject getJsonFromStream(InputStreamReader streamReader) {
		return parser.parse(new BufferedReader(streamReader)).getAsJsonObject();
	}
}
