package bots;

import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.entity.channel.VoiceChannel;

import static bots.Snowflakes.*;
import static bots.Main.*;

public class DirectMessage{
    public static void main(String[] args){
        client.getEventDispatcher().on(VoiceStateUpdateEvent.class)
                .filter(event -> event.isJoinEvent() || event.isMoveEvent())
                .map(VoiceStateUpdateEvent::getCurrent)
                .flatMap(VoiceState::getMember)
                .filter(member -> !member.isBot())
                .filter(member ->
                        client.getChannelById(client.getVoiceConnectionRegistry()
                                .getVoiceConnection(GUILD.getId())
                                .block().getChannelId().block()).cast(VoiceChannel.class)
                                .block().getVoiceStates()
                                .flatMap(VoiceState::getMember)
                                .collectList().block().contains(member)
                )
                .doOnNext(member -> {
                    client.getGuildById(GUILD.getId()).block().getChannels()
                            .filter(guildChannel -> guildChannel.getType().equals(Channel.Type.GUILD_TEXT))
                            .map(guildChannel -> ((TextChannel) guildChannel))
                            .filter(textChannel -> textChannel.getCategory()
                                    .block().getId().equals(GAMECHANNELS.getId()))
                            .doOnNext(System.out::println).subscribe();


                })
                .subscribe();

        client.onDisconnect().block();
    }
}
