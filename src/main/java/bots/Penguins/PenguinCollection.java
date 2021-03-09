package bots.Penguins;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class PenguinCollection {
	private final Map<Long, Penguin> collection;
	public static final Path PINGU_PATH = Path.of("src/main/java/bots/data/penguins.csv");
	private long nextId;

	public PenguinCollection() {
		collection = parseCollection();
		new Thread(() -> {
			while (true) {
				updateCollection();
				try {
					Thread.sleep(10000);
				} catch (InterruptedException ignore) {
				}
			}
		}).start();
		nextId = getCollection().size() + 1;
	}

	/**
	 * Methode um penguin.csv zu parsen und in einer Map zurückzugeben. Der Key ist
	 * der Id des jeweiligen Pinguins und das Value der Penguin. Wenn du dem Pinguin
	 * weitere attribute hinzufügen möchtest, musst du das 1. In der Klasse Penguin
	 * machen, 2. hier in der Methode im inneren try-block und 3. in der csv datei
	 * selbst.
	 * 
	 * @return die in der CSV datei stehende PinguCollection als Map
	 */
	static Map<Long, Penguin> parseCollection() {
		try {
			return Files.lines(PINGU_PATH).skip(1).map(line -> line.split(",")).map(line -> {
				try {
					long id = Long.parseLong(line[0].trim());
					String name = line[1].trim();
					String imgUrl = line[2].trim();
					int level = Integer.parseInt(line[3].trim());
					return new Penguin(id, name, imgUrl, level);
				} catch (Exception e) {
					System.err
							.println("Error while parsing line: " + Arrays.toString(line) + ". Error: " + e.toString());
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
	private synchronized void updateCollection() {
		try (BufferedWriter writer = Files.newBufferedWriter(PINGU_PATH)) {
			writer.write("Id, Name, ImageUrl, Level");
			collection.entrySet().stream().map(entry -> entry.getValue()).forEach(pingu -> {
				try {
					writer.write(System.lineSeparator());
					writer.write(pingu.getInCsvFormat());
				} catch (IOException e) {
					System.err.println("Error while writing Penguin: " + pingu.toString());
				}
			});
			writer.flush();
		} catch (SecurityException e) {
			System.err.println("Error: Access to the File has been denied due to security reasons.");
		} catch (IOException e) {
			System.err.println(e.toString() + ". Could not access file!");
		}
	}

	public Map<Long, Penguin> getCollection() {
		return collection;
	}

	/**
	 * Ein threadsafes Map.putIfAbsent
	 * 
	 * @param penguin der eingefügt werden soll
	 * @return ob das einfügen erfolgreich war
	 */
	public synchronized boolean addPenguin(Penguin penguin) {
		if (collection.containsKey(penguin.getId()))
			return false;
		collection.put(penguin.getId(), penguin);
		return true;
	}

	/**
	 * @return die nächste verfügbare Id für ein Pinguin. Wird diese benutzt oder
	 *         nicht, ist sie dennoch dann vergeben.
	 */
	public synchronized long generateId() {
		return nextId++;
	}

	public static void main(String args[]) throws InterruptedException {
		PenguinCollection p = new PenguinCollection();
		System.out.println(p.getCollection());
		p.getCollection().put(2L, new Penguin(2, "Duke",
				"https://upload.wikimedia.org/wikipedia/commons/thumb/5/5d/Duke_%28Java_mascot%29_waving.svg/568px-Duke_%28Java_mascot%29_waving.svg.png",
				10));
		Thread.sleep(15000);
		System.out.println(p.getCollection());
	}
}
