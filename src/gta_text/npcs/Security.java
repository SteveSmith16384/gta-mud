package gta_text.npcs;

import java.io.IOException;
import gta_text.locations.Location;
import ssmith.lang.Functions;
import gta_text.Server;
import java.util.Enumeration;

public abstract class Security extends NonPlayerCharacter {

    public Security(int type, String name, Location loc) throws IOException {
        super(type, name, loc);
    }

    public void process() throws Exception {
        try {
            if (this.enemy == null) {
                if (this.getCurrentLocation() != null) {
                    Enumeration enumr = this.getCurrentLocation().
                                        getCharacterEnum();
                    while (enumr.hasMoreElements()) {
                        GameCharacter charac = (GameCharacter) enumr.
                                               nextElement();
                        if (charac.getWantedLevel() > 0) {
                            if (charac instanceof PlayersCharacter) {
                                this.enemy = charac;
                                int x = Functions.rnd(1, 2);
                                switch(x) {
                                case 1:
                                    this.sayTo(charac, "Hey!");
                                    break;
                                case 2:
                                    this.sayTo(charac, "You're going down!");
                                    break;
                                default:
                                    this.say("Script!");
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    System.err.println(this.name + " has null location!");
                }
            }
        } catch (Exception ex) {
            Server.HandleError(ex);
        }

        super.process(); // Must be after we check for wanted people, otherwise we might leave the loc.
    }

    public void mugged(GameCharacter charac) throws IOException {
        this.sayTo(charac, "Don't waste my time!");
        this.enemy = charac;
    }

    public void seenShooting(GameCharacter shooter, GameCharacter target) throws IOException {
        if (Functions.rnd(1, 3) == 1) {
            this.say("Put down your weapon!");
        }

        if (enemy == null) {
            if (target instanceof Security && (shooter instanceof Security) == false) {
                shooter.ensureWantedLevel(1);
                this.enemy = shooter;
            } else if (shooter instanceof Security && (target instanceof Security) == false) {
                this.enemy = target;
            } else if ((shooter instanceof Security) == false && (target instanceof Security) == false) {
                shooter.ensureWantedLevel(1);
                this.enemy = shooter;
            }
        }
    }

    public void seenAttack(GameCharacter attacker, GameCharacter target) throws IOException {
        if (Functions.rnd(1, 3) == 1) {
            this.say("Put down your weapon!");
        }

        if (enemy == null) {
            if (target instanceof Security && (attacker instanceof Security) == false) {
                this.enemy = attacker;
            } else if (attacker instanceof Security && (target instanceof Security) == false) {
                this.enemy = target;
            } else if ((attacker instanceof Security) == false && (target instanceof Security) == false) {
                attacker.ensureWantedLevel(1);
                this.enemy = attacker;
            }
        }
    }

}
