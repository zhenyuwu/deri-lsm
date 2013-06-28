package deri.sensor.weather.yahoo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class YahooWeatherStateMap {
	public static Map<Integer,String> code_state_map = new HashMap<Integer,String>(40);
	public static Map<String,Integer> state_code_map = new HashMap<String,Integer>(40);
	static{
		initialise_code_state_map();
		initialise_state_code_map();
	}
	
	private static void initialise_code_state_map() {
		code_state_map.put(0, "tornado");
		code_state_map.put(1, "tropical storm");
		code_state_map.put(2, "hurricane");
		code_state_map.put(3, "severe thunderstorms");
		code_state_map.put(4, "thunderstorms");
		code_state_map.put(5, "mixed rain and snow");
		code_state_map.put(6, "mixed rain and sleet");
		code_state_map.put(7, "mixed snow and sleet");
		code_state_map.put(8, "freezing drizzle");
		code_state_map.put(9, "drizzle");
		code_state_map.put(10, "freezing rain");
		code_state_map.put(11, "showers");
		code_state_map.put(12, "showers");
		code_state_map.put(13, "snow flurries");
		code_state_map.put(14, "light snow showers");
		code_state_map.put(15, "blowing snow");
		code_state_map.put(16, "snow");
		code_state_map.put(17, "hail");
		code_state_map.put(18, "sleet");
		code_state_map.put(19, "dust");
		code_state_map.put(20, "foggy");
		code_state_map.put(21, "haze");
		code_state_map.put(22, "smoky");
		code_state_map.put(23, "blustery");
		code_state_map.put(24, "windy");
		code_state_map.put(25, "cold");
		code_state_map.put(26, "cloudy");
		code_state_map.put(27, "mostly cloudy (night)");
		code_state_map.put(28, "mostly cloudy (day)");
		code_state_map.put(29, "partly cloudy (night)");
		code_state_map.put(30, "partly cloudy (day)");
		code_state_map.put(31, "clear (night)");
		code_state_map.put(32, "sunny");
		code_state_map.put(33, "fair (night)");
		code_state_map.put(34, "fair (day)");
		code_state_map.put(35, "mixed rain and hail");
		code_state_map.put(36, "hot");
		code_state_map.put(37, "isolated thunderstorms");
		code_state_map.put(38, "scattered thunderstorms");
		code_state_map.put(39, "scattered thunderstorms");
		code_state_map.put(40, "scattered showers");
		code_state_map.put(41, "heavy snow");
		code_state_map.put(42, "scattered snow showers");
		code_state_map.put(43, "heavy snow");
		code_state_map.put(44, "partly cloudy");
		code_state_map.put(45, "thundershowers");
		code_state_map.put(46, "snow showers");
		code_state_map.put(47, "isolated thundershowers");
		code_state_map.put(3200, "not available");
	}
	
	private static void initialise_state_code_map(){
		state_code_map.put("tornado", 0);
		state_code_map.put("tropical storm", 1);
		state_code_map.put("hurricane", 2);
		state_code_map.put("severe thunderstorms", 3);
		state_code_map.put("thunderstorms", 4);
		state_code_map.put("mixed rain and snow", 5);
		state_code_map.put("mixed rain and sleet", 6);
		state_code_map.put("mixed snow and sleet", 7);
		state_code_map.put("freezing drizzle", 8);
		state_code_map.put("drizzle", 9);
		state_code_map.put("freezing rain", 10);
		state_code_map.put("showers", 11);
		state_code_map.put("showers", 12);
		state_code_map.put("snow flurries", 13);
		state_code_map.put("light snow showers", 14);
		state_code_map.put("blowing snow", 15);
		state_code_map.put("snow", 16);
		state_code_map.put("hail", 17);
		state_code_map.put("sleet", 18);
		state_code_map.put("dust", 19);
		state_code_map.put("haze", 21);
		state_code_map.put("foggy", 20);
		state_code_map.put("smoky", 22);
		state_code_map.put("blustery", 23);
		state_code_map.put("windy", 24);
		state_code_map.put("cold", 25);
		state_code_map.put("cloudy", 26);
		state_code_map.put("mostly cloudy (night)", 27);
		state_code_map.put("mostly cloudy (day)", 28);
		state_code_map.put("partly cloudy (night)", 29);
		state_code_map.put("partly cloudy (day)", 30);
		state_code_map.put("clear (night)", 31);
		state_code_map.put("sunny", 32);
		state_code_map.put("fair (night)", 33);
		state_code_map.put("fair (day)", 34);
		state_code_map.put("mixed rain and hail", 35);
		state_code_map.put("hot", 36);
		state_code_map.put("isolated thunderstorms", 37);
		state_code_map.put("scattered thunderstorms", 38);
		state_code_map.put("scattered thunderstorms", 39);
		state_code_map.put("scattered showers", 40);
		state_code_map.put("heavy snow", 41);
		state_code_map.put("scattered snow showers", 42);
		state_code_map.put("heavy snow", 43);
		state_code_map.put("partly cloudy", 44);
		state_code_map.put("thundershowers", 45);
		state_code_map.put("snow showers", 46);
		state_code_map.put("isolated thundershowers", 47);
		state_code_map.put("not available", -1);// in order to show it on the chart
	}
	
	public static String getState(int code){
		if(code >47 || code < 0 || code_state_map.get(code) == null){
			return "not available";
		}
		return code_state_map.get(code);
	}
	
	public static int getCode(String state){
		if(state != null || state_code_map.get(state) == null){
			return 0;
		}
		return state_code_map.get(state);
	}
	
	public static void main(String[] args) throws Exception{
	/*	SensorManager sensorManager = ServiceLocator.getSensorManager();
		List<String> sources = sensorManager.getAllDataSourcesJustSource();
		for(String source : sources){
			List<Weather> weathers = sensorManager.getWeatherForOneSource(source);
			if(weathers != null){
				for(Weather weather : weathers){
					if(!weather.getState().trim().equals("") && NumberUtil.isInteger(weather.getState())){
						int code = Integer.parseInt(weather.getState());
						String state = code_state_map.get(code);
						weather.setState(state);
						sensorManager.updateObject(weather);
					}
				}
			}
		}*/
	}
}
