package deri.sensor.ADSBHub.flight;

import java.util.ArrayList;
import java.util.Date;
import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.MappingMap;
import deri.sensor.hashmaps.util.VirtuosoConstantUtil;
import deri.sensor.javabeans.*;
import deri.sensor.manager.SensorManager;
import deri.sensor.utils.DateUtil;
import deri.sensor.utils.NumberUtil;

/**
 * @author Hoan Nguyen Mau Quoc
 */

public class ADSBFlightSignalTelnetParser {
	private static SensorManager sensorManager = ServiceLocator.getSensorManager(); 
	public static void getFlightDetailsFromStream(ArrayList<String> arrStr,Sensor sensor){
		if(arrStr.size()<=0) return;
		int size = arrStr.size();
		String ciao = "";
		String planeCallSign = "";
		double lat = 0;
		double lng = 0;
		double al = 0;
		double flightSpeed = 0;
		String day = "";
		String time = "";
		String flightDeparture = "";
		String flightArrival = "";
		String flightTransit = "";
		try{
			String[] split = arrStr.get(0).split(",");
			ciao = split[ADSBFlightStreamSignMap.getADSBFlightStreamSign(ADSBFlightStreamSign.ciao)];
			day = split[ADSBFlightStreamSignMap.getADSBFlightStreamSign(ADSBFlightStreamSign.date)];
			time = split[ADSBFlightStreamSignMap.getADSBFlightStreamSign(ADSBFlightStreamSign.time)];
			//System.out.println(ciao);
			//get altitude
			String strloca = split[ADSBFlightStreamSignMap.getADSBFlightStreamSign(ADSBFlightStreamSign.altitude)];
			if(NumberUtil.isDouble(strloca)){
				al = Double.parseDouble(strloca);
				//System.out.println(lng);
			}
			else{
				return;
			}
			
			// get latitude
			strloca = split[ADSBFlightStreamSignMap.getADSBFlightStreamSign(ADSBFlightStreamSign.lat)];
			if(NumberUtil.isDouble(strloca)){
				lat = Double.parseDouble(strloca);
				//System.out.println(lat);
			}
			else{ 
				return;
			}
			//get longitude
			strloca = split[ADSBFlightStreamSignMap.getADSBFlightStreamSign(ADSBFlightStreamSign.lng)];
			if(NumberUtil.isDouble(strloca)){
				lng = Double.parseDouble(strloca);
				//System.out.println(lng);
			}
			else{
				return;
			}
			
			//get speed
			if(arrStr.size()>3){
				split = arrStr.get(2).split(",");
				strloca = split[ADSBFlightStreamSignMap.getADSBFlightStreamSign(ADSBFlightStreamSign.speed)];
				if(NumberUtil.isDouble(strloca)){
					flightSpeed = Double.parseDouble(strloca);
					//System.out.println(lat);
				}
				else{ 
					flightSpeed = 0;
				}
			} 
			
			split = arrStr.get(size-1).split(",");
			planeCallSign = split[ADSBFlightStreamSignMap.getADSBFlightStreamSign(ADSBFlightStreamSign.callsign)];
			if(planeCallSign=="")
				return;
		//		HashMap hsm = new HashMap<String, String>();
		//		List<ObservedProperty> lstObsP = sensorManager.getAllObservedProperty();
		//		for(ObservedProperty osp:lstObsP){
		//			hsm.put(osp.getClassURI(),osp.getUrl());
		//		}
			Observation observation = null;
			FlightCIAO flightCIAO = null;
			Latitude latitude = null;
			Longitude longitude = null;
			Altitude altitude = null;
			Speed speed = null;
			Departure departure = null;
			Destination destination = null;
			Transit transit = null;
			
			Date date = new Date();
			date = DateUtil.string2Date(day+" "+time, "yyyy/MM/dd hh:mm:ss");
			
			//CallSign callSign = sensorManager.getNewestCallSignWithCallSignValue(planeCallSign);
			CallSign callSign = null;
			flightCIAO = sensorManager.getNewestFlightCIAOWithCIAOValue(ciao);
			if(flightCIAO==null){
				observation = new Observation();
				observation.setSensor(sensor);		
				String foi = VirtuosoConstantUtil.sensorObjectDataPrefix + ciao;
				observation.setFeatureOfInterest(foi);
			}
			else{
							
				observation = flightCIAO.getObservation();
				//flightCIAO = sensorManager.getFlightCIAOForOneObservation(observation.getId());
				callSign = sensorManager.getFlightCallSignForOneObservation(observation.getId());
				latitude = sensorManager.getFlightLatitudeForOneObservation(observation.getId());
				longitude = sensorManager.getFlightLongitudeForOneObservation(observation.getId());
				altitude = sensorManager.getFlightAltitudeForOneObservation(observation.getId());
				speed = sensorManager.getFlightSpeedForOneObservation(observation.getId());
				//departure = sensorManager.getFlightDepartureForOneObservation(observation.getId());
				observation.setTimes(date);
				flightCIAO.setTimes(date);
				
				callSign.setTimes(date);	
				callSign.setValue(planeCallSign);
				latitude.setValue(lat);
				latitude.setTimes(date);
				longitude.setValue(lng);
				longitude.setTimes(date);
				altitude.setValue(al);
				altitude.setTimes(date);
				speed.setValue(flightSpeed);
				speed.setTimes(date);
							
				sensorManager.updateObject(flightCIAO);
				sensorManager.updateObject(callSign);
				sensorManager.updateObject(latitude);
				sensorManager.updateObject(longitude);
				sensorManager.updateObject(altitude);
				sensorManager.updateObject(speed);
				sensorManager.updateObject(observation);
				return;
			}			
			observation.setTimes(date);
			
			callSign = new CallSign();
			callSign.setValue(planeCallSign);
			String className = MappingMap.table2ClassURI.get(callSign.getClass().getSimpleName().toLowerCase());
			//callSign.setObservedURL(hsm.get(className).toString());
			callSign.setPropertyName(callSign.getClass().getSimpleName());
			callSign.setObservation(observation);
			callSign.setTimes(date);
			sensorManager.addObject(callSign);
			
			flightCIAO = new FlightCIAO();
			flightCIAO.setValue(ciao);
			className = MappingMap.table2ClassURI.get(flightCIAO.getClass().getSimpleName().toLowerCase());
			//flightCIAO.setObservedURL(hsm.get(className).toString());
			flightCIAO.setPropertyName(flightCIAO.getClass().getSimpleName());
			flightCIAO.setObservation(observation);
			flightCIAO.setTimes(date);
			sensorManager.addObject(flightCIAO);
			
			latitude = new Latitude();
			latitude.setValue(lat);
			latitude.setTimes(date);
			className = MappingMap.table2ClassURI.get(latitude.getClass().getSimpleName().toLowerCase());
			//latitude.setObservedURL(hsm.get(className).toString());
			latitude.setPropertyName(latitude.getClass().getSimpleName());
			latitude.setObservation(observation);
			sensorManager.addObject(latitude);
			
			longitude = new Longitude();
			longitude.setValue(lng);
			longitude.setTimes(date);
			className = MappingMap.table2ClassURI.get(longitude.getClass().getSimpleName().toLowerCase());
			//longitude.setObservedURL(hsm.get(className).toString());
			longitude.setPropertyName(longitude.getClass().getSimpleName());
			longitude.setObservation(observation);
			sensorManager.addObject(longitude);	
			
			altitude = new Altitude();
			altitude.setValue(al);
			altitude.setTimes(date);
			altitude.setUnit("feet");
			className = MappingMap.table2ClassURI.get(altitude.getClass().getSimpleName().toLowerCase());
			//altitude.setObservedURL(hsm.get(className).toString());
			altitude.setPropertyName(altitude.getClass().getSimpleName());
			altitude.setObservation(observation);
			sensorManager.addObject(altitude);
			
			speed = new Speed();
			speed.setValue(flightSpeed);
			speed.setTimes(date);
			speed.setUnit("kts");
			className = MappingMap.table2ClassURI.get(speed.getClass().getSimpleName().toLowerCase());
			//speed.setObservedURL(hsm.get(className).toString());
			speed.setPropertyName(speed.getClass().getSimpleName());
			speed.setObservation(observation);
			sensorManager.addObject(speed);
			
			FlightRoute flightRoute = sensorManager.getFlightRouteForOneFlight(planeCallSign);
			if(flightRoute!=null){
				flightDeparture = flightRoute.getDeparture();
				flightTransit = flightRoute.getTransit();
				flightArrival = flightRoute.getDestination();
			}
			departure = new Departure();
			departure.setValue(flightDeparture);
			departure.setTimes(date);			
			className = MappingMap.table2ClassURI.get(departure.getClass().getSimpleName().toLowerCase());
			//speed.setObservedURL(hsm.get(className).toString());
			departure.setPropertyName(departure.getClass().getSimpleName());
			departure.setObservation(observation);
			sensorManager.addObject(departure);
			
			transit = new Transit();
			transit.setValue(flightTransit);
			transit.setTimes(date);			
			className = MappingMap.table2ClassURI.get(transit.getClass().getSimpleName().toLowerCase());
			//speed.setObservedURL(hsm.get(className).toString());
			transit.setPropertyName(transit.getClass().getSimpleName());
			transit.setObservation(observation);
			sensorManager.addObject(transit);
			
			destination = new Destination();
			destination.setValue(flightArrival);
			destination.setTimes(date);			
			className = MappingMap.table2ClassURI.get(destination.getClass().getSimpleName().toLowerCase());
			//speed.setObservedURL(hsm.get(className).toString());
			destination.setPropertyName(destination.getClass().getSimpleName());
			destination.setObservation(observation);
			sensorManager.addObject(destination);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
