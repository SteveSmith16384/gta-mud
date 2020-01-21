/*
 * Created on 21-Jun-2006
 *
 */
package gta_text.locations;

import gta_text.items.Toilet;
import gta_text.npcs.GameCharacter;

import java.io.IOException;
import gta_text.items.Vomit;

public class Toilets extends Location {

    private Toilet toilet;

    public Toilets(String n, String exit) throws IOException {
        super(n);

        toilet = new Toilet(exit);
        this.addItem(toilet);
        this.addItem(new Vomit());
    }

    public void regenChars() throws IOException {
        // Do nothing
    }

    public boolean bespokeCommand(GameCharacter character, String cmd) throws IOException {
        if (cmd.toUpperCase().startsWith("PISS")) {
            toilet.use(character);
            return true;
        }
        return false;
    }

}
