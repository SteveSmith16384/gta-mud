/*
 * Created on 19-May-2006
 *
 */
package gta_text;

import ssmith.lang.Functions;

public class GameWeather {

	public static final int SUNNY=0, RAINING=1;
	public static final int MAX_WEATHER=1;

	public int current_weather;

	public GameWeather() {
		super();
	}

	public void process() {
		if (Functions.rnd(1, 50) == 1) {
			this.current_weather = Functions.rnd(SUNNY, MAX_WEATHER);
		}
	}

	public String getDesc() {
		switch(this.current_weather) {
		case SUNNY:
			return "It is sunny.";
		case RAINING:
			return "It has started raining.";
		default:
			return "The weather is nondescript.";
		}
	}

}
