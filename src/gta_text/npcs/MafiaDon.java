package gta_text.npcs;

import gta_text.locations.Location;
import java.io.IOException;
import ssmith.lang.Functions;
import gta_text.items.Uzi;
import gta_text.items.RangedWeapon;
import gta_text.Server;

public class MafiaDon extends MemberOfGroup {

    public MafiaDon(Location loc) throws IOException {
        super(NonPlayerCharacter.MAFIA_DON, "MafiaDon", loc, NonPlayerCharacter.MAFIA_DON, false);

        this.other_names.add("MAFIA");

        this.addItem(new Uzi());
    }

    public void process() throws Exception {
        super.process();

        this.ensureWantedLevel(2);
    }

    public void otherCharacterEntered(GameCharacter character, Location old_loc) throws IOException {
        if (character instanceof PlayersCharacter) {
            if (character != this.enemy) {
                if (character.current_item instanceof RangedWeapon) {
                    this.sayTo(character, "I'd put that gun away before someone gets hurt.");
                } else {
                    this.sayTo(character, "Can I help you?  You must be lost.");
                }
            } else {
                this.say("So, we meet again?");
            }
        }
    }

    public void seenShooting(GameCharacter shooter, GameCharacter target) throws
            IOException {
        if (this.getCurrentLocation() == Server.game.mafia_house) {
            this.goto_location = Server.game.gambling_room;
        } else {
            super.seenShooting(shooter, target);
        }
    }

    public void shotAtBy(GameCharacter character, RangedWeapon item) throws IOException {
        this.enemy = character;
        if (this.getCurrentLocation() == Server.game.mafia_house) {
            Server.game.informOthersOfMiscAction(this, null,
                                                 "The " + this.name + " says 'Take care of him'.");
            this.goto_location = Server.game.gambling_room;
        }
    }

    protected void performRandomAction() throws IOException {
        int x = Functions.rnd(1, 7);
        switch(x) {
        case 1:
        case 2:
            this.say("Can I help you?");
            break;
        case 3:
            this.say("Am I a stereotype?");
            break;
        case 4:
            this.say("Have I made myself heard?");
            break;
        case 5:
            this.say("Your're either with us or against us.");
            break;
        case 6:
            this.say("Do I look like I'm joking?");
            break;
        case 7:
            this.say("We're looking for someone who might be able to work for us.");
            break;
        default:
            this.say("Where's my script?");
            break;
        }
    }

    public boolean spokenTo(GameCharacter charac, String msg, boolean only_them) throws IOException {
       if (msg.toUpperCase().indexOf("JOB") >= 0 || msg.toUpperCase().indexOf("MISSION") >= 0 || msg.toUpperCase().indexOf("JOIN") >= 0 || msg.toUpperCase().indexOf("WORK") >= 0) {
           if (charac.gang == GameCharacter.GANG_MAFIA) {
               this.sayTo(charac, "You already work for me.  What you talkin' about?");
           } else {
               this.sayTo(charac, "So you want to work for the Mafia?  We were just wondering what to do because we have a problem.  The Yakuza seem to be everywhere, and they need to be, you know, 'thinned out' a bit.  See how many you can get rid of and you'll be handsomely rewarded.");
               charac.gang = GameCharacter.GANG_MAFIA;
           }
           return true;
        } else {
            if (only_them) {
                int x = Functions.rnd(1, 3);
                switch(x) {
                case 1:
                    this.sayTo(charac, "You talkin' to me?");
                    break;
                case 2:
                    this.sayTo(charac, "Get outta here.");
                    break;
                case 3:
                    this.sayTo(charac, "Don't waste my time.");
                    break;
                }
                return true;
            }
        }
        return super.spokenTo(charac, msg, only_them);
    }

    protected String getRandomSayingWhenAttacked() {
        return "I don't want to have to hurt you.";
    }

}
