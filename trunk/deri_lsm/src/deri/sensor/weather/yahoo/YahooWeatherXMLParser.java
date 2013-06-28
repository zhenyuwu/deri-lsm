package deri.sensor.weather.yahoo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.MappingMap;
import deri.sensor.javabeans.AbstractProperty;
import deri.sensor.javabeans.WeatherSignShown;
import deri.sensor.manager.SensorManager;
import deri.sensor.utils.DateUtil;
import deri.sensor.utils.NumberUtil;

/**
 * @author Hoan Nguyen Mau Quoc
 */
/**
 * parse the yahoo weather xml to map. The content format is:
 * 
		temprature -- 18
		state -- fair (night)
		wind_direction -- 350
		wind_speed -- 3.22
		wind_chill -- 18
		atmosphere_pressure -- 982.05
		atmosphere_visibility -- 9.99
		atmosphere_pressure_rising -- steady
		atmosphere_humidity -- 28
		time -- Sat, 08 May 2010 12:00 am CST
 */
public class YahooWeatherXMLParser {
	public static Date readerTime;
	@SuppressWarnings("unchecked")
	public static Map<String,String> getWeatherMapFromYahooXML(String xml){
		if(xml == null || xml.trim().equals("")){
			return null;
		}
		
		Map<String,String> result = new HashMap<String,String>(5);
		try {
			Document document = DocumentHelper.parseText(xml);

			String wind_express = YahooWeatherXMLSignMap.assembleWeatherSign(YahooWeatherXMLSign.yweather_wind);
			Element wind_element = (Element)document.selectSingleNode(wind_express);
			Iterator<Attribute> wind_iter = wind_element.attributeIterator();
			for(int i=0;wind_iter.hasNext();i++){
				Attribute attribute = wind_iter.next();
				String name  = attribute.getName();
				if(name.equals("chill")){
					String value = attribute.getValue();
					result.put(WeatherSignShown.wind_chill.toString(), value);
				}else if(name.equals("direction")){
					String value = attribute.getValue();
					result.put(WeatherSignShown.wind_direction.toString(), value);
				}else if(name.equals("speed")){
					String value = attribute.getValue();
					result.put(WeatherSignShown.wind_speed.toString(), value);
				}
			}
			
			String atmosphere_express = YahooWeatherXMLSignMap.assembleWeatherSign(YahooWeatherXMLSign.yweather_atmosphere);
			Element atmosphere_element = (Element)document.selectSingleNode(atmosphere_express);
			Iterator<Attribute> atmosphere_iter = atmosphere_element.attributeIterator();
			for(int i=0;atmosphere_iter.hasNext();i++){
				Attribute attribute = atmosphere_iter.next();
				String name  = attribute.getName();
				if(name.equals("humidity")){
					String value = attribute.getValue();
					result.put(WeatherSignShown.atmosphere_humidity.toString(), value);
				}else if(name.equals("visibility")){
					String value = attribute.getValue();
					result.put(WeatherSignShown.atmosphere_visibility.toString(), value);
				}else if(name.equals("pressure")){
					String value = attribute.getValue();
					result.put(WeatherSignShown.atmosphere_pressure.toString(), value);
				}
			}
			
			String condition_express = YahooWeatherXMLSignMap.assembleWeatherSign(YahooWeatherXMLSign.item,YahooWeatherXMLSign.yweather_condition);
			Element condition_element = (Element)document.selectSingleNode(condition_express);
			Iterator<Attribute> condition_iter = condition_element.attributeIterator();
			for(int i=0;condition_iter.hasNext();i++){
				Attribute attribute = condition_iter.next();
				String name  = attribute.getName();
				if(name.equals("code")){
					String value = attribute.getValue();
					result.put(WeatherSignShown.state.toString(), YahooWeatherStateMap.getState(Integer.parseInt(value)));
				}else if(name.equals("temp")){
					String value = attribute.getValue();
					result.put(WeatherSignShown.temperature.toString(), value);
				}else if(name.equals("date")){
					String value = attribute.getValue();
					int index = value.lastIndexOf(" ");
					value = value.substring(0,index);
					result.put("time", value);
				}
			}
			
			String description_expression = YahooWeatherXMLSignMap.assembleWeatherSign(YahooWeatherXMLSign.item,YahooWeatherXMLSign.description);
			Element description_element = (Element)document.selectSingleNode(description_expression);
			String description = description_element.getText();
			int index_src = description.indexOf("src=");
			int index_gif = description.indexOf("gif");
			String src = description.substring(index_src + 5,index_gif + 3);
			
			result.put("pic",src);
			
		} catch (Exception e) {
			return null;
		} 
		return result;
	}
	
	
	@SuppressWarnings("unchecked")
	public static ArrayList<AbstractProperty> getWeatherElementsFromYahooXML(String xml){
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
			AbstractProperty wc = new AbstractProperty();
			AbstractProperty direction = new AbstractProperty();
			AbstractProperty wspeed = new AbstractProperty();
			AbstractProperty atHumi =  new AbstractProperty();
			AbstractProperty atPress = new AbstractProperty();
			AbstractProperty atVisi = new AbstractProperty();
			AbstractProperty status = new AbstractProperty();
			AbstractProperty temp = new AbstractProperty();
			AbstractProperty picture = new AbstractProperty();
			
			String wind_express = YahooWeatherXMLSignMap.assembleWeatherSign(YahooWeatherXMLSign.yweather_wind);
			Element wind_element = (Element)document.selectSingleNode(wind_express);
			Iterator<Attribute> wind_iter = wind_element.attributeIterator();
			for(int i=0;wind_iter.hasNext();i++){
				Attribute attribute = wind_iter.next();
				String name  = attribute.getName();
				if(name.equals("chill")){
					String wind_chill = attribute.getValue();
					if(NumberUtil.isDouble(wind_chill)){
						double _wind_chill = Double.parseDouble(wind_chill);						
						wc.setValue(_wind_chill);	
						String className = MappingMap.table2ClassURI.get("windchill");						
						wc.setPropertyName(className);
//						className = className.substring(className.lastIndexOf("#")+1);
						wc.setObservedURL(hsm.get(className).toString());
						weatherArr.add(wc);
					}
				}else if(name.equals("direction")){
					String wind_direction = attribute.getValue();
					if(NumberUtil.isDouble(wind_direction)){
						double _wind_direction = Double.parseDouble(wind_direction);						
						direction.setValue(Double.toString(_wind_direction));						
						String className = MappingMap.table2ClassURI.get("direction");				
						direction.setPropertyName(className);
//						className = className.substring(className.lastIndexOf("#")+1);
						direction.setObservedURL(hsm.get(className).toString());
						weatherArr.add(direction);
					}
				}else if(name.equals("speed")){
					String wind_speed = attribute.getValue();
					if(NumberUtil.isDouble(wind_speed)){
						double _wind_speed = Double.parseDouble(wind_speed);						
						wspeed.setValue(_wind_speed);
						String className = MappingMap.table2ClassURI.get("windspeed");				
						wspeed.setPropertyName(className);
//						className = className.substring(className.lastIndexOf("#")+1);
						wspeed.setObservedURL(hsm.get(className).toString());
						weatherArr.add(wspeed);
					}
				}
			}
			
			String atmosphere_express = YahooWeatherXMLSignMap.assembleWeatherSign(YahooWeatherXMLSign.yweather_atmosphere);
			Element atmosphere_element = (Element)document.selectSingleNode(atmosphere_express);
			Iterator<Attribute> atmosphere_iter = atmosphere_element.attributeIterator();
			for(int i=0;atmosphere_iter.hasNext();i++){
				Attribute attribute = atmosphere_iter.next();
				String name  = attribute.getName();
				if(name.equals("humidity")){
					String atmosphere_humidity = attribute.getValue();
					if(NumberUtil.isDouble(atmosphere_humidity)){
						double _atmosphere_humidity = Double.parseDouble(atmosphere_humidity);						
						atHumi.setValue(_atmosphere_humidity);
						String className = MappingMap.table2ClassURI.get("atmospherehumidity");				
						atHumi.setPropertyName(className);
//						className = className.substring(className.lastIndexOf("#")+1);
						atHumi.setObservedURL(hsm.get(className).toString());
						weatherArr.add(atHumi);
					}
				}else if(name.equals("visibility")){
					String atmosphere_visibility = attribute.getValue();
					if(NumberUtil.isDouble(atmosphere_visibility)){
						double _atmosphere_visibility = Double.parseDouble(atmosphere_visibility);						
						atVisi.setValue(_atmosphere_visibility);
						String className = MappingMap.table2ClassURI.get("atmospherevisibility");				
						atVisi.setPropertyName(className);
//						className = className.substring(className.lastIndexOf("#")+1);
						atVisi.setObservedURL(hsm.get(className).toString());
						weatherArr.add(atVisi);
					}
				}else if(name.equals("pressure")){
					String atmosphere_pressure = attribute.getValue();
					if(NumberUtil.isDouble(atmosphere_pressure)){
						double _atmosphere_pressure = Double.parseDouble(atmosphere_pressure);						
						atPress.setValue(_atmosphere_pressure);
						String className = MappingMap.table2ClassURI.get("atmospherepressure");				
						atPress.setPropertyName(className);
//						className = className.substring(className.lastIndexOf("#")+1);
						atPress.setObservedURL(hsm.get(className).toString());
						weatherArr.add(atPress);
					}
					
				}
			}
			
			String condition_express = YahooWeatherXMLSignMap.assembleWeatherSign(YahooWeatherXMLSign.item,YahooWeatherXMLSign.yweather_condition);
			Element condition_element = (Element)document.selectSingleNode(condition_express);
			Iterator<Attribute> condition_iter = condition_element.attributeIterator();
			for(int i=0;condition_iter.hasNext();i++){
				Attribute attribute = condition_iter.next();
				String name  = attribute.getName();
				if(name.equals("code")){
					String _state = attribute.getValue();
					if(NumberUtil.isInteger(_state)){
						_state = YahooWeatherStateMap.getState(Integer.parseInt(_state));
					}else{
						_state = "not available";
					}					
					status.setValue(_state);
					String className = MappingMap.table2ClassURI.get("status");		
					status.setPropertyName(className);
//					className = className.substring(className.lastIndexOf("#")+1);
					status.setObservedURL(hsm.get(className).toString());
					weatherArr.add(status);
					
				}else if(name.equals("temp")){
					String temprature = attribute.getValue();
					if(NumberUtil.isDouble(temprature)){
						double _temprature = Double.parseDouble(temprature);						
						temp.setValue(_temprature);
						String className = MappingMap.table2ClassURI.get("temperature");	
						temp.setPropertyName(className);
//						className = className.substring(className.lastIndexOf("#")+1);
						temp.setObservedURL(hsm.get(className).toString());
						weatherArr.add(temp);
					}
				}else if(name.equals("date")){
					String value = attribute.getValue();
					int index = value.lastIndexOf(" ");
					value = value.substring(0,index);
					
					String time = value;
					Date _time = DateUtil.RFC822Section5Format_to_Date(time);
					if(_time != null){
						//weather.setTimes(_time);
						wc.setTimes(_time);
						wspeed.setTimes(_time);
						status.setTimes(_time);
						direction.setTimes(_time);
						atHumi.setTimes(_time);
						atPress.setTimes(_time);
						atVisi.setTimes(_time);
						temp.setTimes(_time);
						picture.setTimes(_time);
						readerTime = _time;
					}
				}
			}
			
			String description_expression = YahooWeatherXMLSignMap.assembleWeatherSign(YahooWeatherXMLSign.item,YahooWeatherXMLSign.description);
			Element description_element = (Element)document.selectSingleNode(description_expression);
			String description = description_element.getText();
			int index_src = description.indexOf("src=");
			int index_gif = description.indexOf("gif");
			String src = description.substring(index_src + 5,index_gif + 3);
			
			picture.setValue(src);
			
			//weather.setPic(src);
			
			String units_express = YahooWeatherXMLSignMap.assembleWeatherSign(YahooWeatherXMLSign.yweather_units);
			Element units_element = (Element)document.selectSingleNode(units_express);
			Iterator<Attribute> units_iter = units_element.attributeIterator();
			for(int i=0;units_iter.hasNext();i++){
				Attribute attribute = units_iter.next();
				String name  = attribute.getName();
				if(name.equals("temperature")){
					String temperature_unit = attribute.getValue();
					temp.setUnit(temperature_unit);
					wc.setUnit(temperature_unit);					
				}else if(name.equals("pressure")){
					String pressure_units = attribute.getValue();
					atPress.setUnit(pressure_units);				
				}else if(name.equals("speed")){
					String windspeed_unit = attribute.getValue();
					wspeed.setUnit(windspeed_unit);
				}
			}
		} catch (Exception e) {
			return null;
		} 
		return weatherArr;
	}
	public static void main(String[] args) {
		Date date = new Date();
		long timeMills = System.currentTimeMillis();
		date.setTime(timeMills);
		System.out.println(date);
		date.setTime((long)1277905103 * 1000);
		System.out.println(date);
		
	}
	
}
