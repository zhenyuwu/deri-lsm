package deri.sensor.wrapper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import deri.sensor.coreservlets.MyServlet;
import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.VirtuosoConstantUtil;
import deri.sensor.hashmaps.util.XSLTMapFile;
import deri.sensor.javabeans.Observation;
import deri.sensor.javabeans.Sensor;
import deri.sensor.manager.SensorManager;
import deri.sensor.meta.SourceType;
import deri.sensor.utils.DateUtil;
import deri.sensor.weather.weatherbug.WeatherBugXMLParser;


public class WBugWeatherWrapper{
	private SensorManager sensorManager = ServiceLocator.getSensorManager();
	
	private String url;
	
	public WBugWeatherWrapper(){
		
	}
	
	public WBugWeatherWrapper(String url){
		this.url = url;
	}
	
	public void postWeatherElement(String url){		
			try{
				String xml = WebServiceURLRetriever.RetrieveFromURL(url);
				ArrayList weatherElement = WeatherBugXMLParser.getWeatherElementsFromWeatherBugXML(xml);
				if(weatherElement.size()>1){				
					Sensor sensor = sensorManager.getSpecifiedSensorWithSource(url);
					Observation newest = sensorManager.getNewestObservationForOneSensor(sensor.getId());
					if(newest == null || DateUtil.isBefore(newest.getTimes(), WeatherBugXMLParser.readerTime)){
						TransformerFactory tFactory = TransformerFactory.newInstance();
						String txt="";					
						String xsltPath =  MyServlet.realPath + XSLTMapFile.sensordata2xslt.get(SourceType.getSourceType(sensor.getSource()));
						try {
							Transformer transformer =
							    tFactory.newTransformer(new StreamSource(new File(xsltPath)));
							
							String id = sensor.getId().substring(sensor.getId().lastIndexOf("/")+1);
							String foi = Double.toString(sensor.getPlace().getLat()).replace(".", "").replace("-", "")+
									Double.toString(sensor.getPlace().getLng()).replace(".", "").replace("-", "");
							
							transformer.setParameter("sensorId", id);
							transformer.setParameter("sourceType", sensor.getSourceType());
							transformer.setParameter("sensorType", sensor.getSensorType());
							transformer.setParameter("sourceURL", sensor.getSource());
							transformer.setParameter("foi", foi);						
							transformer.setParameter("utc-timestamp", DateUtil.date2StandardString(new Date()));
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
	
	public static void main(String[] args) {
		WBugWeatherWrapper bw = new WBugWeatherWrapper();
	}
}
