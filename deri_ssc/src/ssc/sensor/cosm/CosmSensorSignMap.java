package ssc.sensor.cosm;

import java.util.EnumMap;

public class CosmSensorSignMap {
public static EnumMap<CosmSensorSign,String> cosm_xml_sign = new EnumMap<CosmSensorSign,String>(CosmSensorSign.class);
	
	static{
		initialize_cosm_xml_sign();
	}
	
	private static void initialize_cosm_xml_sign(){
		cosm_xml_sign.put(CosmSensorSign.basic, "/rss/channel");
		cosm_xml_sign.put(CosmSensorSign.title, "/*[name()='eeml']/*[name()='environment']/*[name()='title']");
		cosm_xml_sign.put(CosmSensorSign.description, "/*[name()='eeml']/*[name()='environment']/*[name()='description']");
		cosm_xml_sign.put(CosmSensorSign.website, "/*[name()='eeml']/*[name()='environment']/*[name()='website']");
		cosm_xml_sign.put(CosmSensorSign.feed, "/*[name()='eeml']/*[name()='environment']/*[name()='feed']");
		cosm_xml_sign.put(CosmSensorSign.lat, "/*[name()='eeml']/*[name()='environment']/*[name()='location']/*[name()='lat']");
		cosm_xml_sign.put(CosmSensorSign.lng, "/*[name()='eeml']/*[name()='environment']/*[name()='location']/*[name()='lon']");
		cosm_xml_sign.put(CosmSensorSign.unit, "/*[name()='eeml']/*[name()='environment']/*[name()='data']/*[name()='unit']");
		cosm_xml_sign.put(CosmSensorSign.data, "/*[name()='eeml']/*[name()='environment']/*[name()='data']");
		cosm_xml_sign.put(CosmSensorSign.value, "/*[name()='eeml']/*[name()='environment']/*[name()='data']/*[name()='current_value']");
//		cosm_xml_sign.put(CosmSensorSign.time, "/*[name()='eeml']/*[name()='environment']/*[name()='data']");
		cosm_xml_sign.put(CosmSensorSign.tag, "/*[name()='eeml']/*[name()='environment']/*[name()='data']/*[name()='tag']");
		cosm_xml_sign.put(CosmSensorSign.environment, "/*[name()='eeml']/*[name()='environment']");
	}
	
	public static String getCosmXMLSign(CosmSensorSign sign){
		return cosm_xml_sign.get(sign);
	}
	
	
}
