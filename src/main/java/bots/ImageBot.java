package bots;

import java.util.Map;


import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;
import static bots.Main.*;

public class ImageBot {
	static Map<String,String> bildReactions = Map.of(
			"good bot","https://media.tenor.com/images/6cb817f79a934a3f20d4e1b56d13519c/tenor.gif" ,
			"$flags", "https://cdn.discordapp.com/attachments/806541558278258748/816011349662367814/unknown.png"
			);
	public static void main(String[] args) {
		client.getEventDispatcher().on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage)
		.filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
		.filter(message -> bildReactions.containsKey(message.getContent()))
		.flatMap(message -> message.getChannel().block().createEmbed(embed -> {
			embed.setColor(Color.DISCORD_BLACK);
			embed.setImage(bildReactions.get(message.getContent()));
		})).subscribe();
	}

}
