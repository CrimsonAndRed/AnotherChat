package util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.InputMismatchException;

public class JsonPropertiesReader {

	private JsonObject obj;

	public JsonObject getJson() {
		return obj;
	}

	public JsonPropertiesReader(String filePath) {
		try {
			obj = new JsonParser().parse(new BufferedReader(new InputStreamReader(new FileInputStream(filePath)))).getAsJsonObject();
		} catch (FileNotFoundException e) {
			throw new InputMismatchException("File does not exist!");
		}
	}

	public JsonPropertiesReader(JsonObject obj) {
		this.obj = obj;
	}

	public String readProperty(String key) {
		String value = obj.getAsJsonPrimitive(key).getAsString();
		if (value == null) {
			throw new InputMismatchException("No " + key + " property found");
		}
		return value;
	}
}
