package bots;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.TextChannel;

import static bots.Main.*;

public class DirectMessage{
    public static void main(String[] args){
        while(true){
            client.getEventDispatcher().on(VoiceStateUpdateEvent.class).subscribe(event -> {
                if(!event.isJoinEvent())
                    return;
                if(event.getCurrent().getChannel().block().getId().equals(
                        client.getVoiceConnectionRegistry().getVoiceConnection(Snowflake.of("816218743565713459")).block().getChannelId()))
                    client.getChannelById(event.getCurrent().getMember().block().getId()).cast(TextChannel.class).block().createMessage("Hallo").block();
                return;
            });
            client.onDisconnect().block();
        }
    }


}
