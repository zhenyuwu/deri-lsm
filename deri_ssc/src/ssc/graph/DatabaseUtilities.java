package ssc.graph;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.sql.SQLException;


import ssc.controller.UserController;
import ssc.utils.VirtuosoConstantUtil;
import ssc.wrapper.WebServiceURLRetriever;
import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;



public class DatabaseUtilities {
  static{
	  if(UserController.getGraphPool()==null)
		  UserController.init();
  }
  public static String getQueryResults(String driver,
                                          String url,
                                          String username,
                                          String password,
                                          String query,
                                          boolean close) {
	  String result="";
    try {
    	VirtGraph graph = new VirtGraph(url, username, password);
    	VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
    	ResultSet results = vqe.execSelect();
//        result = ResultSetFormatter.asXMLString(results);
    	ResultSetFormatter.outputAsJSON(System.out,results);
        if (close) {
          graph.close();
        }
    	
    } catch(Exception cnfe) {
      System.err.println("Error connecting: " + cnfe);
      return(null);
    } 
    return result;
  }

  /** Retrieves results as in previous method but uses
   *  an existing connection instead of opening a new one.
   */
  
  public static OutputStream getQueryResults(String query,boolean close) {
    try {
      VirtGraph graph = UserController.getGraphPool().getConnection();
      VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
      ResultSet results = vqe.execSelect();
      ByteArrayOutputStream out = new ByteArrayOutputStream();; 
      ResultSetFormatter.outputAsJSON(out,results);
//      ResultSetFormatter.outputAsRDF(out,"N3",results);
      if (close) {
    	  UserController.getGraphPool().free(graph);
      }
      return out;
    } catch(Exception sqle) {
      System.err.println("Error connecting: " + sqle);
      return(null);
    } 
  }
  
  public static OutputStream getNewestSensorData(String sensorURL){
	  VirtGraph graph = null;
		try {
			graph = UserController.getGraphPool().getConnection();
			String query ="select ?s ?p ?o "+
				" from <"+ VirtuosoConstantUtil.sensormasherDataGraphURI +"> " +
				"where{"+
				  "{"+
				   "select ?x where"+
				     "{"+
				       "?x <http://purl.oclc.org/NET/ssnx/ssn#observedBy> <"+ sensorURL +">."+
				       "?x <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+
				      "}order by desc(?time) limit 1"+
				  "}"+ 
				  "?s <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?x."+
				  "?s ?p ?o."+
				"}";
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
			ResultSet results = vqe.execSelect();
			ByteArrayOutputStream out = new ByteArrayOutputStream(); 
			ResultSetFormatter.outputAsJSON(out,results);
			//String xml = ResultSetFormatter.asXMLString(results);
			UserController.getGraphPool().free(graph);			
			return out;
		} catch(Exception sqle) {
			System.err.println("Error connecting: " + sqle);
			 UserController.getGraphPool().free(graph);
			return(null);
		} 
	}

	public static OutputStream getSensorAroundLocation(String sensorType, String lat, String lng, String radius,
			boolean close) throws SQLException {
		double distance = Double.parseDouble(radius);
		VirtGraph graph = UserController.getGraphPool().getConnection();
		String query = "select distinct(?sensor) ?lat ?lng ?name ?city ?country <bif:st_distance>(?geo,<bif:st_point>("+
			lng+","+lat+")) as ?distance "+		
				" from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> " + 			
				"where {"+			
				"?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>;" +
				"<http://www.w3.org/2000/01/rdf-schema#label> ?name."+
				"?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
				"?typeId <http://www.w3.org/2000/01/rdf-schema#label> \""+sensorType+"\"."+							
				"?sensor <"+ VirtuosoConstantUtil.sensorHasPlacePrefix+"> ?place. "+
				"?place <http://lsm.deri.ie/ont/lsm.owl#is_in_city> ?cityId."+
				"?cityId <http://www.w3.org/2000/01/rdf-schema#label> ?city."+
				"?place <http://linkedgeodata.org/property/is_in_country> ?counId."+
				"?counId <http://www.w3.org/2000/01/rdf-schema#label> ?country."+
				"?place geo:geometry ?geo."+			
				"?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat."+
				"?place <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?lng."+
				"filter (<bif:st_intersects>(?geo,<bif:st_point>("+
						lng+","+lat+"),"+distance+"))." +
				"} order by ?distance";
		try{
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
		    ResultSet results = vqe.execSelect();
		    ByteArrayOutputStream out = new ByteArrayOutputStream();; 
		    ResultSetFormatter.outputAsJSON(out,results);
		    if (close)
		    	UserController.getGraphPool().free(graph);
		    return out;
		}catch(Exception e){
			e.printStackTrace();
			UserController.getGraphPool().free(graph);
		}		
		return null;
	}

	public static OutputStream getSensorMetadata(String sensorURL) {
		VirtGraph graph = null;		
		try {
			graph = UserController.getGraphPool().getConnection();
			String query ="select ?p ?o "+
				" from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> " +
				"where{"+
				  "<"+sensorURL+"> ?p ?o."+
				"}";
			VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (query, graph);
			ResultSet results = vqe.execSelect();
			ByteArrayOutputStream out = new ByteArrayOutputStream(); 
			ResultSetFormatter.outputAsJSON(out,results);
			UserController.getGraphPool().free(graph);			
			return out;
		} catch(Exception sqle) {
			System.err.println("Error connecting: " + sqle);
			UserController.getGraphPool().free(graph);
			return(null);
		} 		
	}

	public static String getSindiceSearch(String keyword) {
		// TODO Auto-generated method stub
		String searchURL = "http://api.sindice.com/v3/search?q="+keyword+"&format=json";
		String result = WebServiceURLRetriever.RetrieveFromURL(searchURL);
		return result.toString();
	}
  
	public static void main(String[] agr){
		String query = "select * from <http://lsm.deri.ie/sensormeta#> where{?s ?p ?o.} limit 10";
		OutputStream out = getQueryResults(query, false);
		System.out.println(out);
	}
	
}