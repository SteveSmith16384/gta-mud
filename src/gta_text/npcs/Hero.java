/*
 * Created on 16-Jun-2006
 *
 */
package gta_text.npcs;

import gta_text.locations.Location;

import java.io.IOException;

import ssmith.lang.Functions;

public class Hero extends NonPlayerCharacter {

    public static final String NAME = "Hero";

    public Hero(Location loc) throws IOException {
        super(NonPlayerCharacter.HERO, NAME, loc);
    }

    protected String getRandomSayingWhenAttacked() {
        int x = Functions.rnd(1, 3);
        switch (x) {
        case 1:
            return "One touch from me and a nuclear bomb will seem like the easy way out!";
        case 2:
            return "If you can see me, I can see you. If you can't see me, you may be only seconds away from death.";
        case 3:
            return "The last time I coughed, there were no survivors.";
        default:
            return "SCript?";
        }

    }

    protected void performRandomAction() throws IOException {
        int x = Functions.rnd(1, 21);
        switch(x) {
        case 1:
            this.say("I once built a time machine and went back in time to stop the JFK assassination. As Oswald shot, I met all three bullets in my beard, deflecting them. JFK's head exploded out of sheer amazement.");
            break;
        case 2:
            this.say("The only sure things are Death and Taxes, and when I go to work for the IRS, they'll be the same thing.");
            break;
        case 3:
            this.say("I don't read books.  I stare them down until I get the information I want.");
            break;
        case 4:
            this.say("The quickest way to a man's heart is with my fist.");
            break;
        case 5:
            this.say("When the Boogeyman goes to sleep every night he checks his closet for me.");
            break;
        case 6:
            this.say("I've counted to infinity. Twice.");
            break;
        case 7:
            this.say("My blood type is AK+. Ass-Kicking Positive. It is compatible only with heavy construction equipment, tanks, and fighter jets.");
            break;
        case 8:
            this.say("Hey, I CAN believe it's not butter.");
            break;
        case 9:
            this.say("When I order a Big Mac at Burger King, I get one.");
            break;
        case 10:
            this.say("I would go the bar, but it would be instantly destroyed, as that level of awesome cannot be contained in one building.");
            break;
        case 11:
            this.say("On my day off, I play Russion Roulette with a loaded gun.");
            break;
        case 12:
            this.say("When I do a pushup, I'm not lifting myself up, I'm pushing the Earth down.");
            break;
        case 13:
            this.say("Here's the weather forecast: slightly cloudy with a 75% chance of Pain.");
            break;
        case 14:
            this.say("I can stretch diamonds back into coal.");
            break;
        case 15:
            this.say("When I want an egg, I crack open the chicken.");
            break;
        case 16:
            this.say("We are alone in the Universe.  But we weren't until my first space expedition.");
            break;
        case 17:
            this.say("Behind every successful man, there is a woman. Behind every dead man, there is me.");
            break;
        case 18:
            this.say("I'm the only person to ever win a staring contest against Ray Charles and Stevie Wonder at the same time.");
            break;
        case 19:
            this.say("'Brokeback Mountain' is not just a movie. It's also what I call the pile of dead bodies in my front yard.");
            break;
        case 20:
            this.say("When the Hulk gets mad, he turns into me.");
            break;
        case 21:
            this.say("You've gotta be cruel to be kind, and my kind of kindness is fatal.");
            break;
    }
    }

}
