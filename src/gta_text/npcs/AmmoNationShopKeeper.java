package gta_text.npcs;

import gta_text.items.AK47;
import gta_text.items.Item;
import gta_text.items.Knife;
import gta_text.items.Pistol;
import gta_text.items.Uzi;
import gta_text.locations.Location;
import java.io.FileNotFoundException;
import java.io.IOException;
import ssmith.lang.Functions;
import gta_text.items.Shotgun;
import gta_text.Server;
import gta_text.ServerConn;

public class AmmoNationShopKeeper extends MemberOfGroup implements ISeller {

  public AmmoNationShopKeeper(Location loc) throws FileNotFoundException, IOException {
      super(NonPlayerCharacter.AMMO_SHOPKEEPER, "Shopkeeper", loc, NonPlayerCharacter.AMMO_SHOPKEEPER, false);
  }

  protected void performRandomAction() throws IOException {
	  int x = Functions.rnd(1, 4);
	  switch(x) {
	  case 1:
              this.say("What can I get you?");
              break;
          case 2:
              this.say("Do you have any special requirement?");
              break;
          case 3:
              this.say("I was in 'Nam, y'know.");
              break;
          case 4:
              Server.game.informOthersOfMiscAction(this, null, "The shopkeeper polishes his shotgun.");
              break;
	  default:
	      this.say("I need a better script.");
              break;
	  }
  }

  public void givenCash(GameCharacter char_from, int amt) throws IOException {
      //super.givenCash(char_from, amt);
      this.sayTo(char_from, "Just buy something.");
  }

  protected String getRandomSayingWhenAttacked() {
    return "Get out of here motherfucker!";
  }

  public boolean shagAttemptedBy(GameCharacter character) throws IOException {
    this.say("Are you on heat or something?");
    return false;
  }

  public void otherCharacterEntered(GameCharacter character, Location old_loc) throws IOException {
    this.say("If you're looking for weapons, you've come to the right place.");
  }

  public void otherCharacterLeft(GameCharacter character, Location new_loc, boolean disappeared, String to_dir) throws IOException {
    this.say("What a tight bastard that was.");
  }

/*  public void seenShooting(GameCharacter shooter, GameCharacter target) throws IOException {
    this.say("Hey!  Get the fuck out of my shop!");
  }

  public void seenAttack(GameCharacter shooter, GameCharacter target) throws IOException {
    this.say("Ok!  Take it outside!");
  }
*/
  public boolean spokenTo(GameCharacter charac, String msg, boolean only_them) throws IOException {
      if (this.attemptBuyFrom(charac, msg)) {
          return true;
      } else if (containBuyKeyword(msg) || msg.toUpperCase().indexOf("GUNS") >= 0 || msg.toUpperCase().indexOf("RIFLE") >= 0) {
           this.sayTo(charac, this.getSalesPitch());
           return true;
      } else if (msg.toUpperCase().indexOf("MUCH") >= 0 || msg.toUpperCase().indexOf("COST") >= 0) {
          this.sayTo(charac, "Try buying something and I'll tell you if you've got enough.");
          return true;
      } else {
          if (only_them) {
              this.sayTo(charac, "You lookin' for something powerful?");
              return true;
          }
      }
      return super.spokenTo(charac, msg, only_them);
  }

  public String getSalesPitch() {
      return "I've got knives, pistols, uzis, shotguns and AK47s for sale.  What do you want to buy?";
  }

  public boolean attemptBuyFrom(GameCharacter charac, String item_name) throws IOException {
      if (item_name.equalsIgnoreCase("WEAPON")) {
          this.sayTo(charac, "You have to specify which weapon you want.");
          return true;
      } else {
          Item item = null;
          if (item_name.toUpperCase().indexOf("AK47") >= 0) {
              item = new AK47();
          } else if (item_name.toUpperCase().indexOf("SHOTGUN") >= 0) { // Must be before "GUN"
              item = new Shotgun();
          } else if (item_name.toUpperCase().indexOf("PISTOL") >= 0 || item_name.toUpperCase().indexOf("GUN") >= 0) {
              item = new Pistol();
          } else if (item_name.toUpperCase().indexOf("KNIFE") >= 0 || item_name.toUpperCase().indexOf("KNIVE") >= 0) {
              item = new Knife();
          } else if (item_name.toUpperCase().indexOf("UZI") >= 0) {
              item = new Uzi();
          }
          if (item != null) {
              if (charac.cash >= item.cost) {
                  charac.sendMsgToPlayer(ServerConn.ACTION_COL, "You buy a " + item.name + " for $" + item.cost + ".");
                  charac.addItem(item);
                  charac.cash -= item.cost;
              } else {
                  item.removeFromGame(false, false);
                  charac.sendMsgToPlayer(ServerConn.ERROR_COL, "You don't have enough money.  They cost $" + item.cost + ".");
              }
              return true;
          }
      }
      return false;
  }

}
