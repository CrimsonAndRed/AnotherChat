package model.emotes.twitch;

import model.emotes.EmotesLoader;
import web.CustomUrlBuilder;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/*
curl -H 'Accept: application/vnd.twitchtv.v5+json' \
-H 'Client-ID: uo6dggojyb8d6soh92zknwmi5ej1q2' \
-X GET 'https://api.twitch.tv/kraken/chat/emoticons'
 */
public class TwitchEmotesLoader extends EmotesLoader {

	@Override
	protected InputStreamReader getStream() throws IOException{
		return new CustomUrlBuilder(new URL(getUrl()))
				.setAttribute("Accept", "application/vnd.twitchtv.v5+json")
				.getStream();
	}

	@Override
	protected String getUrl() {
		return "https://api.twitch.tv/kraken/chat/emoticons";
	}
}
