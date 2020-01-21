package gta_text.locations;

import java.io.IOException;
import gta_text.items.Noticeboard;
import gta_text.npcs.GameCharacter;

public class Bridge extends Location {

    public Bridge(String no) throws IOException {
        super(no);

        this.addItem(new Noticeboard());
        //this.addItem(new Map());
    }

    public boolean bespokeCommand(GameCharacter character, String cmd) throws IOException {
        if (cmd.equalsIgnoreCase("MESSAGES")) {
            character.read(Noticeboard.NAME);
            return true;
        }
        return false;
    }

    public void regenChars() throws IOException {
        // do nothing
    }

}
