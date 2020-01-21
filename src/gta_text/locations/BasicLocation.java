package gta_text.locations;

import gta_text.npcs.Policeman;
import ssmith.lang.Functions;
import java.io.IOException;


public class BasicLocation extends Location {

    public BasicLocation(String no) throws IOException {
        super(no);

        if (Functions.rnd(1, 8) == 1) {
            this.addCharacter(new Policeman(this));

        }
    }

    public void regenChars() throws IOException {
        // do nothing
    }

}
