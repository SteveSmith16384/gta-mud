package gta_text.npcs;

import gta_text.locations.Location;
import java.io.IOException;
import ssmith.lang.Functions;

public class SecurityGuard extends Security {

    public SecurityGuard(Location loc) throws IOException {
        super(NonPlayerCharacter.SECURITY_GUARD, "SecurityGuard", loc);
    }

    protected void performRandomAction() throws IOException {
        int x = Functions.rnd(1, 2);
        switch(x) {
        case 1:
            this.say("Nearly the end of my shift.");
            break;
        case 2:
            this.say("I've got my eye on you.");
            break;
        default:
            this.say("Script?");
            break;
        }
    }

    protected String getRandomSayingWhenAttacked() {
        return "Right, that's it.";
    }

}
