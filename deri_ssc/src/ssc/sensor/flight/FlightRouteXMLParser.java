package ssc.sensor.flight;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class FlightRouteXMLParser {
	public static ArrayList<String> getFlightRoute(String xml){
		ArrayList<String> route = new ArrayList<String>();
		try{
			xml = xml.trim().replaceFirst("^([\\W]+)<","<");
			Document document = DocumentHelper.parseText(xml);
			Element root = document.getRootElement();
			List<Element> elements = root.elements();		
			for(Element element:elements){
				//System.out.println(element.getName());
				if(element.getName().equals(FlightXMLSignMap.Hits.toString())){
					String content = element.getStringValue();
					if(content.equals("0")) return null;
				}
				if(element.getName().equals(FlightXMLSignMap.Results.toString())){
					List<Element> itemElements = element.elements();
					for(Element item:itemElements){
						List<Element> flights = item.elements();
						for(Element flight:flights)
							if(flight.getName().equals(FlightXMLSignMap.Departure.toString())){
								route.add(flight.getStringValue());	
							}else if(flight.getName().equals(FlightXMLSignMap.Arrival.toString())){
								route.add(flight.getStringValue());	
							}else if(flight.getName().equals(FlightXMLSignMap.Via.toString())){
								String transit = flight.getStringValue();
								if((!transit.equals("-"))&&(transit!=""))															
									route.add(flight.getStringValue().replaceAll("-", " "));	
							} 					
					}
				}
			}
		}catch(Exception e){
			
		}
		return route;
	}
}
