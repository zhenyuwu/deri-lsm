package deri.sensor.london.railwaytation;

import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import deri.sensor.database.ServiceLocator;
import deri.sensor.javabeans.Sensor;
import deri.sensor.manager.SensorManager;
import deri.sensor.wrapper.WebServiceURLRetriever;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class LondonRailwayStationInitializedLineData {
	private static String lineSource = "http://cloud.tfl.gov.uk/TrackerNet/PredictionSummary/";
	private static String detailedSource = "http://cloud.tfl.gov.uk/TrackerNet/PredictionDetailed/";
	
	public static void updateLineForRailwayStation(){
		SensorManager sensorManager = ServiceLocator.getSensorManager();
		for(LineCode code:LineCode.values()){
			String url = lineSource + code;
			try{
				String xml = WebServiceURLRetriever.RetrieveFromURL(url);
				xml = xml.trim().replaceFirst("^([\\W]+)<","<");
				Document document = DocumentHelper.parseText(xml);
				Element root = document.getRootElement();
				String stationCode = "";
				List<Element> elements = root.elements();
				for(Element element:elements){
					System.out.println(element.getPath());
					List<Attribute> iner_attributes = element.attributes();
					for(Attribute attribute:iner_attributes){
						System.out.println(attribute.getPath());
						if(attribute.getPath().equals(LineCodeXMLSignMap.assembleLindeCodeSign(LineCodeXMLSign.Code))){
							stationCode = attribute.getStringValue().trim();
							Sensor sensor = sensorManager.getRailwayStationWithSpecifiedStationCode(stationCode);
							if(sensor==null) continue;
							sensor.setSourceType(code.toString());
							String detail = detailedSource+code+"/"+stationCode;
							sensor.setSource(detail);
							sensorManager.updateObject(sensor);
						}
					}
//					List<Element> iner_elements = element.elements();					
//					for(Element iner_element:iner_elements){
//						System.out.println(iner_element.getPath());
//					}
				}
			}catch(Exception e){
				e.printStackTrace();
				continue;
			}
			
		}
	}
	
	public static void main(String[] agrs){
		LondonRailwayStationInitializedLineData.updateLineForRailwayStation();
	}
}
