package gta_text.npcs;

import ssmith.lang.Functions;
import gta_text.locations.Location;
import gta_text.AbstractGame;
import gta_text.Server;
import gta_text.Game;
import gta_text.items.Corpse;
import gta_text.items.Item;
import gta_text.items.RangedWeapon;
import java.util.Enumeration;
import java.util.Vector;
import java.io.IOException;
import java.util.ArrayList;
import gta_text.items.ISpecialAttack;
import ssmith.util.CSVString;
import ssmith.util.Interval;
import gta_text.ServerConn;

public abstract class GameCharacter {

  public static final int FIST_DAMAGE=4, MAX_THIRST=600, MAX_HUNGER = 1000, MAX_HEALTH=100;

  // Gangs
  public static final int GANG_NONE=0, GANG_YAKUZA = 1, GANG_MAFIA = 2, GANG_OWN_GANG = -1;

  public String name;
  private String desc = "They look average.";
  private int health;// Needs to be private!
  protected int max_health;
  public int cash, accuracy;
  private int wanted_level=0;
  public boolean male;
  private Vector items = new Vector();
  private Location current_location; // Must be private so we can't directly set it.  We need to remove us from the location list as well
  public Item current_item;
  public int last_day_period = -1;
  public int last_weather_type = 0;
  private String last_dir = "";
  public int drunkness = 0;
  public ArrayList other_names = new ArrayList();
  public boolean blinded = false;
  public long blinded_end_time;
  public int fist_damage = FIST_DAMAGE;
  private Interval reduce_wanted_interval = new Interval(1000*60*4);
  private int thirst = 350, hunger = 500;
  public boolean bounty_hunter = false, remember_enemy = false;
  public int gang = GANG_NONE;

  public abstract void sendMsgToPlayer(String col, String msg) throws IOException;

  // These functions should be the characters response.
  public abstract boolean spokenTo(GameCharacter character, String msg, boolean only_them) throws IOException;
  public abstract boolean attemptBuyFrom(GameCharacter character, String item_name) throws IOException;
  public abstract void attackedBy(GameCharacter character, String method) throws IOException;
  public abstract void shotAtBy(GameCharacter character, RangedWeapon item) throws IOException;
  public abstract void seenShooting(GameCharacter shooter, GameCharacter target) throws IOException;
  public abstract void seenAttack(GameCharacter attacker, GameCharacter defender) throws IOException;
  public abstract void otherCharacterEntered(GameCharacter character, Location old_loc) throws IOException;
  public abstract void otherCharacterLeft(GameCharacter character, Location new_loc, boolean disappeared, String to_dir) throws IOException;
  protected abstract String getFightMethod();
  public abstract boolean shagAttemptedBy(GameCharacter character) throws IOException;
  public abstract void kissedBy(GameCharacter character) throws IOException;
  public abstract void greeted(GameCharacter character) throws IOException;
  public abstract void touched(GameCharacter character) throws IOException;
  public abstract void mugged(GameCharacter charac) throws IOException;

  public GameCharacter(String name, Location loc, int health, int cash, boolean male, boolean show_loc_desc) throws IOException {
      this.name = name;
      this.setHealth(health);
      this.max_health = health;
      this.cash = cash;
      this.male = male;

      Server.game.addCharacter(this);
      //Server.write("Character " + name + " created.");
      if (loc != null) {
          this.setCurrentLocation(loc, "", true, show_loc_desc);
      }
  }

  public void process() throws Exception {
      if (this.blinded) {
          if (this.blinded_end_time < System.currentTimeMillis()) {
              this.blinded = false;
              this.sendMsgToPlayer(ServerConn.NORMAL_COL, "The pepper spray has worn off.");
          }
      }

      if (wanted_level > 0) {
          if (reduce_wanted_interval.hitInterval()) {
              this.incWantedLevel(-1);
          }
      }

      if (thirst > 0) {
          thirst--;
      }
      if (thirst < 100) {
          if (Functions.rnd(0, 15) == 0) {
              this.sendMsgToPlayer(ServerConn.NORMAL_COL, "You are thirsty.");
          }
      }

      if (hunger > 0) {
          hunger--;
      }
      if (hunger < 100) {
          if (Functions.rnd(0, 20) == 0) {
              this.sendMsgToPlayer(ServerConn.NORMAL_COL, "You are hungry.");
          }
      }
  }

  public String getDesc() {
      return this.desc;
  }

  public void setDesc(String d) {
      this.desc = d;
  }

  public Location getCurrentLocation() {
    return this.current_location;
  }

  public void removeFromGame() throws IOException {
      Server.game.removeCharacter(this);
      this.setCurrentLocation(null, "", true, true);
      //Remove all items.
      Enumeration enumr = this.items.elements();
      while (enumr.hasMoreElements()) {
          Item item = (Item)enumr.nextElement();
          item.removeFromGame(false, false);
      }
      //Server.write("Character " + name + " removed.");
  }

  /**
   * Pass this null if a character has left the game.
   * @param loc
   * @throws IOException
   */
  public void setCurrentLocation(Location loc, String from_dir, boolean override, boolean show_desc) throws IOException {
      // Check it's open
      if (loc != null) {
          if (!loc.is_open && override == false && Server.cheat_on == false) {
              if (this.getCurrentLocation().internal == false) { // So they don' get trapped inside a closed building
                  this.sendMsgToPlayer(ServerConn.NORMAL_COL, "You cannot go in that direction.  " +
                                       loc.closed_reason);
                  return;
              }
          }
      }

      Location old_loc = this.getCurrentLocation();
      Enumeration enumr;
      if (old_loc != null) {
          //this.sendMsgToPlayer(ServerConn.ACTION_COL, "You go " + dir + "."); todo

          // Remove us from this location
          this.current_location.removeCharacter(this);

          // Tell other characters of us leaving
          enumr = this.current_location.getCharacterEnum();
          while (enumr.hasMoreElements()) {
              GameCharacter character = (GameCharacter) enumr.nextElement();
              if (character != this && character.blinded == false) {
                  character.otherCharacterLeft(this, loc, loc == null, from_dir);
              }
          }
      }

      this.current_location = loc;
      if (loc != null) {
          loc.addCharacter(this);

          if (show_desc) {
              this.sendMsgToPlayer(ServerConn.NORMAL_COL, this.getCurrentLocation().getDesc(this));
          }

          // Tell other characters of us arriving
          enumr = this.current_location.getCharacterEnum();
          while (enumr.hasMoreElements()) {
              GameCharacter character = (GameCharacter) enumr.nextElement();
              if (character != this && character.blinded == false) {
                  character.otherCharacterEntered(this, old_loc);
              }
          }
      } else {
          //System.out.println("** Location for " + this.name + " set to null.");
      }
  }

  public void move(String dir) throws IOException {
      if (this.getCurrentLocation().all_exits.containsKey(dir.toUpperCase())) {
          String new_loc_no = (String) this.getCurrentLocation().all_exits.get(dir.toUpperCase());
          Location loc = (Location) Server.game.locations.get(new_loc_no.toUpperCase());
          // Check its wanderable
          if (this instanceof NonPlayerCharacter) {
              if (loc.wanderable == false && this.getCurrentLocation().wanderable == true) {
                  return;
              }
          }
          this.last_dir = dir;
          this.setCurrentLocation(loc, dir, false, true);
    } else {
      this.sendMsgToPlayer(ServerConn.ERROR_COL, "You cannot go in that direction.");
    }
  }

  public void move() throws IOException {
        if (this.getCurrentLocation() != null) {
            this.move(this.getCurrentLocation().getRandomExitDir());
        }
    }

  public boolean exit() throws IOException {
      if (this.last_dir.length() > 0) {
          if (last_dir.equalsIgnoreCase("N")) {
              this.move("S");
          } else if (last_dir.equalsIgnoreCase("S")) {
              this.move("N");
          } else if (last_dir.equalsIgnoreCase("E")) {
              this.move("W");
          } else if (last_dir.equalsIgnoreCase("W")) {
              this.move("E");
          }
          return true;
      } else {
          return false;
      }

  }

  public void setHealth(int amt) {
    this.health = amt;
  }

  public void eat(String item_name, String style) throws IOException {
      if (this.hasItem(item_name)) {
          this.eat(this.getCharactersItem(item_name), style);
      } else if (this.getCurrentLocation().containsItem(item_name)) {
          this.eat(this.getCurrentLocation().getItem(item_name), style);
      } else if (item_name.equalsIgnoreCase("URINE")) {
          this.sendMsgToPlayer(ServerConn.NORMAL_COL, "You're not *that* thirsty.");
      } else {
        this.sendMsgToPlayer(ServerConn.ERROR_COL, "You can't see a " + item_name + ".");
      }
  }

  public boolean eat(Item item, String style) throws IOException {
      if (item.edible) {
          Server.game.informOthersOfMiscAction(ServerConn.ACTION_COL, this, null, "The " + this.name + " " + style + "s a "+ item.name+".");
          if (item.eat(this, style)) {
              if (this.hasItem(item)) {
                  item.removeFromGame(false, true);
              } else if (this.getCurrentLocation().containsItem(item)) {
                  item.removeFromGame(true, false);
              }
              return true;
          } else {
              Server.game.informOthersOfMiscAction(ServerConn.ACTION_COL, this, null, "The " + this.name + " tries and fails to " + style + " a "+ item.name+".");
          }
      } else {
          this.sendMsgToPlayer(ServerConn.ERROR_COL, "You want to " + style + " that?");
      }
      return false;
  }

  public void increaseHealth(int amt, GameCharacter killer) throws
          IOException {
      if (amt != 0) {
          this.health += amt;
          if (this.health > MAX_HEALTH) {
              this.health = MAX_HEALTH;
          }
          if (amt < 0) {
              this.sendMsgToPlayer(ServerConn.COMBAT_COL, "* You lose " + ( -amt) +
                                   " health.  You have " + this.health +
                                   " left. *");
              Server.game.informOthersOfMiscAction(ServerConn.COMBAT_COL, this, null, "The " + this.name + " loses " + (-amt) + " health; they have " +
                                   this.getHealth() + " left.");
          } else {
              if (health > max_health) {
                  health = max_health;
              }
          }
          if (this.health <= 0) {
              this.sendMsgToPlayer(ServerConn.COMBAT_COL, "** You have died **".toUpperCase());
              Server.game.informOthersOfMiscAction(ServerConn.COMBAT_COL, this, null,
                      ("The " + this.name + " has died.").toUpperCase()); // Should be before we inc wanted level
              if (this.getCurrentLocation() != null) {
                  Server.game.callTheCops(this.getCurrentLocation());
/*                  if (this.getCurrentLocation().containsCharacter("POLICEMAN") ||
                      this.getCurrentLocation().internal == false) {
                      killer.incWantedLevel(1);
                  }*/
                  Server.game.last_death_description =
                          "In San Torino, the body of a " + this.name +
                          " was found at the " +
                          this.getCurrentLocation().getName() +
                          " just after " +
                          Server.game.game_time.getTimeAsString() +
                          ".";
                  if (killer != null) {
                      Server.game.last_death_description =
                              Server.game.last_death_description +
                              "  A " + killer.name +
                              " was seen running from the scene.";
                  } else {
                      Server.game.last_death_description =
                              Server.game.last_death_description +
                              "  Police are looking for witnesses.";
                  }
                  this.getCurrentLocation().cash += (this.cash/2); // Reduce inflation
                  this.cash = 0;
                  this.getCurrentLocation().addItems(this.items);
                  Corpse c = new Corpse(this);
                  this.getCurrentLocation().addItem(c);
              } else {
                  System.err.println("Error: " + this.name + " has null location!");
                  this.removeFromGame();
              }
              Server.game.last_death_time = System.currentTimeMillis();
              this.items.clear();
              this.removeFromGame();

              if (killer != null) {
                  if (killer.bounty_hunter) {
                      killer.sendMsgToPlayer(ServerConn.ACTION_COL, "You hear a voice on your radio:-");
                      if (this.wanted_level > 0) {
                          int c = this.wanted_level * 30;
                          killer.sendMsgToPlayer(ServerConn.SPEECH_COL, "'Well done.  You'll get $" + c + " bounty for that kill.'");
                          killer.cash += c;
                      } else {
                          killer.sendMsgToPlayer(ServerConn.SPEECH_COL, "'They were innocent!  We're comin' after you!'");
                          killer.incWantedLevel(1);
                      }
                  } else {
                      if (killer.gang != 0 && this.gang != 0) {
                          if (killer.gang != this.gang) {
                              killer.sendMsgToPlayer(ServerConn.NORMAL_COL, "You receive $40 from your gang boss." );
                              killer.cash += 40;
                          } else {
                              killer.sendMsgToPlayer(ServerConn.ERROR_COL, "You have been thrown out of your gang!".toUpperCase());
                              killer.gang = GANG_OWN_GANG; // So everyone attacks them
                          }
                      }
                      if (this instanceof Law) {
                          killer.incWantedLevel(2);
                      } else {
                          killer.incWantedLevel(1);
                      }
                  }
              }
          }
      }
  }

  public void say(String msg) throws IOException {
      this.sendMsgToPlayer(ServerConn.SPEECH_COL, "You say '" + expandForDrunkeness(msg) + "' to no-one in particular.");
      Server.game.informOthersOfMiscAction(ServerConn.SPEECH_COL, this, null, "The " + this.name + " says '" + msg +
              "'.");
  }

  public void sayTo(GameCharacter to_char, String msg) throws IOException {
      this.sayTo(to_char, msg, false);
  }

  public void sayTo(GameCharacter to_char, String msg, boolean whisper) throws IOException {
    try {
        if (this.getCurrentLocation() == null) {
            this.removeFromGame();
            return;
        }
        if (this.getCurrentLocation().containsCharacter(to_char.name)) {
            this.sendMsgToPlayer(ServerConn.SPEECH_COL, "You say '" + expandForDrunkeness(msg) +
                                 "' to the " + to_char.name +
                                 ".");
            if (!whisper) {
                Server.game.informOthersOfMiscAction(ServerConn.SPEECH_COL, this, to_char, "The " + this.name + " says '" + msg + "' to the " + to_char.name + ".");
            }
            if (this instanceof PlayersCharacter || to_char instanceof PlayersCharacter) { // SO NPC's don't get into a loop talking to each other
                to_char.spokenTo(this, msg, true);
            }
        } else {
            this.sendMsgToPlayer(ServerConn.ERROR_COL, "They are not here.");
        }
    } catch (IOException ex) {
        Server.HandleError(ex);
    }
  }

  private String expandForDrunkeness(String msg) {
      if (Functions.rnd(1, 100) <= this.drunkness) {
          return msg.replaceFirst(" ", "- [hic] -");
      }
      return msg;
  }


  public void give(String item_name, GameCharacter char_to) throws IOException {
      try {
          // They are giving them some money
          String s_c = item_name.replaceAll("\\$", "");
          int c = Integer.parseInt(s_c);
          if (this.cash >= c || this instanceof NonPlayerCharacter) {
              if (this instanceof PlayersCharacter) {
                  this.cash -= c;
              }
              char_to.cash += c;
              this.sendMsgToPlayer(ServerConn.ACTION_COL, "You give $" + c + " to the " + char_to.name +
                                   ".");
              Server.game.informOthersOfMiscAction(this, char_to,
                      "The " + this.name + " gives some cash to the " + char_to.name +
                      ".");
              char_to.givenCash(this, c); // Should be after we inform others, so that players see what this character is responding to.
          } else {

              this.sendMsgToPlayer(ServerConn.ERROR_COL, "You don't have that kind of money.");
          }
          return;
      } catch (java.lang.NumberFormatException ex) {
          Item item = this.getCharactersItem(item_name);
          if (item != null) {
              this.give(item, char_to);
          } else if (item_name.equalsIgnoreCase("CASH") || item_name.equalsIgnoreCase("MONEY")) {
              this.sendMsgToPlayer(ServerConn.ERROR_COL, "Try 'GIVE [amount] TO [character]'.  Enter 'HELP GIVE' for more help.");
          } else {
              this.sendMsgToPlayer(ServerConn.ERROR_COL, "You don't have a " + item_name + " to give away.");
          }
      }
  }

  public void give(Item item, GameCharacter char_to) throws IOException {
      this.removeItem(item);
      this.sendMsgToPlayer(ServerConn.ACTION_COL, "You give the " + item.name + " to " + char_to.name + ".");
      char_to.addItem(item); // Must be before we call given() on the receiving char
      char_to.given(this, item);
      Server.game.informOthersOfMiscAction(ServerConn.ACTION_COL, this, char_to,
              this.name + " gives a " +
              item.name + " to the " + char_to.name +
              ".");
  }

  public abstract void given(GameCharacter char_from, Item item) throws IOException;

  public abstract void givenCash(GameCharacter char_from, int amt) throws IOException;

  public int getHealth() {
    return this.health;
  }

  public int getMaxHealth() {
    return this.max_health;
  }

  public boolean hasItem(Item item) {
      return this.items.contains(item);
  }

  public boolean hasItem(String name) {
      return getCharactersItem(name) != null;
  }

  /*public Item getItem(String name) {
      int idx = 1;
      if (name.indexOf(".") >= 0) {
          CSVString csv = new CSVString(name, ".");
          String no = csv.getFirstSection();
          if (Functions.isNumeric(no)) {
              idx = new Integer(no).intValue();
          }
          name = csv.getSection(1);
      }

      for(int i=0 ; i<items.size() ; i++) {
          Item item = (Item)items.get(i);
          if (item.name.toUpperCase().indexOf(name.toUpperCase()) >= 0) {
              idx--;
              if (idx == 0) {
                  return item;
              }
          }
      }
      return null;
  }
*/
  public void addItem(Item item) {
      this.items.add(item);
      if (this.current_item == null) {
          this.current_item = item;
      }
  }

  public int getNoOfItems() {
      return this.items.size();
  }

  public Item getCharactersItem(String name) {
      name = name.replaceFirst("a ", "");
      int idx = 1;
      if (name.indexOf(".") >= 0) {
          CSVString csv = new CSVString(name, ".");
          String no = csv.getFirstSection();
          if (Functions.isNumeric(no)) {
              idx = new Integer(no).intValue();
          }
          name = csv.getSection(1);
      }

      for(int i=0 ; i<items.size() ; i++) {
          Item item = (Item)items.get(i);
          if (item.name.toUpperCase().indexOf(name.toUpperCase()) >= 0) {
              idx--;
              if (idx == 0) {
                  return item;
              }
          }
      }
      return null;
  }

  public boolean pickupItem(String item_name) throws IOException {
      Location loc = this.getCurrentLocation();
      Item item = (Item) loc.getItem(item_name);
      if (item.carryable) {
          loc.removeItem(item);
          this.addItem(item);
          if (this.current_item == null) {
              this.current_item = item;
          }
          this.sendMsgToPlayer(ServerConn.ACTION_COL, "You pick up the " + item.name + ".");
          Server.game.informOthersOfMiscAction(this, null, "The " + this.name +
                  " picks up the " + item.name + ".");
          return true;
      } else {
          this.sendMsgToPlayer(ServerConn.ACTION_COL, "You cannot take the " + item.name + ".");
          Server.game.informOthersOfMiscAction(this, null, "The " + this.name +
                  " tries and fails to take the " + item.name + ".");
          return false;
      }

  }

  public void getCash() throws IOException {
      Location loc = this.getCurrentLocation();
      if (loc.cash > 0) {
          this.sendMsgToPlayer(ServerConn.ACTION_COL, "You pick up $" + loc.cash + " cash.");
          Server.game.informOthersOfMiscAction(ServerConn.ACTION_COL, this, null, "The " + this.name + " picks up some cash.");
          this.cash += loc.cash;
          loc.cash = 0;
      } else {
          this.sendMsgToPlayer(ServerConn.ERROR_COL, "There is no money here.");
      }
  }

  public void removeItem(String item_name) {
      Item item = this.getCharactersItem(item_name);
      if (item != null) {
          this.removeItem(item);
      }
  }

  public void removeItem(Item item) {
      this.items.remove(item);
      if (this.current_item == item) {
          this.current_item = null;
      }
  }

  public String getItemList() {
	  if (this.items.size() > 0) {
          StringBuffer str = new StringBuffer();
          for(int i=0 ; i<items.size() ; i++) {
              Item item = (Item)items.get(i);
			  str.append(item.name + Server.CR);
		  }
          return str.toString();
	  }
      return "";
  }

  public void fight(GameCharacter defender, String method) throws IOException {
      if (this.blinded == false) {
          Server.game.fight_occurred = true;

          int damage = 0;
          if (this.current_item != null) {
              this.sendMsgToPlayer(ServerConn.COMBAT_COL, "You " + method + " the " + defender.name +
                                   " with your " + this.current_item.name + ".");
              defender.sendMsgToPlayer(ServerConn.COMBAT_COL, "The " + this.name +
                      " attacks you with a " +
                      current_item.name + "."); // Must be before the special attack!
              Server.game.informOthersOfAttack(this, defender,
                              this.current_item.name);
              if (this.current_item instanceof ISpecialAttack) {
                  ISpecialAttack sa = (ISpecialAttack)this.current_item;
                  sa.attack(this, defender);
              } else {
                  int max_dam = this.current_item.max_damage;
                  if (max_dam < GameCharacter.FIST_DAMAGE) {
                      max_dam = FIST_DAMAGE;
                  }
                  damage = Functions.rnd(1, max_dam);
              }
          } else {
              this.sendMsgToPlayer(ServerConn.COMBAT_COL, "You " + method + " the " + defender.name + ".");
              damage = Functions.rnd(1, fist_damage);
              defender.sendMsgToPlayer(ServerConn.COMBAT_COL, "The " + this.name + " " + method + "s you.");
              Server.game.informOthersOfAttack(this, defender, "");
          }

//    if (damage > 0) {
          /*this.sendMsgToPlayer("You do " + damage + " damage; they have " +
                               (defender.getHealth() - damage) +
                               " health left. Blood spurts everywhere!");*/
          defender.increaseHealth( -damage, this);
          if (defender.getHealth() > 0) {
              if (this.current_item != null) {
                  defender.attackedBy(this, this.current_item.name);
              } else {
                  defender.attackedBy(this, "");
              }
          }
      } else {
          this.sendMsgToPlayer(ServerConn.ERROR_COL, "You can't see anything.");
      }
  }

  public void shoot(GameCharacter target) throws IOException {
      Item weapon = this.current_item;
      if (weapon instanceof RangedWeapon) {
          Server.game.fight_occurred = true;
          RangedWeapon wep = (RangedWeapon) weapon;
          if (wep.ammo > 0) {
              this.sendMsgToPlayer(ServerConn.COMBAT_COL, "You shoot " + target.name + " with your " +
                                   wep.name + ".");
              target.sendMsgToPlayer(ServerConn.COMBAT_COL, "The " + this.name + " shoots at you with a "+wep.name+".");
              Server.game.informOthersOfShooting(this, target, wep);
              this.sendMsgToPlayer(ServerConn.ACTION_COL, Game.getRandomShootingDescription());
              int damage = Functions.rnd(1, wep.max_damage);
              /*this.sendMsgToPlayer("You do " + damage + " damage; they have " +
                                   (target.getHealth() - damage) +
                                   " health left.");*/
              target.increaseHealth( -damage, this);
              if (target.getHealth() > 0) {
                  target.shotAtBy(this, wep);
              }
          } else {
              this.sendMsgToPlayer(ServerConn.COMBAT_COL, "The " + wep.name +
                                   " goes 'click'.  You need some ammo.");
          }
      } else {
          this.sendMsgToPlayer(ServerConn.ERROR_COL, "You're not using a gun!");
      }
  }

  public void browse() throws IOException {
      Enumeration enumr = this.getCurrentLocation().getCharacterEnum();
      while (enumr.hasMoreElements()) {
          GameCharacter charac = (GameCharacter) enumr.nextElement();
          if (charac instanceof ISeller) {
              ISeller seller = (ISeller) charac;
              charac.sayTo(this, seller.getSalesPitch());
              //Server.game.informOthersOfMiscAction(this, null, "The " + name + " browses around the shop.");
              return;
          }
      }

      // If we get here then there's no-one selling
      this.sendMsgToPlayer(ServerConn.ERROR_COL, "There's no-one selling anything here.");
  }

  public void setBlinded(long time) {
      this.blinded = true;
      this.blinded_end_time = System.currentTimeMillis() + time;
  }

  protected boolean containsSwearing(String msg) {
      if (msg.toUpperCase().indexOf("BASTARD") >= 0 || msg.toUpperCase().indexOf("FUCK") >= 0 || msg.toUpperCase().indexOf("SHIT") >= 0) {
          return true;
      }
      return false;
  }

  protected boolean containsRobbery(String msg) {
      if ((msg.toUpperCase().indexOf("GIVE") >= 0 || msg.toUpperCase().indexOf("HAND") >= 0) && (msg.toUpperCase().indexOf("CASH") >= 0 || msg.toUpperCase().indexOf("MONEY") >= 0)) {
          return true;
      } else if (msg.toUpperCase().indexOf("ROBBERY") >= 0) {
          return true;
      } else {
          return false;
      }
  }

  public void flash() throws IOException {
      this.sendMsgToPlayer(ServerConn.ACTION_COL, "You flash.");
      Server.game.informOthersOfMiscAction(ServerConn.ACTION_COL, this, null, "The " + this.name + " suddenly opens up his coat and shows you his shrivelled up penis.");
  }

  public void drop(String item_name) throws IOException {
      if (item_name.equalsIgnoreCase("ALL")) {
          //for (int i = 0; i < this.getNoOfItems(); i++) {
          while (this.items.size() > 0) {
              Item item = (Item)this.items.get(0);
              this.drop(item);
          }
      } else if (this.hasItem(item_name)) {
          Item item = (Item)this.getCharactersItem(item_name);
          drop(item);
      } else {
          this.sendMsgToPlayer(ServerConn.ERROR_COL, "You aren't carrying a " + item_name + ".");
      }
  }

  public void drop(Item item) throws IOException {
      if (this.hasItem(item)) {
          this.sendMsgToPlayer(ServerConn.ACTION_COL, "You drop the " + item.name + ".");
          if (this.current_item == item) {
              this.current_item = null;
          }
          this.removeItem(item);

          // Check it's not an empty gun
          if (item instanceof RangedWeapon && item.ammo == 0) {
              item.removeFromGame(false, false);
          } else {
              Location loc = this.getCurrentLocation();
              loc.addItem(item);
              Server.game.informOthersOfMiscAction(ServerConn.ACTION_COL, this, null, "The " + this.name + " drops a " + item.name + " on the floor.");
          }
      } else {
          this.sendMsgToPlayer(ServerConn.ERROR_COL, "You aren't carrying a " + item.name + ".");
      }
  }

  public void junkItem(String item_name) throws IOException {
      if (this.hasItem(item_name)) {
          Item item = (Item)this.getCharactersItem(item_name);
          if (item.permanent == false) {
              if (this.current_item == item) {
                  this.current_item = null;
              }
              this.removeItem(item_name.toUpperCase());
              item.removeFromGame(false, false);
              this.sendMsgToPlayer(ServerConn.ACTION_COL, "You destroy the " + item.name + ".");
              Server.game.informOthersOfMiscAction(ServerConn.ACTION_COL, this, null, "The " + this.name +
                                               " destroys the " + item.name + ".");
          } else {
              this.sendMsgToPlayer(ServerConn.ERROR_COL, "You cannot destroy the " + item.name + ".");
          }
      } else {
          this.sendMsgToPlayer(ServerConn.ERROR_COL, "You aren't carrying a " + item_name + ".");
      }
  }

  public void ensureWantedLevel(int w) throws IOException {
      if (this instanceof Law == false) {
          if (this.wanted_level < w) {
              this.wanted_level = w;
              this.sendMsgToPlayer(ServerConn.NORMAL_COL, ("Your wanted level is now " + w + ".").toUpperCase());
          }
      }
  }

  public void incWantedLevel(int w) throws IOException {
      // Don't reduce wanted level if we can be sene by a copper.
      if (w < 0 && this.getCurrentLocation().containsCharacter(Policeman.NAME)) {
          return;
      }
      if (this instanceof Law == false) {
          if (w > 0) {
              if (this.bounty_hunter) {
                  w = w * 2;
                  this.bounty_hunter = false;
                  this.sendMsgToPlayer(ServerConn.NORMAL_COL, "You are no longer a bounty hunter.");
              }
          }

          this.wanted_level += w;
          if (this.wanted_level > 0) {
              String s = "Your wanted level is now " +
                         this.wanted_level +
                         ".";
              if (w > 0) {
                  s = s.toUpperCase();
              }
              this.sendMsgToPlayer(ServerConn.NORMAL_COL, s);

              if (this.wanted_level >= AbstractGame.CALL_ARMY_IN_LEVEL) {
                  Server.game.callTheArmy(this.getCurrentLocation());
              }
          } else {
              this.sendMsgToPlayer(ServerConn.NORMAL_COL, "You are no longer wanted by the police.");
              this.wanted_level = 0; // In case it's gone negative FSR.
          }
      }
  }

  public int getWantedLevel() {
      return this.wanted_level;
  }

  public void greet(GameCharacter charac) throws IOException {
      this.sendMsgToPlayer(ServerConn.ACTION_COL, "You greet the " + charac.name + ".");
      //charac.sendMsgToPlayer("The " + this.name + " greets you.");
      charac.greeted(this);
  }

  public void touch(GameCharacter charac) throws IOException {
      this.sendMsgToPlayer(ServerConn.ACTION_COL, "You touch the " + charac.name + ".");
      charac.touched(this);
  }

  public void dance() throws IOException {
      this.sendMsgToPlayer(ServerConn.ACTION_COL, "You dance around seductively.");
      Server.game.informOthersOfMiscAction(ServerConn.ACTION_COL, this, null, "The " + this.name +
                      " dances seductively.");
  }

  public void reduceThirst(int amt) {
      this.thirst += amt; // Notice we actually increase thirst.
      if (thirst > MAX_THIRST) {
          thirst = MAX_THIRST;
      }
  }

  public void reduceHunger(int amt) {
      this.hunger += amt; // Notice we actually increase thirst.
      if (hunger > MAX_HUNGER) {
          hunger = MAX_HUNGER;
      }
  }

  public void kiss(GameCharacter character) throws IOException {
      if (character != this) {
          this.sendMsgToPlayer(ServerConn.ACTION_COL, "You kiss the " + character.name + ".");
          Server.game.informOthersOfMiscAction(this, character, "The " + this.name +
                  " kisses the " + character.name + ".");
          character.kissedBy(this);
      } else {
          this.sendMsgToPlayer(ServerConn.ERROR_COL, "You want to kiss yourself?");
      }

  }

  public void read(String item_name) throws IOException {
      if (this.blinded == false) {
          Location loc = this.getCurrentLocation();
          // Is it an item on the floor?
          if (loc.containsItem(item_name)) {
              Item item = loc.getItem(item_name);
              this.sendMsgToPlayer(ServerConn.ACTION_COL, item.read(this));
              Server.game.informOthersOfMiscAction(this, null,
                      "The " + this.name +
                      " reads the " + item.name + ".");
              return;
          }
          // Is it one we are carrying?
          if (this.hasItem(item_name.toUpperCase())) {
              Item item = (Item)this.getCharactersItem(item_name);
              this.sendMsgToPlayer(ServerConn.ACTION_COL, item.getDesc());
              Server.game.informOthersOfMiscAction(this, null,
                      "The " + this.name +
                      " reads their " + item.name + ".");
              return;
          }
          this.sendMsgToPlayer(ServerConn.ERROR_COL, "I can't see a " + item_name + " here.");
      } else {
          this.sendMsgToPlayer(ServerConn.ERROR_COL, "You can't see anything at the moment!");
      }
  }

  /**
   * Override if reqd.
   * @param character
   */
  public void seenCharacterUseBed(GameCharacter character) {

  }

}
