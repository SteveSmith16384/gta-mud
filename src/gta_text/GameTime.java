package gta_text;

import ssmith.lang.Functions;

public class GameTime {

    public static final int TIME_DAWN = 1000*60*6*60;
    public static final int TIME_MIDDAY = 1000*60*12*60;
    public static final int TIME_DUSK = 1000*60*18*60;
    public static final int TIME_NIGHT = 1000*60*22*60;

    public static final int DAWN = 0;
    public static final int MIDDAY = 1;
    public static final int DUSK = 2;
    public static final int NIGHT = 3;

    private static final int TIME_MULT = 20;

    private long last_time_check;
    private int ms; // We store the time in ms (game time)

    public GameTime() {
        ms = Functions.rnd(0, 1000*60*60*24);
        this.last_time_check = System.currentTimeMillis();
    }

    public void process() {
        long diff = System.currentTimeMillis() - this.last_time_check;
        diff = (diff*TIME_MULT);
        this.ms += diff;

        while (ms >= (1000*60*60*24)) {
            ms -= (1000*60*60*24);
        }

        this.last_time_check = System.currentTimeMillis();
    }

    public int getDayPeriodNo() {
      if (this.ms > TIME_NIGHT) {
          return NIGHT;
      } else if (this.ms > TIME_DUSK) {
          return DUSK;
      } else if (this.ms > TIME_MIDDAY) {
          return MIDDAY;
      } else if (this.ms > TIME_DAWN) {
          return DAWN;
      } else {
          return NIGHT;
      }
    }

    public String getDayPeriodName() {
        switch (this.getDayPeriodNo()) {
        case NIGHT:
            return "night time";
        case DAWN:
            return "dawn";
        case MIDDAY:
            return "mid-day";
        case DUSK:
            return "dusk";
        default:
            return "unknown";
        }
    }

    public String getTimeAsString() {
        int mins = ms/1000/60;
        int h = (mins/60);
        String m = ("0" + (mins % 60));
        m = m.substring(m.length()-2, m.length());
        return h + ":" + m;
    }

    public static String GetTimeAsString(long t) {
        int mins = (int)(t/1000/60);
        int h = (mins/60);
        String m = ("0" + (t % 60));
        m = m.substring(m.length()-2, m.length());
        return h + ":" + m;
    }

    public long GetMS() {
        return ms;
    }

}
