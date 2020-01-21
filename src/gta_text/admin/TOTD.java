/*
 * Created on 03-Jul-2006
 *
 */
package gta_text.admin;

import gta_text.Game;

import java.io.IOException;
import java.util.ArrayList;

import ssmith.io.TextFile;
import ssmith.lang.Functions;

public class TOTD {
    
    private ArrayList totds;
    
    public TOTD() throws IOException {
        totds = new ArrayList();
        TextFile tf = new TextFile();
        tf.openFile(Game.DATA_DIR + "totd.txt", TextFile.READ);
        while (tf.isEOF() == false) {
            String line = tf.readLine();
            if (line.length() > 0) {
                totds.add(line);
            }
        }
    }
    
    public String getTip() {
        int x = Functions.rnd(0, totds.size()-1);
        return (String)totds.get(x);
    }
}
