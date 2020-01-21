package gta_text.npcs;

import gta_text.locations.Location;
import gta_text.*;
import gta_text.items.Alcohol;
import gta_text.items.Corpse;
import gta_text.items.Pistol;
import gta_text.items.RangedWeapon;
import java.io.IOException;
import java.util.Enumeration;
import ssmith.lang.Functions;

public class Policeman extends Law {

    public static final String NAME = "Policeman";

    public static int total_police = 0;
    private boolean queried_corpse = false;

  public Policeman(Location loc) throws IOException {
      super(NonPlayerCharacter.POLICEMAN, NAME, loc);
      this.addItem(new Pistol());
      total_police++;

//      this.name = this.name + total_police;
  }

  public Policeman(Location loc, boolean wander) throws IOException {
      this(loc);
      if (wander == false) {
          this.wander_chance = 1;
      }
  }

  public void process() throws Exception {
      super.process();

      // Clear the 'queried corpse' flag
      if (this.getCurrentLocation().containsItem(Corpse.NAME) == false) {
          this.queried_corpse = false;
      }

      if (enemy == null) {
          if (Functions.rnd(1, 12) == 1) {
              if (this.getCurrentLocation() != null) {

                  Enumeration enumr = this.getCurrentLocation().
                                      getCharacterEnum();
                  while (enumr.hasMoreElements()) {
                      GameCharacter charac = (GameCharacter) enumr.nextElement();
                      if (charac instanceof PlayersCharacter) {
                          if (charac.current_item != null) {
                              if (charac.current_item instanceof RangedWeapon) {
                                  this.sayTo(charac,
                                             "Do you have a licence for that weapon?");
                                  break;
                              } else if (charac.current_item instanceof Alcohol && this.getCurrentLocation().internal == false) {
                                      this.sayTo(charac,
                                                 "Do you know it is an offence to drink alcohol in the street?");
                                      break;
                              }
                          }
                      }
                  }
              }
          }

          // Check there's not two or more police.  If so, move one of them
          int no_of_police = 0;
          Location loc = this.getCurrentLocation();
          if (loc != null) {
              Enumeration enumr = loc.getCharacterEnum();
              while (enumr.hasMoreElements()) {
                  if (enumr.nextElement() instanceof Policeman) {
                      no_of_police++;
                  }
              }
              if (no_of_police > 1) {
                  if (this.wander_chance > 1) {
                      this.move();
                  }
              }
          }
      }
  }

  public void removeFromGame() throws IOException {
      super.removeFromGame();
      Policeman.total_police--;
  }

  public boolean shagAttemptedBy(GameCharacter character) throws IOException {
      this.sayTo(character, "Get the fuck away from me!");
      this.enemy = character;
      return false;
  }

  protected void performRandomAction() throws IOException {
      int x = Functions.rnd(1, 4);
      switch(x) {
      case 1:
          this.say("Keep out of trouble.");
          break;
      case 2:
          this.say("Do I recognise you from somewhere?");
          break;
      case 3:
          Server.game.informOthersOfMiscAction(this, null, "The " + name + " eye's everyone up suspiciously.");
          break;
      case 4:
          Server.game.informOthersOfMiscAction(this, null, "The " + name + "'s radio crackles something incomprehensible.");
          break;
      default:
          this.say("What are my lines?");
          break;
      }

  }

  protected String getRandomSayingWhenAttacked() {
    return "You are going down!";
  }

  public boolean spokenTo(GameCharacter charac, String msg, boolean only_them) throws IOException {
      if (only_them && charac instanceof PlayersCharacter) {
          if (this.containsSwearing(msg)) {
              this.sayTo(charac, "You shouldn't talk like that to the police.");
          /*} else if (containsRobbery(msg)) {
              this.sayTo(charac, "I think you've got me confused with a bank.  A bank is a building; I am a person.");*/
          } else if (this.getCurrentLocation().containsItem("CORPSE") && queried_corpse == false) {
              this.sayTo(charac, "And do you know anything about how this corpse came to be here?");
              queried_corpse = true;
          } else if (msg.equalsIgnoreCase("NO") || msg.indexOf("NO ") >= 0) {
              this.sayTo(charac, "I think I believe you.");
          } else if (msg.toUpperCase().indexOf("GIVE ME") >= 0) {
              this.sayTo(charac, "No.");
          } else {
              if (Functions.rnd(1, 2) == 1) {
                  this.sayTo(charac, "I'm sorry, I didn't catch that.");
              } else {
                  this.sayTo(charac, "Are you having a joke?");
              }
          }
          return true;
      } else if (msg.equalsIgnoreCase("YES")) {
          this.sayTo(charac, "Let's see it then.");
      }
      return super.spokenTo(charac, msg, only_them);
  }

  public static int GetMaxPolice() {
      int max_police = (Server.game.locations.size()/5);
      if (Server.game.game_time.getDayPeriodNo() == GameTime.NIGHT) {
          max_police = max_police / 2;
      }
      return max_police;
  }

}
