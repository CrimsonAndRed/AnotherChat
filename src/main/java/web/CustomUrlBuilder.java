package web;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CustomUrlBuilder {

	private HttpURLConnection connection;

	public CustomUrlBuilder(URL url) throws IOException {
		connection = (HttpURLConnection)url.openConnection();
	}

	public CustomUrlBuilder setAttribute(String key, String value) {
		connection.setRequestProperty(key, value);
		return this;
	}

	public CustomUrlBuilder setMethod(String method) throws IOException{
		connection.setRequestMethod(method);
		return this;
	}

	public InputStreamReader getStream() throws IOException{
		return new InputStreamReader(connection.getInputStream());
	}

}