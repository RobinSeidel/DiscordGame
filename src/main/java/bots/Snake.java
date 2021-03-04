package bots;

import java.awt.Point;
import java.util.Arrays;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.reaction.ReactionEmoji;

public class Snake {
	private static GatewayDiscordClient client = DiscordClientBuilder.create("ODE2MjMxNTg2NTY2ODk3Njc0.YD385w.blC0wtWw41lCYqoz7nY-0FuYriE").build()
			.login().block();
	public static final ReactionEmoji UP = ReactionEmoji.unicode("⬆️");
	public static final ReactionEmoji DOWN = ReactionEmoji.unicode("⬇️");
	public static final ReactionEmoji LEFT = ReactionEmoji.unicode("⬅️");
	public static final ReactionEmoji RIGHT = ReactionEmoji.unicode("➡️");
	private final char[][] spielFeld;
	private Point pinguPos;
	private int direction;
	private final User player;

	public Snake(User player,Message trigger) {	
		spielFeld = new char[10][10];
		for (char[] line : spielFeld)
			Arrays.fill(line, ' ');
		pinguPos = new Point(5, 5);
		this.player=player;
		playGame(startGame(trigger));
		
	}

	public static void main(String[] args) {
		client.getEventDispatcher().on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage)
				.filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
				.filter(message -> message.getContent().equalsIgnoreCase("!snake")).map(message -> {
					new Snake(message.getAuthor().get(),message);
					return message;
				}).subscribe();
		
		client.onDisconnect().block();
	}

	private Message startGame(Message message) {
		message.addReaction(ReactionEmoji.unicode("🐍")).block();
		Message[] game = new Message[1];
		message.getChannel().map(channel -> {
			game[0] = channel.createMessage("Welcome to the Game of snake").block();
			return game[0];
		}).block();
		return game[0];
	}

	private String parseGame(char[][] field) {
		StringBuilder s = new StringBuilder();
		Arrays.stream(field).forEach(line -> {
			for (char pos : line)
				switch (pos) {
				case 'p' -> s.append(":penguin:");
				case ' ' -> s.append(":black_circle:");
				default -> s.append(":new_moon_with_face:");
				}
			s.append(System.lineSeparator());
		});
		return s.toString();
	}

	private void playGame(Message field) {
		field.addReaction(RIGHT).block();
		field.addReaction(LEFT).block();
		field.addReaction(UP).block();
		field.addReaction(DOWN).block();
		repaint(field);
		while (true){
			if(field.getReactors(RIGHT).any(user-> user.equals(player)).block()) {
				direction = 1;
				field.removeReaction(RIGHT, player.getId()).block();
			}
			if(field.getReactors(UP).any(user-> user.equals(player)).block()) {
				direction =2;
				field.removeReaction(UP, player.getId()).block();
			}
			if(field.getReactors(LEFT).any(user-> user.equals(player)).block()) {
				direction = 3;
				field.removeReaction(LEFT, player.getId()).block();
			}
			if(field.getReactors(DOWN).any(user-> user.equals(player)).block()) {
				direction = 0;
				field.removeReaction(DOWN, player.getId()).block();
			}
			try {
			step();
			} catch(ArrayIndexOutOfBoundsException e) {
				break;
			}
			repaint(field);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
		}
		field.getChannel().flatMap(channel->channel.createMessage("GAME OVER")).block();
	}

	private void step() {
		spielFeld[pinguPos.x][pinguPos.y] = ' ';
		switch (direction) {
		case 0 -> pinguPos.x++;
		case 1 -> pinguPos.y++;
		case 2 -> pinguPos.x--;
		case 3 -> pinguPos.y--;
		}
		spielFeld[pinguPos.x][pinguPos.y] = 'p';
	}

	private void repaint(Message game) {
		game.edit(m -> m.setContent(parseGame(spielFeld))).block();
	}
}
