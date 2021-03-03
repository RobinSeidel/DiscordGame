package bots;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.rest.util.Color;

public class CuteBot {
	static GatewayDiscordClient client = DiscordClientBuilder.create("ODE2MjMxNTg2NTY2ODk3Njc0.YD385w.blC0wtWw41lCYqoz7nY-0FuYriE").build()
			.login().block();
	
	public static void main(String[] args) {
		client.getEventDispatcher().on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage)
		.filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
		.filter(message -> message.getContent().equalsIgnoreCase("good bot")).flatMap(Message::getChannel)
		.flatMap(channel -> channel.createEmbed(embed -> {
			embed.setTitle("Thanks bro :wink:");
			embed.setColor(Color.DISCORD_BLACK);
			embed.setImage(
					"https://media.tenor.com/images/6cb817f79a934a3f20d4e1b56d13519c/tenor.gif");
		})).subscribe();
	}

}
