package gta_text.npcs;

import gta_text.locations.Location;
import gta_text.*;
import java.io.IOException;

import ssmith.lang.Functions;

public class Hooker extends NonPlayerCharacter {

  public Hooker(Location loc) throws IOException {
      super(NonPlayerCharacter.HOOKER, "Hooker", loc);
  }

  public void process() throws Exception {
      super.process();

      // Time to go?
      long ms = Server.game.game_time.GetMS();
      if (ms < Server.game.rl_district.start_time && ms > Server.game.rl_district.end_time) {
              // Check we're not fighting
              if (this.getCurrentLocation().containsCharacter(this.enemy) == false) {
                  this.say("Time for me to go home.");
                  this.removeFromGame();
              }
          }
  }

  public void givenCash(GameCharacter char_from, int amt) throws IOException {
      if (amt <= 3) {
          this.sayTo(char_from, "You want a peck on the cheek?");
      } else {
          this.sayTo(char_from, "You can have me all night long baby!");
          if (Functions.rnd(1, 3) == 1) {
              this.sayTo(char_from, "And what would you like me to do..?");
          } else {
              sex(char_from);
          }
      }
  }

  private void sex(GameCharacter character) throws IOException {
      if (Functions.rnd(1, 2) == 1) {
          character.sendMsgToPlayer(ServerConn.ACTION_COL, "The hooker takes you around the side of the building and shows you a good time.");
          Server.game.informOthersOfMiscAction(this, character,
                  "The " + this.name + " takes the " + character.name +
                  " round the side of the building into the shadows.  You can hear some moaning.");
      } else {
          if (character.male) {
              character.sendMsgToPlayer(
                      ServerConn.ACTION_COL, "The hooker gets on her knees and gives you some head.");
              Server.game.informOthersOfMiscAction(this, character,
                      "The " + this.name + " gets on her knees and takes the " +
                      character.name +
                      " in her mouth.");
          } else {
              this.sayTo(character, "It's not often I service a woman...");
              character.sendMsgToPlayer(
                      ServerConn.ACTION_COL, "The hooker gets on her knees and pleasures you between your legs.");
              Server.game.informOthersOfMiscAction(this, character,
                      "The " + this.name + " gets on her knees and burys her head between the " +
                      character.name +
                      "'s legs.");
          }
      }
  }

  public boolean shagAttemptedBy(GameCharacter character) throws IOException {
      sex(character);
      character.sendMsgToPlayer(ServerConn.ACTION_COL, "That costs you $20.");
      character.cash -= 20;
      character.increaseHealth(10, null);
      if (Functions.rnd(1, 2) == 1) {
          this.say("I could do it all night long with you baby.'");
      } else {
          this.say("Let's do it it again!'");
      }
      return true;
  }

  protected void performRandomAction() throws IOException {
	  int x = Functions.rnd(0, 5);
	  switch (x) {
	  case 0:
              this.say("Hi honey.");
              break;
          case 1:
              this.say("You wanna go somewhere?");
              break;
          case 2:
              this.say("Do you like to roleplay?");
              break;
          case 3:
              Server.game.informOthersOfMiscAction(this, null, "The " + this.name + " adjusts her bra.");
              break;
          case 4:
              Server.game.informOthersOfMiscAction(this, null, "The " + this.name + " looks at you seductively.");
              break;
          case 5:
              Server.game.informOthersOfMiscAction(this, null, "The " + this.name + " caresses her breasts in your direction.");
              break;
	  default:
              this.say("I don't need a script.");
              break;
	  }
  }

  protected String getRandomSayingWhenAttacked() {
      int x = Functions.rnd(0, 1);
      switch (x) {
      case 0:
          return "You bastard!  I'll rip your shitty eyes out!";
      case 1:
          return "What do you want to hit a woman for?";
      default:
          return "I don't need a script.";
      }
  }

  public void otherCharacterEntered(GameCharacter character, Location old_loc) throws IOException {
      if (character instanceof Law == false) {
        if (character != this.enemy) {
            this.sayTo(character, "You lookin' for a good time?");
        } else {
            this.sayTo(character, "You gonna be nice?");
        }
      }
  }

  public String getFightMethod() {
    return "slap";
  }

  public void seenShooting(GameCharacter shooter, GameCharacter target) throws IOException {
    Server.game.informOthersOfMiscAction(this, null, "The "+name+" runs away screaming like a tart.");
    this.move(); // Run away screaming
  }

  public boolean spokenTo(GameCharacter charac, String msg, boolean only_them) throws IOException {
      if (Functions.isNumeric(msg.replaceAll("\\$", ""))) {
          this.sayTo(charac, "Not much, but if you give it to me you can have me.");
          return true;
      } else if (msg.toUpperCase().indexOf("SUCK") >= 0 || msg.toUpperCase().indexOf("BLOW") >= 0 || msg.toUpperCase().indexOf("SWALLOW") >= 0) {
          this.sayTo(charac, "You dirty boy!");
          return true;
      } else if (msg.toUpperCase().indexOf("YES") >= 0 || msg.toUpperCase().indexOf("YEA") >= 0 || msg.toUpperCase().indexOf("SURE") >= 0 || msg.toUpperCase().indexOf("DO IT") >= 0) {
          this.sayTo(charac, "It's gonna cost you.");
          return true;
      } else if (msg.toUpperCase().indexOf("MMM") >= 0 || msg.toUpperCase().indexOf("BREASTS") >= 0 || msg.toUpperCase().indexOf("TIT") >= 0) {
          this.sayTo(charac, "You like the look of these?");
          return true;
      } else if (msg.toUpperCase().indexOf("LOVE") >= 0) {
          this.sayTo(charac, "Aww, you can marry me baby.");
          return true;
      } else if (msg.toUpperCase().indexOf("BITCH") >= 0) {
          this.sayTo(charac, "I ain't your bitch!");
          return true;
      } else if (msg.toUpperCase().indexOf("REMOVE") >= 0 || msg.toUpperCase().indexOf("TAKE OFF") >= 0) {
          this.sayTo(charac, "Maybe later.  It's too cold out here.");
          return true;
      } else if (msg.toUpperCase().indexOf("MUCH") >= 0) {
          this.sayTo(charac, "How much have you got?");
          return true;
      } else if (msg.toUpperCase().indexOf("SEX") >= 0) {
          this.sayTo(charac, "Would you like to get your hands on me?");
          return true;
      } else if (only_them && msg.toUpperCase().indexOf("FUCK") >= 0) {
          this.sayTo(charac, "Would you like to get your hands on me?");
          return true;
      } else if (msg.equalsIgnoreCase("NO")) {
          this.sayTo(charac, "Please yourself.");
          return true;
      } else if (Functions.isNumeric(msg)) {
          this.sayTo(charac, "Are you gonna give it to me?");
          return true;
      } else {
          if (only_them) {
              if (msg.toUpperCase().indexOf("FOLLOW") >= 0) {
                  this.sayTo(charac, "I gotta stay here or my Pimp will get angry.");
                  return true;
              } else {
                  int x = Functions.rnd(1, 2);
                  switch (x) {
                  case 1:
                      this.sayTo(charac, "Okay baby.");
                      break;
                  case 2:
                      this.sayTo(charac,
                                 "My, you've got big hands for a " +
                                 charac.name + ".");
                      break;
                  default:
                      this.sayTo(charac,
                                 "Ooh, my cracks kickin' off a right pong.");
                      break;
                  }
                  return true;
              }
          }
      }
      return super.spokenTo(charac, msg, only_them);
  }

  public void attackedBy(GameCharacter character, String method) throws IOException {
      super.attackedBy(character, method);

      if (this.getCurrentLocation().containsCharacter("PIMP") == false) {
          if (Functions.rnd(1, 3) == 1) {
              new Pimp(this, this.getCurrentLocation());
          }
      }
  }

  public boolean attemptBuyFrom(GameCharacter charac, String item_name) throws IOException {
      return false;
  }

  public void kissedBy(GameCharacter character) throws IOException {
      this.sayTo(character, "Hey, I hardly know you!  But if you give me some cash, we could get to know each other a little bit better...");
  }

  public void touched(GameCharacter character) throws IOException {
      this.sayTo(character, "I normally charge people to do that.");
  }

}
