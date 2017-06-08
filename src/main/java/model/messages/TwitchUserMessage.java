package model.messages;

import model.Origin;
import model.emotes.EmotesContainer;
import model.emotes.twitch.TwitchEmotesContainer;
import model.responses.ResponseType;

/*
Example: :xail24!xail24@xail24.tmi.twitch.tv PRIVMSG #pgg :erik_kartmen1 не просто голоса похожие
 */
public class TwitchUserMessage extends AbstractUserMessage {


	public TwitchUserMessage(ResponseType type, String message) {
		this.origin = Origin.TWITCHTV;
		this.type = type;
		this.originalLine = message;
	}


	@Override
	public void parseOriginalMessage() {
		String[] splits = originalLine.split(" ");
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
}
