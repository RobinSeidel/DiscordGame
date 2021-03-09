package bots;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.entity.channel.VoiceChannel;

import javax.rmi.ssl.SslRMIClientSocketFactory;

import static bots.Main.*;

public class DirectMessage{
    public static void main(String[] args){
            client.getEventDispatcher().on(VoiceStateUpdateEvent.class).subscribe(event -> {
                if(!event.isJoinEvent() && !event.isMoveEvent())
                    return;
                event.getCurrent().getChannel().block().getVoiceStates().flatMap(voiceState -> voiceState.getMember())
                        .doOnNext(System.out::println).subscribe();
            });
            client.onDisconnect().block();
    }
}
