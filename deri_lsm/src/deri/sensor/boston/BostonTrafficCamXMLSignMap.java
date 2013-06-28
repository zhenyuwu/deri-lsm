package deri.sensor.boston;

import java.util.EnumMap;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class BostonTrafficCamXMLSignMap {
public static EnumMap<BostonTrafficCamXMLSign,String> bostontrafficcam_xml_sign = new EnumMap<BostonTrafficCamXMLSign,String>(BostonTrafficCamXMLSign.class);
	
	static{
		initialize_bostontrafficcam_xml_sign();
	}
	
	private static void initialize_bostontrafficcam_xml_sign(){
		bostontrafficcam_xml_sign.put(BostonTrafficCamXMLSign.basic, "/TrafficCams");		
		bostontrafficcam_xml_sign.put(BostonTrafficCamXMLSign.cam, "/TrafficCams/Cam");
		bostontrafficcam_xml_sign.put(BostonTrafficCamXMLSign.name, "/TrafficCams/Cam/name");
		bostontrafficcam_xml_sign.put(BostonTrafficCamXMLSign.active, "/TrafficCams/Cam/active");
		bostontrafficcam_xml_sign.put(BostonTrafficCamXMLSign.image, "/TrafficCams/Cam/image");
		bostontrafficcam_xml_sign.put(BostonTrafficCamXMLSign.description, "/TrafficCams/Cam/description");
		bostontrafficcam_xml_sign.put(BostonTrafficCamXMLSign.geo_lat, "/TrafficCams/Cam/lat");
		bostontrafficcam_xml_sign.put(BostonTrafficCamXMLSign.geo_long, "/TrafficCams/Cam/long");
	}	
	
	public static String assembleTrafficSign(BostonTrafficCamXMLSign sign){
		String express = bostontrafficcam_xml_sign.get(sign);
//		for(bostonTrafficCamXMLSign sign : signs){
//			express += "/" + getTrafficXMLSign(sign);
//		}
		return express;
	}
}
