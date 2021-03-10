package bots;

import bots.musicHelper.MusicHandler;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.VoiceConnection;
import static bots.Snowflakes.*;
import static bots.Main.*;

/**
 *  When notified, spawns bot in a random VoiceChannel intended for bot use.
 */

public class RandomSpawn implements Runnable{

    @Override
    public void run(){
        MusicHandler musicHandler = new MusicHandler();
        while(true){
            Snowflake toJoin = returnRandomVoiceChannel();
            System.out.println("Attempting to join Channel " + client
                    .getChannelById(toJoin).cast(VoiceChannel.class).block().getName());

            //In case the bot is already connected, terminate this connection.
            VoiceConnection currentVoiceConnection = client.getVoiceConnectionRegistry()
                    .getVoiceConnection(GUILD.getId()).block();
            if(currentVoiceConnection != null)
                currentVoiceConnection.disconnect().block();

            //Wait one second before attempting to rejoin, this is mainly bc of Discords rate limiting
            try{
                Thread.sleep(1000);
            }catch(InterruptedException ignored){
            }


            //Get Channel and attempt to join it, playing music and changing the Nickname
            client.getChannelById(toJoin).cast(VoiceChannel.class).flatMap(
                    voiceChannel -> {
                        client.getGuildById(GUILD.getId()).block().changeSelfNickname(client
                                .getChannelById(toJoin).cast(VoiceChannel.class).block()
                                .getName()).block();
                        return voiceChannel.join(spec -> spec.setProvider(musicHandler.loadUrlAndPlay("https://www.youtube.com/watch?v=cF47sHTmd4I", 10)));
                    }
            ).subscribe();

            try{
                synchronized(Thread.currentThread()){
                    Thread.currentThread().wait();
                }
            }catch(InterruptedException ignored){}
        }
    }
}

