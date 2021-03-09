package bots;

import bots.funAdditions.ImageBot;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
public class Main {
	public static GatewayDiscordClient client = DiscordClientBuilder.create("ODE2MjMxNTg2NTY2ODk3Njc0.YD385w.blC0wtWw41lCYqoz7nY-0FuYriE").build()
			.login().block();

	public static void main(String[] args) {
		new Thread(() -> TestingBot.main(null)).start();
		new Thread(() -> Snake.main(null)).start();
		new Thread(() -> ImageBot.main(null)).start();
		new Thread(() -> RandomSpawn.main(null)).start();
		new Thread(() -> RoleHandler.main(null)).start();
		new Thread(() -> DirectMessage.main(null)).start();
	}

}
