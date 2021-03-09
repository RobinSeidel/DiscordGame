package bots.musicHelper;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.voice.AudioProvider;

public class MusicHandler{
    final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    final AudioPlayer player = playerManager.createPlayer();
    AudioProvider provider = new LavaPlayerAudioProvider(player);
    final TrackScheduler scheduler = new TrackScheduler(player);

    public MusicHandler(){
        playerManager.getConfiguration()
                .setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);
        AudioSourceManagers.registerRemoteSources(playerManager);
    }

    public AudioProvider loadUrlAndPlay(String url,int volume){
        playerManager.loadItem(url,scheduler);
        player.setVolume(volume);
        return provider;
    }

}
