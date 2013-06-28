package deri.sensor.flight.libhomeradar;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import deri.sensor.wrapper.WebServiceURLRetriever;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class FlightAroundXMLParser {
	private static String flightAroundURL = "http://www.libhomeradar.org/assets/lhr.api?n=username&p=password=around&r=50";
	public static ArrayList<List> getFlightAroundInformation(double userlat,double userlng){
		ArrayList resultsList = new ArrayList<List>();;
		flightAroundURL+="&lat=" + userlat +"&lon=" + userlng;
		try{
			String xml = WebServiceURLRetriever.RetrieveFromURL(flightAroundURL);
			xml = xml.trim().replaceFirst("^([\\W]+)<","<");
			Document document = DocumentHelper.parseText(xml);
			Element root = document.getRootElement();
			List<Element> elements = root.elements();
			String callSign = "";
			String routing="";
			String registration="";
			String model="";
			String lat="";
			String lng="";
			String distance="";			
			for(Element element:elements){
				if(element.getName().equals(FlightXMLSignMap.Hits.toString())){
					String content = element.getStringValue();
					if(content.equals("0")) return null;
				}				
				
				if(element.getName().equals(FlightXMLSignMap.Results.toString())){
					List<Element> itemElements = element.elements();
					for(Element item:itemElements){
						if(item.getName().equals(FlightXMLSignMap.Item.toString())){
							List<Element> flights = item.elements();
							List inforList = new ArrayList<String>();
							for(Element flight:flights){
								if(flight.getName().equals(FlightXMLSignMap.Registration.toString())){
									registration = flight.getStringValue();
									inforList.add(registration);
								}else if(flight.getName().equals(FlightXMLSignMap.Model.toString())){
									model = flight.getStringValue();
									inforList.add(model);
								}else if(flight.getName().equals(FlightXMLSignMap.Callsign.toString())){
									callSign = flight.getStringValue();
									inforList.add(callSign);
								}else if(flight.getName().equals(FlightXMLSignMap.Routing.toString())){
									routing = flight.getStringValue();
									inforList.add(routing);
								}else if(flight.getName().equals(FlightXMLSignMap.LAT.toString())){								
									lat = flight.getStringValue();
									inforList.add(lat);
								}else if(flight.getName().equals(FlightXMLSignMap.LON.toString())){
									lng = flight.getStringValue();
									inforList.add(lng);
								}
								else if(flight.getName().equals(FlightXMLSignMap.Distance.toString())){
									distance = flight.getStringValue();
									inforList.add(distance);
								}
							}
							resultsList.add(inforList);
						}
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}		
		return resultsList;
	}
}
