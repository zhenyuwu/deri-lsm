package deri.sensor.weather.wunderground;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.MappingMap;
import deri.sensor.javabeans.AbstractProperty;
import deri.sensor.javabeans.Picture;
import deri.sensor.javabeans.WeatherSignShown;
import deri.sensor.manager.SensorManager;
import deri.sensor.utils.DateUtil;
import deri.sensor.utils.NumberUtil;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class WUnderGroundXMLParser {
	public static Date readerTime = new Date();
	
	public static ArrayList<AbstractProperty> getWeatherElementsFromWUnderGroundXML(String xml){
		if(xml == null || xml.trim().equals("")){
			return null;
		}
		
		ArrayList<AbstractProperty> weatherArr = new ArrayList<AbstractProperty>();
		readerTime = null;
		AbstractProperty wc = new AbstractProperty();
		AbstractProperty direction = new AbstractProperty();
		AbstractProperty wspeed = new AbstractProperty();
		AbstractProperty atHumi =  new AbstractProperty();
		AbstractProperty atPress = new AbstractProperty();
		AbstractProperty status = new AbstractProperty();
		AbstractProperty temp = new AbstractProperty();
		Picture picture = new Picture();
		
		try {
			Document document = DocumentHelper.parseText(xml);
			
			SensorManager sensorManager = ServiceLocator.getSensorManager();
			HashMap hsm = new HashMap<String, String>();
			ArrayList<List> lstObsP = sensorManager.getAllSensorPropertiesForSpecifiedSensorType("weather");
			for(int i=0;i<lstObsP.get(0).size();i++){
				hsm.put(lstObsP.get(1).get(i),lstObsP.get(0).get(i));
			}
			//temperature
			String temprature_express = WUnderGroundXMLSignMap.getExpression(WeatherSignShown.temperature.toString());
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
			String state_express = WUnderGroundXMLSignMap.getExpression(WeatherSignShown.state.toString());
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
					
			String wind_chill_express = WUnderGroundXMLSignMap.getExpression(WeatherSignShown.wind_chill.toString());
			Element wind_chill_element = (Element)document.selectSingleNode(wind_chill_express);
			String wind_chill = (String)wind_chill_element.getData();
			if(NumberUtil.isDouble(wind_chill)){
				wc.setValue(Double.parseDouble(wind_chill));
				wc.setUnit("C");
				String className = MappingMap.table2ClassURI.get("windchill");
				wc.setPropertyName(className);
//				className = className.substring(className.lastIndexOf("#")+1);
				wc.setObservedURL(hsm.get(className).toString());
				weatherArr.add(wc);
			}
						
			
			String wind_direction_express = WUnderGroundXMLSignMap.getExpression(WeatherSignShown.wind_direction.toString());
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
			
			
			String wind_speed_express = WUnderGroundXMLSignMap.getExpression(WeatherSignShown.wind_speed.toString());
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
			
			String atmosphere_humidity_express = WUnderGroundXMLSignMap.getExpression(WeatherSignShown.atmosphere_humidity.toString());
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
			
			
			String atmosphere_pressure_express = WUnderGroundXMLSignMap.getExpression(WeatherSignShown.atmosphere_pressure.toString());
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
			
			
			String pic_express = WUnderGroundXMLSignMap.getExpression("pic");
			Element pic_element = (Element)document.selectSingleNode(pic_express);
			String pic = (String)pic_element.getData();
			if(pic != null && !pic.trim().equals("")){
				picture.setValue(pic);
				String className = MappingMap.table2ClassURI.get("picture");						
				picture.setPropertyName(className);
//				className = className.substring(className.lastIndexOf("#")+1);
				picture.setObservedURL(hsm.get(className).toString());
				weatherArr.add(picture);
			}
			
			
			String time_express = WUnderGroundXMLSignMap.getExpression("time");
			Element time_element = (Element)document.selectSingleNode(time_express);
			String time = (String)time_element.getData();
			if(time != null && !time.trim().equals("")){
				int index_last_blank = time.lastIndexOf(" ");
				time = time.substring(0,index_last_blank);
				if(DateUtil.isRFC822WUnderGroundFormat(time)){
					Date _time = DateUtil.RFC822WUnderGroundFormat_to_date(time);
					wc.setTimes(_time);
					wspeed.setTimes(_time);
					status.setTimes(_time);
					direction.setTimes(_time);
					atHumi.setTimes(_time);
					atPress.setTimes(_time);
					temp.setTimes(_time);
					picture.setTimes(_time);
					readerTime = _time;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} 

		return readerTime==null?null:weatherArr;
	}

}
