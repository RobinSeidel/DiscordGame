import discord4j.common.util.Snowflake;
import discord4j.core.*;
import discord4j.core.event.domain.guild.MemberJoinEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.event.domain.message.ReactionAddEvent;
import discord4j.core.object.entity.Member;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.Role;
import discord4j.core.object.entity.User;
import discord4j.core.object.reaction.ReactionEmoji;


public class TestBot{
    public static void main(String[] args){
        System.out.println("Test");
        GatewayDiscordClient client = DiscordClientBuilder.create("ODE2MjMxNTg2NTY2ODk3Njc0.YD385w.br69s25LUjx-SdMa5lsJ3IBqW4g")
                .build()
                .login()
                .block();
        client.getEventDispatcher().on(ReadyEvent.class)
                .subscribe(event -> {
                    final User self = event.getSelf();
                    System.out.printf(
                            "Logged in as %s#%s%n", self.getUsername(), self.getDiscriminator()
                    );
                });

        client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(message -> message.getAuthor().map(user -> !user.isBot()).orElse(false))
                .filter(message -> message.getContent().equalsIgnoreCase("!IchHabDeineMutterImArschGefickt"))
                .flatMap(Message::getChannel)
                .flatMap(channel -> channel.createMessage("Geh nicht zu Mutter du kleiner Hurensohn " +
                        "\n https://www.youtube.com/watch?v=vW2DrSqXQME"))
                .subscribe();

        client.getEventDispatcher().on(MemberJoinEvent.class).subscribe(memberJoinEvent -> {
            memberJoinEvent.getMember().addRole(Snowflake.of(816219559933247519L)).block();
        });

        client.getEventDispatcher().on(ReactionAddEvent.class).subscribe(ReactionAddEvent -> {
            if(ReactionAddEvent.getChannel().block().getId().equals(Snowflake.of(816294645339914250L))){
                ReactionAddEvent.getMember().get().addRole(Snowflake.of(816223585580351499L)).block();
                ReactionAddEvent.getMember().get().addRole(Snowflake.of(816223848260173834L)).block();
                ReactionAddEvent.getMember().get().removeRole(Snowflake.of(816219559933247519L)).block();
            }
        });
        client.onDisconnect().block();



    }


}

