package bots;
import discord4j.core.event.domain.guild.GuildUpdateEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import static bots.Main.*;

public class GuildJoinEvent{
    public static void main(String[] args){
        InputStreamReader inputStreamReader = new InputStreamReader(System.in);
        int test = 0;
        try{
            test = inputStreamReader.read();
        }catch(IOException e){
            e.printStackTrace();
        }
        System.out.println(test);

    }
}
