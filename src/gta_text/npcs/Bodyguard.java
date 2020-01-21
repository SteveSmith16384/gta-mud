package gta_text.npcs;

import gta_text.locations.Location;
import java.io.IOException;
import gta_text.items.Uzi;

public class Bodyguard extends MemberOfGroup {

    public Bodyguard(String name, Location loc, int gang, boolean follow_boss) throws IOException {
        super(NonPlayerCharacter.BODYGUARD, name, loc, gang, follow_boss);

        this.addItem(new Uzi());

    }

    protected void performRandomAction() throws IOException {
        this.say("Just keep away.");
    }

    protected String getRandomSayingWhenAttacked() {
        return "You're history, pal.";
    }

}
