package bots;

import bots.musicHelper.LavaPlayerAudioProvider;
import bots.musicHelper.TrackScheduler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.common.util.Snowflake;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;

import java.time.LocalTime;
import java.util.Random;

import static bots.Main.*;

public class RandomSpawn{
    private static final int timeDelay = 60;
    public static void main(String[] args){

        final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
        playerManager.getConfiguration()
                .setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        AudioSourceManagers.registerRemoteSources(playerManager);
        final AudioPlayer player = playerManager.createPlayer();
        AudioProvider provider = new LavaPlayerAudioProvider(player);
        final TrackScheduler scheduler = new TrackScheduler(player);

        Random random = new Random();
        long[] channels = {816223463236435978L, 816306786151301130L, 816307063600840744L, 816307147805687848L};
        LocalTime toWaitFor = LocalTime.now();
        int oldId = random.nextInt(channels.length);
        int newId = oldId;
        while(true){
            if(LocalTime.now().isAfter(toWaitFor)){
                System.out.println("Attempting to join Channel " + client
                        .getChannelById(Snowflake.of(channels[oldId])).cast(VoiceChannel.class).block().getName());
                VoiceConnection voiceConnection = client.getVoiceConnectionRegistry()
                        .getVoiceConnection(Snowflake.of("816218743565713459")).block();
                if(voiceConnection != null)
                    voiceConnection.disconnect().block();
                try{
                    Thread.sleep(1000);
                }catch(InterruptedException ignored){}
                final int temp = oldId;

                client.getChannelById(Snowflake.of(channels[oldId])).cast(VoiceChannel.class).flatMap(
                        voiceChannel -> {
                            playerManager.loadItem("https://www.youtube.com/watch?v=eBNcjvxLfFc&t=29s",scheduler);
                            player.setVolume(1);
                            client.getGuildById(Snowflake.of(816218743565713459L)).block().changeSelfNickname(client
                                    .getChannelById(Snowflake.of(channels[temp])).cast(VoiceChannel.class).block()
                                    .getName()).block();
                            return voiceChannel.join(spec -> spec.setProvider(provider));
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
