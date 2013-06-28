package deri.sensor.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public enum SourceType {
	yahoo, australia, wunderground, weatherbug,google,
	webcam, // for camera is just a link. so just let the webcam be the sourceType, the same as the sensorType
	noaa,
	psmsl,
	others, traffic, bikehire, railwaystation;
	
	public static Map<String,SourceType> source_type_map = new HashMap<String,SourceType>();
	public static List<SourceType> weather_source_type = new ArrayList<SourceType>();
	static{
		initialize_source_type_map();
		initialize_weather_source_type_map();
	}
	
	private static void initialize_source_type_map(){
		for(SourceType type : SourceType.values()){
			source_type_map.put(type.toString(), type);
		}
	}
	
	private static void initialize_weather_source_type_map(){
		weather_source_type.add(SourceType.yahoo);
		weather_source_type.add(SourceType.australia);
		weather_source_type.add(SourceType.wunderground);
		weather_source_type.add(SourceType.weatherbug);
		weather_source_type.add(SourceType.google);
	}
	
	public static SourceType getSourceType(String sourceType){
		return source_type_map.get(sourceType);
	}
	public static void main(String[] args) {
		
	}
}