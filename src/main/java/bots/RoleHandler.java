package bots;

import bots.users.PlayerCollection;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import static bots.Snowflakes.*;
import static bots.Main.*;


/**
 *  Handles UserJoinEvents and rule-accepting
 */

public class RoleHandler{

    public static void main(String[] args){

        //Default role on join: unauthenticated
        client.getEventDispatcher().on(MemberJoinEvent.class).subscribe(memberJoinEvent -> {
            System.out.println(memberJoinEvent.getMember().getDisplayName() + " joined");
            memberJoinEvent.getMember().addRole(UNAUTHENTICATED.getId()).block();
        });

        //When a user accepts the rules by reacting to any emote in the readme channel, remove role unauthenticated and
        //add roles Level1 and authenticated and register player in PLAYER_COLLECTION
        client.getEventDispatcher().on(ReactionAddEvent.class)
                .filter(messageChannel -> messageChannel.getChannel().block().getId().equals(README.getId()))
                .map(ReactionAddEvent::getMember)
                .subscribe(member ->{
                    if(member.isEmpty()) return;
                    member.get().removeRole(UNAUTHENTICATED.getId()).block();
                    member.get().addRole(LEVEL1.getId()).block();
                    member.get().addRole(AUTHENTICATED.getId()).block();
                    PLAYER_COLLECTION.registerNewPlayer(member.get());
                });

        client.onDisconnect().block();
    }
}
