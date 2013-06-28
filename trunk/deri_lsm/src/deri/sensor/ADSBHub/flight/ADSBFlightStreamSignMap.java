package deri.sensor.ADSBHub.flight;

import java.util.EnumMap;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class ADSBFlightStreamSignMap {
	public static EnumMap<ADSBFlightStreamSign, Integer> flight_xml_sign = new EnumMap<ADSBFlightStreamSign,Integer>(ADSBFlightStreamSign.class);
	
	static{
		initialize_weather_xml_sign();
	}
	
	private static void initialize_weather_xml_sign(){
		flight_xml_sign.put(ADSBFlightStreamSign.ciao,4);
		flight_xml_sign.put(ADSBFlightStreamSign.altitude,11);
		flight_xml_sign.put(ADSBFlightStreamSign.lat,14);
		flight_xml_sign.put(ADSBFlightStreamSign.lng,15);
		flight_xml_sign.put(ADSBFlightStreamSign.callsign,10);
		flight_xml_sign.put(ADSBFlightStreamSign.date,6);
		flight_xml_sign.put(ADSBFlightStreamSign.speed,12);
		flight_xml_sign.put(ADSBFlightStreamSign.time,7);		
	}
	
	public static int getADSBFlightStreamSign(ADSBFlightStreamSign sign){
		return flight_xml_sign.get(sign);
	}
	
}
