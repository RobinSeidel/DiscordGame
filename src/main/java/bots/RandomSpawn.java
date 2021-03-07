package bots;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClient;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.VoiceChannel;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Random;

import static bots.Main.*;

public class RandomSpawn{
    public static void main(String[] args){
        Random random = new Random();
        long[] channels = {816223463236435978L, 816306786151301130L, 816307063600840744L, 816307147805687848L};
        LocalTime toWaitFor = LocalTime.now().plusSeconds(10);
        int oldId = random.nextInt(channels.length);
        int newId = oldId;
        while(true){
            if(LocalTime.now().isAfter(toWaitFor)){
                System.out.println("Attempting to join Channel");
                client.getChannelById(Snowflake.of(channels[oldId])).cast(VoiceChannel.class).flatMap(
                        voiceChannel -> voiceChannel.join(spec -> spec.setProvider(null))
                ).subscribe();
                while(newId == oldId)
                    newId = random.nextInt(channels.length);
                oldId = newId;
                toWaitFor = LocalTime.now().plusSeconds(10);
            }
        }
    }
}
