package bots;

import bots.Penguins.PenguinCollection;
import bots.funAdditions.EntertainerBot;
import bots.users.PlayerCollection;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;

public class Main {
	public static GatewayDiscordClient client = DiscordClientBuilder.create("ODE2MjMxNTg2NTY2ODk3Njc0.YD385w.blC0wtWw41lCYqoz7nY-0FuYriE").build()
			.login().block();
	public static final PenguinCollection PINGU_COLLECTION = new PenguinCollection();
	public static final PlayerCollection PLAYER_COLLECTION = new PlayerCollection();
	
	public static void main(String[] args) {
		new Thread(() -> Snake.main(null)).start();
		new Thread(() -> EntertainerBot.main(null)).start();
		new Thread(() -> RandomSpawn.main(null)).start();
		new Thread(() -> DirectMessage.main(null)).start();
	}

}
