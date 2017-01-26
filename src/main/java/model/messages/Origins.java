package model.messages;

public enum Origins {
	TWITCHTV("Twitch"), PEKA2TV("Peka");

	private String name;

	Origins(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
