package model.responses;

/*
	Twitch responses examples:
		- System message
		  :tmi.twitch.tv 001 socrimson :Welcome, GLHF!
		- User message
		  :vlad_kadigrob!vlad_kadigrob@vlad_kadigrob.tmi.twitch.tv PRIVMSG #starladder5 :#СоповДайКейс
		- Ping
		  PING :tmi.twitch.tv
 */

public class TwitchChecker implements Checker {

	@Override
	public ResponseType checkType(String msg) {
		String[] inputs = msg.split(" ");
		if (inputs.length > 3 && inputs[1].equals("PRIVMSG")) {
			return ResponseType.MSG;
		} else if (inputs[0].equals(":tmi.twitch.tv")) {
			return ResponseType.SYSMSG;
		} else if (msg.equals("PING :tmi.twitch.tv")) {
			return ResponseType.PING;
		} else {
			return ResponseType.UNKNOWN;
		}
	}
}
