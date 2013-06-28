package deri.sensor.weather.weatherbug;



import java.util.HashMap;
import java.util.Map;

import deri.sensor.javabeans.WeatherSignShown;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class WeatherBugXMLSignMap {
	private static Map<String,String> signs = new HashMap<String,String>();
	
	static{
		initialize();
	}
	private static void initialize(){
		String basic = "/aws:weather/aws:ob";
		signs.put(WeatherSignShown.temperature.toString(), basic + "/aws:temp");
		signs.put(WeatherSignShown.state.toString(), basic + "/aws:current-condition");
		signs.put(WeatherSignShown.wind_direction.toString(), basic + "/aws:wind-direction-degrees");
		signs.put(WeatherSignShown.wind_speed.toString(), basic + "/aws:wind-speed");
		signs.put(WeatherSignShown.atmosphere_humidity.toString(), basic + "/aws:humidity");
		signs.put(WeatherSignShown.atmosphere_pressure.toString(), basic + "/aws:pressure");
		
		signs.put("year",  basic + "/aws:ob-date/aws:year");
		signs.put("month",  basic + "/aws:ob-date/aws:month");
		signs.put("day",  basic + "/aws:ob-date/aws:day");
		signs.put("hour",  basic + "/aws:ob-date/aws:hour");
		signs.put("minute",  basic + "/aws:ob-date/aws:minute");
		signs.put("second",  basic + "/aws:ob-date/aws:second");
	}
	
	
	public static String getExpression(String key){
		return signs.get(key);
	}
}
