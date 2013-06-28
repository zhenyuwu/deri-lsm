package deri.sensor.wrapper;

import java.util.List;

import deri.sensor.database.ServiceLocator;
import deri.sensor.manager.*;
import deri.sensor.meta.SourceType;
import deri.sensor.utils.ThreadUtil;
import deri.sensor.weather.australia.AUJsonParser;


/**
 * @author Hoan Nguyen Mau Quoc
 */
public class AuWeatherWrapper extends Thread {

	@Override
	public void run() {
		long count = 0;
		System.out.println("----------------------------------- AuWeatherWrapper has started -----------------------------------");
		SensorManager sensorManager = ServiceLocator.getSensorManager();
		for(;;){
			List<String> sources = sensorManager.getAllSensorSourceWithSpecifiedSourceType(SourceType.australia.toString());
			for(String url : sources){
				String json = WebServiceURLRetriever.RetrieveFromURL(url);
				AUJsonParser.json2WeatherList(json,url);
			}			
			System.out.println("australia:\torder " + ++count + " has finished");
			ThreadUtil.sleepForDays(2);
		}
	}
	
	public void postWeatherElements(String url){
		String json = WebServiceURLRetriever.RetrieveFromURL(url);
		AUJsonParser.json2WeatherList(json,url);
	}
	
	public static void main(String[] args) {
		AuWeatherWrapper auw = new AuWeatherWrapper(); 
		auw.postWeatherElements("http://www.bom.gov.au/fwo/IDQ60801/IDQ60801.94569.json");
	}
}
