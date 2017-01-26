package model.messages;

import model.emotes.EmotesContainer;
import model.emotes.twitch.TwitchEmotesContainer;

/*
Example: :xail24!xail24@xail24.tmi.twitch.tv PRIVMSG #pgg :erik_kartmen1 не просто голоса похожие
 */
public class TwitchUserMessage implements UserMessage {

	private String message;
	private String channel;
	private String sender;

	public TwitchUserMessage(String input) {
		//TODO Copy Pasta spam kills me
		// Maybe..
		String[] splits = input.split(" ");
		channel = splits[2].substring(1);
		//message = input.substring(input.indexOf(':', 1) + 1);

		EmotesContainer container = TwitchEmotesContainer.getInstance();
		StringBuilder builder = new StringBuilder();
		splits[3] = splits[3].substring(1);
		String url = null;

		for (int i = 3; i < splits.length; i++) {
			builder.append(" ");
			if ((url = container.getEmote(splits[i])) == null) {
				builder.append(splits[i]);
			} else {
				builder.append(url);
			}
		}

		message = builder.toString();

		// '!' symbol cannot be used as part of nickname
		sender = splits[0].substring(1, splits[0].indexOf('!'));
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public Origins getOrigin() {
		return Origins.TWITCHTV;
	}

	@Override
	public String getSender() {
		return sender;
	}

	@Override
	public String getChannel() {
		return channel;
	}
}
