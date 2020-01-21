/*
 * Created on 16-Jun-2006
 *
 */
package gta_text.npcs;

import gta_text.locations.Location;

import java.io.IOException;

import ssmith.lang.Functions;

public class Footballer extends NonPlayerCharacter {

    public static final String NAME = "Footballer";

    public Footballer(Location loc) throws IOException {
        super(NonPlayerCharacter.FOOTBALLER, NAME, loc);
    }

    protected String getRandomSayingWhenAttacked() {
        int x = Functions.rnd(1, 2);
        switch(x) {
        case 1:
            return "I'm gone slide-tackle you into history!";
        case 2:
            return "Don't mess wid me unless you want a foot-sizes bruise to the face!";
        default:
            return "Script?";
        }
    }

    protected void performRandomAction() throws IOException {
        int x = Functions.rnd(1, 7);
        switch(x) {
        case 1:
            this.say("I created the universe by slide-tackling the nothingness and told it to get a job.  Then there was the beginning.");
            break;
        case 2:
            this.say("The Bermuda Triangle used to be the Bermuda Square, until I slide-tackled one of the corners off.");
            break;
        case 3:
            this.say("Little Miss Muffet sat on her tuffet, until I slide-tackled her into a glacier.");
            break;
        case 4:
            this.say("My slide-tackles don't really kill people. They wipe out their entire existence from the space-time continuum.");
            break;
        case 5:
            this.say("Some people wear a Pele shirt. Pele wears my shirt.");
            break;
        case 6:
            this.say("Nagasaki never had a bomb dropped on it.  I jumped out of a plane and slide-tackled the ground.");
            break;
        case 7:
            this.say("I don't look both ways before crossing the street... I just slide-tackle any cars that get too close.");
            break;
        }
    }

}
