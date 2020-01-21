package gta_text;

import java.io.IOException;
import gta_text.npcs.*;
import gta_text.items.*;
import java.util.Hashtable;
import gta_text.locations.Location;
import java.util.Enumeration;
import java.util.Vector;
import ssmith.lang.Functions;

public abstract class AbstractGame {

    public static final int CALL_ARMY_IN_LEVEL = 5;

    public Hashtable locations = new Hashtable();
    protected Vector characters = new Vector();
    public Vector items = new Vector();
    public GameTime game_time = new GameTime();
    public GameWeather game_weather = new GameWeather();
    public Location cops_needed_at, army_needed_at;

    public AbstractGame() {
    }

    public abstract void loadGame() throws IOException;

    public abstract void game_loop() throws IOException;

    public void addCharacter(GameCharacter charac) {
        this.characters.add(charac);
    }

    public Enumeration getCharacterEnum() {
        return this.characters.elements();
    }

    public void removeCharacter(GameCharacter charac) {
        this.characters.remove(charac);
    }

    public boolean containsCharacter(String name) {
        for(int i=0 ; i<characters.size() ; i++) {
            GameCharacter charac = (GameCharacter)characters.get(i);
            if (charac.name.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    public Location getRandomLocation(boolean external_only) {
        Location loc = null;
        while (true) {
            int i = Functions.rnd(0, this.locations.size()-1);
            Enumeration enumr = this.locations.elements();
            for(int x=0 ; x<i ; x++) {
                enumr.nextElement();
            }
            loc = (Location)enumr.nextElement();
            if (loc.is_open) {
                if (external_only == false || loc.internal == false) {
                    return loc;
                }
            }
        }
    }

    public void informOthersOfMiscAction(GameCharacter actor1, GameCharacter actor2, String msg) throws IOException {
        informOthersOfMiscAction(ServerConn.ACTION_COL, actor1, actor2, msg);
    }

    public void informOthersOfMiscAction(String col, GameCharacter actor1, GameCharacter actor2, String msg) throws IOException {
        try {
            GameCharacter charac;
            Location loc = actor1.getCurrentLocation();
            if (loc != null) {
                Enumeration enumr = loc.getCharacterEnum();
                while (enumr.hasMoreElements()) {
                    charac = (GameCharacter) enumr.nextElement();
                    if (charac instanceof PlayersCharacter) {
                        PlayersCharacter pc = (PlayersCharacter) charac;
                        if (pc != actor1 && pc != actor2 && pc.blinded == false) {
                            pc.conn.sendOutput(col, msg);
                        }
                    }
                }
            }
        } catch (Exception e) {
        	Server.HandleError(e);
        }
    }

    public void informAllCharsInLocation(Location loc, String msg) throws
            IOException {
        GameCharacter charac;
        Enumeration enumr = loc.getCharacterEnum();
        while (enumr.hasMoreElements()) {
            charac = (GameCharacter) enumr.nextElement();
            if (charac instanceof PlayersCharacter) {
                PlayersCharacter pc = (PlayersCharacter) charac;
                if (pc.blinded == false) {
                    pc.conn.sendOutput(ServerConn.ACTION_COL, msg);
                }
            }
        }
    }

    public void informOthersOfShooting(GameCharacter shooter, GameCharacter target, RangedWeapon weapon) throws IOException {
      GameCharacter charac;
      Location loc = shooter.getCurrentLocation();
      Enumeration enumr = loc.getCharacterEnum();
      while (enumr.hasMoreElements()) {
        charac = (GameCharacter)enumr.nextElement();
        if (charac != shooter && charac != target && charac.blinded == false) {
            charac.sendMsgToPlayer(ServerConn.COMBAT_COL, "The " + shooter.name + " shoots at the "+target.name+" with their "+weapon.name+".");
            charac.seenShooting(shooter, target);
        }
      }
    }

    public void informOthersOfAttack(GameCharacter attacker, GameCharacter defender, String with) throws IOException {
        GameCharacter charac;
        Location loc = attacker.getCurrentLocation();
        Enumeration enumr = loc.getCharacterEnum();
        while (enumr.hasMoreElements()) {
            charac = (GameCharacter)enumr.nextElement();
            if (charac != attacker && charac != defender && charac.blinded == false) {
                if (with.length() > 0) {
                    charac.sendMsgToPlayer(ServerConn.COMBAT_COL, "The " + attacker.name + " attacks the " +
                            defender.name + " with a " + with + ".");
                } else {
                    charac.sendMsgToPlayer(ServerConn.COMBAT_COL, "The " + attacker.name + " attacks the " +
                            defender.name + ".");
                }
                charac.seenAttack(attacker, defender);
            }
        }
    }

    public void callTheCops(Location loc) throws IOException {
        if (this.cops_needed_at != loc) {
            if (loc.wanderable) {
                if (loc.containsCharacter(Policeman.NAME) == false) {
                    this.cops_needed_at = loc;
                    if (Functions.rnd(1, 2) == 1) {
                        Server.game.informAllCharsInLocation(loc, "You can hear sirens getting closer.");
                    }
                }
            }
        }
    }

    public void callTheArmy(Location loc) throws IOException {
        if (this.army_needed_at != loc) {
            if (loc.wanderable) {
                if (loc.containsCharacter("Soldier") == false) {
                    this.army_needed_at = loc;
                    if (Functions.rnd(1, 3) == 1) {
                        Server.game.informAllCharsInLocation(loc, "You can hear heavy machinery getting closer.");
                    }
                }
            }
        }

    }

    public static String getRandomShootingDescription() {
        int x = Functions.rnd(1, 3);
        switch(x) {
        case 1:
            return "Blood spurts everywhere as the bullets rip through their flesh!";
        case 2:
            return "The hot leads cuts through the limbs like a knife through butter.";
        case 3:
            return "The noise of the gun drowns out the screaming.";
        default:
            return "Words can't describe the mayhem.";
        }
    }

    public int GetHighestWantedLevel() {
        int highest = 0;
        Enumeration enumr = characters.elements();
        while (enumr.hasMoreElements()) {
            GameCharacter charac = (GameCharacter) enumr.nextElement();
            highest = Math.max(highest, charac.getWantedLevel());
        }
        return highest;
    }

    public NonPlayerCharacter getRandomNPC(GameCharacter ignore) {
        GameCharacter gc = null;
        while (true) {
            int i = Functions.rnd(0, this.characters.size()-1);
            Enumeration enumr = this.characters.elements();
            for(int x=0 ; x<i ; x++) {
                enumr.nextElement();
            }
            gc = (GameCharacter)enumr.nextElement();
            if (gc.getCurrentLocation().wanderable && gc != ignore) { // So we don't choose mssr etc..
                if (gc instanceof NonPlayerCharacter) {
                    return (NonPlayerCharacter) gc;
                }
            }
        }
    }

}
