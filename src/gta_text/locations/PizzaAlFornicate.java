/*
 * Created on 03-Jul-2006
 *
 */
package gta_text.locations;

import gta_text.npcs.Mafioso;
import gta_text.npcs.PizzaChef;
import java.io.FileNotFoundException;
import java.io.IOException;
import gta_text.items.Notice;
import ssmith.lang.Functions;

public class PizzaAlFornicate extends Location {

    public PizzaAlFornicate(String no) throws FileNotFoundException, IOException {
        super(no);

        new PizzaChef(this);
        new Mafioso(this);
        this.addItem(new Notice("Wanted: Pizza Delivery Boy.  Ask the chef for details."));
    }

    public void regenChars() throws IOException {
        if (this.containsCharacter(PizzaChef.NAME) == false) {
            new PizzaChef(this);
        }

        if (Mafioso.tot_mafia < 4) {
            if (Functions.rnd(1, 20) == 1) {
                if (this.containsCharacter(Mafioso.NAME) == false) {
                    new Mafioso(this);
                }
            }
        }
    }

}

