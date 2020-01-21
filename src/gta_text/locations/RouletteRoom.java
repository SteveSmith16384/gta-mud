package gta_text.locations;

import java.io.IOException;
import gta_text.npcs.GameCharacter;
import gta_text.*;
import ssmith.util.CSVString;
import ssmith.lang.Functions;
import gta_text.npcs.Croupier;

public class RouletteRoom extends Location {

    public RouletteRoom(String no) throws IOException {
        super(no);

        new Croupier(this);
    }

    public void regenChars() throws IOException {
        if (this.containsCharacter("CROUPIER") == false) {
            new Croupier(this);
        }
    }

    public boolean bespokeCommand(GameCharacter character, String cmd) throws IOException {
        if (cmd.toUpperCase().startsWith("PUT ") || cmd.toUpperCase().startsWith("BET ") || cmd.toUpperCase().startsWith("PLACE ")) {
            boolean succeed = false;
            CSVString csv = new CSVString(cmd.toUpperCase(), " ");
            if (csv.getNoOfSections() == 3) {
                csv = new CSVString(csv.getSection(0) + csv.getSection(1) + " ON " + csv.getSection(2), " ");
            }
            if (csv.getNoOfSections() == 4) {
                if (csv.getSection(3).equalsIgnoreCase("RED") ||
                    csv.getSection(3).equalsIgnoreCase("BLACK")) {
                    try {
                        // They are giving them some money
                        String s_c = csv.getSection(1).replaceAll("\\$", "");
                        int c = Integer.parseInt(s_c);
                        succeed = true;
                        if (character.cash >= c) {
                            character.cash -= c;
                            character.sendMsgToPlayer(ServerConn.ACTION_COL, "You put $" + c + " on " + csv.getSection(3) + ".");
                            Server.game.informOthersOfMiscAction(character,
                                    null,
                                    "The " + character.name + " lays $" + c + " on the table.");
                            int i_col = Functions.rnd(1, 2);
                            String col = "";
                            switch (i_col) {
                            case 1:
                                character.sendMsgToPlayer(ServerConn.ACTION_COL, "It comes up red.");
                                col = "RED";
                                break;
                            case 2:
                                character.sendMsgToPlayer(ServerConn.ACTION_COL, "It comes up black.");
                                col = "BLACK";
                                break;
                            }
                            if (csv.getSection(3).equalsIgnoreCase(col)) {
                                character.sendMsgToPlayer(ServerConn.ACTION_COL, "You win!");
                                character.sendMsgToPlayer(ServerConn.ACTION_COL, "The croupier gives you $" + (c*2) + ".");
                                character.cash += (c*2);
                                Server.game.informOthersOfMiscAction(character, null, "The " + character.name + " wins $" + (c*2) + "!");
                            } else {
                                character.sendMsgToPlayer(ServerConn.ACTION_COL, "You lose.");
                            }

                        } else {
                            character.sendMsgToPlayer(
                                    ServerConn.ERROR_COL, "You don't have that kind of money.");
                        }
                    } catch (java.lang.NumberFormatException ex) {
                        // Do nothing
                    }
                }
            }
            if (!succeed) {
                character.sendMsgToPlayer(ServerConn.ERROR_COL, "Sorry, I don't quite understand.  Try something like 'put [amount] on [red|black]'.");
            }
            return true;
        } else if (cmd.toUpperCase().startsWith("GAMBLE") || cmd.toUpperCase().startsWith("PLAY")) {
            character.sendMsgToPlayer(ServerConn.ERROR_COL, "Sorry, I don't quite understand.  Try something like 'put [amount] on [red|black]'.");
            return true;
        }
       return false;
   }



}
