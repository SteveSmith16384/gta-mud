/*
 * Created on 20-Jun-2006
 *
 */
package gta_text.locations;

import gta_text.items.DailyMail;

import java.io.IOException;

public class Mansion extends Location {

    public Mansion(String n) throws IOException {
        super(n);

        this.addItem(new DailyMail());
    }

    public void regenChars() throws IOException {
        if (this.containsItem(DailyMail.NAME) == false) {
            this.addItem(new DailyMail());
        }
    }


}
