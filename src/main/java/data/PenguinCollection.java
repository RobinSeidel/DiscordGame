package data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class PenguinCollection {
	private Map<Long, Penguin> collection;

	static Map<Long, Penguin> parseCollection() {
		try {
			return Files.lines(Path.of("src/main/data/penguins.csv")).skip(1).map(line -> line.split(",")).map(line -> {
				try {
					long id = Long.parseLong(line[0]);
					String name = line[1];
					String imgUrl = line[2];
					int level = Integer.parseInt(line[3]);
					return new Penguin(id, name, imgUrl, level);
				} catch (Exception e) {
					System.err
							.println("Error while parsing line: " + Arrays.toString(line) + ". Error: " + e.toString());
					return null;
				}
			}).collect(Collectors.toMap(pingu -> pingu.getId(), pingu -> pingu));
		} catch (IOException e) {
			System.err.println("Error while pasring: " + e.toString());
			return Map.of();
		}
	}
}
