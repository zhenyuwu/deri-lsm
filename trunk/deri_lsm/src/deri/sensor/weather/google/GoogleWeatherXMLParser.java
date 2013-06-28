package deri.sensor.weather.google;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.MappingMap;
import deri.sensor.javabeans.AbstractProperty;
import deri.sensor.javabeans.ObservedProperty;
import deri.sensor.manager.SensorManager;
import deri.sensor.utils.DateUtil;
import deri.sensor.wrapper.WebServiceURLRetriever;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class GoogleWeatherXMLParser {
	public static Date readerTime;
	@SuppressWarnings("unchecked")
	public static ArrayList getWeatherElementsFromGoogleXML(String xml){
		if(xml == null || xml.trim().equals("")){
			return null;
		}
		SensorManager sensorManager = ServiceLocator.getSensorManager();
		HashMap hsm = new HashMap<String, String>();
		List<ObservedProperty> lstObsP = sensorManager.getAllObservedProperty();
		for(ObservedProperty osp:lstObsP){
			hsm.put(osp.getClassURI(),osp.getUrl());
		}
		
		ArrayList weatherArr = new ArrayList();
		try {			
			Document document = DocumentHelper.parseText(xml);
			Element root = document.getRootElement();			
			List<Element> elements = root.elements();
			for(Element element : elements){				
				if(element.getName().equals(GoogleWeatherXMLSign.weather.toString())){
					AbstractProperty direction = new AbstractProperty();
					AbstractProperty wspeed = new AbstractProperty();
					AbstractProperty atHumi =  new AbstractProperty();			
					AbstractProperty status = new AbstractProperty();
					AbstractProperty temp = new AbstractProperty();
					
					
					List<Element> inner_elements = element.elements();
					for(Element inner_element : inner_elements){
						System.out.println(inner_element.getName());
						if(inner_element.getName().equals(GoogleWeatherXMLSign.forecast_information.toString())){
							List<Element> forecast_elements = inner_element.elements();
							for(Element forecast_element : forecast_elements){
								if(forecast_element.getName().equals(GoogleWeatherXMLSign.current_date_time.toString())){
									Attribute attribute = forecast_element.attribute("data");
									String value = attribute.getValue();
									readerTime = DateUtil.standardString2Date(value);
								}
							}
						}else if(inner_element.getName().equals(GoogleWeatherXMLSign.current_conditions.toString())){							
							List<Element> current_elements = inner_element.elements();
							for(Element current_element : current_elements){
								Attribute attribute = current_element.attribute("data");								
								String content = attribute.getValue();
								if(current_element.getName().equals(GoogleWeatherXMLSign.condition.toString())){
									status.setValue(content);
									status.setTimes(readerTime);
									String className = MappingMap.table2ClassURI.get(status.getClass().getSimpleName().toLowerCase());
									status.setObservedURL(hsm.get(className).toString());
									status.setPropertyName(status.getClass().getSimpleName());
									weatherArr.add(status);
								}else if(current_element.getName().equals(GoogleWeatherXMLSign.temp_c.toString())){
									double _temprature = Double.parseDouble(content);						
									temp.setValue(_temprature);
									temp.setUnit("C");
									temp.setTimes(readerTime);
									String className = MappingMap.table2ClassURI.get(temp.getClass().getSimpleName().toLowerCase());
									temp.setObservedURL(hsm.get(className).toString());
									temp.setPropertyName(temp.getClass().getSimpleName());
									weatherArr.add(temp);
								}else if(current_element.getName().equals(GoogleWeatherXMLSign.humidity.toString())){
									int startIdx = content.indexOf(":");
									int endIdx = content.indexOf("%");									
									String value = content.substring(startIdx+1, endIdx).trim();
									double _atmosphere_humidity = Double.parseDouble(value);						
									atHumi.setValue(_atmosphere_humidity);
									String className = MappingMap.table2ClassURI.get(atHumi.getClass().getSimpleName().toLowerCase());
									atHumi.setObservedURL(hsm.get(className).toString());
									atHumi.setPropertyName(atHumi.getClass().getSimpleName());
									atHumi.setUnit("%");
									atHumi.setTimes(readerTime);
									weatherArr.add(atHumi);
								}else if(current_element.getName().equals(GoogleWeatherXMLSign.wind_condition.toString())){
									int startIdx = content.indexOf(":");
									int endIdx = content.indexOf("at");
									String direc = content.substring(startIdx+1,endIdx).trim();
									int unitIdx = content.indexOf("mph");
									String speed = content.substring(endIdx+2,unitIdx).trim();
									String unit = content.substring(unitIdx).trim();
									direction.setValue(direc);
									String className = MappingMap.table2ClassURI.get(direction.getClass().getSimpleName().toLowerCase());
									direction.setObservedURL(hsm.get(className).toString());
									direction.setPropertyName(direction.getClass().getSimpleName());
									weatherArr.add(direction);
									
									wspeed.setValue(Double.parseDouble(speed));
									wspeed.setTimes(readerTime);
									wspeed.setUnit(unit);
									className = MappingMap.table2ClassURI.get(wspeed.getClass().getSimpleName().toLowerCase());
									wspeed.setObservedURL(hsm.get(className).toString());	
									wspeed.setPropertyName(wspeed.getClass().getSimpleName());
									weatherArr.add(wspeed);
								}								
							}							
						}
					}
				}
			}
		} catch (Exception e) {
			return null;
		} 
		return weatherArr;
	}
	
	public static void main(String[] args) {
		String url = "http://www.google.com/ig/api?weather=hue";
		String dataIn = WebServiceURLRetriever.RetrieveFromURL(url);
		//System.out.println(dataIn);
		GoogleWeatherXMLParser.getWeatherElementsFromGoogleXML(dataIn);
}
}
