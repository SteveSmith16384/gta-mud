/*
 * Created on 03-Jul-2006
 *
 */
package gta_text.locations;

import gta_text.npcs.Yakuza;
import java.io.IOException;
import ssmith.lang.Functions;

public class Laundrette extends Location {

    public Laundrette(String n) throws IOException {
        super(n);

        new Yakuza(this);
    }

    public void regenChars() throws IOException {
        if (Yakuza.tot_yakuza < 4) {
            if (Functions.rnd(1, 20) == 1) {
                if (this.containsCharacter(Yakuza.NAME) == false) {
                    new Yakuza(this);
                }
            }
        }
    }

}
