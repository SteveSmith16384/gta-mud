package gta_text.npcs;

import ssmith.lang.Functions;
import gta_text.items.Item;
import gta_text.locations.Location;
import java.io.IOException;
import java.util.Enumeration;

public class Dog extends MemberOfGroup {

    private GameCharacter owner;

  public Dog(Location loc, GameCharacter boss) throws IOException {
      super(NonPlayerCharacter.DOG, "Dog", loc, NonPlayerCharacter.AMMO_SHOPKEEPER, true);

      this.fist_damage = FIST_DAMAGE*3;
  }

  public void process() throws Exception {
      super.process();

      if (this.owner == null) {
          if (this.getCurrentLocation().getNoOfCharacters() >= 2) {
              Enumeration enumr = this.getCurrentLocation().getCharacterEnum();
              while (enumr.hasMoreElements()) {
                  GameCharacter charac = (GameCharacter)enumr.nextElement();
                  if (charac instanceof Dog == false) {
                      this.owner = charac;
                      break;
                  }
              }
          }
      }
  }

  protected void performRandomAction() throws IOException {
	  int x = Functions.rnd(1, 2);
	  switch(x) {
	  case 1:
		  this.say("Woof");
              break;
	  case 2:
		  this.say("Grrrr");
                  break;
	  default:
		  this.say("Script?");
                  break;
	  }

  }

  protected String getRandomSayingWhenAttacked() {
    return "Bark!  Bark!";
  }

  public String getFightMethod() {
    return "gore";
  }

  public void given(GameCharacter char_from, Item item) throws IOException {
      this.sayTo(char_from, "Woof.");
  }

  public void givenCash(GameCharacter char_from, int amt) throws IOException {
      this.sayTo(char_from, "Woof woof.");
  }

  public boolean spokenTo(GameCharacter charac, String msg, boolean only_them) throws IOException {
      if (only_them) {
          this.sayTo(charac, "Arooo!");
          return true;
      }
      return false;
}

  public void kissedBy(GameCharacter character) throws IOException {
      this.sayTo(character, "Grr!");
  }
}
