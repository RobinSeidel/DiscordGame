package bots;

import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.TextChannel;
import java.time.Instant;
import static bots.Main.*;
import static bots.Snowflakes.*;

/**
 * Deletes text channels with category Game-Channels if the last message was written 24h or more ago.
 */

public class GarbageCollector implements Runnable{

    @Override
    public void run(){
        while(true){
            try{
                client.getGuildChannels(GUILD.getId()).onErrorStop().subscribe(guildChannel -> {
                            if(guildChannel.getType().equals(Channel.Type.GUILD_TEXT))
                                if(((TextChannel) guildChannel).getCategory().block().getId()
                                        .equals(GAMECHANNELS.getId()))
                                    if(((TextChannel) guildChannel).getLastMessage().block() != null)
                                        if(((TextChannel) guildChannel).getLastMessage().block().getTimestamp()
                                                .isBefore(Instant.now().minusSeconds(60L * 60L * 24L)))
                                            guildChannel.delete().block();
                        }
                );
                try{
                    Thread.sleep(60_000);
                }catch(InterruptedException ignored){
                }
            }catch(NullPointerException ignored){
            }
        }
    }
}
