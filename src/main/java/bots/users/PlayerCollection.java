package bots.users;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import bots.Main;
import bots.Penguins.Penguin;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.User;

public class PlayerCollection {
	public static final Path USER_PATH = Path.of("src/main/java/bots/data/users.csv");
	private final Map<Long, Player> players;

	public PlayerCollection() {
		this.players = parseCsv();
		new Thread(() -> {
			while (true) {
				updateCollection();
				try {
					Thread.sleep(10000);
				} catch (InterruptedException ignore) {
				}
			}
		}).start();
	}

	/**
	 * @return eine Map mit der Snowflake als long als Key und den jeweiligen
	 *         Spieler als Value. (hoffentlich funktionierts)
	 */
	static Map<Long, Player> parseCsv() {
		List<Penguin> list = new ArrayList<>();
		try {
			Files.lines(USER_PATH).limit(1).map(line -> line.split(",")).forEach(arr -> {
				for (int i = 1; i < arr.length; i++) {
					list.add(Main.PINGU_COLLECTION.getCollection().get(Long.parseLong(arr[i].trim())));
				}
			});
			return Files.lines(USER_PATH).skip(1).map(line -> line.split(",")).map(line -> {
				try {
					long id = Long.parseLong(line[0].trim());
					Map<Penguin, Integer> collected = new HashMap<>();
					for (int i = 1; i < list.size(); i++) {
						collected.put(list.get(i), Integer.parseInt(line[i].trim()));
					}
					return new Player(id, collected);
				} catch (NumberFormatException e) {
					System.err.println(e.toString());
					return null;
				}
			}).filter(player -> !(player == null))
					.collect(Collectors.toMap(player -> player.getSnowFlake().asLong(), player -> player));
		} catch (IOException e) {
			return new HashMap<>();
		}
	}

	public Map<Long, Player> getPlayers() {
		return players;
	}

	/**
	 * Methode die ein mal die collection naiv updatet, in dem alles gelöscht wird
	 * und neu geschrieben wird. Achtung, in der Zeit sollten am besten keine
	 * zugriffe auf die Collection geschehen
	 */
	private synchronized void updateCollection() {
		try (BufferedWriter writer = Files.newBufferedWriter(USER_PATH)) {
			//write first line
			List<Penguin> penguins = Main.PINGU_COLLECTION.getCollection().entrySet().stream()
					.map(entry -> entry.getValue()).collect(Collectors.toList());
			writer.write("Snowflake");
			for (Penguin current : penguins) {
				writer.write("," + current.getId());
			}
			//write data of every player
			players.entrySet().stream().map(entry -> entry.getValue()).forEach(player -> {
				try {
					writer.write(System.lineSeparator());
					writer.write(""+player.getSnowFlake().asLong());
					for(Penguin penguin : penguins) {
						writer.write("," + player.getCollectedPenguins().get(penguin));
					}
				} catch (IOException e) {
					System.err.println("Error while writing Penguin: " + player.toString());
				}
			});
			writer.flush();
		} catch (SecurityException e) {
			System.err.println("Error: Access to the File has been denied due to security reasons.");
		} catch (IOException e) {
			System.err.println(e.toString() + ". Could not access file!");
		}
	}

	public Player getPlayer(User user) {
		return players.get(user.getId().asLong());
	}
	
	public Player registerNewPlayer(User user) {
		return players.put(user.getId().asLong(), new Player(user.getId().asLong()));
	}
	
	public Player getPlayer(Member member) {
		return players.get(member.getId().asLong());
	}

}
