package gta_text.npcs;

import ssmith.io.TextFile;
import ssmith.lang.Functions;
import gta_text.locations.Location;
import gta_text.*;
import gta_text.items.Item;
import gta_text.items.RangedWeapon;
import java.io.IOException;
import gta_text.items.Corpse;
import gta_text.items.Drink;
import gta_text.items.Food;
import gta_text.items.MediKit;
import gta_text.items.Pizza;

public abstract class NonPlayerCharacter extends GameCharacter {

    private static final long RANDOM_ACTION_INTERVAL = 12000;

    private static final int TYPE=1, CHAR_NAME=2, CHAR_DESC=3, SEX=4, WANDER=5, HEALTH=6;

    public static final int HOOKER=1, DOCTOR=2, MERCENARY=3, POLICEMAN=4, DOG=5, ZOMBIE=6;
    public static final int CRIMINAL=7, AMMO_SHOPKEEPER=8, GEEZER=9, BARMAID=10, MFC_ASSISTANT=11, DESK_SEARGENT=12;
    public static final int BOUNCER=13, MUGGER=14, BARFLY=15, MAFIA_DON=16, BODYGUARD=17, ADDICT=18;
    public static final int SECURITY_GUARD=19, TELLER=20, FLASHER=21, GAMBLING_LADY=22;
    public static final int CROUPIER=23, PIMP=24, DANCING_GIRL=25, TAKEAWAY_ASSISTANT=26;
    public static final int SOLDIER=27, FOOTBALLER=28, HERO=29, MASSEUR=30, HOTEL_RECEPTIONIST=31;
    public static final int YAKUZA_NPC=32, MAFIA_NPC=33, PAWN_BROKER=34, PIZZA_CHEF=35;
    public static final int SPURNED_WOMAN=36, STALKER=37;

    public static final int CREATION_REALTIME = 1000*60*60; // How long to recreate a dead character.

  protected GameCharacter enemy;
  public int wander_chance;
  protected Location goto_location = null;
  private long last_random_action_performed_time;
  public boolean wants_pizza = false;
  public long pizza_time_up;

  public NonPlayerCharacter(int type, String name, Location loc) throws IOException {
    super(name, loc, 0, Functions.rnd(5, 10), true, true);

    boolean found = false;
    TextFile tf = new TextFile();
    tf.openFile(Game.DATA_DIR + "characters.txt", TextFile.READ);
    tf.readLine();
    while (tf.isEOF() == false) {
        String line = tf.readLine();
        if (line.startsWith("#") == false) {
            int t = new Integer(Functions.GetParam(TYPE, line, Game.SEPERATOR)).intValue();
            if (t == type) {
                found = true;
                //name = Functions.GetParam(CHAR_NAME, line, Game.SEPERATOR);
                setDesc(Functions.GetParam(CHAR_DESC, line, Game.SEPERATOR));
                male = Functions.GetParam(SEX, line, Game.SEPERATOR).
                equalsIgnoreCase("M");
                wander_chance = new Integer(Functions.GetParam(WANDER, line, Game.SEPERATOR)).intValue();
                int h = new Integer(Functions.GetParam(HEALTH, line,
                        Game.SEPERATOR)).intValue();
                this.setHealth(h);
                this.max_health = h;
                break;

            }
        }
    }
    tf.close();

    if (!found) {
        throw new IOException("Type " + type + " not found in NPC file.");
    }

  }

  //protected abstract String getRandomSaying();
  protected abstract String getRandomSayingWhenAttacked();

  /**
   * Override if reqd
   */
  protected String getFightMethod() {
      if (this.current_item != null) {
          if (this.current_item.max_damage >= GameCharacter.FIST_DAMAGE) {
              return "dunno"; //todo
          }
      }
    return "punch";
  }

  public void process() throws Exception {
      super.process();

      if (ServerConn.DEBUG_OBJS) {
          if (this.getNoOfItems() > 10) {
              System.err.println("NPC " + name + " has more than 10 items.");
          }
      }

      if (this.getCurrentLocation() != null) {
          if (this.getHealth() > 0) {
              if (this.cash > 10) {
                  cash = Functions.rnd(5, 10); // Make sure they don't have too much.
              }
              if (blinded == false) {
                  if (this.goto_location != null) {
                      this.setCurrentLocation(this.goto_location, "", false, true);
                      this.goto_location = null;
                  } else if (enemy != null) {
                      if (enemy.getCurrentLocation() == this.getCurrentLocation()) {
                          if (this.current_item instanceof RangedWeapon) {
                              if (Functions.rnd(1, 3) == 1) {
                                  this.sayTo(enemy, "You're going down!");
                              }
                              this.shoot(enemy);
                          } else {
                              if (Functions.rnd(1, 3) == 1) {
                                  this.sayTo(enemy, "You wanna fight?");
                              }
                              this.fight(enemy, "attack");
                          }
                          if (enemy.getHealth() <= 0) {
                              enemy = null;
                          }
                      } else {
                          if (remember_enemy == false) {
                              enemy = null;
                          }
                      }
                  } else if (this.getCurrentLocation().cash > 0 &&
                             Functions.rnd(1, 25) == 1) {
                      this.getCash();
                      this.say("Well, if no-one else wants it...");
                  } else if (Functions.rnd(1, wander_chance) == 1 &&
                             this.wander_chance > 1 && wants_pizza == false) {
                      this.move();
                  } else {
                      if (this.last_random_action_performed_time < System.currentTimeMillis() + RANDOM_ACTION_INTERVAL) {
                          if (this.getCurrentLocation().robber == null) {
                              if (this.getCurrentLocation() != Server.game.cops_needed_at) {
                                  if (Functions.rnd(1,
                                          6 *
                                          this.getCurrentLocation().
                                          getNoOfCharacters()) ==
                                      1) {
                                      this.last_random_action_performed_time = System.currentTimeMillis();
                                      this.performRandomAction();
                                  }
                              }
                          }
                          if (this.current_item instanceof Drink || this.current_item instanceof Food) {
                              if (Functions.rnd(1, 5) == 1) {
                                  this.eat(current_item.name, "drink");
                              }
                          }
                      }
                  }
              } else {
                  int x = Functions.rnd(1, 5);
                  switch (x) {
                  case 1:
                      this.say("My eyes!");
                      break;
                  case 2:
                      this.say("I can't see a thing!");
                      break;
                  }
              }
          }
      } else {
          System.err.println("WARNING: Character " + this.name + " has null location!");
          this.removeFromGame();
      }

  }

  protected abstract void performRandomAction() throws IOException;

  public void sendMsgToPlayer(String col, String msg) throws IOException {
      // Do nothing
  }

  public void given(GameCharacter char_from, Item item) throws IOException {
      if (item instanceof Corpse) {
          this.sayTo(char_from, "What the hell do I want that for?  Eugh!");
          this.drop(item.name);
      } else if (item instanceof MediKit) {
          this.sayTo(char_from, "Thanks, just what I need.");
          item.use(this);
      } else if (item instanceof Pizza && this.wants_pizza) {
          if (System.currentTimeMillis() <= this.pizza_time_up) {
              this.sayTo(char_from, "Mmm, just what I've been waiting for.");
              this.give("" + 15, char_from);
              Server.game.informOthersOfMiscAction(this, null, "The " + name + " munches on the pizza.");
          } else {
              this.sayTo(char_from, "Where the hell you bin?  I lost my appetite hours ago!");
              //Server.game.informOthersOfMiscAction(this, null, "The " + name + " throws the pizza on the floor.");
              this.drop(item);
          }
          this.wants_pizza = false;
      } else if (item instanceof RangedWeapon) {
        try {
            if (item.max_damage >= this.current_item.max_damage) {
                this.sayTo(char_from, "Thanks, just what I need.");
                this.current_item = item;
            } else {
                this.sayTo(char_from, "Thanks, but it's a bit weak for my tastes.  I prefer something with a bit more firepower.");
            }
        } catch (Exception ex) {
            Server.HandleError(ex);
        }
      } else {
          this.sayTo(char_from, "Thanks.");
      }
  }

  public void givenCash(GameCharacter char_from, int amt) throws IOException {
      this.sayTo(char_from, "Thanks, but I don't need it.");
  }

  public void attackedBy(GameCharacter character, String method) throws
          IOException {
    try {
        if (this.getCurrentLocation() == null) {
            this.removeFromGame();
            return;
        }
        if (enemy == null || this.getCurrentLocation().containsCharacter(enemy) == false) {
            this.say(getRandomSayingWhenAttacked());
            this.enemy = character;
        }
    } catch (Exception ex) {
        Server.HandleError(ex);
        this.removeFromGame();
    }
  }

  public void shotAtBy(GameCharacter character, RangedWeapon item) throws IOException {
      if (enemy == null || this.getCurrentLocation().containsCharacter(enemy) == false) {
          this.say(this.getRandomSayingWhenAttacked());
        this.enemy = character;
      }
  }

  public void otherCharacterEntered(GameCharacter character, Location old_loc) throws IOException {
      if (character == enemy) {
          this.say("Come back for some more?");
      }
  }

  public void otherCharacterLeft(GameCharacter character, Location new_loc, boolean disappeared, String to_dir) throws IOException {
    // Override if reqd.
  }

  public void seenShooting(GameCharacter shooter, GameCharacter target) throws IOException {
    // Override if reqd.
  }

  public void seenAttack(GameCharacter shooter, GameCharacter target) throws IOException {
    // Override if reqd.
  }

  public boolean spokenTo(GameCharacter charac, String msg, boolean only_them) throws IOException {
      if (only_them && charac instanceof PlayersCharacter) {
          if (containsSwearing(msg)) {
              this.sayTo(charac, "You mind your language.");
          } else if (containsRobbery(msg)) {
              mugged(charac);
          } else if (msg.toUpperCase().indexOf("SEX") >= 0) {
              this.sayTo(charac, "I'm game if you are.", false);
          } else if (msg.toUpperCase().indexOf("KNEECAPS") >= 0 ||
                     msg.toUpperCase().indexOf("CASINO") >= 0) {
              this.sayTo(charac, "What about it?", false);
          } else if (msg.toUpperCase().indexOf("NLP") >= 0) {
              this.sayTo(charac, "What is NLP?", false);
          } else if (msg.equalsIgnoreCase("THANKS") ||
                     msg.equalsIgnoreCase("CHEERS")) {
              this.sayTo(charac, "That's okay.", false);
          } else if (msg.toUpperCase().startsWith("HELP")) {
              this.sayTo(charac, "What's wrong?", false);
          } else {
              // Default speech.
              if (Functions.rnd(1, 2) == 1) {
                  this.sayTo(charac, "Hello.");
              } else {
                  this.sayTo(charac, "Okay.");
              }
          }
      }
      return true;
  }

  public void mugged(GameCharacter charac) throws IOException {
      if (Functions.rnd(1, 4) == 1 || charac == this.enemy) {
          this.sayTo(charac, "No way!");
          this.enemy = charac;
      } else {
          if (Server.game.game_time.getDayPeriodNo() == GameTime.MIDDAY) {
              int x = Functions.rnd(1, 2);
              switch (x) {
              case 1:
                  this.sayTo(charac, "You're mugging me in broad daylight?");
                  break;
              case 2:
                  this.sayTo(charac, "You'll get your comuppance!");
                  break;
              }
          } else {
              int x = Functions.rnd(1, 2);
              switch (x) {
              case 1:
                  this.sayTo(charac, "What?  I heard this was a nice area!");
                  break;
              case 2:
                  this.sayTo(charac, "You won't get away with this!");
                  break;
              }

          }
          int c = Functions.rnd(1, 15);
          this.give("" + c, charac);
          if (Functions.rnd(1, 4) == 1) {
              charac.incWantedLevel(1);
             Server.game.callTheCops(this.getCurrentLocation());
          }
      }
  }

  public boolean shagAttemptedBy(GameCharacter character) throws IOException {
      this.sayTo(character, "Get away from me!");
    this.fight(character, "attack");
    return false;
  }

  public boolean containBuyKeyword(String msg) {
      return (msg.toUpperCase().indexOf("SELL") >= 0 || msg.toUpperCase().indexOf("SALE") >= 0 || msg.toUpperCase().indexOf("BUY") >= 0 || msg.toUpperCase().indexOf("LIST") >= 0 || msg.toUpperCase().indexOf("YES") >= 0 || msg.toUpperCase().indexOf("WHAT") >= 0);

  }

  /**
   * Overide if reqd.
   */
  public boolean attemptBuyFrom(GameCharacter character, String item_name) throws IOException {
      return false;
  }

  public void kissedBy(GameCharacter character) throws IOException {
      this.sayTo(character, "Hey, I hardly know you!");
  }

  public void greeted(GameCharacter character) throws IOException {
      this.sayTo(character, "Hello.");
  }

  public void touched(GameCharacter character) throws IOException {
      this.sayTo(character, "Can I help you?");
  }

  public boolean isEnemyInRoom() {
      return this.getCurrentLocation().containsCharacter(this.enemy);
  }

}
