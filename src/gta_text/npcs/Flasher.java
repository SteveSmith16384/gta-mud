package gta_text.npcs;

import gta_text.locations.Location;
import java.io.IOException;

public class Flasher extends NonPlayerCharacter {

    public Flasher(Location loc) throws IOException {
        super(NonPlayerCharacter.FLASHER, "Flasher", loc);
    }

    protected void performRandomAction() throws IOException {
        if (this.getCurrentLocation().containsPlayer()) {
            this.say("Boo!");
            this.flash();
            this.removeFromGame();
        } else {
            this.say("Boo!");
        }
    }

    public void givenCash(GameCharacter char_from, int amt) throws IOException {
        this.sayTo(char_from, "Thanks, but I don't need it.  I'm actually a bank manager.");
    }

    protected String getRandomSayingWhenAttacked() {
        return "Alright!  No harm meant!";
    }

}

