package gta_text.npcs;

import java.io.IOException;

import gta_text.items.MeatCleaver;
import gta_text.locations.Location;
import gta_text.*;
import ssmith.lang.Functions;

public class Yakuza extends MemberOfGang {

    public static final String NAME = "Yakuza";

    public static int tot_yakuza = 0;

    public Yakuza(Location loc) throws IOException {
        super(NonPlayerCharacter.YAKUZA_NPC, NAME, loc);
        this.gang = GANG_YAKUZA;

        this.addItem(new MeatCleaver());

        tot_yakuza++;
    }

    public void removeFromGame() throws IOException {
        super.removeFromGame();
        tot_yakuza--;
    }

    public void process() throws Exception {
        super.process();
    }

    public void givenCash(GameCharacter char_from, int amt) throws IOException {
        this.sayTo(char_from, "Do I look like I need your money?  I am YAKUZA!.");
    }

    protected String getRandomSayingWhenAttacked() {
        return "You gonna die now!";
    }

    public boolean spokenTo(GameCharacter charac, String msg, boolean only_them) throws
    IOException {
        if (msg.toUpperCase().indexOf("JOB") >= 0 || msg.toUpperCase().indexOf("WORK") >= 0) {
            this.sayTo(charac, "You are joking right?  You are not Yakuza.");
            return true;
        }
        return super.spokenTo(charac, msg, only_them);
    }
    
    protected void performRandomAction() throws IOException {
        int x = Functions.rnd(1, 3);
        switch (x) {
        case 1:
            Server.game.informOthersOfMiscAction(this, null, "The " + NAME + " looks shiftily from left to right.");
            break;
        case 2:
            this.say("If I see any Mafia, I kill them.");
            break;
        case 3:
            Server.game.informOthersOfMiscAction(this, null, "The " + NAME + " polishes his weapon.");
            break;
        }
    }

}
