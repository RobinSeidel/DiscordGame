package bots;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;

import static bots.Main.*;

public class RoleHandler{

    public static void main(String[] args){

        //Default role on join: unauthenticated
        client.getEventDispatcher().on(MemberJoinEvent.class).subscribe(memberJoinEvent -> {
            System.out.println(memberJoinEvent.getMember().getDisplayName() + " joined");
            memberJoinEvent.getMember().addRole(Snowflake.of("816219559933247519")).block();
        });

        //When a user accepts the rules by reacting to any emote in the readme channel, remove role unauthenticated and
        //add roles Level1 and authenticated
        client.getEventDispatcher().on(ReactionAddEvent.class)
                .filter(messageChannel -> messageChannel.getChannel().block().getId().equals(Snowflake.of("816294645339914250")))
                .map(ReactionAddEvent::getMember)
                .subscribe(member ->{
                    if(member.isEmpty()) return;
                    member.get().removeRole(Snowflake.of("816219559933247519")).block();
                    member.get().addRole(Snowflake.of("816223848260173834")).block();
                    member.get().addRole(Snowflake.of("816223585580351499")).block();
                });

        client.onDisconnect().block();



    }
}
