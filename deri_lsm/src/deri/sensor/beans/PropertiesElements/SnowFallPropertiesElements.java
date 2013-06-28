package deri.sensor.beans.PropertiesElements;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public enum SnowFallPropertiesElements {
	amount,elevation,duration,station,all;
	
private static Map<String,SnowFallPropertiesElements> snowfall_elements_map = new HashMap<String,SnowFallPropertiesElements>();
	
	static{
		initialize_snowfall_elements_map();
	}
	
	private static void initialize_snowfall_elements_map(){
		for(SnowFallPropertiesElements type : SnowFallPropertiesElements.values()){
			snowfall_elements_map.put(type.toString(), type);
		}
	}
		
	public static SnowFallPropertiesElements getSnowFallPropertiesElements(String property){
		return snowfall_elements_map.get(property);
	}
}
