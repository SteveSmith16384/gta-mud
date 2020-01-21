package gta_text.npcs;

import gta_text.locations.Location;
import java.io.IOException;
import ssmith.lang.Functions;

public class Croupier extends NonPlayerCharacter {

    public Croupier(Location loc) throws IOException {
        super(NonPlayerCharacter.CROUPIER, "Croupier", loc);
    }

    public void givenCash(GameCharacter char_from, int amt) throws IOException {
        this.sayTo(char_from, "Please put your money on red or black");
        this.give("" + amt, char_from);
    }

    public void otherCharacterEntered(GameCharacter character, Location old_loc) throws IOException {
        if (character != this.enemy) {
            this.sayTo(character, "Please place your bets.");
        }
    }

    protected void performRandomAction() throws IOException {
        int x = Functions.rnd(1, 2);
        switch(x) {
        case 1:
            this.say("Place your bets please.");
            break;
        case 2:
            this.say("Please place your money on red or black.");
            break;
        }
    }

    protected String getRandomSayingWhenAttacked() {
        return "I'm not as weak as I look!";
    }
}
