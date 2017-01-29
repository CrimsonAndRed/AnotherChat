package model.emotes.twitch;

import model.emotes.Emotion;

/*
{"status":200,"emotes":[{"url":"//cdn.betterttv.net/emote/54fa925e01e468494b85b54d/1x","width":28,"height":28,"imageType":"png","regex":"OhMyGoodness","channel":null},{"url":"//cdn.betterttv.net/emote/54fa927801e468494b85b54e/1x","width":28,"height":28,"imageType":"png","regex":"PancakeMix","channel":null},{"url":"//cdn.betterttv.net/emote/54fa928f01e468494b85b54f/1x","width":28,"height":28,"imageType":"png","regex":"PedoBear","channel":null} ... }
 */
public class BetterTvJsonEmotion implements Emotion {

	private String url;
	private String width;
	private String height;
	private String imageType;
	private String regex;
	private String channel;

	public String getRegex() {
		return regex;
	}

	public String getUrl() {
		return "https:" + url;
	}
}
