package bots.data;

public class Penguin {
	private final String name;
	private final long id;
	private final int level;
	private final String imageUrl;

	public Penguin(long id, String name, String imageUrl, int level) {
		this.id = id;
		this.name = name;
		this.imageUrl = imageUrl;
		this.level = level;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getName() {
		return name;
	}

	public long getId() {
		return id;
	}

	public int getLevel() {
		return level;
	}

	/**
	 * @return eine String repräsentierung mit der sich clean in die csv datei schreiben lässt
	 */
	String getInCsvFormat() {
		return id + ", " + name + ", " + imageUrl + ", " + level;
	}

	/**
	 * Autogenerierte toString methode, damit man beim debuggen checkt um welchen
	 * atzen es sich handelt.
	 */
	@Override
	public String toString() {
		return "Penguin [name=" + name + ", id=" + id + ", level=" + level + ", imageUrl=" + imageUrl + "]";
	}

}
