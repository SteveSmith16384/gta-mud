package gta_text.npcs;

import gta_text.locations.Location;
import java.io.IOException;

import gta_text.items.CashCard;
import gta_text.items.Pistol;
import ssmith.lang.Functions;

public class Addict extends MemberOfGroup {

    public Addict(String name, Location loc) throws IOException {
        super(NonPlayerCharacter.ADDICT, name, loc, NonPlayerCharacter.ADDICT, true);

        this.addItem(new Pistol());
    }

    public void process() throws Exception {
        super.process();

        this.ensureWantedLevel(1);
        
        if (this.getCurrentLocation().containsItem(CashCard.NAME)) {
            this.pickupItem(CashCard.NAME);
        }
    }

    public void givenCash(GameCharacter char_from, int amt) throws IOException {
        this.sayTo(char_from, "Thanks man.  Have you got any more?  I need a hit.");
    }

    protected void performRandomAction() throws IOException {
        int x = Functions.rnd(1, 3);
        switch(x) {
        case 1:
            this.say("You got any?");
            break;
        case 2:
            this.say("Hey man, I'm desperate.");
            break;
        case 3:
            this.say("Hey man, you got any?");
            break;
        default:
            this.say("I need a script desperately.");
            break;
        }
    }

    protected String getRandomSayingWhenAttacked() {
        return "Hey!";
    }

}
