package deri.sensor.components.Map;

import java.util.HashMap;
import java.util.Map;
import deri.sensor.javabeans.WeatherSignShown;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class WeatherUnitMap {
	
	public static Map<String,String> weather_units_map = new HashMap<String,String>();
	
	static{
		init_weather_units_map();
	}
	
	private static void init_weather_units_map(){
		weather_units_map.put(WeatherSignShown.temperature.toString(), "C");
		weather_units_map.put(WeatherSignShown.wind_chill.toString(), "C");
		weather_units_map.put(WeatherSignShown.wind_speed.toString(), "km/h");
		weather_units_map.put(WeatherSignShown.atmosphere_pressure.toString(), "mb");
		weather_units_map.put(WeatherSignShown.atmosphere_humidity.toString(), "%");
		weather_units_map.put(WeatherSignShown.atmosphere_visibility.toString(), "km");
	}
	
	public static String getUnit(String sign){
		return weather_units_map.get(sign);
	}
	
}
