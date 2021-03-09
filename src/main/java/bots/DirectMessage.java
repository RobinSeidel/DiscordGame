package bots;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.entity.channel.VoiceChannel;

import javax.rmi.ssl.SslRMIClientSocketFactory;

import java.util.List;

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
                            .getVoiceConnection(Snowflake.of("816218743565713459"))
                            .block().getChannelId().block()).cast(VoiceChannel.class)
                            .block().getVoiceStates()
                            .flatMap(VoiceState::getMember)
                            .collectList().block().contains(member)
                )
                .doOnNext(member -> member.getPrivateChannel().block().createMessage("Was geht").block())
                .subscribe();

        client.onDisconnect().block();
    }
}
