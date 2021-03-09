package bots;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import static bots.Main.*;

public class TestBot{
    public static void main(String[] args){
        System.out.println("Test");
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
                .filter(message -> message.getContent().equalsIgnoreCase("!dm"))
                .map(message->message.getAuthor().get())
                .map(user->user.getPrivateChannel().block().createMessage("was geht bro").block())
                .subscribe();

        client.onDisconnect().block();
    }
}

