/*
 * Created on 07-Jul-2006
 *
 */
package gta_text.items;

import gta_text.ServerConn;
import gta_text.npcs.GameCharacter;

import java.io.IOException;

import ssmith.lang.Functions;

public class DailyMail extends Item {

    public static final String NAME = "DailyMail";

    private String headline;

    public DailyMail() throws IOException {
        super(Item.DAILY_MAIL);

        headline = this.getHeadline();
    }

    public boolean use(GameCharacter character) throws IOException {
        character.sendMsgToPlayer(ServerConn.NORMAL_COL, getDesc());
        return true;
    }

    public String getDesc() {
        return "It's the latest edition of the Daily Mail newspaper.  The headline is '" + headline + "'.";
    }

    private String[] p0 = {"", "", "Minister says", "report reveals", "opinion poll shows", "Unknown", "Surprise"};
    private String[] p1 = {"immigration", "senator", "Tax hike", "womens lib", "traffic blackspots", "earthquake"};
    private String[] p2 = {"causes", "leads to", "affected by", "disrupts"};
    private String[] p3 = {"rise in crime", "road deaths", "rude behaviour", "tax increase", "alcoholism", "war"};
    private String[] p4 = {"", "", "Among pensioners", "among teens", "among the poor", "among the rich"};

    private String getHeadline() {
        int a = Functions.rnd(0, p0.length-1);
        int b = Functions.rnd(0, p1.length-1);
        int c = Functions.rnd(0, p2.length-1);
        int d = Functions.rnd(0, p3.length-1);
        int e = Functions.rnd(0, p4.length-1);

        return p0[a] + " " + p1[b] + " " + p2[c] + " " + p3[d] + " " + p4[e];
    }
}
