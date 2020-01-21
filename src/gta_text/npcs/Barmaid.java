package gta_text.npcs;

import gta_text.items.Coke;
import gta_text.items.Item;
import gta_text.items.Lager;
import gta_text.items.Shot;
import gta_text.locations.Location;
import java.io.FileNotFoundException;
import java.io.IOException;
import ssmith.lang.Functions;
import gta_text.items.GAndT;
import gta_text.items.Beer;
import gta_text.items.PepperSpray;
import gta_text.*;

public class Barmaid extends CanBeRobbed implements ISeller {

  public Barmaid(String name, Location loc) throws FileNotFoundException, IOException {
      super(NonPlayerCharacter.BARMAID, name, loc);

      this.addItem(new PepperSpray());
  }

  protected void performRandomAction() throws IOException {
          int x = Functions.rnd(1, 5);
          switch(x) {
          case 1:
              this.say("Can I get you something to drink?");
              break;
          case 2:
              this.say("I've got to wash these glasses first.");
              break;
          case 3:
              Server.game.informOthersOfMiscAction(this, null, "The " + this.name + " wipes up some glasses.");
              break;
          case 4:
              Server.game.informOthersOfMiscAction(this, null, "The " + this.name + " collects empty glasses from the tables.");
              break;
          case 5:
              this.say("Is this your first time here?");
              break;
          default:
              this.say("I need a better script.");
              break;
          }
  }

  public void otherCharacterEntered(GameCharacter character, Location old_loc) throws IOException {
      if (character != this.enemy) {
          if (this.getCurrentLocation().is_open) {   
              this.sayTo(character, "Would you like a drink?");
          }
      } else {
          this.sayTo(character, "Hey, you're barred from here!");
      }
  }

  public void givenCash(GameCharacter char_from, int amt) throws IOException {
      char_from.sendMsgToPlayer(ServerConn.ACTION_COL, "The barmaid leans towards you, giving you a good view of her chest.");
      this.sayTo(char_from, "Maybe I'll see you later?");
  }

  protected String getRandomSayingWhenAttacked() {
    return "You don't wanna mess with me!";
  }

  public boolean shagAttemptedBy(GameCharacter character) throws IOException {
    this.say("Hey, I'm here to serve drinks.  I don't do extras.");
    return false;
  }

  public void seenShooting(GameCharacter shooter, GameCharacter target) throws IOException {
      if (this.isEnemyInRoom() == false) {
          this.say("Leave all guns at the door!");
      }
  }

  public void seenAttack(GameCharacter shooter, GameCharacter target) throws IOException {
	  if (Functions.rnd(1, 2) == 1) {
		  this.say("Ok!  Take it outside!  We don't want any trouble in here.");
	  }
  }

  public String getSalesPitch() {
      return "A shot, G&T, lager or a beer?";
  }

  public boolean spokenTo(GameCharacter charac, String msg, boolean only_them) throws IOException {
      if (this.attemptBuyFrom(charac, msg)) {
          return true;
      } else if (super.containBuyKeyword(msg) || msg.toUpperCase().indexOf("DRINK") >= 0) {
          this.sayTo(charac, this.getSalesPitch());
          return true;
      } else if (msg.toUpperCase().indexOf("SEX") >= 0) {
          this.sayTo(charac, "I think you want to be down the Red Light District.");
          return true;
      } else if (msg.toUpperCase().indexOf("MUCH") >= 0) {
          this.sayTo(charac, "Try buying something and I'll tell you if you've got enough.");
          return true;
      } else {
          if (only_them) {
              if (msg.equalsIgnoreCase("NO") || msg.toUpperCase().startsWith("NO ")) {
                  this.sayTo(charac, "Please yourself.");
                  return true;
              }
          }
      }
      return super.spokenTo(charac, msg, only_them);
  }

  public boolean attemptBuyFrom(GameCharacter charac, String item_name) throws IOException {
          Item item = null;
          if (item_name.toUpperCase().indexOf("LAGER") >= 0) {
              item = new Lager();
          } else if (item_name.toUpperCase().indexOf("BEER") >= 0 ||
                     item_name.toUpperCase().indexOf("DRINK") >= 0) {
              item = new Beer();
          } else if (item_name.toUpperCase().indexOf("SHOT") >= 0) {
              item = new Shot();
          } else if (item_name.toUpperCase().indexOf("COKE") >= 0 || item_name.toUpperCase().indexOf("SODA") >= 0) {
              item = new Coke();
          } else if (item_name.toUpperCase().indexOf("G&T") >= 0 ||
                     item_name.toUpperCase().indexOf("GIN") >= 0) {
              item = new GAndT();
          }

          if (item != null) {
              if (charac.cash >= item.cost) {
                  charac.sendMsgToPlayer(ServerConn.ACTION_COL, "You buy a " + item.name + " for $" +
                                         item.cost + ".");
                  charac.addItem(item);
                  charac.cash -= item.cost;
              } else {
                  item.removeFromGame(false, false);
                  charac.sendMsgToPlayer(ServerConn.ERROR_COL,
                          "You don't have enough money.  They cost $" + item.cost +
                          ".");
              }
              return true;
          }
          return false;
  }

  public void kissedBy(GameCharacter character) throws IOException {
      this.sayTo(character, "Hey, you could at least buy me a drink first.");
  }


}
