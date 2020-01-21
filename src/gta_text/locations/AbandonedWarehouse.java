/*
 * Created on 19-May-2006
 *
 */
package gta_text.locations;

import gta_text.items.Stick;
import java.io.IOException;

public class AbandonedWarehouse extends Location {

    public AbandonedWarehouse(String no) throws IOException {
        super(no);

        this.addItem(new Stick());
    }

    public void regenChars() throws IOException {
        // do nothing
    }

}
