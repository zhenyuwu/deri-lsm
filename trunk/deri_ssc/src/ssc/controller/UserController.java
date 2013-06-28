package ssc.controller;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.deri.cqels.data.Mapping;
import org.deri.cqels.engine.ContinuousListener;
import org.deri.cqels.engine.ContinuousSelect;
import org.deri.cqels.engine.ExecContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

//import com.mchange.v2.c3p0.DataSources;
//import com.mchange.v2.c3p0.PooledDataSource;

import ssc.cqels.TriplesStream;
import ssc.graph.ConnectionPool;
import ssc.graph.DatabaseUtilities;
import ssc.sensor.places.yahoo.YahooWhereURLXMLParser;

import com.hp.hpl.jena.sparql.core.Var;
import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

@Controller
public class UserController {

	private static ConnectionPool graphPool;
	private static BoneCP SQLPooled; 

    
	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public ModelAndView index() {	
//		init();
		return new ModelAndView("index");
	}
	static{
		init();
	}
	
	public static BoneCP getConnectionPool(){
		return SQLPooled;
	}
	
	public static ConnectionPool getGraphPool(){
		if(graphPool==null)
			init();
		return graphPool;
	}
	
	public static void init(){
		  String driver="virtuoso.jdbc4.Driver";
		  String host = "jdbc:virtuoso://140.203.155.176:1111/DERI.DBA/log_enable=2";			
          String username = "dba";
	      String password = "dba";
	      try {
	    	  if (graphPool!= null) return;
	          graphPool =
	          new ConnectionPool(host, username, password,
	                             initialConnections(),
	                             maxConnections(),
	                             true);
	          
	          Class.forName(driver);		
	          BoneCPConfig config = new BoneCPConfig();
	          config.setJdbcUrl("jdbc:virtuoso://140.203.155.176:1111/DERI.DBA/log_enable=2"); // jdbc url specific to your database, eg jdbc:mysql://127.0.0.1/yourdb
				config.setUsername("dba"); 
				config.setPassword("dba");
				config.setMinConnectionsPerPartition(5);
				config.setMaxConnectionsPerPartition(10);
				config.setPartitionCount(1);
				config.setTransactionRecoveryEnabled(true);
				config.setAcquireRetryAttempts(5);//default 5
				SQLPooled = new BoneCP(config); // setup the connection pool	
//	  		  DataSource unpooled = DataSources.unpooledDataSource(host,username, password);
//	  		  SQLPooled = DataSources.pooledDataSource( unpooled ,"intergalactoApp");

	      } catch(SQLException sqle) {
	        System.err.println("Error making pool: " + sqle);	        
	        graphPool = null;
	      } catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 protected static void destroy() {
	      graphPool.closeAllConnections();
	 }
	
	    /** Override this in subclass to change number of initial
	     *  connections.
	     */
	    
	 protected static int initialConnections() {
	      return(2);
	 }
	
	    /** Override this in subclass to change maximum number of 
	     *  connections.
	     */
	
	 protected static int maxConnections() {
	      return(2);
	 }
	    
	 public static void attemptClose(ResultSet o)
	    {
//		try
//		    { if (o != null) o.close();}
//		catch (Exception e)
//		    { e.printStackTrace();}
	    }

	 public  static void attemptClose(Statement o)
	    {
//		try
//		    { if (o != null) o.close();}
//		catch (Exception e)
//		    { e.printStackTrace();}
	    }

	 public  static void attemptClose(Connection o)
	    {
		try
		    { if (o != null) o.close();}
		catch (Exception e)
		    { e.printStackTrace();}
	    }
	    
	    
	@RequestMapping(value = "/discover", method = RequestMethod.GET)
	public @ResponseBody
	String getSensorsDiscover(@RequestParam("info") String object) throws SQLException {
		try{
	//		System.out.println(object);
			JSONObject json = (JSONObject) JSONSerializer.toJSON( object );
			String address = json.getString("location");
			String strLat = json.getString("lat");
			String strLong = json.getString("long");
			String radius = json.getString("radius");
			String sensorType = json.getString("sensorType").toLowerCase().replaceAll(" ","");
			System.out.println(sensorType);
			OutputStream result = DatabaseUtilities.getSensorAroundLocation(sensorType,strLat,strLong,radius,true);	
	//		System.out.println(result);
			return result.toString();
		}catch(Exception e){
			e.printStackTrace();
		}
		System.out.println("error");
		return null;
	}
	
	@RequestMapping(value = "/get_location", method = RequestMethod.GET, headers = "Accept=*/*")
	public @ResponseBody
	String getData(@RequestParam("info") String object) {
//	String getData(String query) {
		System.out.println(object);
		JSONObject json = (JSONObject) JSONSerializer.toJSON( object );
		String address = json.getString("info");
		String locationPra = 
				(address.trim().equals("")?  ""  : (address + ", "))
				+ json.getString("lat")+","+json.getString("long");
		ssc.beans.Place place = YahooWhereURLXMLParser.placeStr2PlaceObj(locationPra);
		
		JSONObject jObj=new JSONObject();
		if(place!=null){
			jObj.put("address", place.toString());
			jObj.put("lat", place.getLat());
			jObj.put("long", place.getLng());
		}else{
			jObj.put("address", "not found");
			jObj.put("lat", "not found");
			jObj.put("long", "not found");
		}
		return jObj.toString();
	}
	
	@RequestMapping(value = "/URLDiscover", method = RequestMethod.GET, headers = "Accept=*/*")
	public @ResponseBody
	String getURLDiscover(@RequestParam("info") String object) throws SQLException {
		JSONObject json = (JSONObject) JSONSerializer.toJSON( object );
		String keyword = json.getString("keyword");		
		try {
			keyword = URLEncoder.encode(keyword, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String result = DatabaseUtilities.getSindiceSearch(keyword);
		JSONObject jsonResult = (JSONObject) JSONSerializer.toJSON( result );
		String[ ] aryStrings = {"Title","Formats","Updated"};
		jsonResult.put("vars", aryStrings);
		System.out.println(jsonResult);
		return jsonResult.toString();
	}
	
	@RequestMapping(value = "/get_query", method = RequestMethod.GET, headers = "Accept=*/*")
	public @ResponseBody
	String getQueryResult(@RequestParam("para") String object) {
		System.out.println(object);
		JSONObject json = (JSONObject) JSONSerializer.toJSON( object );
		String query = json.getString("sparql");
    	String sensorURL = json.getString("url"); 
    	String sensorType = json.getString("type").toLowerCase().replaceAll(" ","");        	
    	String result = null;        	
		try {				
			if(!query.isEmpty()){
				OutputStream out = DatabaseUtilities.getQueryResults(query, true);
				result = out.toString();
			}
			else{}
//				result = reformLastestSensorData(sensorURL, sensorType);			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();				
			JSONObject jsonResult = new JSONObject();				
			jsonResult.put("error", "true");	
			result = jsonResult.toString();
		}        	
		if(result==null){
			JSONObject jsonResult = new JSONObject();				
			jsonResult.put("error", "true");	
			result = jsonResult.toString();
		}
    	return result;		
	}
	

     @RequestMapping(value = "/cqelsfilter", method = RequestMethod.GET, headers = "Accept=*/*")
 	public @ResponseBody
 	String getCqelsResult(@RequestParam("para") String object) {
 		System.out.println(object);
 		JSONObject json = (JSONObject) JSONSerializer.toJSON( object );
 		String query = json.getString("sparql");
     	String result = null;        	
 		try {				
 			result = Cqels(query, json.getString("ntriples"));			
 		} catch (Exception e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();				
 			JSONObject jsonResult = new JSONObject();				
 			jsonResult.put("error", "true");	
 			result = jsonResult.toString();
 		}        	
 		if(result==null){
 			JSONObject jsonResult = new JSONObject();				
 			jsonResult.put("error", "true");	
 			result = jsonResult.toString();
 		}
     	return result;		
 	}
     
    static String cqelsResult="";
    private String Cqels(String query,String data){
    	String HOME ="/root/Java/Cqels/cqels_data/stream";
    	String triple="";
    	final ExecContext context=new ExecContext(HOME, false);
    	TriplesStream stream = new TriplesStream(context, "http://deri.org/streams/rfid");
    	
    	if(!query.equals("")){
		     ContinuousSelect selQuery=context.registerSelect(query);
			 selQuery.register(new ContinuousListener()
			 {
			       public void update(Mapping mapping){
			          String result="";
			          for(Iterator<Var> vars=mapping.vars();vars.hasNext();)
			              result+=" "+ context.engine().decode(mapping.get(vars.next()));		
			          cqelsResult+=result;
			       } 
			 });
    	}
    	stream.queue.add(data);
    	Thread t = new Thread((Runnable) stream);
		t.start();
		try {
			t.join();
			triple = cqelsResult;
			cqelsResult = "";
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return triple;
    }
}
