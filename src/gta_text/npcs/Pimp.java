package gta_text.npcs;

import gta_text.locations.Location;
import gta_text.items.Knife;
import java.io.IOException;
import ssmith.lang.Functions;

public class Pimp extends NonPlayerCharacter {

    public Pimp(Hooker hk, Location loc) throws IOException {
        super(NonPlayerCharacter.PIMP, "Pimp", loc);

        this.addItem(new Knife());
        this.enemy = hk.enemy;
    }

    protected void performRandomAction() throws IOException {
        int x = Functions.rnd(1, 3);
        switch(x) {
        case 1:
            this.say("Yo bitch, shut the fuck up.");
            break;
        case 2:
            this.say("You bin messin' wid my bitch?");
            break;
        case 3:
            this.say("I'm a bad mo'fo'!");
            break;
        default:
            this.say("Where's ma script?");
            break;
        }
    }

    protected String getRandomSayingWhenAttacked() {
        int x = Functions.rnd(1, 2);
        switch(x) {
        case 1:
            return "I'm gonna stick you real good, sucka!";
        case 2:
            return "You gonna wish you'd never bin born!";
        default:
            return "Script?";
        }

    }
}
