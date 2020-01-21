package gta_text.npcs;

import gta_text.items.Knife;
import gta_text.items.RangedWeapon;
import gta_text.locations.Location;

import java.io.IOException;
import java.util.Enumeration;
import ssmith.util.Interval;
import ssmith.lang.Functions;

public class Mugger extends NonPlayerCharacter {

    public static int total_muggers=0;

    private GameCharacter target;
    private int stage = 0;
    private Interval stage_interval = new Interval(1000*10);
    private int tmp_wander; // For storing wander when we're on a job.

    public Mugger(Location loc) throws IOException {
        super(NonPlayerCharacter.MUGGER, "Mugger", loc);
        this.addItem(new Knife());
        total_muggers++;
    }

    public void removeFromGame() throws IOException {
        super.removeFromGame();
        total_muggers--;
    }

    public void process() throws Exception {
        super.process();

        this.ensureWantedLevel(1);

        if (this.target == null) {
            if (this.getCurrentLocation().getNoOfCharacters() == 2) { // Just us
                Enumeration enumr = this.getCurrentLocation().getCharacterEnum();

                boolean found = false;
                GameCharacter charac = null;
                while (enumr.hasMoreElements()) {
                    charac = (GameCharacter) enumr.nextElement();
                    if (charac instanceof PlayersCharacter) {
                        if (charac.current_item == null || (charac.current_item instanceof RangedWeapon) == false) {
                            found = true;
                            break;
                        }
                    }
                }
                if (found) {
                    this.target = charac;
                    this.stage = 1;
                    this.tmp_wander = this.wander_chance;
                    this.wander_chance = 1;
                }
            }
        } else if (this.target.getCurrentLocation() == this.getCurrentLocation()) {
            if (this.getCurrentLocation().getNoOfCharacters() == 2) { // Just us
                if (this.stage_interval.hitInterval()) {
                    this.stage++;
                    switch (stage) {
                    case 1:
                        this.sayTo(target, "Give us your money.");
                        break;
                    case 2:
                        this.sayTo(target, "Give us your money or else.");
                        break;
                    case 3:
                        this.sayTo(target, "I said, give me your money or else.");
                        break;
                    default:
                        this.enemy = target;
                    }
                }
            }
        } else {
            target = null; // They've left!
            this.stage = 0;
            this.wander_chance = this.tmp_wander;
        }
    }

    public boolean spokenTo(GameCharacter charac, String msg, boolean only_them) throws IOException {
        if (only_them && charac instanceof PlayersCharacter) {
            if (charac == target) {
                if (msg.equalsIgnoreCase("NO")) {
                    this.sayTo(charac, "I'll pretend I didn't hear that.");
                } else if (msg.equalsIgnoreCase("FUCK YOU")) {
                    this.sayTo(charac, "You got balls, man!");
                    this.fight(charac, "fight");
                } else {
                    this.sayTo(charac, "Did you hear what I said?");
                }
            } else {
                this.sayTo(charac, "What you on about?");
            }
            return true;
        }
        return super.spokenTo(charac, msg, only_them);
    }

    public void otherCharacterLeft(GameCharacter character, Location new_loc, boolean disappeared, String to_dir) throws IOException {
        if (character == target && disappeared == false) {
            if (Functions.rnd(1, 3) > 1) {
                this.goto_location = new_loc; // Follow them
                if (this.stage > 0) {
                    stage--;
                }
            }
        }
    }

    public void givenCash(GameCharacter char_from, int amt) throws IOException {
        if (char_from == target) {
            if (amt < 10) {
                this.sayTo(char_from, "Sorry, not enough.");
                this.stage--;
            } else {
                this.sayTo(char_from, "A pleasure doing business with you.");
                this.target = null;
                this.stage = 0;
                this.move();
            }
        } else {
            this.sayTo(char_from, "Do I look poor to you?");
        }
    }

    protected void performRandomAction() throws IOException {
        if (this.getCurrentLocation().containsCharacter(this.target)) {
            return; // Do nothing
        }

        int x = Functions.rnd(1, 2);
        switch(x) {
        case 1:
            this.say("The police just like to pick on me.");
            break;
        case 2:
            this.say("I ain't done nothing wrong.");
            break;
        default:
            this.say("script?");
            break;
        }
    }

    protected String getRandomSayingWhenAttacked() {
        return "I'm gonna stick you.";
    }


}
