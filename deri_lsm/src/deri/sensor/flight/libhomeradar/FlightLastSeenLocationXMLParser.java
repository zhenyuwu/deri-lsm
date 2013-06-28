package deri.sensor.flight.libhomeradar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import deri.sensor.database.ServiceLocator;
import deri.sensor.javabeans.CallSign;
import deri.sensor.javabeans.Departure;
import deri.sensor.javabeans.Destination;
import deri.sensor.javabeans.FlightCIAO;
import deri.sensor.javabeans.FlightRoute;
import deri.sensor.javabeans.Latitude;
import deri.sensor.javabeans.Longitude;
import deri.sensor.javabeans.Observation;
import deri.sensor.javabeans.Transit;
import deri.sensor.manager.SensorManager;
import deri.sensor.utils.NumberUtil;
import deri.sensor.wrapper.WebServiceURLRetriever;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class FlightLastSeenLocationXMLParser {
	String lastSeenURL = "http://www.libhomeradar.org/assets/lhr.api?n=username&p=password=lastseen&r=";
	public static String flightRouteURL = "http://www.libhomeradar.org/assets/lhr.api?n=username&p=password&t=route&r=";
	private static SensorManager sensorManager = ServiceLocator.getSensorManager(); 
	
	public void updateFlightInformation(String ciao){
		try{
			String xml = WebServiceURLRetriever.RetrieveFromURL(lastSeenURL+ciao);
			xml = xml.trim().replaceFirst("^([\\W]+)<","<");
			Document document = DocumentHelper.parseText(xml);
			Element root = document.getRootElement();
			List<Element> elements = root.elements();
			String callSign = "";
			ArrayList<String> routing;
			double lat=0;
			double lng=0;
			for(Element element:elements){
				//System.out.println(element.getName());
				if(element.getName().equals(FlightXMLSignMap.Hits.toString())){
					String content = element.getStringValue();
					//if(content.equals("0")) return;
				}				
				if(element.getName().equals(FlightXMLSignMap.Results.toString())){
					List<Element> itemElements = element.elements();
					for(Element item:itemElements){
						List<Element> flights = item.elements();
						for(Element flight:flights){
							if(flight.getName().equals(FlightXMLSignMap.Callsign.toString())){
								callSign = flight.getStringValue();								
//							}else if(flight.getName().equals(FlightXMLSignMap.Routing.toString())){
//								routing = flight.getStringValue();
//								if(routing.equals("")){
//									String routeXML = WebServiceURLRetriever.RetrieveFromURL(flightRouteURL+callSign);
//									routing = FlightRouteXMLParser.getFlightRoute(routeXML);
//									System.out.println("route:"+routing);		
//								}
							}else if(flight.getName().equals(FlightXMLSignMap.LAT.toString())){
								if(NumberUtil.isDouble(flight.getStringValue())){
									lat = Double.parseDouble(flight.getStringValue());
									System.out.println("lat:"+lat);
								}
							}else if(flight.getName().equals(FlightXMLSignMap.LON.toString()))
								if(NumberUtil.isDouble(flight.getStringValue())){
									lng = Double.parseDouble(flight.getStringValue());
									System.out.println("long:"+lng);
								}
							//System.out.println(flight.getName());
							//System.out.println(flight.getData());							
						}
					}
				}
			}
			FlightCIAO flightCiao = sensorManager.getNewestFlightCIAOWithCIAOValue(ciao);
			Observation observation = flightCiao.getObservation();
			Latitude latitude = sensorManager.getFlightLatitudeForOneObservation(observation.getId());
			Longitude longitude = sensorManager.getFlightLongitudeForOneObservation(observation.getId());
			if(lat!=0){
				Date date = new Date();
				latitude.setValue(lat);
				latitude.setTimes(date);
				longitude.setValue(lng);
				longitude.setTimes(date);				
				observation.setTimes(date);
				
				sensorManager.updateObject(longitude);
				sensorManager.updateObject(latitude);
				sensorManager.updateObject(observation);
			}
			Departure departure = sensorManager.getFlightDepartureForOneObservation(observation.getId());
			Destination destination = sensorManager.getFlightDestinationForOneObservation(observation.getId());
			
			//if(routing==""){
				CallSign flightCallSign = sensorManager.getFlightCallSignForOneObservation(observation.getId());
				String routeXML = WebServiceURLRetriever.RetrieveFromURL(flightRouteURL+flightCallSign.getValue());
				routing = FlightRouteXMLParser.getFlightRoute(routeXML);
			//}
			//String[] route = routing.split("-");
			if((routing!=null)&&(!departure.getValue().equals(routing.get(0)))){		
				FlightRoute flightRoute = sensorManager.getFlightRouteForOneFlight(callSign);
				if(flightRoute!=null){
					flightRoute.setDeparture(routing.get(0));
					flightRoute.setDestination(routing.get(1));
				}
				departure.setValue(routing.get(0));
				destination.setValue(routing.get(1));
				if(routing.size()>2){
					flightRoute.setTransit(routing.get(2));
				}
				sensorManager.updateObject(flightRoute);
				sensorManager.updateObject(departure);
				sensorManager.updateObject(destination);
			}
		}catch(Exception e){
			
		}
	}
}
