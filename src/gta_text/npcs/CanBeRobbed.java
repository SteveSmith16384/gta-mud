package gta_text.npcs;

import gta_text.locations.Location;
import java.io.IOException;
import gta_text.Server;
import ssmith.lang.Functions;

public abstract class CanBeRobbed extends NonPlayerCharacter {

    private boolean given_money = false;

    public CanBeRobbed(int type, String name, Location loc) throws IOException {
        super(type, name, loc);
    }

    public void process() throws Exception {
        super.process();

        // Give robber cash
        if (this.getCurrentLocation() != null) {
            if (this.getCurrentLocation().containsCharacter(this.getCurrentLocation().
                    robber)) {
                if (given_money == false) {
                    this.give("10", this.getCurrentLocation().robber);
                    given_money = true;
                } else {
                    int x = Functions.rnd(1, 3);
                    switch (x) {
                    case 1:
                        this.give(""+Functions.rnd(5, 15), this.getCurrentLocation().robber);
                        break;
                    case 2:
                        this.say(getRandomSayingWhenAttacked());
                        break;
                    case 3:
                        Server.game.informOthersOfMiscAction(this, null, "The " + this.name + " fumbles around for more cash.");
                        break;
                    }
                }

                this.getCurrentLocation().robber.ensureWantedLevel(2);
                Server.game.callTheCops(this.getCurrentLocation());
            } else {
                this.getCurrentLocation().robber = null;
                given_money = false;
            }
        }
    }

    public boolean spokenTo(GameCharacter charac, String msg, boolean only_them) throws IOException {
        if (containsRobbery(msg)) {
            this.robbed(charac);
            return true;
        } else if (msg.toUpperCase().indexOf("HURRY") >= 0) {
            this.sayTo(charac, "I'm going as fast as I can!");
            return true;
        } else {
            return super.spokenTo(charac, msg, only_them);
        }
    }

    public void robbed(GameCharacter charac) throws IOException {
        this.sayTo(charac, "Okay, just don't hurt me!");
        if (charac != this.getCurrentLocation().robber) {
            charac.incWantedLevel(1);
        }
        this.getCurrentLocation().robber = charac;
    }

    public void seenShooting(GameCharacter shooter, GameCharacter target) throws IOException {
        if (this.getCurrentLocation().containsCharacter(Policeman.NAME) == false) {
            this.say("I'm calling the cops!");
            Server.game.callTheCops(this.getCurrentLocation());
        }
    }

    protected String getRandomSayingWhenAttacked() {
        if (this.getCurrentLocation().containsCharacter(Policeman.NAME) == false) {
            int x = Functions.rnd(1, 2);
            switch (x) {
            case 1:
                return
                        "I've pressed the alarm button so you better get your ass out of here!";
            case 2:
                return "The police are on their way!";
            default:
                return "script?";
            }
        } else {
            return "You're in trouble now!";
        }
    }

    public void otherCharacterEntered(GameCharacter character, Location old_loc) throws IOException {
        if (this.getCurrentLocation().robber == null) {
            this.sayTo(character, "Can I help you?");
        } else if (character == this.getCurrentLocation().robber) {
            this.sayTo(character, "Not you again!");
        }
    }

    public void otherCharacterLeft(GameCharacter character, Location new_loc, boolean disappeared, String to_dir) throws IOException {
        if (disappeared == false && this.getCurrentLocation().robber == null) {
            this.say("Have a nice day.");
        }
    }

}
