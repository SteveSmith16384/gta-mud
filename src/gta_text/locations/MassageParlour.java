/*
 * Created on 21-Jun-2006
 *
 */
package gta_text.locations;

import gta_text.items.MassageOil;
import gta_text.npcs.GameCharacter;
import gta_text.npcs.Masseur;

import java.io.IOException;
import gta_text.items.Bed;
import gta_text.ServerConn;

public class MassageParlour extends Location {

    public GameCharacter customer;

    public MassageParlour(String n) throws IOException {
        super(n);

        new Masseur(this);
        this.addItem(new Bed());
        this.addItem(new MassageOil());
    }

    public void process() throws IOException {
        super.process();

        if (this.containsPlayer() == false) {
            this.customer = null;
        }
    }

    public void regenChars() throws IOException {
        if (this.containsCharacter(Masseur.NAME) == false) {
            new Masseur(this);
        }
        if (this.containsItem(MassageOil.NAME) == false) {
            this.addItem(new MassageOil());
        }
    }

    public boolean bespokeCommand(GameCharacter character, String cmd) throws IOException {
        if (cmd.toUpperCase().indexOf("LIE") >= 0 || cmd.toUpperCase().indexOf("LAY") >= 0) {
            character.sendMsgToPlayer(ServerConn.ACTION_COL, "You lie on the bed.");
            customer = character;
            return true;
        }
        return false;
    }

}
