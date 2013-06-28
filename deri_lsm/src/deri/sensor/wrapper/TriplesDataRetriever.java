package deri.sensor.wrapper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


import deri.sensor.coreservlets.MyServlet;
import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.VirtuosoConstantUtil;
import deri.sensor.hashmaps.util.XSLTMapFile;
import deri.sensor.javabeans.Place;
import deri.sensor.javabeans.Sensor;
import deri.sensor.manager.SensorManager;
import deri.sensor.meta.SourceType;
import deri.sensor.utils.DateUtil;
import net.sf.saxon.*;

public class TriplesDataRetriever {

	public static String getTripleDataHasUnit(String dataType,String value,String unit,String observationId,String observedURL,Date time){
		String triples = "";
		long id = System.nanoTime();
		triples+="<http://lsm.deri.ie/resource/"+id+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <"+dataType+">.\n"+ 
				"<http://lsm.deri.ie/resource/"+id+"> <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> <http://lsm.deri.ie/resource/"+observationId+">.\n"+
				"<http://lsm.deri.ie/resource/"+id+"> <http://lsm.deri.ie/ont/lsm.owl#value> \""+value+"\"^^<http://www.w3.org/2001/XMLSchema#double>.\n"+
				"<http://lsm.deri.ie/resource/"+id+"> <http://lsm.deri.ie/ont/lsm.owl#unit> \""+unit+"\".\n"+
				"<http://lsm.deri.ie/resource/"+id+"> <http://purl.oclc.org/NET/ssnx/ssn#observedProperty> <"+observedURL+">.\n"+
				"<http://lsm.deri.ie/resource/"+id+"> <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> \""+DateUtil.date2StandardString(time)+"\"^^<http://www.w3.org/2001/XMLSchema#dateTime>.\n";
		return triples;
	}
	
	public static String getTripleComplexTypeData(String dataType,String name,String value,String unit,String observationId,String observedURL,Date time){
		String triples = "";
		long id = System.nanoTime();
		triples+="<http://lsm.deri.ie/resource/"+id+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <"+dataType+">.\n"+ 
				"<http://lsm.deri.ie/resource/"+id+"> <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> <http://lsm.deri.ie/resource/"+observationId+">.\n"+
				"<http://lsm.deri.ie/resource/"+id+"> <http://lsm.deri.ie/ont/lsm.owl#value> \""+value+"\"^^<http://www.w3.org/2001/XMLSchema#string>.\n"+
				"<http://lsm.deri.ie/resource/"+id+"> <http://lsm.deri.ie/ont/lsm.owl#unit> \""+unit+"\"^^<http://www.w3.org/2001/XMLSchema#string>.\n"+
				"<http://lsm.deri.ie/resource/"+id+"> <http://www.w3.org/2000/01/rdf-schema#label> \""+name+"\".\n"+
				"<http://lsm.deri.ie/resource/"+id+"> <http://purl.oclc.org/NET/ssnx/ssn#observedProperty> <"+observedURL+">.\n"+
				"<http://lsm.deri.ie/resource/"+id+"> <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> \""+DateUtil.date2StandardString(time)+"\"^^<http://www.w3.org/2001/XMLSchema#dateTime>.\n";
		return triples;
	}
	
	
	public static String getTripleDataHasNoUnit(String dataType,String value,String observationId,String observedURL,Date time){
		String triples = "";
		long id = System.nanoTime();
		triples+="<http://lsm.deri.ie/resource/"+id+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <"+dataType+">.\n"+ 
				"<http://lsm.deri.ie/resource/"+id+"> <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> <http://lsm.deri.ie/resource/"+observationId+">.\n"+
				"<http://lsm.deri.ie/resource/"+id+"> <http://lsm.deri.ie/ont/lsm.owl#value> \""+value+"\".\n"+
				"<http://lsm.deri.ie/resource/"+id+"> <http://purl.oclc.org/NET/ssnx/ssn#observedProperty> <"+observedURL+">.\n"+
				"<http://lsm.deri.ie/resource/"+id+"> <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> \""+DateUtil.date2StandardString(time)+"\"^^<http://www.w3.org/2001/XMLSchema#dateTime>.\n";
		return triples;
	}
	
	public static String getObservationTripleData(String obsId,String sensorId,String featureOfInterest,Date time){
		String triples = "";		
		triples+="<http://lsm.deri.ie/resource/"+obsId+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Observation>.\n"+ 
				"<http://lsm.deri.ie/resource/"+obsId+"> <http://purl.oclc.org/NET/ssnx/ssn#observedBy> <"+sensorId+">.\n"+				
				"<http://lsm.deri.ie/resource/"+obsId+"> <http://purl.oclc.org/NET/ssnx/ssn#featureOfInterest> <"+featureOfInterest+">.\n"+
				"<http://lsm.deri.ie/resource/"+obsId+"> <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> \""+DateUtil.date2StandardString(time)+"\"^^<http://www.w3.org/2001/XMLSchema#dateTime>.\n";
		return triples;
	}
	
	public static String getSensorTripleMetadata(Sensor s){
		String triples = "";
		String xsltPath = XSLTMapFile.sensormeta2xslt;
//		xsltPath = MyServlet.realPath + xsltPath;
		xsltPath = "WebContent/WEB-INF" + xsltPath;
		TransformerFactory tFactory = TransformerFactory.newInstance();
        String xml = "";
        try {
        	Place place = s.getPlace();
        	String foi = VirtuosoConstantUtil.sensorObjectDataPrefix + 
					Double.toString(place.getLat()).replace(".", "").replace("-", "")+
					Double.toString(place.getLng()).replace(".", "").replace("-", "");
        	
            Transformer transformer = tFactory.newTransformer(new StreamSource(new File(xsltPath)));
            transformer.setParameter("sensorId", s.getId());
            transformer.setParameter("sourceType", s.getSourceType());
            transformer.setParameter("sensortype", s.getSensorType());
            transformer.setParameter("sourceURL", s.getSource());
            transformer.setParameter("placeId", place.getId());
            transformer.setParameter("geonameId", place.getGeonameid());
            transformer.setParameter("city", place.getCity());
            transformer.setParameter("province", place.getProvince());
            transformer.setParameter("country", place.getCountry());
            transformer.setParameter("continent", place.getContinent());
            transformer.setParameter("lat", place.getLat());
            transformer.setParameter("lng", place.getLng());
            transformer.setParameter("foi", foi);
            transformer.setParameter("name", s.getName());
            transformer.setParameter("userId",s.getUser().getId());
//            if(s.getSensorType().equals("weather")){
////            	xml = WebServiceURLRetriever.RetrieveFromURL(s.getSource());
////            	xml = xml.trim().replaceFirst("^([\\W]+)<","<");
//            	xml="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?><root></root>";
//            	xml = xml.trim().replaceFirst("^([\\W]+)<","<");
//            }
//            else{
            	xml="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?><root></root>";
            	xml = xml.trim().replaceFirst("^([\\W]+)<","<");
//            }
            InputStream inputStream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
            Writer outWriter = new StringWriter();  
            StreamResult result = new StreamResult( outWriter );            
            transformer.transform(new StreamSource(inputStream),result);
            triples = outWriter.toString().trim();                      
        } catch (Exception e) {
            e.printStackTrace();
        }
		return triples;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.setProperty("javax.xml.transform.TransformerFactory",
                "net.sf.saxon.TransformerFactoryImpl");
		SensorManager sensormanager = ServiceLocator.getSensorManager();
		Sensor sensor = sensormanager.getSpecifiedSensorWithSensorId("http://lsm.deri.ie/resource/8a82919d3264f4ac013264f4e14501c0");
		
//		sensor.setSourceType("yahoo");
//		sensor.setSensorType("bikehire");
		System.out.println(getSensorTripleMetadata(sensor));
	}

}
