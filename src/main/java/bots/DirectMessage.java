package bots;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.object.entity.Member;
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
                System.out.println(event.getCurrent().getMember().block().getDisplayName());
                event.getCurrent().getMember().block().getPrivateChannel().block().createMessage("Hallo").block();
            });
            client.onDisconnect().block();
    }
}
