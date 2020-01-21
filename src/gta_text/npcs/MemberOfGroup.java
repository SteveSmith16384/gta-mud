package gta_text.npcs;

import gta_text.locations.Location;
import java.io.IOException;

public abstract class MemberOfGroup extends NonPlayerCharacter {

    public int group;
    private boolean stay_with_boss;

    public MemberOfGroup(int type, String name, Location loc, int g, boolean follow_boss) throws
            IOException {
        super(type, name, loc);

        this.group = g;
        stay_with_boss = follow_boss;
    }

    public void otherCharacterLeft(GameCharacter character, Location new_loc,
                                   boolean disappeared, String dir) throws
            IOException {
        if (!disappeared) {
            // Do we follow our boss?
            if (character instanceof MemberOfGroup) {
                MemberOfGroup comrade = (MemberOfGroup) character;
                if (comrade.group == this.group) {
                    if (this.enemy == null || this.stay_with_boss) {
                        this.goto_location = new_loc;
                        return;
                    }
                }
            }

/*            if (character == enemy) {
                    // Follow them!
                this.goto_location = new_loc;
                return;
            }*/
        }
    }

    public void seenShooting(GameCharacter shooter, GameCharacter target) throws
            IOException {
        if (this.enemy == null) {
            if (shooter instanceof MemberOfGroup) {
                MemberOfGroup comrade = (MemberOfGroup) shooter;
                if (comrade.group == this.group) {
                    this.enemy = target;
                    return;
                }
            } else if (target instanceof MemberOfGroup) {
                MemberOfGroup comrade = (MemberOfGroup) target;
                if (comrade.group == this.group) {
                    this.enemy = shooter;
                    return;
                }
            }
        }
    }

    public void seenAttack(GameCharacter attacker, GameCharacter target) throws
            IOException {
        super.seenAttack(attacker, target);

        if (this.enemy == null) {
            if (attacker instanceof MemberOfGroup) {
                MemberOfGroup comrade = (MemberOfGroup) attacker;
                if (comrade.group == this.group) {
                    this.enemy = target;
                    return;
                }
            }
            if (target instanceof MemberOfGroup) {
                MemberOfGroup comrade = (MemberOfGroup) target;
                if (comrade.group == this.group) {
                    this.enemy = attacker;
                    return;
                }
            }
        }

    }

}
