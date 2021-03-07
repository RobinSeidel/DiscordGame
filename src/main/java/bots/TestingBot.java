package bots;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.reaction.ReactionEmoji;

public class TestingBot {
	private final Map<String, String> answers;
	private final Map<String, String> reactions;
	private final GatewayDiscordClient client;

	public TestingBot() {
		answers = loadAnswers();
		reactions = loadReactions();
		client = DiscordClientBuilder.create("ODE2MjMxNTg2NTY2ODk3Njc0.YD385w.blC0wtWw41lCYqoz7nY-0FuYriE").build()
				.login().block();
	}

	public static void main(String[] args) {
		TestingBot b = new TestingBot();
		b.answers.entrySet().stream().forEach(entry -> {
			b.client.getEventDispatcher().on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage)
					.map(message -> {
						try {
							b.reactToMessage(message);
						} catch (Exception ignore) {
						}
						return message;
					}).filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
					.filter(message -> message.getContent().equalsIgnoreCase(entry.getKey()))
					.flatMap(Message::getChannel).flatMap(channel -> channel.createMessage(entry.getValue()))
					.subscribe();
		});
		b.client.onDisconnect().block();
	}

	void reactToMessage(Message message) {
		reactions.entrySet().stream().forEach(entry -> {
			if (message.getContent().toLowerCase().contains(entry.getKey()))
				message.addReaction(ReactionEmoji.unicode(entry.getValue())).block();
		});
	}

	private static Map<String, String> loadAnswers() {
		Map<String, String> output;
		try {
			output = Files.lines(Path.of("src/main/java/bots/BotReactions.csv")).skip(1)
					.takeWhile(line -> !line.contains("#")).filter(line -> !line.equals(","))
					.map(line -> line.split(",")).collect(Collectors.toMap(a1 -> a1[0], a1 -> a1[1]));
			StringBuilder help = new StringBuilder();
			Files.lines(Path.of("src/main/java/bots/BotReactions.csv")).skip(1)
					.takeWhile(line -> !line.contains("#reactions")).filter(line -> !line.equals(","))
					.map(line -> line.split(",")[0]).forEach(string -> help.append(string + System.lineSeparator()));
			output.put("!help", help.toString());
		} catch (IOException e) {
			output = new HashMap<>();
		}

		return output;
	}

	private static Map<String, String> loadReactions() {
		try {
			return Files.lines(Path.of("src/main/java/bots/BotReactions.csv"))
					.dropWhile(line -> !line.contains("#reactions")).skip(1).takeWhile(line -> !line.contains("#"))
					.filter(line -> !line.equals(",")).map(line -> line.split(","))
					.collect(Collectors.toMap(a1 -> a1[0], a1 -> a1[1]));
		} catch (IOException e) {
			return new HashMap<>();
		}
	}
}
