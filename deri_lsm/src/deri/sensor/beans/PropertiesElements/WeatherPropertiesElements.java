package deri.sensor.beans.PropertiesElements;


import java.util.HashMap;
import java.util.Map;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public enum WeatherPropertiesElements {
	temperature, windchill,windspeed,picture,atmosphere_humidity,atmosphere_pressure,
	atmosphere_visibility,status,wind_direction,all;
	
	private static Map<String,WeatherPropertiesElements> weather_elements_map = new HashMap<String,WeatherPropertiesElements>();
	
	static{
		initialize_weather_elements_map();
	}
	
	private static void initialize_weather_elements_map(){
		for(WeatherPropertiesElements type : WeatherPropertiesElements.values()){
			weather_elements_map.put(type.toString(), type);
		}
	}
		
	public static WeatherPropertiesElements getWeatherPropertiesElements(String property){
		return weather_elements_map.get(property);
	}


}