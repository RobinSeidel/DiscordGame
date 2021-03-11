package bots;

import discord4j.common.util.Snowflake;
import java.util.Random;

/**
 *  Collection of Snowflake Ids, intended to improve readability and provide useful methods.
 *  Make shure to use Snowflakes.ENUM.getId() to get the Snowflake of this enum
 */

public enum Snowflakes{
    GUILD(816218743565713459L),
    README(816294645339914250L),
    TESTCHANNEL(816274161265279066L),
    ALLGEMEIN(816306019155312701L),
    DANKMEMES(817460065350189126L),
    WETTBEWERB(818244015957999636L),
    OLYMPIAPARK(816223463236435978L),
    MÜNCHNERFREIHEIT(816306786151301130L),
    ENGLISCHERGARTEN(816307063600840744L),
    CAMPUSGARCHING(816307147805687848L),
    MOZART(816436601500336168L),
    GAMECHANNELS(819133802486956062L),
    TEXTKANAELE(816218744073879593L),
    SPRACHKANAELE(816218744073879594L),
    UNAUTHENTICATED(816219559933247519L),
    AUTHENTICATED(816223585580351499L),
    LEVEL1(816223848260173834L),
    LEVEL2(816223876346413086L),
    LEVEL3(816223892120797194L),
    LEVEL4(816223907404316712L),
    LEVEL5(816223932339585045L),
    EVERYONE(816218743565713459L);

    private final Snowflake id;

    private static final Random random = new Random();
    private static final Snowflakes[] botVoiceChannels = new Snowflakes[]{OLYMPIAPARK,MÜNCHNERFREIHEIT,ENGLISCHERGARTEN,CAMPUSGARCHING};
    private static int oldId = random.nextInt(botVoiceChannels.length);
    private static int newId = oldId;

    Snowflakes(Long id){
        this.id = Snowflake.of(id);
    }

    /**
     *  @return a random VoiceChannel intended for bot use, never returns the same channel in a row
     */
    public static Snowflake returnRandomVoiceChannel(){
        Snowflake toReturn = botVoiceChannels[oldId].getId();
        while(newId == oldId)
            newId = random.nextInt(botVoiceChannels.length);
        oldId = newId;
        return toReturn;
    }

    public Snowflake getId(){
        return id;
    }

    @Override
    public String toString(){
        return id.asString();
    }
}
