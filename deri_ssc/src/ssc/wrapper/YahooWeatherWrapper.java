package ssc.wrapper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import ssc.beans.Sensor;
import ssc.beans.SensorManager;
import ssc.utils.SourceType;
import ssc.utils.VirtuosoConstantUtil;
import ssc.utils.XSLTMapFile;
import ssc.websocket.echo.MyServlet;

public class YahooWeatherWrapper{	

	int sleepDuration;
	SensorManager sensorManager =  new SensorManager();
	public YahooWeatherWrapper() {
		
		// TODO Auto-generated constructor stub
	}	
					
	public String feed(String sensorId){		
		String nt="";
		try{	
			Sensor sensor = sensorManager.getSpecifiedSensorWithSensorId(sensorId);
			if(sensor==null) return null;
			String xml = WebServiceURLRetriever.RetrieveFromURL(sensor.getSource());	
							
			System.setProperty("javax.xml.transform.TransformerFactory",
                       "net.sf.saxon.TransformerFactoryImpl");
			TransformerFactory tFactory = TransformerFactory.newInstance();
			String xsltPath = XSLTMapFile.sensordata2xslt.get(SourceType.getSourceType(sensor.getSourceType()));
			xsltPath = MyServlet.realPath + xsltPath;
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
				InputStream inputStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));				
				Writer outWriter = new StringWriter();  
				StreamResult result = new StreamResult( outWriter );            
				transformer.transform(new StreamSource(inputStream),result);					
				nt = outWriter.toString().trim();
//				System.out.println(nt);
				sensorManager.insertTriplesToGraph(VirtuosoConstantUtil.sensormasherDataGraphURI, nt);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}catch(Exception e){
			e.printStackTrace();		
		}		
		return nt;
	}
	
	public static void main(String[] args) {
		 try {
	        	YahooWeatherWrapper wuWrapper = new YahooWeatherWrapper();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}

