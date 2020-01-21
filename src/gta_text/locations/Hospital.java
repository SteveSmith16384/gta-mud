/*
 * Created on 15-May-2006
 *
 */
package gta_text.locations;

import java.io.FileNotFoundException;
import java.io.IOException;
import gta_text.items.MediKit;
import gta_text.npcs.Doctor;

public class Hospital extends Location {

    public Hospital(String no) throws FileNotFoundException, IOException {
        super(no);

        new Doctor(this);

        this.addItem(new MediKit());
    }

    public void regenChars() throws IOException {
        if (this.containsCharacter(Doctor.NAME) == false) {
            new Doctor(this);
        }

        if (this.containsItem("MEDIKIT") == false) {
            this.addItem(new MediKit());
        }
    }

}
