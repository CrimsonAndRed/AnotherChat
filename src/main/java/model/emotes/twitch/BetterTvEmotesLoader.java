package model.emotes.twitch;

import model.emotes.EmotesLoader;
import web.CustomUrlBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class BetterTvEmotesLoader extends EmotesLoader {

	@Override
	protected InputStreamReader getStream() throws IOException {
		return new CustomUrlBuilder(new URL(getUrl())).getStream();
	}

	@Override
	protected String getUrl() {
		return "https://api.betterttv.net/emotes";
	}
}
