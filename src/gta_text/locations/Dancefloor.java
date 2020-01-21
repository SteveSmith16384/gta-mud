package gta_text.locations;

import java.io.IOException;
import gta_text.npcs.DancingGirl;
import gta_text.npcs.Hero;

public class Dancefloor extends Location {

    public Dancefloor(String no) throws IOException {
        super(no);

        new Hero(this); //todo - regen etc...
        new DancingGirl(this);
    }

    public void regenChars() throws IOException {
        if (this.containsCharacter(DancingGirl.NAME) == false) {
            new DancingGirl(this);
        }
    }

}
