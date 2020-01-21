/*
 * Created on 20-Jun-2006
 *
 */
package gta_text.locations;

import java.io.IOException;
import java.util.Enumeration;
import ssmith.lang.Functions;
import gta_text.*;
import gta_text.items.Bed;
import gta_text.items.Cigs;
import gta_text.items.MediKit;
import gta_text.items.Picture;
import gta_text.items.Water;
import gta_text.npcs.GameCharacter;
import gta_text.npcs.Policeman;

public class Bedroom extends Location {

    public Bedroom(String n) throws IOException {
        super(n);

        createRegens();
    }

    public void regenChars() throws IOException {
        createRegens();
    }

    private void createRegens() throws IOException {
        if (this.containsItem(Bed.NAME) == false) {
            this.addItem(new Bed());
        }
        if (this.containsItem(Picture.NAME) == false) {
            this.addItem(new Picture("It's an abstract painting, showing swirls of light rotating around a colourful column of air."));
        }
        if (this.containsItem(Water.NAME) == false) {
            this.addItem(new Water());
        }
        if (this.containsItem(MediKit.NAME) == false) {
            this.addItem(new MediKit());
        }
        if (this.containsItem(Cigs.NAME) == false) {
            this.addItem(new Cigs());
        }

    }

    public void process() throws IOException {
        super.process();

        if (this.containsPlayer()) {
            if (Server.game.drugs_bust_mission.stage == 0) {
                if (this.containsCharacter(Policeman.NAME) == false) {
                    for (int x = 0; x < 3; x++) {
                        new Policeman(Server.game.mansion, false);
                    }
                }
                Server.game.informAllCharsInLocation(this, "You hear a loud crash from downstairs, and the sound of broken glass!");
                Server.game.drugs_bust_mission.stage = 1;

                // Make the players wanted.
                Enumeration enumr = this.getCharacterEnum();
                while (enumr.hasMoreElements()) {
                    GameCharacter charac = (GameCharacter)enumr.nextElement();
                    charac.ensureWantedLevel(2);
                }
            } else if (Server.game.drugs_bust_mission.stage == 1) {
                Server.game.informAllCharsInLocation(this, "A voice shouts from downstairs: 'This is a raid!  You're under arrest!'");
                Server.game.drugs_bust_mission.stage = 2;

            } else if (Server.game.drugs_bust_mission.stage == 2) {
                int x = Functions.rnd(1, 3);
                switch (x){
                case 1:
                    Server.game.informAllCharsInLocation(this, "A voice shouts from downstairs: 'Come down with your hands up!'");
                    break;
                case 2:
                    Server.game.informAllCharsInLocation(this, "You hear a voice: 'They must be around here somewhere.'");
                    break;
                case 3:
                    Server.game.informAllCharsInLocation(this, "A voice shouts from downstairs: 'Come out with your hands up!'");
                    break;
                }

                // Ensure the players are still wanted.
                Enumeration enumr = this.getCharacterEnum();
                while (enumr.hasMoreElements()) {
                    GameCharacter charac = (GameCharacter)enumr.nextElement();
                    charac.ensureWantedLevel(2);
                }
            }
        } else {
            // Clear out police
            if (this.containsCharacter(Policeman.NAME)) {
                GameCharacter charac = this.getCharacter(Policeman.NAME);
                charac.removeFromGame();
            } else {
                if (Server.game.mansion.containsCharacter(Policeman.NAME) == false) {
                    Server.game.drugs_bust_mission.setCompleted();
                }
            }
        }


    }



}
