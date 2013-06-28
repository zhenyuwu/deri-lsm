package deri.sensor.javabeans;

import java.util.ArrayList;
import java.util.List;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public enum WeatherSignShown {
	temperature,
	state,
	wind_direction,
	wind_speed,
	wind_chill,
	atmosphere_pressure,
	atmosphere_humidity,
	atmosphere_visibility;
	
	
	public static List<String> getAllSigns(){
		List<String> signs = new ArrayList<String>();
		for(WeatherSignShown sign : WeatherSignShown.values()){
			signs.add(sign.toString());
		}
		return signs;
	}
}
