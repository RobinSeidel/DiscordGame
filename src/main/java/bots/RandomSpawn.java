package bots;

import bots.musicHelper.MusicHandler;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.VoiceConnection;

import java.time.LocalTime;
import java.util.Random;

import static bots.Main.*;

public class RandomSpawn{
    private static final int timeDelay = 60;
    public static void main(String[] args){
        Random random = new Random();
        MusicHandler musicHandler = new MusicHandler();
        long[] channels = {816223463236435978L, 816306786151301130L, 816307063600840744L, 816307147805687848L};
        LocalTime toWaitFor = LocalTime.now();
        int oldId = random.nextInt(channels.length);
        int newId = oldId;
        while(true){
            if(LocalTime.now().isAfter(toWaitFor)){

                System.out.println("Attempting to join Channel " + client
                        .getChannelById(Snowflake.of(channels[oldId])).cast(VoiceChannel.class).block().getName());

                //In case the bot is already connected, terminate this connection.
                VoiceConnection currentVoiceConnection = client.getVoiceConnectionRegistry()
                        .getVoiceConnection(Snowflake.of("816218743565713459")).block();
                if(currentVoiceConnection != null)
                    currentVoiceConnection.disconnect().block();

                //Wait one second before attempting to rejoin, this is mainly bc of Discords rate limiting
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException ignored){}

                //Kinda janky but using a var inside a lambda requires it to be final
                final int temp = oldId;

                //Get Channel and attempt to join it, playing music and changing the Nickname
                client.getChannelById(Snowflake.of(channels[oldId])).cast(VoiceChannel.class).flatMap(
                        voiceChannel -> {
                            client.getGuildById(Snowflake.of(816218743565713459L)).block().changeSelfNickname(client
                                    .getChannelById(Snowflake.of(channels[temp])).cast(VoiceChannel.class).block()
                                    .getName()).block();
                            return voiceChannel.join(spec -> spec.setProvider(musicHandler.loadUrlAndPlay("https://www.youtube.com/watch?v=vfc42Pb5RA8",10)));
                        }
                ).subscribe();


                while(newId == oldId)
                    newId = random.nextInt(channels.length);
                oldId = newId;
                toWaitFor = LocalTime.now().plusSeconds(timeDelay);
            }
        }
    }
}
