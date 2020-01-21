/*
 * Created on 21-Jun-2006
 *
 */
package gta_text.npcs;

import gta_text.locations.Location;
import gta_text.*;
import java.io.IOException;
import ssmith.lang.Functions;

public class Masseur extends NonPlayerCharacter {

    public static final String NAME = "Masseur";

    public Masseur(Location loc) throws IOException {
        super(NonPlayerCharacter.MASSEUR, NAME, loc);
    }

    protected String getRandomSayingWhenAttacked() {
        return "I keell you deed!";
    }

    public void greeted(GameCharacter character) throws IOException {
        this.sayTo(character, "I here to give you pleasure.");
    }

    public void otherCharacterEntered(GameCharacter character, Location old_loc) throws IOException {
        this.sayTo(character, "Heyyo.");
    }

    protected void performRandomAction() throws IOException {
        if (Server.game.parlour.customer == null) {
            int x = Functions.rnd(1, 3);
            switch(x) {
            case 1:
                this.say("You lie on bed please?");
                break;
            case 2:
                this.say("You want massage?");
                break;
            case 3:
                Server.game.informOthersOfMiscAction(this, null, "The " + NAME + " applies liberal amounts of oil to her hands.");
                break;
            }
        }
    }

    public void process() throws Exception {
        super.process();

        if (Server.game.parlour.customer != null) {
            int x = Functions.rnd(1, 18); // This is deliberately higher!
            switch(x) {
            case 1:
                Server.game.parlour.customer.sendMsgToPlayer(ServerConn.ACTION_COL, "The " + NAME + " rubs her hands all over your body ensuring the oil goes everywhere.");
                Server.game.informOthersOfMiscAction(this, Server.game.parlour.customer, "The " + NAME + " rubs oil all over the " + Server.game.parlour.customer.name + ".");
                break;
            case 2:
                this.sayTo(Server.game.parlour.customer, "There you go.  I finished now.  Lie on bed if you want more.");
                Server.game.parlour.customer = null;
                break;
            case 3:
                Server.game.parlour.customer.sendMsgToPlayer(ServerConn.ACTION_COL, "The " + NAME + " climbs on top of you.");
                Server.game.informOthersOfMiscAction(this, Server.game.parlour.customer, "The " + NAME + " climbs on top of the " + Server.game.parlour.customer.name + ".");
                break;
            case 4:
                Server.game.parlour.customer.sendMsgToPlayer(ServerConn.ACTION_COL, "The " + NAME + " starts rubbing oil over you using her chest.");
                Server.game.informOthersOfMiscAction(this, Server.game.parlour.customer, "The " + NAME + " rubs oil on the " + Server.game.parlour.customer.name + " using her chest.");
                break;
            case 5:
                Server.game.parlour.customer.sendMsgToPlayer(ServerConn.ACTION_COL, "The " + NAME + " winks at you.");
                Server.game.informOthersOfMiscAction(this, Server.game.parlour.customer, "The " + NAME + " winks at the " + Server.game.parlour.customer.name + ".");
                break;
            case 6:
                this.sayTo(Server.game.parlour.customer, "You like that?");
                break;
            case 7:
                this.sayTo(Server.game.parlour.customer, "You tell me what you like?");
                break;
            case 8:
                Server.game.informOthersOfMiscAction(ServerConn.ACTION_COL, this, null, "The " + NAME + " applies more oil to her hands.");
                break;
            case 9:
                Server.game.parlour.customer.sendMsgToPlayer(ServerConn.ACTION_COL, "The " + NAME + " slides her hands between your thighs.");
                Server.game.informOthersOfMiscAction(this, Server.game.parlour.customer, "The " + NAME + " slides her hands between the thighs of the " + Server.game.parlour.customer.name + ".");
                break;
            case 10:
                Server.game.informOthersOfMiscAction(this, null, "The " + NAME + " stands to the side of the bed.");
                break;
            case 11:
                Server.game.informOthersOfMiscAction(this, null, "The " + NAME + " undoes a few more buttons on her uniform.");
                break;
            default:
                // Do nothing.
                break;
            }
        }
    }

    public boolean spokenTo(GameCharacter charac, String msg, boolean only_them) throws IOException {
        if (charac != Server.game.parlour.customer) {
            if (msg.toUpperCase().indexOf("YES") >= 0 || msg.toUpperCase().indexOf("PLEASURE") >= 0 || msg.toUpperCase().indexOf("MASSAGE") >= 0) {
                this.sayTo(charac, "You must lie on bed first.");
                return true;
            }
        }

        if (msg.toUpperCase().indexOf("EXTRA") >= 0) {
            this.sayTo(charac, "You dirty boy!  What you want then?");
        } else if (msg.toUpperCase().indexOf("HOW MUCH") >= 0) {
            this.sayTo(charac, "It free to all members of club.");
        } else if (msg.toUpperCase().indexOf("PLEASURE") >= 0) {
            this.sayTo(charac, "That give you lots of pleasure?");
        } else if (msg.toUpperCase().indexOf("SEX") >= 0 || msg.toUpperCase().indexOf("FUCK") >= 0 || msg.toUpperCase().indexOf("SHAG") >= 0) {
            this.sayTo(charac, "You very dirty boy!");
            charac.sendMsgToPlayer(ServerConn.ACTION_COL, "The " + NAME + " climbs on top of you and proceeds to make oily love to your body.");
            Server.game.informOthersOfMiscAction(this, charac, "The " + NAME + " climbs on top of the " + charac.name + " and makes love.");
        } else if (msg.toUpperCase().indexOf("HAND") >= 0 || msg.toUpperCase().indexOf("JOB") >= 0) {
            this.sayTo(charac, "I hope you say that.  That my speciality.");
            charac.sendMsgToPlayer(ServerConn.ACTION_COL, "The " + NAME + " climbs on top of you and proceeds to touch your groinal area.");
            Server.game.informOthersOfMiscAction(this, charac, "The " + NAME + " climbs on top of the " + charac.name + " and proceeds to massage their groin.");
        } else if (msg.toUpperCase().indexOf("BLOW") >= 0 || msg.toUpperCase().indexOf("JOB") >= 0) {
            this.sayTo(charac, "Very sorry, I not do them.");
        } else if (msg.equalsIgnoreCase("YES")) {
            this.sayTo(charac, "Very good.");
        } else {
            this.sayTo(charac, "Sorry, my english not so good.");
        }
        return true;
    }

    public void givenCash(GameCharacter char_from, int amt) throws IOException {
        this.sayTo(char_from, "Thankyou, but massage free to members of club.");
    }

    public void seenCharacterUseBed(GameCharacter character) {
        Server.game.parlour.customer = character;
    }

    public void touched(GameCharacter character) throws IOException {
        this.sayTo(character, "You like to touch me?");
    }

    public boolean shagAttemptedBy(GameCharacter character) throws IOException {
        this.sayTo(character, "I been waiting for you to have me.");
        character.sendMsgToPlayer(ServerConn.ACTION_COL, "The " + NAME + " spreads her legs wide for you while you pound her.");
        Server.game.informOthersOfMiscAction(this, character, "The " + NAME + " lets the " + character.name + " enter her.");
      return true;
    }

}
