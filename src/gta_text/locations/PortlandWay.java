/*
 * Created on 07-Jul-2006
 *
 */
package gta_text.locations;

import java.io.IOException;
import gta_text.*;

public class PortlandWay extends Location {

    public PortlandWay(String n) {
        super(n);
    }

    public String getBespokeDesc() {
        if (Server.game.nightclub.is_open) {
            return "  You can hear loud music booming from the club.";
        }
        return super.getBespokeDesc();
    }
    
    public void regenChars() throws IOException {
    }
    
    

}
