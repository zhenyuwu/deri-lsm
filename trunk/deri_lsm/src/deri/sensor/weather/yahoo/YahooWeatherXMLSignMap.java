package deri.sensor.weather.yahoo;

import java.util.EnumMap;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class YahooWeatherXMLSignMap {
	public static EnumMap<YahooWeatherXMLSign,String> weather_xml_sign = new EnumMap<YahooWeatherXMLSign,String>(YahooWeatherXMLSign.class);
	
	static{
		initialize_weather_xml_sign();
	}
	
	private static void initialize_weather_xml_sign(){
		weather_xml_sign.put(YahooWeatherXMLSign.basic, "/rss/channel");
		weather_xml_sign.put(YahooWeatherXMLSign.yweather_wind, "yweather:wind");
		weather_xml_sign.put(YahooWeatherXMLSign.yweather_atmosphere, "yweather:atmosphere");
		weather_xml_sign.put(YahooWeatherXMLSign.item, "item");
		weather_xml_sign.put(YahooWeatherXMLSign.yweather_condition, "yweather:condition");
		weather_xml_sign.put(YahooWeatherXMLSign.yweather_units, "yweather:units");
		weather_xml_sign.put(YahooWeatherXMLSign.description, "description");
	}
	
	public static String getWeatherXMLSign(YahooWeatherXMLSign sign){
		return weather_xml_sign.get(sign);
	}
	
	public static String assembleWeatherSign(YahooWeatherXMLSign... signs){
		String express = getWeatherXMLSign(YahooWeatherXMLSign.basic);
		for(YahooWeatherXMLSign sign : signs){
			express += "/" + getWeatherXMLSign(sign);
		}
		return express;
	}
}
