/*
 * Created on 15-May-2006
 *
 */
package gta_text.npcs;

import gta_text.locations.Location;
import java.io.IOException;
import gta_text.items.Pistol;
import ssmith.lang.Functions;
import gta_text.ServerConn;

public class Bouncer extends Security {

    public Bouncer(Location loc) throws IOException {
        super(NonPlayerCharacter.BOUNCER, "Bouncer", loc);

        this.addItem(new Pistol());
    }

    public boolean spokenTo(GameCharacter charac, String msg, boolean only_them) throws IOException {
        if (only_them) {
            charac.sendMsgToPlayer(ServerConn.ACTION_COL, "The bouncer just looks at you like you're something he scraped off his shoe.");
            return true;
        }
        return super.spokenTo(charac, msg, only_them);
    }

    protected void performRandomAction() throws IOException {
        int x = Functions.rnd(1, 3);
        switch(x) {
        case 1:
            this.say("If you're name's not on the list, you're not coming in.");
            break;
        case 2:
            this.say("Let's avoid the bloodbath we had last night, eh lads?");
            break;
        case 3:
            this.say("I've got my eye on you.");
            break;
        default:
            this.say("Script?");
            break;
        }
    }

    protected String getRandomSayingWhenAttacked() {
        return "Come on then, punk.";
    }

}
