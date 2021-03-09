package bots.users;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import bots.Penguins.Penguin;
import discord4j.common.util.Snowflake;

public class Player {
	private final Map<Penguin, Integer> collectedPenguins;
	private final Snowflake snowFlake;

	public Player(long snowFlake) {
		this.snowFlake = Snowflake.of(snowFlake);
		collectedPenguins = new HashMap<>();
	}

	public Player(long snowFlake, Map<Penguin, Integer> collectedPenguins) {
		this.collectedPenguins = collectedPenguins;
		this.snowFlake = Snowflake.of(snowFlake);
	}

	public Snowflake getSnowFlake() {
		return snowFlake;
	}

	public Map<Penguin, Integer> getCollectedPenguins() {
		return collectedPenguins;
	}

	/**
	 * Methode die dem Player auf dem die Methode aufgerufen wird den übergebenen
	 * Pinguin hinzufügt.
	 * 
	 * @param penguin der hinzugefügt werden soll
	 * @return die anzahl an pinguinen diesen Typs in der Collectiion des Players
	 */
	public int addPenguin(Penguin penguin) {
		if (!collectedPenguins.containsKey(penguin))
			return collectedPenguins.put(penguin, 1);

		int anzahl = collectedPenguins.get(penguin);
		return collectedPenguins.replace(penguin, ++anzahl) + 1;
	}

	/**
	 * Methode die vom Player auf dem die Methode aufgerufen wird den übergebenen
	 * Pinguin entfernt.
	 * 
	 * @param penguin der entfernt werden soll
	 * @return die anzahl an pinguinen diesen Typs in der Collectiion des Players
	 */
	public int removePenguin(Penguin penguin) {
		if (!collectedPenguins.containsKey(penguin) || collectedPenguins.get(penguin) == 0)
			throw new NoSuchElementException("Player doesn't have any such Penguin: " + penguin.toString());
		int anzahl = collectedPenguins.get(penguin);
		return collectedPenguins.replace(penguin, --anzahl) - 1;
	}
}
