package gta_text.npcs;

import gta_text.Server;
import gta_text.locations.Location;
import java.io.IOException;
import ssmith.lang.Functions;

public class GamblingLady extends NonPlayerCharacter {

    public static final String NAME = "GamblingLady";

    public GamblingLady(Location loc) throws IOException {
        super(NonPlayerCharacter.GAMBLING_LADY, NAME, loc);
    }

    protected void performRandomAction() throws IOException {
        int x = Functions.rnd(1, 3);
        switch(x) {
        case 1:
            this.say("This fucking machine never pays out!");
            break;
        case 2:
            this.say("I need a piss but this machine's gotta pay out soon.");
            break;
        case 3:
            Server.game.informOthersOfMiscAction(this, null, "You hear a clatter as coins appear at the bottom of the " + NAME + "'s slot machine.");
            //this.cash += Functions.rnd(1, 5);
            this.say("At fucking last!");
            break;
        default:
            this.say("Script?");
            break;
        }
    }

    protected String getRandomSayingWhenAttacked() {
        return "Fuck off you shitfaced bastard!";
    }

    public boolean spokenTo(GameCharacter charac, String msg, boolean only_them) throws IOException {
        if (only_them && charac instanceof PlayersCharacter) {
            this.sayTo(charac, "I hope you're having more luck than me cos these machines are pissing me off.");
            return true;
        }
        return super.spokenTo(charac, msg, only_them);
    }

}
