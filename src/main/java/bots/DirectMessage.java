package bots;

import bots.Penguins.Penguin;
import discord4j.core.event.domain.VoiceStateUpdateEvent;
import discord4j.core.object.ExtendedPermissionOverwrite;
import discord4j.core.object.PermissionOverwrite;
import discord4j.core.object.VoiceState;
import discord4j.core.object.entity.channel.Channel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.core.object.entity.channel.VoiceChannel;
import discord4j.discordjson.json.ImmutableOverwriteData;
import discord4j.discordjson.json.OverwriteData;
import discord4j.rest.util.Color;
import discord4j.rest.util.Permission;
import discord4j.rest.util.PermissionSet;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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
                    List<String> channels = client.getGuildById(GUILD.getId()).block().getChannels()
                            .filter(guildChannel -> guildChannel.getType().equals(Channel.Type.GUILD_TEXT))
                            .map(guildChannel -> ((TextChannel) guildChannel))
                            .filter(textChannel -> textChannel.getCategory()
                                    .block().getId().equals(GAMECHANNELS.getId()))
                            .map(textChannel -> textChannel.getName())
                            .collectList().block();

                    if(!channels.contains(member.getUsername().toLowerCase(Locale.ROOT).replace(' ','-'))){
                        client.getGuildById(GUILD.getId()).block().createTextChannel(textChannelCreateSpec -> {
                                    textChannelCreateSpec.setName(member.getUsername());
                                    textChannelCreateSpec.setParentId(GAMECHANNELS.getId());
                                    textChannelCreateSpec.setTopic("Dieser Kanal ist nur fuer dich sichtbar. Wenn du 24h nicht aktiv warst wird er geloescht. Sobald du wieder aktiv bist, wird automatisch ein neuer Kanal erstellt.");
                                    PermissionOverwrite everyone = PermissionOverwrite.forRole(EVERYONE.getId(),PermissionSet.of(0),PermissionSet.of(1024));
                                    PermissionOverwrite user = PermissionOverwrite.forMember(member.getId(),PermissionSet.of(67492928),PermissionSet.of(0));
                                    textChannelCreateSpec.setPermissionOverwrites(Set.of(everyone,user));
                                }
                        ).block().createMessage("Hey " +member.getMention() + " a wild penguin appeared").block().getChannel().block().createEmbed(embed -> {
                            Penguin randy = PINGU_COLLECTION.getRandomPenguin(2);
                            System.out.println("Spawning " + randy.getName());
                            embed.setColor(Color.DISCORD_BLACK);
                            embed.setTitle(randy.getName());
                            embed.setImage(randy.getImageUrl());
                        }).block();
                    }
                })
                .subscribe();

        client.onDisconnect().block();
    }
}
