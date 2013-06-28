package deri.sensor.weather.weatherbug;

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
import deri.sensor.javabeans.Direction;
import deri.sensor.javabeans.ObservedProperty;
import deri.sensor.javabeans.WeatherSignShown;
import deri.sensor.manager.SensorManager;
import deri.sensor.utils.DateUtil;
import deri.sensor.utils.NumberUtil;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class WeatherBugXMLParser {
	public static Date readerTime;
	
	@SuppressWarnings("unchecked")
	public static ArrayList<AbstractProperty> getWeatherElementsFromWeatherBugXML(String xml){
		if(xml == null || xml.trim().equals("")){
			return null;
		}		
		SensorManager sensorManager = ServiceLocator.getSensorManager();
		HashMap hsm = new HashMap<String, String>();
		ArrayList<List> lstObsP = sensorManager.getAllSensorPropertiesForSpecifiedSensorType("weather");
		for(int i=0;i<lstObsP.get(0).size();i++){
			hsm.put(lstObsP.get(1).get(i),lstObsP.get(0).get(i));
		}
		
		ArrayList<AbstractProperty> weatherArr = new ArrayList<AbstractProperty>();
		try {
			Document document = DocumentHelper.parseText(xml);			
			readerTime = null;
			AbstractProperty direction = new Direction();
			AbstractProperty wspeed = new AbstractProperty();
			AbstractProperty atHumi =  new AbstractProperty();
			AbstractProperty atPress = new AbstractProperty();
			AbstractProperty status = new AbstractProperty();
			AbstractProperty temp = new AbstractProperty();
			AbstractProperty picture = new AbstractProperty();
			
			//temperature
			String temprature_express = WeatherBugXMLSignMap.getExpression(WeatherSignShown.temperature.toString());
			Element temprature_element = (Element)document.selectSingleNode(temprature_express);
			String temperature = (String)temprature_element.getData();
			if(NumberUtil.isDouble(temperature)){
				temp.setValue(Double.parseDouble(temperature));
				temp.setUnit("C");
				String className = MappingMap.table2ClassURI.get("temperature");					
				temp.setPropertyName(className);
//				className = className.substring(className.lastIndexOf("#")+1);
				temp.setObservedURL(hsm.get(className).toString());
				weatherArr.add(temp);
			}
			
			//state
			String state_express = WeatherBugXMLSignMap.getExpression(WeatherSignShown.state.toString());
			Element state_element = (Element)document.selectSingleNode(state_express);
			String state = (String)state_element.getData();
			if(state != null && !state.trim().equals("")){
				status.setValue(state);
				String className = MappingMap.table2ClassURI.get("status");					
				status.setPropertyName(className);
//				className = className.substring(className.lastIndexOf("#")+1);
				status.setObservedURL(hsm.get(className).toString());
				weatherArr.add(status);
			}
			
			//pic
			List<Attribute> attributes = state_element.attributes();
			if(attributes != null){
				Attribute pic_attribute = attributes.get(0);
				if(pic_attribute!=null && pic_attribute.getName().trim().equals("icon")){
					String pic = pic_attribute.getValue();
					if(pic != null && !pic.trim().equals("")){
						picture.setValue(pic);
						String className = MappingMap.table2ClassURI.get("picture");						
						picture.setPropertyName(className);
//						className = className.substring(className.lastIndexOf("#")+1);
						picture.setObservedURL(hsm.get(className).toString());
						weatherArr.add(picture);
					}
				}
			}
			
			
			
			String wind_direction_express = WeatherBugXMLSignMap.getExpression(WeatherSignShown.wind_direction.toString());
			Element wind_direction_element = (Element)document.selectSingleNode(wind_direction_express);
			String wind_direction = (String)wind_direction_element.getData();
			if(NumberUtil.isDouble(wind_direction)){
				direction.setValue(wind_direction);
				String className = MappingMap.table2ClassURI.get("direction");				
				direction.setPropertyName(className);
//				className = className.substring(className.lastIndexOf("#")+1);
				direction.setObservedURL(hsm.get(className).toString());
				weatherArr.add(direction);
			}
			
			
			String wind_speed_express = WeatherBugXMLSignMap.getExpression(WeatherSignShown.wind_speed.toString());
			Element wind_speed_element = (Element)document.selectSingleNode(wind_speed_express);
			String wind_speed = (String)wind_speed_element.getData();
			if(NumberUtil.isDouble(wind_speed)){
				wspeed.setValue(Double.parseDouble(wind_speed));
				wspeed.setUnit("mph");
				String className = MappingMap.table2ClassURI.get("windspeed");				
				wspeed.setPropertyName(className);
//				className = className.substring(className.lastIndexOf("#")+1);
				wspeed.setObservedURL(hsm.get(className).toString());
				weatherArr.add(wspeed);
			}
			
			
			String atmosphere_humidity_express = WeatherBugXMLSignMap.getExpression(WeatherSignShown.atmosphere_humidity.toString());
			Element atmosphere_humidity_element = (Element)document.selectSingleNode(atmosphere_humidity_express);
			String atmosphere_humidity = (String)atmosphere_humidity_element.getData();
			if(NumberUtil.isDouble(atmosphere_humidity)){
				atHumi.setValue(Double.parseDouble(atmosphere_humidity));
				String className = MappingMap.table2ClassURI.get("atmospherehumidity");				
				atHumi.setPropertyName(className);
//				className = className.substring(className.lastIndexOf("#")+1);
				atHumi.setObservedURL(hsm.get(className).toString());
				weatherArr.add(atHumi);
			}
			
			
			String atmosphere_pressure_express = WeatherBugXMLSignMap.getExpression(WeatherSignShown.atmosphere_pressure.toString());
			Element atmosphere_pressure_element = (Element)document.selectSingleNode(atmosphere_pressure_express);
			String atmosphere_pressure = (String)atmosphere_pressure_element.getData();
			if(NumberUtil.isDouble(atmosphere_pressure)){
				atPress.setValue(Double.parseDouble(atmosphere_pressure));
				atPress.setUnit("mb");
				String className = MappingMap.table2ClassURI.get("atmospherepressure");				
				atPress.setPropertyName(className);
//				className = className.substring(className.lastIndexOf("#")+1);
				atPress.setObservedURL(hsm.get(className).toString());
				weatherArr.add(atPress);
			}
			
			String year_express = WeatherBugXMLSignMap.getExpression("year");
			Element year_element = (Element)document.selectSingleNode(year_express);
			String year = year_element.attributeValue("callsign");
			
			String month_express = WeatherBugXMLSignMap.getExpression("month");
			Element month_element = (Element)document.selectSingleNode(month_express);
			String month = NumberUtil.to2DigitsFormat(month_element.attributeValue("callsign"));
			
			String day_express = WeatherBugXMLSignMap.getExpression("day");
			Element day_element = (Element)document.selectSingleNode(day_express);
			String day = NumberUtil.to2DigitsFormat(day_element.attributeValue("callsign"));
			
			String hour_express = WeatherBugXMLSignMap.getExpression("hour");
			Element hour_element = (Element)document.selectSingleNode(hour_express);
			String hour = NumberUtil.to2DigitsFormat(hour_element.attributeValue("hour-24"));
			
			String minute_express = WeatherBugXMLSignMap.getExpression("minute");
			Element minute_element = (Element)document.selectSingleNode(minute_express);
			String minute = NumberUtil.to2DigitsFormat(minute_element.attributeValue("callsign"));
			
			String second_express = WeatherBugXMLSignMap.getExpression("second");
			Element second_element = (Element)document.selectSingleNode(second_express);
			String second = NumberUtil.to2DigitsFormat(second_element.attributeValue("callsign"));
			
			String fullTime = year + month + day + hour + minute + second;
			Date time = DateUtil.fullFormatDigits2Date(fullTime);
			wspeed.setTimes(time);
			status.setTimes(time);
			direction.setTimes(time);
			atHumi.setTimes(time);
			atPress.setTimes(time);
			temp.setTimes(time);
			picture.setTimes(time);
			readerTime = time;
			
		} catch (Exception e) {
			return null;
		} 

		return readerTime==null?null:weatherArr;
	}
}
