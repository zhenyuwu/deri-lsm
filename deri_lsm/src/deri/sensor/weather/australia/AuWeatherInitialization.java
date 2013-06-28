package deri.sensor.weather.australia;

import java.util.Date;
import java.util.Map;

import deri.sensor.database.ServiceLocator;
import deri.sensor.javabeans.Place;
import deri.sensor.javabeans.Sensor;
import deri.sensor.manager.PlaceManager;
import deri.sensor.manager.SensorManager;
import deri.sensor.meta.SourceType;
import deri.sensor.wrapper.WebServiceURLRetriever;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class AuWeatherInitialization extends Thread {

	
	public void initializeSource(){
		SensorManager sensorManager = ServiceLocator.getSensorManager();
		PlaceManager placeManager = ServiceLocator.getPlaceManager();
		
		Map<String,String> id_cities = AuWeatherUtil.getAllID_Cities();
		for(String id : id_cities.keySet()){
			String shtml = AuWeatherUtil.assembleID_SHTMLURL(id);
			String json = AuWeatherUtil.assembleID_JSONURL(id);
			String shtml_content = WebServiceURLRetriever.RetrieveFromURL(shtml);
			String json_content =  WebServiceURLRetriever.RetrieveFromURL(json);
			
			double[] lat_lng = AuWeatherUtil.getPositionFromSHTMLContent(shtml_content);
			if(lat_lng != null){
				double lat = lat_lng[0];
				double lng = lat_lng[1];
				Place place = placeManager.getPlaceWithSpecifiedLatLng(lat, lng);
				if(place == null){
					place = new Place();
					place.setLat(lat_lng[0]);
					place.setLng(lat_lng[1]);
					place.setAuthor("admin");
					place.setCity(id_cities.get(id));
					place.setCountry("Australia");
				}
					
					
				Sensor sensor = new Sensor();
				sensor.setAuthor("admin");
				sensor.setSensorType("weather");
				sensor.setSourceType(SourceType.australia.toString());
				sensor.setSource(json);
				sensor.setTimes(new Date());
				sensor.setPlace(place);
				
				
//				List<Weather> list = AUJsonParser.json2WeatherList(json_content);
//				if(list != null && list.size() > 0){
//					for(Weather weather : list){
//						weather.setSource(sensor);
//						sensorManager.addObject(weather);
//					}
//				}
			}
		}
	}
	
	public static void main(String[] args) {
		AuWeatherInitialization au = new AuWeatherInitialization();
		au.initializeSource();
	}
}
