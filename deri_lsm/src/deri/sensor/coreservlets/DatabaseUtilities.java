package deri.sensor.coreservlets;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

import deri.sensor.hashmaps.util.SensorTypeToProperties;
import deri.sensor.hashmaps.util.VirtuosoConstantUtil;
import deri.sensor.javabeans.AbstractProperty;
import deri.sensor.meta.SensorTypeEnum;
import deri.sensor.utils.ConstantsUtil;
import deri.sensor.utils.DateUtil;
import deri.sensor.utils.XMLUtil;
import deri.sensor.wrapper.LondonRailwayStationWrapper;

import virtuoso.jena.driver.VirtGraph;
import virtuoso.jena.driver.VirtuosoQueryExecution;
import virtuoso.jena.driver.VirtuosoQueryExecutionFactory;

/** Three database utilities:<BR>
 *   1) getQueryResults. Connects to a database, executes
 *      a query, retrieves all the rows as arrays
 *      of strings, and puts them inside a DBResults
 *      object. Also places the database product name,
 *      database version, and the names of all the columns
 *      into the DBResults object. This has two versions:
 *      one that makes a new connection and another that
 *      uses an existing connection. <P>
 *   2) createTable. Given a table name, a string denoting
 *      the column formats, and an array of strings denoting
 *      the row values, this method connects to a database,
 *      removes any existing versions of the designated
 *      table, issues a CREATE TABLE command with the
 *      designated format, then sends a series of INSERT INTO
 *      commands for each of the rows. Again, there are
 *      two versions: one that makes a new connection and
 *      another that uses an existing connection. <P>
 *   3) printTable. Given a table name, this connects to
 *      the specified database, retrieves all the rows,
 *      and prints them on the standard output.
 *  <P> 
 * @author Hoan Nguyen Mau Quoc
 *
 */

public class DatabaseUtilities {
  
  /** Connect to database, execute specified query,
   *  and accumulate results into DBRresults object.
   *  If the database connection is left open (use the
   *  close argument to specify), you can retrieve the
   *  connection with DBResults.getConnection.
   */
  
  public static String getQueryResults(String driver,
                                          String url,
                                          String username,
                                          String password,
                                          ArrayList<String> para,
                                          boolean close) {
    try {
    	VirtGraph graph = new VirtGraph(url, username, password);
    	return(getQueryResults(graph, para, close));
    } catch(Exception cnfe) {
      System.err.println("Error connecting: " + cnfe);
      return(null);
    } 
  }

  /** Retrieves results as in previous method but uses
   *  an existing connection instead of opening a new one.
   */
  
  public static String getQueryResults(VirtGraph graph,
                                          ArrayList<String> para,
                                          boolean close) {
    try {
      VirtuosoQueryExecution vqe = VirtuosoQueryExecutionFactory.create (para.get(1), graph);
      ResultSet results = vqe.execSelect();
      String xml = ResultSetFormatter.asXMLString(results);
      if (close) {
        graph.close();
      }
      return(convertToHubAtomXMLFeed(para.get(0), xml));
    } catch(Exception sqle) {
      System.err.println("Error connecting: " + sqle);
      return(null);
    } 
  }
  
  public static String convertToHubAtomXMLFeed(String feedURL,String xml){
	  String feed = "";
	  try{
		  Document sparqlXMLdocument = DocumentHelper.parseText(xml);
		  Element resluts = sparqlXMLdocument.getRootElement();
		  
		  Document feedDoc = XMLUtil.createDocument();
		  
		  Element root = feedDoc.addElement("feed");
		  root.addAttribute("xmlns", "http://www.w3.org/2005/Atom");
		  
		  HashMap<String,String> mapAttr = new HashMap<String, String>();
		  mapAttr.put("rel", "hub");
		  mapAttr.put("href", "http://lsmHub.deri.ie/userfeed");
		  Element hubLink = XMLUtil.addElementToElement(root, "link", mapAttr, null);
		  
		  mapAttr.put("rel", "self");
		  mapAttr.put("href", feedURL);
		  Element feedLink = XMLUtil.addElementToElement(root, "link", mapAttr, null);
		  java.util.Date date = new Date();
		  
		  Element updated = XMLUtil.addElementToElement(root, "updated", null, DateUtil.date2StandardString(date));
		  Element data = XMLUtil.addElementToElement(root, "entry", null, null);
		  data.add(resluts);
		  feed = feedDoc.asXML();
		  //System.out.println(feed);
		  
		  //Document testDoc = DocumentHelper.parseText(feed);
	  }catch(Exception e){
		  e.printStackTrace();
	  }
	  return feed;
  }
  
  public static ArrayList<String> getQueryResults(Connection connection,
          String feedId,
          boolean close) {
	try {
		String query = "select feedURL,query from " + ConstantsUtil.databaseName
				+ "userfeed where id = '" + feedId +"'";
		ArrayList<String> para= new ArrayList();		
		Statement statement = connection.createStatement();
		java.sql.ResultSet resultSet = statement.executeQuery(query);
		while(resultSet.next()){
			para.add(resultSet.getString(1));
			para.add(resultSet.getString(2));
		}
		if (close) {
			connection.close();
		}
		return(para);
		} catch(SQLException sqle) {
		System.err.println("Error connecting: " + sqle);
		return(null);
	} 
  }

	@SuppressWarnings("unchecked")
	public static String getSensorAroundLocation(Connection conn,String lat, String lng,boolean close) {
		// TODO Auto-generated method stub
		double distance =0.5;
		ArrayList<ArrayList> lst = new ArrayList<ArrayList>();
		
		String sql = "sparql select ?id ?name ?type ?lat ?long <bif:st_distance>(?geo,<bif:st_point>("+
			lng+","+lat+")) as ?distance "+		
				" from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> \n" + 			
				"where {"+			  
				"?id <"+ VirtuosoConstantUtil.sensormasherOntologyURI+"hasName> ?name." +
				"?id <"+ VirtuosoConstantUtil.sensormasherOntologyURI+"hasSensorType> ?type." +
				"?id <"+ VirtuosoConstantUtil.sensorHasPlacePrefix+"> ?p. "+
				"?p <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat;" +
			     	"<http://www.w3.org/2003/01/geo/wgs84_pos#long> ?long;" +
			     	"geo:geometry ?geo."+
				"filter (<bif:st_intersects>(?geo,<bif:st_point>("+
						lng+","+lat+"),"+distance+"))." +
				"} order by ?distance";
		try{
			PreparedStatement ps = conn.prepareStatement(sql);
			java.sql.ResultSet rs = ps.executeQuery();
			while(rs.next()){
				ArrayList arr = new ArrayList();
				arr.add(rs.getString(1));
				arr.add(rs.getString(2));
				arr.add(rs.getString(3));
				arr.add(rs.getDouble(4));
				arr.add(rs.getDouble(5));
				arr.add(rs.getDouble(6));
				lst.add(arr);
			}			
			if (close)
				conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		String xml = convertToMobileFeed(lat, lng, lst);
		return xml;
	}  
	
	public static String convertToMobileFeed(String lat,String lng,ArrayList<ArrayList> results){
		  String feed = "";
		  try{	  
			  Document feedDoc = XMLUtil.createDocument();
			  
			  Element root = feedDoc.addElement("mobilefeed");
			  root.addAttribute("latitude", lat);
			  root.addAttribute("longitude", lng);
			  
			  for(int i=0;i<results.size();i++){
				  String id = results.get(i).get(0).toString().substring(results.get(i).get(0).toString().lastIndexOf("/")+1);
				  String url = ConstantsUtil.data_feed_prefix+"?api=latestdata&id="+id+"&type="+results.get(i).get(2).toString();
				  Element elmSensor = root.addElement("Sensor");
				  elmSensor.addAttribute("id", results.get(i).get(0).toString());				  
				  Element name = XMLUtil.addElementToElement(elmSensor, "name", null, results.get(i).get(1).toString());
				  Element type = XMLUtil.addElementToElement(elmSensor, "type", null, results.get(i).get(2).toString());
				  Element latitude = XMLUtil.addElementToElement(elmSensor, "latitude", null, results.get(i).get(3).toString());
				  Element longitude = XMLUtil.addElementToElement(elmSensor, "longitude", null,results.get(i).get(4).toString());
				  Element distance = XMLUtil.addElementToElement(elmSensor, "distance", null, results.get(i).get(5).toString());
				  Element dataUrl = XMLUtil.addElementToElement(elmSensor, "dataURL", null, url);
			  }			  
			  feed = feedDoc.asXML();
			  //System.out.println(feed);
		  }catch(Exception e){
			  e.printStackTrace();
		  }
		  return feed;
	  }
	
	public static String getLatestSensorDataForFeed(Connection conn,String sensorId,String sensorType){
		String xml="";
		if(sensorType.equals(SensorTypeEnum.railwaystation.toString()))
			xml = getLatestRailwayStationData(conn, sensorId);
		else xml = getLatestSensorData(conn, sensorId, sensorType);
		return xml;
	}
	
	public static String getLatestSensorData(Connection conn,String sensorId,String sensorType){
		String xml="";
		String picSource = "";		
		Document feedDoc = XMLUtil.createDocument();
		//String id = sensorIdURL.substring(sensorIdURL.lastIndexOf("/")+1);
		try{
			Element root = feedDoc.addElement("latestfeed");
			Element elmSensor = XMLUtil.addElementToElement(root, "Sensor", null, null);
			elmSensor.addAttribute("id", sensorId);
			elmSensor.addAttribute("type", sensorType);
			  
			String sql = "select top 1 id from deri.dba.observation where sensorid='"+sensorId+"' order by times desc";
			PreparedStatement ps = conn.prepareStatement(sql);
			java.sql.ResultSet rs = ps.executeQuery();		
			String obsId="";
			while(rs.next()){
				obsId = rs.getString(1);				
			}
			if(sensorType.equals(SensorTypeEnum.traffic.toString())||sensorType.equals(SensorTypeEnum.webcam.toString())
					||sensorType.equals(SensorTypeEnum.radar.toString())||sensorType.equals(SensorTypeEnum.radar.toString())){
				sql = "select top 1 source from deri.dba.sensor where id='"+sensorId+"'";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();
				while(rs.next()){
					picSource = rs.getString(1);
				}
			}				
			List<String> signs = SensorTypeToProperties.sensorTypeToTableProperties.get(sensorType);
			for(String sign : signs){
				int unitCol= 0;
				int valueCol=0;
				String value="";
				String unit="";
				sql = "select * from deri.dba."+sign+" where observationid='"+obsId+"'";
				ps = conn.prepareStatement(sql);
				rs = ps.executeQuery();		
				ResultSetMetaData rsmd = rs.getMetaData();
				int numColumns = rsmd.getColumnCount();
				for (int i = 1; i < numColumns + 1; i++) {
				      String columnName = rsmd.getColumnName(i);
				      if(columnName.equals("unit"))
				    	  unitCol = i;
				      else if(columnName.equals("value"))
				    	  valueCol = i;
				}
				while(rs.next()){
					if(sign.equals("Picture"))
						value = picSource;
					else
						value = rs.getString(valueCol);
					if(unitCol!=0) unit = rs.getString(unitCol);
				}
				Element elmPro = XMLUtil.addElementToElement(elmSensor, "property", null,null);
				elmPro.addAttribute("name", sign);
				Element elmValue = XMLUtil.addElementToElement(elmPro, "value", null, value);
				Element elmUnit;
				if(unitCol!=0) 
					elmUnit = XMLUtil.addElementToElement(elmPro, "unit", null, unit);
			}
			xml = feedDoc.asXML();
		}catch(SQLException e){
			e.printStackTrace();
		}
		return xml;
	}
	
	public static String getLatestRailwayStationData(Connection conn,String sensorId){
		String xml="";
		String picSource = "";		
		Document feedDoc = XMLUtil.createDocument();
		//String id = sensorIdURL.substring(sensorIdURL.lastIndexOf("/")+1);
		try{
			LondonRailwayStationWrapper railWrapper = new LondonRailwayStationWrapper();
			railWrapper.updateOneRailwayStation(sensorId);
			Element root = feedDoc.addElement("latestfeed");
			Element elmSensor = XMLUtil.addElementToElement(root, "Sensor", null, null);
			elmSensor.addAttribute("id", sensorId);
			elmSensor.addAttribute("type", SensorTypeEnum.railwaystation.toString());
			  
			String sql = "select id from deri.dba.observation where sensorid='"+sensorId+"' " +
					"and times = (select top 1 times from deri.dba.observation where sensorid='"+sensorId+"' " +
							"order by times desc)";
			PreparedStatement ps = conn.prepareStatement(sql);
			java.sql.ResultSet rs = ps.executeQuery();		
			List<String> lstObsId=new ArrayList<String>();
			while(rs.next()){
				lstObsId.add(rs.getString(1));				
			}							
			List<String> signs = SensorTypeToProperties.sensorTypeToTableProperties.get(SensorTypeEnum.railwaystation.toString());
			for(String obsId:lstObsId){
				Element elmTrain = XMLUtil.addElementToElement(elmSensor, "Train", null, null);
				for(String sign : signs){
					int unitCol= 0;
					int valueCol=0;
					String value="";
					String unit="";
					sql = "select * from deri.dba."+sign+" where observationid='"+obsId+"'";
					ps = conn.prepareStatement(sql);
					rs = ps.executeQuery();		
					ResultSetMetaData rsmd = rs.getMetaData();
					int numColumns = rsmd.getColumnCount();
					for (int i = 1; i < numColumns + 1; i++) {
					      String columnName = rsmd.getColumnName(i);
					      if(columnName.equals("unit"))
					    	  unitCol = i;
					      else if(columnName.equals("value"))
					    	  valueCol = i;
					}
					while(rs.next()){
						if(sign.equals("Picture"))
							value = picSource;
						else
							value = rs.getString(valueCol);
						if(unitCol!=0) unit = rs.getString(unitCol);
					}
					Element elmPro = XMLUtil.addElementToElement(elmTrain, "property", null,null);
					elmPro.addAttribute("name", sign);
					Element elmValue = XMLUtil.addElementToElement(elmPro, "value", null, value);
					Element elmUnit;
					if(unitCol!=0) 
						elmUnit = XMLUtil.addElementToElement(elmPro, "unit", null, unit);
				}
			}			
			xml = feedDoc.asXML();
		}catch(SQLException e){
			e.printStackTrace();
		}		
		return xml;
	}
}