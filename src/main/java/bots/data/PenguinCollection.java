package bots.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class PenguinCollection {
	private Map<Long, Penguin> collection;
	private boolean updating;

	public PenguinCollection() {
		collection = parseCollection();
		new Thread(() -> {
			while (true) {
				updateCollection();
				notifyAll();
				try {
					Thread.sleep(10000);
				} catch (InterruptedException ignore) {
				}
			}
		}).start();
	}

	/**
	 * Methode um penguin.csv zu parsen und in einer Map zurückzugeben. Der Key ist
	 * der Id des jeweiligen Pinguins und das Value der Penguin. Wenn du dem Pinguin
	 * weitere attribute hinzufügen möchtest, musst du das 1. In der Klasse Penguin
	 * machen, 2. hier in der Methode im inneren try-block und 3. in der csv datei
	 * selbst.
	 * 
	 * @return
	 */
	static Map<Long, Penguin> parseCollection() {
		try {
			return Files.lines(Path.of("src/main/java/data/penguins.csv")).skip(1).map(line -> line.split(","))
					.map(line -> {
						try {
							long id = Long.parseLong(line[0].trim());
							String name = line[1].trim();
							String imgUrl = line[2].trim();
							int level = Integer.parseInt(line[3].trim());
							return new Penguin(id, name, imgUrl, level);
						} catch (Exception e) {
							System.err.println(
									"Error while parsing line: " + Arrays.toString(line) + ". Error: " + e.toString());
							return null;
						}
					}).collect(Collectors.toMap(pingu -> pingu.getId(), pingu -> pingu));
		} catch (IOException e) {
			System.err.println("Error while parsing: " + e.toString());
			return Map.of();
		}
	}

	/**
	 * Methode die ein mal die collection naiv updatet, in dem alles gelöscht wird
	 * und neu geparsed wird. Achtung, in der Zeit sollten am besten keine zugriffe
	 * auf die Collection geschehen
	 */
	private void updateCollection() {
		updating = true;
		collection.clear();
		collection.putAll(parseCollection());
		updating = false;
	}

	/**
	 * getter für die collection der aufpasst, dass sie nicht gerade geupdatet wird.
	 */
	public Map<Long, Penguin> getCollection() {
		while (updating) {
			try {
				wait();
			} catch (InterruptedException ignore) {
			}
		}
		return collection;
	}

	public static void main(String args[]) {
		System.out.println(parseCollection());
	}
}
