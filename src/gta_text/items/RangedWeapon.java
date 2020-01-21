package gta_text.items;

import gta_text.npcs.GameCharacter;

import java.io.IOException;

public abstract class RangedWeapon extends Item {

  public RangedWeapon(int type) throws IOException {
    super(type);
  }

  public String getDesc() {
    return desc+"  It has "+this.ammo+" bullets left.  ";
  }

  public boolean use(GameCharacter character) throws IOException {
    return false;
  }

}
