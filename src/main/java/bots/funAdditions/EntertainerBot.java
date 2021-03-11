package bots.funAdditions;

import static bots.Main.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.GuildChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.rest.util.Color;
import static bots.Snowflakes.*;

public class EntertainerBot {
	private final Map<String, String> imageReactions;
	private final Map<String, String> answers;
	private final Map<String, String> reactions;

	public static final String REACTIONS = "#reactions";
	public static final String ANSWERS = "#answers";
	public static final String IMAGE_REACTIONS = "#imageReactions";

	public EntertainerBot() {
		answers = parseCsv(ANSWERS);
		reactions = parseCsv(REACTIONS);
		imageReactions = parseCsv(IMAGE_REACTIONS);
	}

	public static void main(String[] args) {
		EntertainerBot entertainer = new EntertainerBot();
		client.getEventDispatcher().on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage)
				.filter(message -> !message.getChannel().cast(TextChannel.class).block().getCategory().block().getId().equals(GAMECHANNELS.getId()))
				.filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false)).map(message -> {
					entertainer.reactToMessage(message);
					entertainer.commentOnMessage(message);
					entertainer.sendImage(message);
					return message;
				}).subscribe();
		client.onDisconnect().block();
	}

	private void reactToMessage(Message message) {
		reactions.entrySet().stream().forEach(entry -> {
			if (message.getContent().toLowerCase().contains(entry.getKey()))
				message.addReaction(ReactionEmoji.unicode(entry.getValue())).block();
		});
	}

	private void commentOnMessage(Message message) {
		answers.entrySet().stream().forEach(entry -> {
			if (message.getContent().toLowerCase().contains(entry.getKey()))
				message.getChannel().block().createMessage(entry.getValue()).block();
		});
	}

	private void sendImage(Message message) {
		if(imageReactions.containsKey(message.getContent().toLowerCase())){
			message.getChannel().block().createEmbed(embed -> {
				embed.setColor(Color.DISCORD_BLACK);
				embed.setImage(imageReactions.get(message.getContent()));
			}).block();
		}
	}

	private static Map<String, String> parseCsv(String category) {
		try {
			return Files.lines(Path.of("src/main/java/bots/data/BotReactions.csv"))
					.dropWhile(line -> !line.contains(category)).skip(1).takeWhile(line -> !line.contains("#"))
					.filter(line -> !line.equals(",")).map(line -> line.split(","))
					.collect(Collectors.toMap(a1 -> a1[0], a1 -> a1[1]));
		} catch (IOException e) {
			return new HashMap<>();
		}
	}
}
