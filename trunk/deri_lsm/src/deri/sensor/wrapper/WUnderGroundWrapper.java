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
import deri.sensor.javabeans.Sensor;
import deri.sensor.manager.SensorManager;
import deri.sensor.meta.SourceType;
import deri.sensor.utils.DateUtil;
import deri.sensor.weather.wunderground.WUnderGroundXMLParser;

public class WUnderGroundWrapper{
	private SensorManager sensorManager = ServiceLocator.getSensorManager();	

	public WUnderGroundWrapper(){
		
	}		
		
	public void postWeatherElement(String url){		
			try{
				boolean isNewest=true;
				String xml = WebServiceURLRetriever.RetrieveFromURL(url);
				Sensor sensor = sensorManager.getSpecifiedSensorWithSource(url);
				if(sensor==null) return;
				ArrayList weatherElement = WUnderGroundXMLParser.getWeatherElementsFromWUnderGroundXML(xml);
				if(weatherElement==null){
					//sensorManager.deleteAllObservationsForSpecifiedSensor(sensor);
					//sensorManager.deleteObject(sensor);
					return;
				}
				if(weatherElement.size()>1){
//					Observation newest = sensorManager.getNewestObservationForOneSensor(sensor.getId());
//					if(newest == null || DateUtil.isBefore(newest.getTimes(), WUnderGroundXMLParser.readerTime)){
						System.setProperty("javax.xml.transform.TransformerFactory",
		                           "net.sf.saxon.TransformerFactoryImpl");
						TransformerFactory tFactory = TransformerFactory.newInstance();
						String txt="";					
						String xsltPath =  MyServlet.realPath + XSLTMapFile.sensordata2xslt.get(SourceType.getSourceType(sensor.getSourceType()));
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
							transformer.setParameter("foi",foi );						
							transformer.setParameter("utc-timestamp", DateUtil.date2StandardString(new Date()));
							InputStream inputStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
							
							Writer outWriter = new StringWriter();  
							StreamResult result = new StreamResult( outWriter );            
							transformer.transform(new StreamSource(inputStream),result);					
							txt = outWriter.toString().trim();
							
//							System.out.println(txt);
							sensorManager.insertTriplesToGraph(VirtuosoConstantUtil.sensormasherDataGraphURI,txt);
						} catch (Exception e) {
							e.printStackTrace();
						}				
					}	
//				}	
			}catch(Exception e){
				e.printStackTrace();
			}		
	}
	
	public static void main(String[] args) {
		WUnderGroundWrapper wuWrapper = new WUnderGroundWrapper();
		wuWrapper.postWeatherElement("http://api.wunderground.com/weatherstation/WXCurrentObXML.asp?ID=IGAUTENG8");
	}
}
