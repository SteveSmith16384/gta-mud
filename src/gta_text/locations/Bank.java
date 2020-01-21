package gta_text.locations;

import java.io.IOException;
import gta_text.GameTime;
import gta_text.Server;
import gta_text.npcs.GameCharacter;
import gta_text.items.Leaflet;
import gta_text.npcs.SecurityGuard;
import gta_text.npcs.Teller;
import gta_text.ServerConn;

public class Bank extends Location {

    public Bank(String no) throws IOException {
        super(no);

        start_time = 8*60*60*1000;
        end_time = 18*60*60*1000;

        this.closed_reason = "The bank is closed.  The sign on the door says it re-opens at " + GameTime.GetTimeAsString(start_time) + ".";

        new Teller(this);
        new SecurityGuard(this);
    }

    public void process() throws IOException {
        super.process();

        // Are we open?
        if (this.is_open) {
            if (Server.game.game_time.GetMS() < start_time || Server.game.game_time.GetMS() > end_time) {
                this.is_open = false;
            }

        } else {
            if (Server.game.game_time.GetMS() > start_time && Server.game.game_time.GetMS() < end_time) {
                this.is_open = true;

                // Create the assistant
                if (this.containsCharacter("TELLER") == false) {
                    new Teller(this);
                }
                if (this.containsItem("LEAFLET") == false) {
                    this.addItem(new Leaflet("It describes how easy it is to get a loan, at very favourable interest rates."));
                }
            } else {
                // Tell people to leave
                if (this.containsPlayer()) {
                    GameCharacter charac = this.getCharacter("TELLER");
                    if (charac != null) {
                        charac.say("This is a customer announcement.  The shop is closing now.  Could all customers please leave the bank.");
                    }
                }
            }
        }
    }

    public void regenChars() throws IOException {
        if (this.containsCharacter("SECURITYGUARD") == false) {
            new SecurityGuard(this);
        }
    }

    public boolean bespokeCommand(GameCharacter character, String cmd) throws
    IOException {
        if (cmd.toUpperCase().startsWith("USE CASHCARD") || cmd.toUpperCase().startsWith("USE CARD")) {
            /*if (character.current_item instanceof CashCard) {
             if (this.containsCharacter(Teller.NAME)) {
             GameCharacter teller = this.getCharacter(Teller.NAME);
             character.give("Cashcard", teller);
             teller.sayTo(character, "Thank you.  Your account has to be closed, but here is the balance.");
             teller.cash += BALANCE; // So they've got enough to give away
             teller.give("$"+BALANCE, character);
             } else {
             character.sendMsgToPlayer(ServerConn.ERROR_COL,
             "There does not appear to be a teller to serve you.");
             }
             } else {
             character.sendMsgToPlayer(ServerConn.ERROR_COL, "You are not using a cashcard.");
             }*/
            character.sendMsgToPlayer(ServerConn.ERROR_COL, "Try giving your cashcard to the teller.");
            return true;
        } else if (cmd.toUpperCase().startsWith("BANK") || cmd.toUpperCase().startsWith("WITHDRAW")) {
            if (this.containsCharacter(Teller.NAME)) {
                GameCharacter teller = this.getCharacter(Teller.NAME);
                teller.sayTo(character, "Do you have your cashcard?");
            } else {
                character.sendMsgToPlayer(ServerConn.ERROR_COL,
                "There does not appear to be a teller to serve you.");
            }
            return true;
        } else if (cmd.toUpperCase().startsWith("DEPOSIT")) {
            if (this.containsCharacter(Teller.NAME)) {
                GameCharacter teller = this.getCharacter(Teller.NAME);
                teller.sayTo(character, "Can you give me the money you wish to deposit please.");
            } else {
                character.sendMsgToPlayer(ServerConn.ERROR_COL,
                "There does not appear to be a teller to serve you.");
            }
            return true;
        }
        return false;
    }

}
