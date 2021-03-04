package bots;
import discord4j.core.*;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.rest.util.Color;

public class TestBot {
	static final GatewayDiscordClient client = DiscordClientBuilder
			.create("ODE2MjMxNTg2NTY2ODk3Njc0.YD385w.br69s25LUjx-SdMa5lsJ3IBqW4g").build().login().block();

	public static void main(String[] args) {
		System.out.println("Test");

		client.getEventDispatcher().on(ReadyEvent.class).subscribe(event -> {
			final User self = event.getSelf();
			System.out.printf("Logged in as %s#%s%n", self.getUsername(), self.getDiscriminator());
		});
		client.getEventDispatcher().on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage)
				.filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
				.filter(message -> message.getContent().equalsIgnoreCase("!ping")).flatMap(Message::getChannel)
				.flatMap(channel -> channel.createMessage("Pong!")).subscribe();
		client.getEventDispatcher().on(MemberJoinEvent.class);

		client.getEventDispatcher().on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage)
				.filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
				.filter(message -> message.getContent().toLowerCase().contains("krapfen")).flatMap(Message::getChannel)
				.flatMap(channel -> channel.createMessage("Mashallah sag doch Berliner wie ein normaler Mensch..."))
				.subscribe();

		client.getEventDispatcher().on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage)
				.filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
				.filter(message -> message.getContent().equalsIgnoreCase("!show")).flatMap(Message::getChannel)
				.flatMap(channel -> channel.createEmbed(embed -> {
					embed.setTitle("My Embed");
					embed.setColor(Color.DISCORD_BLACK);
					embed.setDescription("test embed");
					embed.setImage(
							"https://upload.wikimedia.org/wikipedia/commons/thumb/5/5d/Duke_%28Java_mascot%29_waving.svg/1200px-Duke_%28Java_mascot%29_waving.svg.png");
					embed.addField("Filed", "Hallo Welt", true);
				})).subscribe();

		sendFile();
		
		client.onDisconnect().block();

	}

	static void sendFile() {
		client.getEventDispatcher().on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage)
		.filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
		.filter(message -> message.getContent().equalsIgnoreCase("!nakedchick")).flatMap(Message::getChannel)
		.flatMap(channel -> channel.createMessage("Junge chill mal"))
				.subscribe();
	}
}
