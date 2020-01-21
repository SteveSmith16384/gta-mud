package gta_text.mission;

import gta_text.Server;
import gta_text.npcs.Bodyguard;
import java.io.IOException;
import java.util.ArrayList;
import gta_text.npcs.MafiaDon;
import gta_text.items.Poster;
import gta_text.items.DeckOfCards;
import gta_text.locations.PoliceStation;
import gta_text.npcs.NonPlayerCharacter;

public class KillMafia extends Mission {

    public MafiaDon don;

    public KillMafia(ArrayList missions) {
        super(missions);
    }

    public void reset() throws IOException {
        // Create chars
        try {
            if (Server.game.mafia_house.containsCharacter("MAFIADON") == false) {
                don = new MafiaDon(Server.game.mafia_house);
            }
            if (Server.game.mafia_house.containsCharacter("BODYGUARD1") == false) {
                new Bodyguard("Bodyguard1", Server.game.mafia_house,
                              NonPlayerCharacter.MAFIA_DON, false);
            }

            if (Server.game.gambling_room.containsCharacter(
                    "BODYGUARD2") == false) {
                new Bodyguard("Bodyguard2", Server.game.gambling_room,
                              NonPlayerCharacter.MAFIA_DON, true);
            }
            if (Server.game.gambling_room.containsCharacter(
                    "BODYGUARD3") == false) {
                new Bodyguard("Bodyguard3", Server.game.gambling_room,
                              NonPlayerCharacter.MAFIA_DON, true);
            }
            if (Server.game.gambling_room.containsCharacter("CARDS") == false) {
                Server.game.gambling_room.addItem(new DeckOfCards());
            }

            PoliceStation loc2 = Server.game.police_station;
            loc2.removeItem("POSTER");
            loc2.addItem(new Poster("It says there is a reward for the Mafia Don.  You must give the corpse to the DeskSergeant to claim the reward."));
        } catch (IOException ex) {
            Server.HandleError(ex);
        }

    }

    public void setCompleted() throws IOException {
        this.completed = true;

        PoliceStation loc = Server.game.police_station;
        loc.removeItem("POSTER");
        loc.addItem(new Poster("It just shows some old faces.  Looks like it hasn't been updated for years."));
    }

}
