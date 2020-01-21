/*
 * Created on 27-Jun-2006
 *
 */
package gta_text.admin;

import gta_text.Server;
import gta_text.items.Item;
import gta_text.locations.Location;
import gta_text.npcs.PlayersCharacter;
import gta_text.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;

import ssmith.io.TextFile;

public class SavedGames {

    public static final String SAVED_GAMES_DIR = "./saved_games/";

    private String getFilename(String pname, String pwd) {
        return (SAVED_GAMES_DIR + pname + "_" + pwd + ".txt").toLowerCase();
    }

    public boolean savedGameExists(String pname, String pwd) {
        String file = getFilename(pname, pwd);
        return (new File(file).canRead());
    }

    public void saveCharacter(PlayersCharacter character) throws FileNotFoundException, IOException {
        try {
            String filename = getFilename(character.conn.playername,
                                          character.conn.pwd);

            TextFile tf = new TextFile();
            tf.openFile(filename, TextFile.WRITE);
            tf.writeLine(character.name);
            if (character.male) {
                tf.writeLine("M");
            } else {
                tf.writeLine("F");
            }
            tf.writeLine(character.getDesc());
            tf.writeLine(character.getCurrentLocation().no);
            tf.writeLine(character.cash + "");
            tf.writeLine(character.getHealth() + "");
            if (character.current_item != null) {
                tf.writeLine(character.current_item.getClass().getName());
            } else {
                tf.writeLine("");
            }

            tf.close();
        } catch (IOException ex) {
            Server.HandleError(ex);
            character.sendMsgToPlayer(ServerConn.ERROR_COL, "Sorry, there was a problem saving the game.");
        }
    }

    public void loadCharactersDetails(PlayersCharacter character) throws FileNotFoundException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
        String filename = getFilename(character.conn.playername, character.conn.pwd);

        TextFile tf = new TextFile();
        tf.openFile(filename, TextFile.READ);
        character.name = tf.readLine();
        character.sendMsgToPlayer(ServerConn.NORMAL_COL, "Loading " + character.name + "...");
        character.male = tf.readLine().equalsIgnoreCase("M");
        character.setDesc(tf.readLine());
        String loc_no = tf.readLine();
        character.cash = new Integer(tf.readLine()).intValue();
        character.setHealth(new Integer(tf.readLine()).intValue());
        String item_name = tf.readLine();
        if (item_name.length() > 0) {
        	try {
        		Item item = (Item) (Class.forName(item_name).newInstance());
        		character.addItem(item);
        	} catch (Exception ex) {
        		Server.HandleError(ex);
        	}
        }
        tf.close();
        //tf.delete();

        Enumeration enumr = Server.game.locations.elements();
        while (enumr.hasMoreElements()) {
            Location loc = (Location)enumr.nextElement();
            if (loc.no.equalsIgnoreCase(loc_no)) {
                character.setCurrentLocation(loc, "", true, true);
            }
        }
    }

}
