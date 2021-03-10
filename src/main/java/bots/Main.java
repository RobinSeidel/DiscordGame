package bots;

import bots.Penguins.PenguinCollection;
import bots.funAdditions.EntertainerBot;
import bots.users.PlayerCollection;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;

import javax.management.relation.Role;
import java.time.LocalTime;

public class Main{
    public static GatewayDiscordClient client = DiscordClientBuilder
            .create("ODE2MjMxNTg2NTY2ODk3Njc0.YD385w.blC0wtWw41lCYqoz7nY-0FuYriE").build().login().block();
    public static final PenguinCollection PINGU_COLLECTION = new PenguinCollection();
    public static final PlayerCollection PLAYER_COLLECTION = new PlayerCollection();

    public static void main(String[] args){
        new Thread(new GarbageCollector()).start();
        new Thread(() -> RoleHandler.main(null)).start();
        new Thread(() -> Snake.main(null)).start();
        new Thread(() -> EntertainerBot.main(null)).start();
        new Thread(() -> DirectMessage.main(null)).start();

        Thread randomSpawnThread = new Thread(new RandomSpawn());
        randomSpawnThread.start();
        LocalTime toWaitFor = LocalTime.now();

        while(true){
            if(LocalTime.now().isAfter(toWaitFor)){
                synchronized(randomSpawnThread){
                    //Notifying the randomSpawnThread causes the bot to join a random VoiceChannel
                    randomSpawnThread.notify();
                }
                toWaitFor = LocalTime.now().plusSeconds(60);
            }
            try{
                Thread.sleep(10_000);
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

}
