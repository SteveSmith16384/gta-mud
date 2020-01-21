package gta_text.npcs;

import java.io.IOException;

import ssmith.lang.Functions;
import gta_text.Server;
import gta_text.items.AK47;
import gta_text.locations.Location;

public class Soldier extends Law {

    private static final String NAME = "Soldier";

    public Soldier(Location loc) throws IOException {
        super(NonPlayerCharacter.SOLDIER, NAME, loc);

        this.addItem(new AK47());
}

    protected String getRandomSayingWhenAttacked() {
        return "You're a dead man!";
    }

    protected void performRandomAction() throws IOException {
        int x = Functions.rnd(1, 4);
        switch(x) {
        case 1:
            this.say("This city is under martial law!");
            break;
        case 2:
            this.say("They must be around here somewhere.");
            break;
        case 3:
            Server.game.informOthersOfMiscAction(this, null, "The soldier cocks his rifle.");
            break;
        case 4:
            this.say("Stay inside the buildings!");
            break;
        default:
            this.say("What are my lines?");
            break;
        }

    }


}
