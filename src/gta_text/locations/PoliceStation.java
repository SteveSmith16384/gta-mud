/*
 * Created on 15-May-2006
 *
 */
package gta_text.locations;

import java.io.FileNotFoundException;
import java.io.IOException;
import ssmith.lang.Functions;
import gta_text.npcs.DeskSergeant;
import gta_text.npcs.Policeman;
import gta_text.npcs.Mugger;
import gta_text.items.Poster;
import gta_text.items.Leaflet;
import gta_text.npcs.GameCharacter;
import gta_text.ServerConn;

public class PoliceStation extends Location {

    public PoliceStation(String no) throws FileNotFoundException, IOException {
        super(no);

        new DeskSergeant(this);
        new Policeman(this);
        new Mugger(this);

        this.addItem(new Poster("It says 'Join the Police Today!  Ask the DeskSergeant for details.'"));

    }

    public void regenChars() throws IOException {
        if (this.containsCharacter("DESKSERGEANT") == false) {
            new DeskSergeant(this);
        }

        if (this.containsItem("LEAFLET") == false) {
            this.addItem(new Leaflet("It describes how you can join the police as a Bounty Hunter. It says to ask the desk sergeant for details."));
        }

        // More police?
        if (Policeman.total_police < Policeman.GetMaxPolice()) {
            if (Functions.rnd(0, 10) == 0) {
                new Policeman(this);
            }
        }
    }

    public boolean bespokeCommand(GameCharacter character, String cmd) throws IOException {
        if (cmd.equalsIgnoreCase("JOIN POLICE")) {
            character.sendMsgToPlayer(ServerConn.ERROR_COL, "Try talking to the desksergeant.");
            return true;
        }
        return false;
    }
}
