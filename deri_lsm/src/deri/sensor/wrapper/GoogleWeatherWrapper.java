package deri.sensor.wrapper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.VirtuosoConstantUtil;
import deri.sensor.hashmaps.util.XSLTMapFile;
import deri.sensor.javabeans.Observation;
import deri.sensor.javabeans.Sensor;
import deri.sensor.manager.SensorManager;
import deri.sensor.meta.SourceType;
import deri.sensor.utils.DateUtil;
import deri.sensor.weather.google.GoogleWeatherXMLParser;
import deri.sensor.weather.weatherbug.WeatherBugXMLParser;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class GoogleWeatherWrapper extends Thread{
	private String url;
	private SensorManager sensorManager = ServiceLocator.getSensorManager();
	public GoogleWeatherWrapper(){
	}
	
	public GoogleWeatherWrapper(String name){
		this.setName(name);
	}
		
	public void postWeatherElement(String url){		
		try{
			boolean isNewest=true;
			String xml = WebServiceURLRetriever.RetrieveFromURL(url);
			ArrayList weatherElement = GoogleWeatherXMLParser.getWeatherElementsFromGoogleXML(xml);
			if(weatherElement.size()>1){				
				Sensor sensor = sensorManager.getSpecifiedSensorWithSource(url);
				Observation newest = sensorManager.getNewestObservationForOneSensor(sensor.getId());
				if(newest == null || DateUtil.isBefore(newest.getTimes(), WeatherBugXMLParser.readerTime)){
					TransformerFactory tFactory = TransformerFactory.newInstance();
					String txt="";					
					String xsltPath = XSLTMapFile.sensordata2xslt.get(SourceType.getSourceType(sensor.getSource()));
					try {
						Transformer transformer =
						    tFactory.newTransformer(new StreamSource(new File(xsltPath)));
						transformer.setParameter("sensorId", sensor.getId());
						transformer.setParameter("sourceType", sensor.getSourceType());
						transformer.setParameter("sensorType", sensor.getSensorType());
						transformer.setParameter("sourceURL", sensor.getSource());
						transformer.setParameter("foi", newest.getFeatureOfInterest());						
						
						InputStream inputStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
						
						Writer outWriter = new StringWriter();  
						StreamResult result = new StreamResult( outWriter );            
						transformer.transform(new StreamSource(inputStream),result);					
						txt = outWriter.toString().trim();
						
						System.out.println(txt);
						sensorManager.insertTriplesToGraph(VirtuosoConstantUtil.sensormasherDataGraphURI,txt);
					} catch (Exception e) {
						e.printStackTrace();
					}				
				}	
			}	
		}catch(Exception e){
			e.printStackTrace();
		}			
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
