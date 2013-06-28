package deri.sensor.weather.australia;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.MappingMap;
import deri.sensor.hashmaps.util.VirtuosoConstantUtil;
import deri.sensor.javabeans.AbstractProperty;
import deri.sensor.javabeans.Observation;
import deri.sensor.javabeans.Sensor;
import deri.sensor.json.JSONArray;
import deri.sensor.json.JSONException;
import deri.sensor.json.JSONObject;
import deri.sensor.manager.SensorManager;
import deri.sensor.meta.WeatherWindDirection;
import deri.sensor.utils.DateUtil;
import deri.sensor.utils.NumberUtil;
import deri.sensor.wrapper.TriplesDataRetriever;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class AUJsonParser {
	public static Date readerTime;
	
	public static void json2WeatherList(String jsonString,String source){
		if(jsonString == null || jsonString.trim().equals("")){
			return;
		}
		readerTime = new Date();
		String triples="";
		try {
			JSONObject jo = new JSONObject(jsonString);
			jo = jo.getJSONObject(AUJsonSign.observations.toString());
			
			JSONArray array   = jo.getJSONArray(AUJsonSign.data.toString());
			SensorManager sensorManager = ServiceLocator.getSensorManager();
			HashMap hsm = new HashMap<String, String>();
    		ArrayList<List> lstObsP = sensorManager.getAllSensorPropertiesForSpecifiedSensorType("weather");
    		for(int i=0;i<lstObsP.get(0).size();i++){
    			hsm.put(lstObsP.get(1).get(i),lstObsP.get(0).get(i));
    		}
    		
			for(int i=0;i<array.length();i++){
				AbstractProperty direction = new AbstractProperty();
				AbstractProperty wspeed = new AbstractProperty();
				AbstractProperty atHumi =  new AbstractProperty();
				AbstractProperty atPress = new AbstractProperty();
				AbstractProperty temp = new AbstractProperty();


				jo = array.getJSONObject(i);
				
				String time_str = jo.get(AUJsonSign.local_date_time_full.toString()).toString();
				if(NumberUtil.isLong(time_str));{
					Date time = DateUtil.fullFormatDigits2Date(time_str);
					readerTime = time;
					wspeed.setTimes(time);
					direction.setTimes(time);
					atHumi.setTimes(time);
					atPress.setTimes(time);					
					temp.setTimes(time);
				}
				
				Sensor sensor = sensorManager.getSpecifiedSensorWithSource(source);
				if (sensor==null) return;
				
				String foi = VirtuosoConstantUtil.sensorObjectDataPrefix 
						+ Double.toString(sensor.getPlace().getLat()).replace(".", "").replace("-", "")+
						Double.toString(sensor.getPlace().getLng()).replace(".", "").replace("-", "");
				String observationId = System.nanoTime()+"";
				triples = TriplesDataRetriever.getObservationTripleData(observationId,sensor.getId(), foi, readerTime);
				
				String temprature_str = jo.get(AUJsonSign.air_temp.toString()).toString();
				if(NumberUtil.isDouble(temprature_str)){
					double temprature = new Double(temprature_str);
					temp.setValue(temprature);
					temp.setUnit("C");
					String className = MappingMap.table2ClassURI.get("temperature");					
					temp.setPropertyName(className);
//					className = className.substring(className.lastIndexOf("#")+1);
					temp.setObservedURL(hsm.get(className).toString());
					triples+=TriplesDataRetriever.getTripleDataHasUnit(temp.getPropertyName(), temp.getValue(),temp.getUnit(),
        					observationId, temp.getObservedURL(), readerTime);
				}
				
				String relative_humidity_str = jo.get(AUJsonSign.rel_hum.toString()).toString();
				if(NumberUtil.isDouble(relative_humidity_str)){
					double relative_humidity = new Double(relative_humidity_str);
					atHumi.setValue(relative_humidity);
					String className = MappingMap.table2ClassURI.get("atmospherehumidity");					
					atHumi.setPropertyName(className);
//					className = className.substring(className.lastIndexOf("#")+1);
					atHumi.setObservedURL(hsm.get(className).toString());
					triples+=TriplesDataRetriever.getTripleDataHasNoUnit(atHumi.getPropertyName(), atHumi.getValue(),
        					observationId, atHumi.getObservedURL(), readerTime);
				}
				
				
				String wind_direction = jo.get(AUJsonSign.wind_dir.toString()).toString();
				if(WeatherWindDirection.map_360.get(wind_direction) != null){
					double direc = WeatherWindDirection.map_360.get(wind_direction);
					direction.setValue(Double.toString(direc));
					String className = MappingMap.table2ClassURI.get("direction");					
					direction.setPropertyName(className);
//					className = className.substring(className.lastIndexOf("#")+1);
					direction.setObservedURL(hsm.get(className).toString());
					triples+=TriplesDataRetriever.getTripleDataHasNoUnit(direction.getPropertyName(), direction.getValue(),
        					observationId, direction.getObservedURL(), readerTime);
				}
				
				
				String wind_speed_str  = jo.get(AUJsonSign.wind_spd_kmh.toString()).toString();
				if(NumberUtil.isDouble(wind_speed_str)){
					double wind_speed = new Double(wind_speed_str);
					wspeed.setValue(wind_speed);
					wspeed.setUnit("kmh");
					String className = MappingMap.table2ClassURI.get("windspeed");					
					wspeed.setPropertyName(className);
//					className = className.substring(className.lastIndexOf("#")+1);
					wspeed.setObservedURL(hsm.get(className).toString());
					triples+=TriplesDataRetriever.getTripleDataHasUnit(wspeed.getPropertyName(), wspeed.getValue(),wspeed.getUnit(),
        					observationId, wspeed.getObservedURL(), readerTime);
				}
				
				String press_qnh_str = jo.get(AUJsonSign.press_qnh.toString()).toString();
				if(NumberUtil.isDouble(press_qnh_str)){
					double _atmosphere_pressure = new Double(press_qnh_str);
					atPress.setValue(_atmosphere_pressure);
					atPress.setUnit("mb");
					String className = MappingMap.table2ClassURI.get("atmospherepressure");					
					atPress.setPropertyName(className);
//					className = className.substring(className.lastIndexOf("#")+1);
					atPress.setObservedURL(hsm.get(className).toString());
					triples+=TriplesDataRetriever.getTripleDataHasUnit(atPress.getPropertyName(), atPress.getValue(),atPress.getUnit(),
        					observationId, atPress.getObservedURL(), readerTime);
				}				
			}
			System.out.println(triples);
//			sensorManager.insertTriplesToGraph(VirtuosoConstantUtil.sensormasherDataGraphURI, triples);
		} catch (NumberFormatException e) {
			return; 
		} catch (JSONException e) {
			return;
		}		
		
	}

}
