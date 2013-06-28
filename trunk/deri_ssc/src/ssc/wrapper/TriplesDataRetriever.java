package ssc.wrapper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import ssc.beans.Place;
import ssc.beans.Sensor;
import ssc.beans.User;
import ssc.utils.DateUtil;
import ssc.utils.VirtuosoConstantUtil;
import ssc.utils.XSLTMapFile;
import ssc.websocket.echo.MyServlet;
import twitter4j.HashtagEntity;
import twitter4j.Status;
import twitter4j.Tweet;

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
	
	public static String getTwitterTripleData(Status object){
		String triples="";
		String twitterText = object.getText();
		String created_at = object.getCreatedAt().toLocaleString();
		String userURL = object.getUser().getName();
		String twitterMes = "<http://twitter.com/"+object.getUser().getScreenName()+"/statuses/"+object.getId()+">";
		String userId = "<http://lsm.deri.ie/resource/" + System.nanoTime()+">";
		triples+=twitterMes+ " <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lsm.deri.ie/ont/lsm.owl#TwitterMessage>.\n"+ 
				 twitterMes + " <http://lsm.deri.ie/ont/lsm.owl#has_creator> " + userId + ".\n"+
				 userId + " <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lsm.deri.ie/ont/lsm.owl#User>.\n"+
				 userId + " <http://www.w3.org/2000/01/rdf-schema#seeAlso> <http://twitter.com/"+object.getUser().getScreenName()+">.\n"+
				 twitterMes + " <http://lsm.deri.ie/ont/lsm.owl#value> \"" + twitterText +"\"^^<http://www.w3.org/2001/XMLSchema#string>.\n"+
				 twitterMes + " <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> \""+DateUtil.date2StandardString(object.getCreatedAt())+"\"^^<http://www.w3.org/2001/XMLSchema#dateTime>.\n";
		HashtagEntity[] hsEnArr = object.getHashtagEntities();
		for(HashtagEntity hs:hsEnArr){
			triples+= twitterMes + " <http://lsm.deri.ie/ont/lsm.owl#hasTopic> \"" + hs.getText() +"\"^^<http://www.w3.org/2001/XMLSchema#string>.\n";
		}
//		getTwitterContent(object);
		return triples;
		
	}
	
	public static String getTwitterTripleData(Tweet object){
		String triples="";
		String twitterText = object.getText();
		String created_at = object.getCreatedAt().toLocaleString();
		String userURL = object.getFromUser();
		String twitterMes = "<http://twitter.com/"+object.getFromUser()+"/statuses/"+object.getId()+">";
		String userId = "<http://lsm.deri.ie/resource/" + System.nanoTime()+">";
		triples+=twitterMes+ " <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lsm.deri.ie/ont/lsm.owl#TwitterMessage>.\n"+ 
				 twitterMes + " <http://lsm.deri.ie/ont/lsm.owl#has_creator> " + userId + ".\n"+
				 userId + " <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lsm.deri.ie/ont/lsm.owl#User>.\n"+
				 userId + " <http://www.w3.org/2000/01/rdf-schema#seeAlso> <http://twitter.com/"+object.getFromUser()+">.\n"+
				 twitterMes + " <http://lsm.deri.ie/ont/lsm.owl#value> \"" + twitterText +"\"^^<http://www.w3.org/2001/XMLSchema#string>.\n"+
				 twitterMes + " <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> \""+DateUtil.date2StandardString(object.getCreatedAt())+"\"^^<http://www.w3.org/2001/XMLSchema#dateTime>.\n";
		HashtagEntity[] hsEnArr = object.getHashtagEntities();
		if(hsEnArr!=null)
			for(HashtagEntity hs:hsEnArr){
				triples+= twitterMes + " <http://lsm.deri.ie/ont/lsm.owl#hasTopic> \"" + hs.getText() +"\"^^<http://www.w3.org/2001/XMLSchema#string>.\n";
			}
//		getTwitterContent(object);
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
          
            xml="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?><root></root>";          
            xml = xml.trim().replaceFirst("^([\\W]+)<","<");
            
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
	
	private static void iteraJSON(JSONObject obj,String keySearch,List valueList){
    	Iterator iter = obj.keys();
    	while(iter.hasNext()){
    		 String key = iter.next().toString();
    		 if(obj.get(key) instanceof JSONObject)
    			 iteraJSON((JSONObject)obj.get(key),keySearch,valueList);
    		 else{
    			if(key.equals(keySearch)){ 
//    					System.out.println(key+":"+obj.get(key));
    					if(obj.get(key) instanceof JSONArray){    	
    						Collection c = (Collection) obj.get(key);
    						valueList.addAll(c);
    					}else
    						valueList.add(obj.get(key));
    			}
    		 }
    	}		
	}
	
	private static HashMap<String, List> getTwitterContent(JSONObject obj){
		HashMap hs = new HashMap<>();
		List ls = new ArrayList();
		iteraJSON(obj, "hashtags",ls);
		System.out.println(ls);
		return null;
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.setProperty("javax.xml.transform.TransformerFactory",
                "net.sf.saxon.TransformerFactoryImpl");
		User user = new User();
		user.setId("dafasdf");
		Sensor sensor  = new Sensor();
        sensor.setId("http://lsm.deri.ie/resource/8a82919d3264f4ac013264f4e14501c0");
        sensor.setName("hello");
        sensor.setSensorType("weatheee");
        sensor.setAuthor("admin");
		sensor.setSensorType("bikehire");
		sensor.setSourceType("sdfg");
		sensor.setInfor("asfdfs");
		sensor.setSource("affag");
		sensor.setUser(user);
		sensor.setTimes(new Date());
        Place place = new Place();
        place.setLat(32325);
        place.setLng(324);
        sensor.setPlace(place);
        System.out.println(TriplesDataRetriever.getSensorTripleMetadata(sensor));
	}

}
