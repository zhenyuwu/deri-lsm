package deri.sensor.beans.PropertiesElements;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public enum SnowDepthPropertiesElements {
	amount,elevation,station,all;
	
	private static Map<String,SnowDepthPropertiesElements> snowdepth_elements_map = new HashMap<String,SnowDepthPropertiesElements>();
		
		static{
			initialize_snowdepth_elements_map();
		}
		
		private static void initialize_snowdepth_elements_map(){
			for(SnowDepthPropertiesElements type : SnowDepthPropertiesElements.values()){
				snowdepth_elements_map.put(type.toString(), type);
			}
		}
			
		public static SnowDepthPropertiesElements getSnowDepthPropertiesElements(String property){
			return snowdepth_elements_map.get(property);
		}
}
