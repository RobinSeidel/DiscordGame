package bots;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.reaction.ReactionEmoji;
import static bots.Main.*;

public class Snake {

	public static final ReactionEmoji UP = ReactionEmoji.unicode("\u2B06");
	public static final ReactionEmoji DOWN = ReactionEmoji.unicode("\u2B07");
	public static final ReactionEmoji LEFT = ReactionEmoji.unicode("\u2B05");
	public static final ReactionEmoji RIGHT = ReactionEmoji.unicode("\u27A1");
	private final char[][] spielFeld;
	private Point pinguPos;
	private final List<Point> body;
	private int direction;
	private final User player;
	private Random randy = new Random();

	public Snake(User player, Message trigger) {
		spielFeld = new char[10][10];
		for (char[] line : spielFeld)
			Arrays.fill(line, ' ');
		pinguPos = new Point(5, 5);
		this.player = player;
		body = new ArrayList<>();
		playGame(startGame(trigger));

	}

	public static void main(String[] args) {
		System.out.println("Snake startet");
		client.getEventDispatcher().on(MessageCreateEvent.class).map(MessageCreateEvent::getMessage)
				.filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
				.filter(message -> message.getContent().equalsIgnoreCase("!snake")).map(message -> {
					System.out.println("Erreicht");
					new Snake(message.getAuthor().get(), message);
					return message;
				}).subscribe();

		client.onDisconnect().block();
	}

	private Message startGame(Message message) {
		message.addReaction(ReactionEmoji.unicode("\uD83D\uDC0D")).block();
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
				case 'b' -> s.append(":white_circle:");
				case 'a' -> s.append(":red_circle:");
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
		while (true) {
			if (field.getReactors(RIGHT).any(user -> user.equals(player)).block()) {
				direction = 1;
				field.removeReaction(RIGHT, player.getId()).block();
			}
			if (field.getReactors(UP).any(user -> user.equals(player)).block()) {
				direction = 2;
				field.removeReaction(UP, player.getId()).block();
			}
			if (field.getReactors(LEFT).any(user -> user.equals(player)).block()) {
				direction = 3;
				field.removeReaction(LEFT, player.getId()).block();
			}
			if (field.getReactors(DOWN).any(user -> user.equals(player)).block()) {
				direction = 0;
				field.removeReaction(DOWN, player.getId()).block();
			}
			try {
				step();
			} catch (ArrayIndexOutOfBoundsException e) {
				break;
			}
			repaint(field);
			if (body.contains(pinguPos) && !body.get(0).equals(pinguPos))
				break;
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
			}
		}
		field.getChannel().flatMap(channel -> channel.createMessage("GAME OVER")).block();
	}

	private Point getRandomApplePos() {
		Point apple;
		do {
			apple = new Point(randy.nextInt(spielFeld.length), randy.nextInt(spielFeld.length));
		} while (body.contains(apple) || pinguPos.equals(apple));
		return apple;
	}

	private void moveBody() {
		body.add(0, new Point(pinguPos.x, pinguPos.y));
		body.remove(body.size() - 1);
	}

	private void step() {
		moveBody();
		spielFeld[pinguPos.x][pinguPos.y] = ' ';
		switch (direction) {
		case 0 -> pinguPos.x++;
		case 1 -> pinguPos.y++;
		case 2 -> pinguPos.x--;
		case 3 -> pinguPos.y--;
		}
		if (spielFeld[pinguPos.x][pinguPos.y] == 'a') {
			body.add(new Point(pinguPos.x, pinguPos.y));
			Point apple = getRandomApplePos();
			spielFeld[apple.x][apple.y] = 'a';
		}
		body.stream().forEach(point -> {
			spielFeld[point.x][point.y] = 'b';
		});
		spielFeld[pinguPos.x][pinguPos.y] = 'p';
	}

	private void repaint(Message game) {
		game.edit(m -> m.setContent(parseGame(spielFeld))).block();
	}
}
