package deri.sensor.weather.wunderground;



import java.util.HashMap;
import java.util.Map;

import deri.sensor.javabeans.WeatherSignShown;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class WUnderGroundXMLSignMap {
	private static Map<String,String> signs = new HashMap<String,String>();
	
	
	static{
		initialize();
	}
	private static void initialize(){
		String basic = "/current_observation";
		signs.put(WeatherSignShown.temperature.toString(), basic + "/temp_c");
		signs.put(WeatherSignShown.state.toString(), basic + "/weather");
		signs.put(WeatherSignShown.wind_chill.toString(), basic + "/windchill_c");
		signs.put(WeatherSignShown.wind_direction.toString(), basic + "/wind_degrees");
		signs.put(WeatherSignShown.wind_speed.toString(), basic + "/wind_mph");
		signs.put(WeatherSignShown.atmosphere_humidity.toString(), basic + "/relative_humidity");
		signs.put(WeatherSignShown.atmosphere_pressure.toString(), basic + "/pressure_mb");
		signs.put("pic",  basic + "/image/url");
		signs.put("time",  basic + "/observation_time_rfc822");
	}
	
	
	public static String getExpression(String key){
		return signs.get(key);
	}
}
