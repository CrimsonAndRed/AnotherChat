package model.emotes.twitch;

/*
	Example1: {"regex":"ydmSatti","images":[{"width":28,"height":28,"url":"https://static-cdn.jtvnw.net/jtv_user_pictures/emoticon-69119-src-0d4b23ce12767ed8-28x28.png","emoticon_set":13794}]}
	Example2: {"regex":"kenmKS","images":[{"width":28,"height":28,"url":"https://static-cdn.jtvnw.net/jtv_user_pictures/emoticon-115022-src-c1b4bb6f3224c8ee-28x28.png","emoticon_set":18890}]}
 */
public class TwitchJsonEmotionImage {
	String width;
	String height;
	String url;
	String emoticon_set;

	public String getWidth() {
		return width;
	}

	public String getHeight() {
		return height;
	}

	public String getUrl() {
		return url;
	}

	public String getEmoticonSet() {
		return emoticon_set;
	}
}
