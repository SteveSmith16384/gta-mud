package gta_text.mission;

import java.io.IOException;
import java.util.ArrayList;

import gta_text.Server;
import gta_text.locations.AbandonedWarehouse;
import gta_text.npcs.Addict;
import ssmith.lang.Functions;
import gta_text.npcs.GameCharacter;
import gta_text.items.CashCard;

public class GetATMCard extends Mission {

    private static final int NO_OF_ADDICTS=3;

    public GetATMCard(ArrayList missions) {
        super(missions);
    }

    public void reset() throws IOException {
        AbandonedWarehouse loc = Server.game.abandoned_warehouse;

        // Create chars
        for(int i=0 ; i<NO_OF_ADDICTS ; i++) {
            if (loc.containsCharacter("ADDICT" + (i+1)) == false) {
                new Addict("Addict" + (i+1), loc);
            }
        }

        int cc = Functions.rnd(1, NO_OF_ADDICTS);
        GameCharacter charac = loc.getCharacter("ADDICT" + cc);
        charac.addItem(new CashCard());
    }

    public void setCompleted() throws IOException {
        this.completed = true;
    }

}
