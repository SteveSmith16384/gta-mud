package gta_text.npcs;

import java.io.IOException;

import gta_text.items.Knife;
import gta_text.locations.Location;
import gta_text.*;
import ssmith.lang.Functions;

public class Mafioso extends MemberOfGang {

    public static final String NAME = "Mafioso";

    public static int tot_mafia = 0;

    public Mafioso(Location loc) throws IOException {
        super(NonPlayerCharacter.MAFIA_NPC, NAME, loc);
        this.gang = GANG_MAFIA;

        this.addItem(new Knife());

        tot_mafia++;
    }

    public void removeFromGame() throws IOException {
        super.removeFromGame();
        tot_mafia--;
    }

    public void process() throws Exception {
        super.process();
    }

    public void givenCash(GameCharacter char_from, int amt) throws IOException {
        this.sayTo(char_from, "Do I look like I need your money?  I am MAFIA!");
    }

    protected String getRandomSayingWhenAttacked() {
        return "You didn't wanna do dat!";
    }

    protected void performRandomAction() throws IOException {
        int x = Functions.rnd(1, 3);
        switch (x) {
        case 1:
            Server.game.informOthersOfMiscAction(this, null, "The " + NAME + " checks himself in the mirror.");
            break;
        case 2:
            this.say("If I see any Yakuza, I kill him.");
            break;
        case 3:
            Server.game.informOthersOfMiscAction(this, null, "The " + NAME + " polishes his weapon.");
            break;
        }
    }

}
