package ssc.websocket.echo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.catalina.websocket.MessageInbound;
import org.apache.catalina.websocket.StreamInbound;
import org.apache.catalina.websocket.WebSocketServlet;
import org.deri.cqels.data.Mapping;
import org.deri.cqels.engine.ContinuousListener;
import org.deri.cqels.engine.ContinuousSelect;
import org.deri.cqels.engine.ExecContext;

import ssc.beans.Block;
import ssc.beans.Observation;
import ssc.beans.Sensor;
import ssc.beans.SensorManager;
import ssc.cqels.TriplesStream;
import ssc.graph.DatabaseUtilities;
import ssc.sensor.cosm.CosmSensorParser;
import ssc.sensor.flight.FlightAroundXMLParser;
import ssc.sensor.london.railwaytation.PredictionDetailedXMLParser;
import ssc.twitter.TwitterSession;
import ssc.utils.DateUtil;
import ssc.utils.JSONUtil;
import ssc.wrapper.WebServiceURLRetriever;
import ssc.wrapper.YahooWeatherWrapper;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

public class SSCWidgetSocket extends WebSocketServlet {

    private static final long serialVersionUID = 12L;
    private volatile int byteBufSize;
    private volatile int charBufSize;
  
    public static ExecContext context;
    public static TriplesStream stream;
    public static ScheduledExecutorService scheduler = null;
   
	@Override
	protected StreamInbound createWebSocketInbound(String arg0,
			HttpServletRequest arg1) {
		// TODO Auto-generated method stub
		return new EchoMessageInbound(byteBufSize,charBufSize);
	}
 
    private static final class EchoMessageInbound extends MessageInbound {

        public EchoMessageInbound(int byteBufferMaxSize, int charBufferMaxSize) {
            super();
            setByteBufferMaxSize(byteBufferMaxSize);
            setCharBufferMaxSize(charBufferMaxSize);
        }

        @Override
        protected void onBinaryMessage(ByteBuffer message) throws IOException {
        	System.out.println(message);
            getWsOutbound().writeBinaryMessage(message);
            
        }
        
             @Override
        protected void onClose(int status){        	
        	if(scheduler!= null){
        		System.out.println("da close");
        		scheduler.shutdownNow();
        	}
        }
        
        @Override
        protected void onTextMessage(CharBuffer message) throws IOException {        	
        	String temp = message.toString();
        	JSONObject json = (JSONObject) JSONSerializer.toJSON( temp );
        	
        	String type = json.getString("type").toLowerCase().replaceAll(" ","");
        	String location = json.getString("location"); 
        	String lat = json.getString("lat");
        	String lng = json.getString("lng");
        	String radius = json.getString("radius");
        	String keywords = json.getString("keywords");
        	        	
        	String result = null;        	
			try {				
				if(type.equals("overview"))
					result = getOverViewData(location, lat, lng, radius, keywords);			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();				
				JSONObject jsonResult = new JSONObject();				
				jsonResult.put("error", "true");	
				result = jsonResult.toString();
			}        	
			if(result==null||result.equals("")){
				JSONObject jsonResult = new JSONObject();				
				jsonResult.put("error", "true");	
				result = jsonResult.toString();
			}
        	Charset charset = Charset.forName("ISO-8859-1");
			CharsetDecoder decoder = charset.newDecoder();
			CharsetEncoder encoder = charset.newEncoder();
			CharBuffer uCharBuffer = CharBuffer.wrap(result);			
		    ByteBuffer bbuf = encoder.encode(uCharBuffer);		   
		    CharBuffer cbuf = decoder.decode(bbuf);		    
            getWsOutbound().writeTextMessage(cbuf);
        }       
        

        private String getOverViewData(String location, String lat, String lng, String radius, String keywords){
        	JSONObject json = new JSONObject();
        	SensorManager sensorManager = new SensorManager();
        	
        	ArrayList<Sensor> sensors = sensorManager.getAllSpecifiedSensorAroundPlace("traffic", lng, lat, radius);
        	ArrayList<String> arr = new ArrayList<>();
        	for(Sensor sensor:sensors){
        		arr.add(sensor.getSource());
        	}
        	json.put("traffic", arr);
        	
        	sensors = sensorManager.getAllSpecifiedSensorAroundPlace("airport", lng, lat, "20");
        	arr = new ArrayList<>();
        	for(Sensor sensor:sensors){
        		arr.add(sensor.getSource());
        	}
        	json.put("airport", arr);
        	
        	sensors = sensorManager.getAllSpecifiedSensorAroundPlace("railwaystation", lng, lat, radius);
        	arr = new ArrayList<>();
        	for(Sensor sensor:sensors){
        		arr.add(sensor.getSource());
        	}
        	json.put("railwaystation", arr);
        	
        	String sindice = DatabaseUtilities.getSindiceSearch(keywords);
    		JSONObject jsonSindice = (JSONObject) JSONSerializer.toJSON( sindice );
    		String[ ] aryStrings = {"Title","Formats","Updated"};
    		jsonSindice.put("vars", aryStrings);
    		json.put("sindice", jsonSindice);
        	
			return json.toString();
        }
                       
        private JSONObject reformLastestCOSMData(String sensorURL,JSONArray filterArr) {
    		// TODO Auto-generated method stub    		
    		String source;
    		JSONObject result = new JSONObject();
         	try{
    	    	SensorManager sensorManager = new SensorManager();
    	    	Sensor sensor = sensorManager.getSpecifiedSensorWithSensorId(sensorURL);
    	    	
    	    	if (sensor==null){
    	    		source = sensorURL;
    	    		sensor = sensorManager.getSpecifiedSensorWithSource(sensorURL);
    	    	}else source = sensor.getSource();
    	    	
    			String xml = WebServiceURLRetriever.RetrieveFromURLWithAuthentication(source, "nmqhoan", "nmqhoan");
    			CosmSensorParser.parseDataElementsFromCosmXML(xml);
        	   
        	    if (sensor==null) 
        		   sensor = sensorManager.getSpecifiedSensorWithSource(source);
        	       	   	
        	    Observation obs = null;
	       		List<String> observations = new ArrayList();
	       		
	       		if(filterArr.size()==0){
	       			obs = sensorManager.getNewestObservationForOneSensor(sensorURL);
	       			if(obs!=null)
	       				observations.add(obs.getId());
	       		}else{
	       			Date fromTime = DateUtil.string2Date(filterArr.getString(2),"MM/dd/yyyy HH:mm");
	       			String operator = filterArr.getString(1);
	       			observations = sensorManager.getObservationsWithTimeCriteria(sensorURL, operator, fromTime, null);
	       		}	       			
        	       	    
        	    JSONObject metaJson = new JSONObject();
        	    metaJson.put("name", sensor.getName());
        	    metaJson.put("source", sensor.getSource());
        	    metaJson.put("sourceType", sensor.getSourceType());
        	    metaJson.put("city", sensor.getPlace().getCity());
        	    metaJson.put("country", sensor.getPlace().getCountry());
        	    
        	    Calendar today = Calendar.getInstance();
        		today.add(Calendar.DATE, -15);
        		Date fromTime = new Date(today.getTimeInMillis()); 
        		
        	    ArrayList historicalJson = sensorManager.getSensorHistoricalData(sensor.getId(),fromTime);
        	    List signs = new ArrayList<String>();
        	    String sign; 
        	    
        	    
        	    Map m1 = new LinkedHashMap();
        	    ArrayList readArr = new ArrayList<>();
        	    for(String obsId:observations){
        	    	List<ArrayList> readings = sensorManager.getReadingDataOfObservation(obsId);
        	    	for(ArrayList reading : readings){
            			sign = reading.get(3).toString();
            			signs.add(sign);						
            			String unit=null;
            			String content=null;
            			try{
            				content = reading.get(1).toString();
            				unit = reading.get(2).toString();				
            			}catch(Exception e){
            			}
            			if(unit == null){
            				unit = "no";
            			}
            			Map m2 = new LinkedHashMap();
            			m2.put("value", content);
            			m2.put("unit", unit);
            			m1.put(sign, m2);	    			
            	    }
            	    readArr.add(m1);
        	    }
        	    	    	    
        	    
        	    JSONObject readingJson = new JSONObject();
        	    readingJson.put("vars", signs);
        	    readingJson.put("readings", readArr);
        
        	    OutputStream out = null;
        	    out = DatabaseUtilities.getNewestSensorData(sensor.getId());
        	    JSONObject json = (JSONObject) JSONSerializer.toJSON( out.toString() );
        	    json.put("updated", DateUtil.date2StandardString(new Date()));
        	    json.put("ntriples", JSONUtil.JSONToNTriple(json.getJSONObject("results")));
        	    json.put("error", "false");
        	    json.put("meta", metaJson);
        	    json.put("data",readingJson);
        	    json.put("history", historicalJson);
        	    System.out.println(json);
        	    result = json;			  
    	  	}catch(Exception e){
    	  		e.printStackTrace();				
    					JSONObject jsonResult = new JSONObject();				
    					jsonResult.put("error", "true");	
    					result = jsonResult;
    	  	}
    	  	return result;
      } 

        private JSONObject reformLastestTrafficData(String sensorURL,JSONArray filterArr){
        	JSONObject result = new JSONObject();
        	try{
	           SensorManager sensorManager = new SensorManager();
	    	   Sensor sensor = sensorManager.getSpecifiedSensorWithSensorId(sensorURL);
	    	   if (sensor==null) return result;
	       		Observation obs = sensorManager.getNewestObservationForOneSensor(sensorURL);
	       		
	    	    JSONObject metaJson = new JSONObject();
	    	    metaJson.put("name", sensor.getName());
	    	    metaJson.put("source", sensor.getSource());
	    	    metaJson.put("sourceType", sensor.getSourceType());
	    	    metaJson.put("city", sensor.getPlace().getCity());
	    	    metaJson.put("country", sensor.getPlace().getCountry());
	    	   
    
	    	    OutputStream out = null;
	    	    out = DatabaseUtilities.getNewestSensorData(sensorURL);
	    	    JSONObject json = (JSONObject) JSONSerializer.toJSON( out.toString() );
	    	    json.put("updated", DateUtil.date2StandardString(new Date()));
	    	    json.put("ntriples", JSONUtil.JSONToNTriple(json.getJSONObject("results")));
	    	    json.put("error", "false");
	    	    json.put("meta", metaJson);

	    	    System.out.println(json);
	    	    result = json;			  
        	}catch(Exception e){
        		e.printStackTrace();				
				JSONObject jsonResult = new JSONObject();				
				jsonResult.put("error", "true");
				jsonResult.put("ntriples", "");
				result = jsonResult;
        	}
        	return result;
        }
        
		private JSONObject reformLastestWeatherData(String sensorURL,JSONArray filterArr){
			JSONObject result = new JSONObject();
        	try{
	           SensorManager sensorManager = new SensorManager();
	    	   Sensor sensor = sensorManager.getSpecifiedSensorWithSensorId(sensorURL);
	    	   if (sensor==null) return result;    	   	
	    	   YahooWeatherWrapper yWrapper = new YahooWeatherWrapper();
	       		yWrapper.feed(sensorURL);    
	       		Observation obs = null;
	       		List<String> observations = new ArrayList();
	       		if(filterArr.size()==0){
	       			obs = sensorManager.getNewestObservationForOneSensor(sensorURL);
	       			if(obs!=null)
	       				observations.add(obs.getId());
	       		}else{
	       			Date fromTime = DateUtil.string2Date(filterArr.getString(2),"MM/dd/yyyy HH:mm");
	       			String operator = filterArr.getString(1);
	       			observations = sensorManager.getObservationsWithTimeCriteria(sensorURL, operator, fromTime, null);
	       		}
	    	    JSONObject metaJson = new JSONObject();
	    	    metaJson.put("name", sensor.getName());
	    	    metaJson.put("source", sensor.getSource());
	    	    metaJson.put("sourceType", sensor.getSourceType());
	    	    metaJson.put("city", sensor.getPlace().getCity());
	    	    metaJson.put("country", sensor.getPlace().getCountry());
	    	    
	    	    Calendar today = Calendar.getInstance();
	    		today.add(Calendar.DATE, -15);
	    		Date fromTime = new Date(today.getTimeInMillis()); 
	    		
	    	    ArrayList historicalJson = sensorManager.getSensorHistoricalData(sensorURL,fromTime);
	    	    List signs = new ArrayList<String>();
	    	    String sign; 
	    	    	    	    
	    	    Map m1 = new LinkedHashMap();
	    	    ArrayList readArr = new ArrayList<>();
	    	    for(String obsId:observations){
		    	    List<ArrayList> readings = sensorManager.getReadingDataOfObservation(obsId);
		    	    for(ArrayList reading : readings){
		    			sign = reading.get(0).toString().substring(reading.get(0).toString().lastIndexOf("#")+1);
		    			signs.add(sign);						
		    			String unit=null;
		    			String content=null;
		    			try{
		    				content = reading.get(1).toString();
		    				unit = reading.get(2).toString();				
		    			}catch(Exception e){
		    			}
		    			if(unit == null){
		    				unit = "no";
		    			}
		    			Map m2 = new LinkedHashMap();
		    			m2.put("value", content);
		    			m2.put("unit", unit);
		    			m1.put(sign, m2);	    			
		    	    }
		    	    readArr.add(m1);	    	    
	    	    }
	    	    
	    	    JSONObject readingJson = new JSONObject();
	    	    readingJson.put("vars", signs);
	    	    readingJson.put("readings", readArr);
	    
	    	    OutputStream out = null;
	    	    out = DatabaseUtilities.getNewestSensorData(sensorURL);
	    	    JSONObject json = (JSONObject) JSONSerializer.toJSON( out.toString() );
	    	    json.put("updated", DateUtil.date2StandardString(new Date()));
	    	    json.put("ntriples", JSONUtil.JSONToNTriple(json.getJSONObject("results")));
	    	    json.put("error", "false");
	    	    json.put("meta", metaJson);
	    	    json.put("data",readingJson);
	    	    json.put("history", historicalJson);
	    	    System.out.println(json);
	    	    result = json;			  
        	}catch(Exception e){
        		e.printStackTrace();				
				JSONObject jsonResult = new JSONObject();				
				jsonResult.put("error", "true");	
				result = jsonResult;
        	}
        	return result;
        }
        
		private JSONObject reformLastestBikehireData(String sensorURL,JSONArray filterArr){
			JSONObject result = new JSONObject();
        	try{
	           SensorManager sensorManager = new SensorManager();
	    	   Sensor sensor = sensorManager.getSpecifiedSensorWithSensorId(sensorURL);
	    	   if (sensor==null) return result;
	       		Observation obs = null;
	       		List<String> observations = new ArrayList();
	       		if(filterArr.size()==0){
	       			obs = sensorManager.getNewestObservationForOneSensor(sensorURL);
	       			if(obs!=null)
	       				observations.add(obs.getId());
	       		}else{
	       			Date fromTime = DateUtil.string2Date(filterArr.getString(2),"MM/dd/yyyy HH:mm");
	       			String operator = filterArr.getString(1);
	       			observations = sensorManager.getObservationsWithTimeCriteria(sensorURL, operator, fromTime, null);
	       		}
	    	    JSONObject metaJson = new JSONObject();
	    	    metaJson.put("name", sensor.getName());
	    	    metaJson.put("source", sensor.getSource());
	    	    metaJson.put("sourceType", sensor.getSourceType());
	    	    metaJson.put("city", sensor.getPlace().getCity());
	    	    metaJson.put("country", sensor.getPlace().getCountry());
	    	    
	    	    Calendar today = Calendar.getInstance();
	    		today.add(Calendar.DATE, -15);
	    		Date fromTime = new Date(today.getTimeInMillis()); 
	    		
	    	    ArrayList historicalJson = sensorManager.getSensorHistoricalData(sensorURL,fromTime);
	    	    List signs = new ArrayList<String>();
	    	    String sign; 
	    	    	    	    
	    	    Map m1 = new LinkedHashMap();
	    	    ArrayList readArr = new ArrayList<>();
	    	    for(String obsId:observations){
		    	    List<ArrayList> readings = sensorManager.getReadingDataOfObservation(obsId);
		    	    for(ArrayList reading : readings){
		    			sign = reading.get(0).toString().substring(reading.get(0).toString().lastIndexOf("#")+1);
		    			signs.add(sign);						
		    			String unit=null;
		    			String content=null;
		    			try{
		    				content = reading.get(1).toString();
		    				unit = reading.get(2).toString();				
		    			}catch(Exception e){
		    			}
		    			if(unit == null){
		    				unit = "no";
		    			}
		    			Map m2 = new LinkedHashMap();
		    			m2.put("value", content);
		    			m2.put("unit", unit);
		    			m1.put(sign, m2);	    			
		    	    }
		    	    readArr.add(m1);	    	    
	    	    }
	    	    
	    	    JSONObject readingJson = new JSONObject();
	    	    readingJson.put("vars", signs);
	    	    readingJson.put("readings", readArr);
	    
	    	    OutputStream out = null;
	    	    out = DatabaseUtilities.getNewestSensorData(sensorURL);
	    	    JSONObject json = (JSONObject) JSONSerializer.toJSON( out.toString() );
	    	    json.put("updated", DateUtil.date2StandardString(new Date()));
	    	    json.put("ntriples", JSONUtil.JSONToNTriple(json.getJSONObject("results")));
	    	    json.put("error", "false");
	    	    json.put("meta", metaJson);
	    	    json.put("data",readingJson);
	    	    json.put("history", historicalJson);
	    	    System.out.println(json);
	    	    result = json;			  
        	}catch(Exception e){
        		e.printStackTrace();				
				JSONObject jsonResult = new JSONObject();				
				jsonResult.put("error", "true");	
				result = jsonResult;
        	}
        	return result;
        }
		
        private JSONObject reformLastestRaiwayStationData(String sensorURL){
        	JSONObject result = new JSONObject();
        	try{
	           SensorManager sensorManager = new SensorManager();
	    	   Sensor sensor = sensorManager.getSpecifiedSensorWithSensorId(sensorURL);
	    	   if (sensor==null) return result;
	    	   PredictionDetailedXMLParser preParser = new PredictionDetailedXMLParser();
	       		preParser.getPredictionDetailedForOneStation(sensor); 
	    	   	
	    	    Observation obs = sensorManager.getNewestObservationForOneSensor(sensorURL);	    	    
	    	    JSONObject metaJson = new JSONObject();
	    	    metaJson.put("name", sensor.getName());
	    	    metaJson.put("source", sensor.getSource());
	    	    metaJson.put("sourceType", sensor.getSourceType());
	    	    metaJson.put("city", sensor.getPlace().getCity());
	    	    metaJson.put("country", sensor.getPlace().getCountry());
	    	    
	    	    ArrayList<String> signs = new ArrayList<String>();
	    	    String sign; 
	    	    
	    	    List<String> observations = sensorManager.getObservationsWithTimeCriteria(sensor.getId(), "=", obs.getTimes(), null);
	    	    ArrayList readArr = new ArrayList<>();
	    	    boolean isFirst = true;
	    	    for(String obsStr: observations){
		    	    List<ArrayList> readings = sensorManager.getReadingDataOfObservation(obsStr);
		    	    Map m1 = new LinkedHashMap();	    	    
		    	    for(int i=0;i<readings.size();i++){
		    	    	ArrayList reading = readings.get(i);
		    			sign = reading.get(0).toString().substring(reading.get(0).toString().lastIndexOf("#")+1);
		    			if(isFirst)	
		    				signs.add(sign);						
		    			String unit=null;
		    			String content=null;
		    			try{
		    				content = reading.get(1).toString();
		    				unit = reading.get(2).toString();				
		    			}catch(Exception e){
		    			}
		    			if(unit == null){
		    				unit = "no";
		    			}
		    			Map m2 = new LinkedHashMap();
		    			m2.put("value", content+"");
		    			m2.put("unit", unit);
		    			m1.put(sign, m2);	    			
		    	    }
		    	    isFirst = false;
		    	    readArr.add(m1);	
	    	    }
	    	    
	    	    JSONObject readingJson = new JSONObject();
	    	    readingJson.put("vars", signs);
	    	    readingJson.put("readings", readArr);
	    
	    	    OutputStream out = null;
	    	    out = DatabaseUtilities.getNewestSensorData(sensorURL);
	    	    JSONObject json = (JSONObject) JSONSerializer.toJSON( out.toString() );
	    	    json.put("updated", DateUtil.date2StandardString(new Date()));
	    	    json.put("ntriples", JSONUtil.JSONToNTriple(json.getJSONObject("results")));
	    	    json.put("error", "false");
	    	    json.put("meta", metaJson);
	    	    json.put("data",readingJson);    	    	    
	    	    System.out.println(json);
	    	    result = json;    
        	}catch(Exception e){
        		e.printStackTrace();				
				JSONObject jsonResult = new JSONObject();				
				jsonResult.put("error", "true");	
				result = jsonResult;
        	}
			return result;
        }
        
        private JSONObject reformLastestAirportData(String airportURL){
        	SensorManager sensorManager = new SensorManager();
     	   Sensor sensor = sensorManager.getSpecifiedSensorWithSensorId(airportURL);
     	   JSONObject j = new JSONObject();
     	   if (sensor==null) return j;
     	       	    
     	   JSONObject metaJson = new JSONObject();
     	   metaJson.put("name", sensor.getName());
     	   metaJson.put("source", sensor.getSource());
     	   metaJson.put("sourceType", sensor.getSourceType());
     	   metaJson.put("city", sensor.getPlace().getCity());
     	   metaJson.put("country", sensor.getPlace().getCountry());
     	   
     	   String airportCode = sensor.getName().substring(sensor.getName().lastIndexOf("(")+1,sensor.getName().lastIndexOf(")"));
     	   ArrayList<ArrayList> depatures = sensorManager.getAllFlightsWithSpecifiedDeparture(airportCode);
     	   ArrayList<ArrayList> arrivals = sensorManager.getAllFlightsWithSpecifiedDestination(airportCode);    	   
     	   ArrayList<List> radarFlights = FlightAroundXMLParser.getFlightAroundInformation(sensor.getPlace().getLat(),sensor.getPlace().getLng());  
     	   
     	   JSONObject depaturesJson = new JSONObject();
     	   String[ ] aryStrings = {"CallSign","FlightNumber","Route"};
     	   depaturesJson.put("vars",aryStrings);
     	   depaturesJson.put("data", depatures);
     	  
     	   JSONObject arrivalsJson = new JSONObject();    	  
     	   arrivalsJson.put("vars",aryStrings);
     	   arrivalsJson.put("data", arrivals);
     	 
     	   JSONObject radarFlightsJson = new JSONObject();
     	   String[ ] flightStrings = {"Registration","Model","CallSign","Route","Latitude","Longitude","Distance(km)"};
     	   radarFlightsJson.put("vars",flightStrings);
     	   radarFlightsJson.put("data", radarFlights);
    	       	  
     	   OutputStream result = null;
     	   result = DatabaseUtilities.getSensorMetadata(airportURL);
 	       JSONObject json = (JSONObject) JSONSerializer.toJSON( result.toString() );
 	       json.put("updated", DateUtil.date2StandardString(new Date()));
 	       json.put("ntriples", JSONUtil.JSONToNTriple(json.getJSONObject("results")));
 	       json.put("error", "false");
     	   json.put("meta", metaJson);
     	   json.put("departures",depaturesJson);    	    
     	   json.put("arrivals",arrivalsJson);
     	   json.put("radars",radarFlightsJson);
     	   System.out.println(json);
     	   return json;
        }      
        
    }
}
