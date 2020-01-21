package gta_text.npcs;

import gta_text.locations.Location;
import java.io.IOException;
import gta_text.items.Pistol;
import ssmith.lang.Functions;
import ssmith.util.CSVString;

/**
 * This class is for chars that follow the player.
 */
public class Mercenary extends NonPlayerCharacter {

    public static int total_gangmembers=0;
    private GameCharacter boss;
    private long boss_until;
    private int tmp_wander; // For storing wander when we're on a job.

  public Mercenary(String name, Location loc) throws IOException {
      super(NonPlayerCharacter.MERCENARY, name, loc);
      this.addItem(new Pistol());
      total_gangmembers++;
  }

  public String getDesc() {
      if (boss == null) {
          return super.getDesc();
      } else {
          return super.getDesc() + "  They are currently in the hire of the " + boss.name + ".";
      }
  }

  public void attackedBy(GameCharacter character, String method) throws
  IOException {
      if (character == boss) {
          this.sayTo(character, "Why, you backstabbing bastard!");
          boss = null;
      }
       super.attackedBy(character, method);
  }

  public void removeFromGame() throws IOException {
      super.removeFromGame();
      total_gangmembers--;
  }

  public void process() throws Exception {
      super.process();

      if (boss != null) {
          if (boss.getHealth() <= 0) {
              this.boss = null;
          } else if (System.currentTimeMillis() > boss_until) {
              if (this.getCurrentLocation().containsCharacter(boss.name)) {
                  this.sayTo(boss, "I'm off now.  I've done my work.");
              }
              boss = null;
              this.wander_chance = this.tmp_wander;
          }
      }
  }

  public void givenCash(GameCharacter char_from, int amt) throws IOException {
      long time = (amt * 1000 * 60)/2;
      if (boss == null || boss == char_from) {
          if (boss == char_from) {
              this.boss_until += time;
          } else {
              this.boss = char_from;
              this.boss_until = System.currentTimeMillis() + time;
          }
          this.sayTo(char_from, "Okay, you got me for " + (time/1000/60) + " minutes [realtime].");
          this.tmp_wander = wander_chance;
          this.wander_chance = 1;
      } else {
          this.boss_until -= time;
          if (boss_until > System.currentTimeMillis()) {
              this.sayTo(char_from, "Thanks, but it aint enough.");
          } else {
              this.sayTo(char_from, "Thanks.  I'm relieved of duties.");
          }
      }
  }

  public void otherCharacterLeft(GameCharacter character, Location new_loc,
                                 boolean disappeared, String dir) throws
          IOException {
      if (!disappeared) {
          if (character == this.boss) {
              this.goto_location = new_loc;
          } else if (boss == null) {
              if (character == enemy) {
                  // Follow them!
                  this.goto_location = new_loc;
              }
          }
      }
  }

  public void seenShooting(GameCharacter shooter, GameCharacter target) throws
          IOException {
      super.seenShooting(shooter, target);

      if (shooter == boss) {
          this.enemy = target;
      } else if (target == boss) {
          this.enemy = shooter;
      }
  }

  public void seenAttack(GameCharacter attacker, GameCharacter target) throws
          IOException {
      super.seenAttack(attacker, target);

      if (attacker == boss) {
          this.enemy = target;
      } else if (target == boss) {
          this.enemy = attacker;
      } else if (boss == null) {
          if (Functions.rnd(1, 4) == 1) {
              this.say("If you give me some cash I'll give you a hand.");
          }
      }

  }

  public boolean spokenTo(GameCharacter charac, String msg, boolean only_them) throws
  IOException {
      if (charac instanceof PlayersCharacter) {
          if (this.boss == null) {
              if (msg.toUpperCase().indexOf("BUSINESS") >= 0 || msg.toUpperCase().indexOf("BUISNESS") >= 0) {
                  if (Functions.rnd(1, 2) == 1) {
                      this.sayTo(charac,
                      "I'm for hire if you're interested.  And I'm not talking children's parties.");
                  } else {
                      this.sayTo(charac, "I'm up for a job if the money is right.");
                  }
                  return true;
              } else if (msg.toUpperCase().indexOf("JOB") >= 0) {
                  this.sayTo(charac, "I'll do a job, but I want money up front.");
                  return true;
              } else if (msg.toUpperCase().indexOf("INTERESTED") >= 0 || msg.toUpperCase().indexOf("OK") >= 0) {
                  this.sayTo(charac, "Let's see the colour of your money.");
                  return true;
              } else if (msg.toUpperCase().indexOf("MONEY") >= 0) {
                  this.sayTo(charac, "Money is only going in one direction - from you to me.");
                  return true;
              } else if (msg.toUpperCase().indexOf("HOW MUCH") >= 0) {
                  this.sayTo(charac, "Depends how long you need me for.  Just give me some and we'll sort it out.");
                  return true;
              }
          } else if (charac == boss) {
              if (msg.toUpperCase().indexOf("FOLLOW") >= 0) {
                  this.sayTo(charac, "Okay.");
                  return true;
              } else if (msg.toUpperCase().startsWith("KILL") || msg.toUpperCase().startsWith("SHOOT") || msg.toUpperCase().startsWith("ATTACK")) {
                  CSVString csv = new CSVString(msg, " ");
                  if (csv.getNoOfSections() == 2) {
                      String new_enemy = csv.getSection(1);
                      if (this.getCurrentLocation().containsCharacter(new_enemy)) {
                          this.enemy = this.getCurrentLocation().getCharacter(new_enemy);
                          this.sayTo(charac, "You got it.");
                      } else {
                          this.sayTo(charac, "I can't see them here.");
                      }
                      return true;
                  } else {
                      this.sayTo(charac, "What?");
                  }
                  return true;
              }
          }
          if (only_them) {
              if (msg.toUpperCase().indexOf("GIVE ME") >= 0 || msg.toUpperCase().indexOf("CAN I HAVE") >= 0) {
                  if (msg.toUpperCase().indexOf("HAND") >= 0) {
                      this.sayTo(charac, "Hey, I'm not a charity!");
                  } else {
                      this.sayTo(charac, "Sorry, you'll have to get your own.");
                  }
                  return true;
              } else if (msg.toUpperCase().indexOf("GUN") >= 0 ||
                         msg.toUpperCase().indexOf("WEAPON") >= 0) {
                  this.sayTo(charac, "I'm loaded and ready to go.");
                  return true;
           } else if (msg.toUpperCase().indexOf("YES") >= 0) {
                  this.sayTo(charac, "I can give you some protection for the right price.");
                  return true;
              } else if (msg.toUpperCase().indexOf("MUCH") >= 0) {
                  this.sayTo(charac, "Depends how long you want me around for.");
                  return true;
              }
          }
      }
      return super.spokenTo(charac, msg, only_them);
  }

  protected void performRandomAction() throws IOException {
      if (this.boss == null) {
          int x = Functions.rnd(1, 4);
          switch(x) {
          case 1:
              this.say("Maybe we can do some business, and I don't mean take a shit.");
              break;
          case 2:
              this.say("Give me some hard cash if you want protection.");
              break;
          case 3:
              this.say("There's a bank around here, and I fancy making a withdrawal if you know what I mean.");
              break;
          case 4:
              this.say("I only just got out alive after my last job.");
              break;
          default:
              this.say("Script?");
              break;
          }
      } else {
          int x = Functions.rnd(1, 3);
          switch(x) {
          case 1:
              this.sayTo(boss, "We should be careful around these parts.");
              break;
          case 2:
              this.sayTo(boss, "It's a good job you've got me here.");
              break;
          case 3:
              this.sayTo(boss, "I'll follow you.");
              break;
          }
      }
  }

  protected String getRandomSayingWhenAttacked() {
    return "You fucking fucker!  I'll get you!";
  }

}
