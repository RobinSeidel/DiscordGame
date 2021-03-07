package bots;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.playback.NonAllocatingAudioFrameBuffer;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.UserUpdateEvent;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.voice.AudioProvider;
import discord4j.voice.VoiceConnection;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.swing.text.StyleContext;
import java.util.*;
import java.util.function.Function;

public class TestMusicBot{

    private static final Map<String, Function<MessageCreateEvent, Mono<Void>>> commands = new HashMap<>();

    static{
        commands.put("isOnline", event -> event.getMessage().getChannel().flatMap(channel -> channel.createMessage("Yes!")).then());
    }

    public static void main(String[] args){
        // Creates AudioPlayer instances and translates URLs to AudioTrack instances
        final AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

        // Optimization
        playerManager.getConfiguration()
                .setFrameBufferFactory(NonAllocatingAudioFrameBuffer::new);

        // Allow playerManager to parse remote sources like YouTube links
        AudioSourceManagers.registerRemoteSources(playerManager);

        // Create an AudioPlayer so Discord4J can receive audio data
        final AudioPlayer player = playerManager.createPlayer();

        // We will be creating bots.LavaPlayerAudioProvider in the next step
        AudioProvider provider = new LavaPlayerAudioProvider(player);

        commands.put("join", event -> Mono.justOrEmpty(event.getMember())
                .flatMap(Member::getVoiceState)
                .flatMap(VoiceState::getChannel)
                // join returns a VoiceConnection which would be required if we were
                // adding disconnection features, but for now we are just ignoring it.
                .flatMap(channel -> channel.join(spec -> spec.setProvider(provider)))
                .then());

        final TrackScheduler scheduler = new TrackScheduler(player);
        commands.put("play", event -> Mono.justOrEmpty(event.getMessage().getContent())
                .map(content -> Arrays.asList(content.split(" ")))
                .doOnNext(command -> playerManager.loadItem(command.get(1), scheduler))
                .then());

        GatewayDiscordClient client = DiscordClientBuilder.create("ODE2MjMxNTg2NTY2ODk3Njc0.YD385w.blC0wtWw41lCYqoz7nY-0FuYriE")
                .build()
                .login()
                .block();

        commands.put("createChannel", event -> Mono.just(event.getMessage().getContent())
                .map(content -> {
                    System.out.println(content);
                    return Arrays.asList(content.split(" "));
                })
                .doOnNext(command ->
                        client.getGuildById(Snowflake.of("816218743565713459")).block().createVoiceChannel(spec -> {
                            try{
                                int size = Math.abs(Integer.parseInt(command.get(2))) <= 100 &&
                                        Math.abs(Integer.parseInt(command.get(2))) > 0 ?
                                        Math.abs(Integer.parseInt(command.get(2))) : 10;
                                String name = command.get(1);
                                spec.setName(name);
                                spec.setUserLimit(size);
                                spec.setParentId(Snowflake.of("817469237828255745"));
                            }catch(IndexOutOfBoundsException | NumberFormatException e){
                                System.out.println("Error wrong Format");
                                event.getMessage().getChannel().flatMap(channel -> channel.createMessage("Wrong Format: use !createChannel <Name> <Size>")).block();
                            }
                        }).block()).then());


        client.getEventDispatcher().on(MessageCreateEvent.class).
                flatMap(event -> Mono.just(event.getMessage().
                        getContent()).
                        flatMap(content -> Flux.fromIterable(commands.entrySet()).
                                // We will be using ! as our "prefix" to any command in the system..
                                        filter(entry -> content.startsWith('!' + entry.getKey())).
                                        flatMap(entry -> entry.getValue().apply(event)).
                                        next())).subscribe();

        client.getEventDispatcher().
                on(VoiceStateUpdateEvent.class).
                filter(VoiceStateUpdateEvent::isJoinEvent).
                map(VoiceStateUpdateEvent::getCurrent).
                filter(voiceState -> !voiceState.getMember().block().isBot()).
                flatMap(VoiceState::getChannel).
                filter(voiceChannel -> voiceChannel.getId().
                        equals(Snowflake.of("816436601500336168"))).
                flatMap(channel ->{
                    playerManager.loadItem("https://www.youtube.com/watch?v=dQw4w9WgXcQ", scheduler);
                    System.out.println("Someone joined Mozart Channel");
                    return channel.join(voiceChannelJoinSpec -> voiceChannelJoinSpec.setProvider(provider));
                })
                .subscribe();

        client.onDisconnect().block();
    }

}