package model;

public enum Origin {
	TWITCHTV("Twitch"), PEKA2TV("Peka");

	private String name;

	Origin(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
