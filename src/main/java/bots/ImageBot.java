package bots;

import java.util.Map;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.rest.util.Color;
import static bots.Main.*;

public class ImageBot {
	static Map<String,String> bildReactions = Map.of(
			"good bot","https://media.tenor.com/images/6cb817f79a934a3f20d4e1b56d13519c/tenor.gif" ,
			"$flags", "https://cdn.discordapp.com/attachments/806541558278258748/816011349662367814/unknown.png",
			"$angry", "https://media3.giphy.com/media/Tgxr8pn069Sf7mgv0e/giphy.gif",
			"$era", "https://media2.giphy.com/media/lRLzrbhmh5pFf4jOga/giphy.gif",
			"$workout","https://media4.giphy.com/media/VekmA7RMfPVG9HDBGT/giphy.gif",
			"stupid bot","https://media3.giphy.com/media/ZcW8zOfmu7pIdzOAWO/giphy.gif",
			"fuck you bot", "https://media.tenor.com/images/16d0803b2f82c34d64518bd9b1289814/tenor.gif",
			"$dance", "https://media1.giphy.com/media/eMOmLrDdmw520R8Sqw/giphy.gif",
			"jesus christ", "https://media2.giphy.com/media/orUDTj9Q5TMzTdB892/giphy.gif"
			);
	public static void main(String[] args) {
		client.getEventDispatcher().on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage)
		.filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
		.filter(message -> bildReactions.containsKey(message.getContent().toLowerCase()))
		.flatMap(message -> message.getChannel().block().createEmbed(embed -> {
			embed.setColor(Color.DISCORD_BLACK);
			embed.setImage(bildReactions.get(message.getContent()));
		})).subscribe();
	}
}
