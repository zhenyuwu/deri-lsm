package ssc.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import net.sf.json.JSONObject;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;

import ssc.controller.UserController;
import ssc.utils.ConstantsUtil;
import ssc.utils.DateUtil;
import ssc.utils.NumberUtil;
import ssc.utils.VirtuosoConstantUtil;
import virtuoso.jdbc4.VirtuosoExtendedString;
import virtuoso.jena.driver.VirtGraph;


public class SensorManager {
	private Connection conn;
	
	
	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

	public SensorManager(){
		if(UserController.getConnectionPool()==null)
			UserController.init();
	}
	
	public void runSpatialIndex(){
		String sql = "DB.DBA.RDF_GEO_FILL()";
		try{
			conn = UserController.getConnectionPool().getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			boolean i = ps.execute(sql);
			if(i) System.out.println("create spatial index succed");		
			UserController.attemptClose(ps);
			UserController.attemptClose(conn);			
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}		
	}
	
	public void insertTriplesToGraph(String graphName, String triples) {
		// TODO Auto-generated method stub
		try{
			conn = UserController.getConnectionPool().getConnection();
			String sql = "sparql insert into graph <" + graphName + ">{" + triples +"}";
			PreparedStatement ps = conn.prepareStatement(sql);
			boolean i = ps.execute(sql);
			System.out.println("Insert triples to graph "+graphName);
			UserController.attemptClose(ps);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
	}
				

	@SuppressWarnings("unchecked")
	public List<String> getAllSourcesWithSpecifiedLatLngSensorType(double lat, double lng, String sensorType) {
		List<String> list = new ArrayList<String>();
		String sql = "sparql select ?source "+
				" from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> " +
				"where{ "+
				   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
				   "?sensor <http://purl.org/net/provenance/ns#PerformedBy> ?source."+
				   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> \""+sensorType+"\"."+				   
				   "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> "+lat+";" +
				   "<http://www.w3.org/2003/01/geo/wgs84_pos#long> "+lng+"." +
				"}";			 
		try{
			conn = UserController.getConnectionPool().getConnection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					list.add(rs.getString(1));					
				}
				UserController.attemptClose(rs);				
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<Sensor> getAllSensorWithSpecifiedLatLngSensorType(double lat, double lng, String sensorType) {
		List<Sensor> sensors = new ArrayList<Sensor>();
		try {
			 conn = UserController.getConnectionPool().getConnection();			
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		PlaceManager placeManager = new PlaceManager();
//		PlaceManager placeManager = new PlaceManager(conn);
		String sql = "sparql select ?sensor ?source ?sourceType ?place ?userId "+
				" from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> " +
				"where{ "+
				   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
				   "?sensor <http://purl.org/net/provenance/ns#PerformedBy> ?source."+
				   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> \""+sensorType+"\"."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#isAddedBy> ?userId." +
				   "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> "+lat+";" +
				   "<http://www.w3.org/2003/01/geo/wgs84_pos#long> "+lng+"." +
				"}";			 
		try{
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					Sensor sensor = new Sensor();
					sensor.setId(rs.getString(1));
					sensor.setSource(rs.getString(2));
					sensor.setSensorType(sensorType);
					sensor.setSourceType(rs.getString(3));
					Place place = placeManager.getPlaceWithPlaceId(rs.getString("place"));
					sensor.setPlace(place);					
					sensors.add(sensor);
				}
				UserController.attemptClose(rs);				
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return sensors.size()>0?sensors:null;
	}
	
	@SuppressWarnings("unchecked")
	public List<Sensor> getAllSensorWithSpecifiedCitySensorType(String city, String country, String sensorType) {
		List<Sensor> sensors = new ArrayList<Sensor>();
		PlaceManager placeManager = new PlaceManager();
//		PlaceManager placeManager = new PlaceManager(conn);
		String sql = "sparql select ?sensor ?source ?sourceType ?place ?userId "+
				" from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> " +
				"where{ "+
				   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
				   "?sensor <http://purl.org/net/provenance/ns#PerformedBy> ?source."+
				   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> \""+sensorType+"\"."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#isAddedBy> ?userId." +
				   "?place <http://lsm.deri.ie/ont/lsm.owl#is_in_city> ?cityId."+
				   "?cityId <http://www.w3.org/2000/01/rdf-schema#label> \"" + city +"\"."+
				   "?place <http://linkedgeodata.org/property/is_in_country> ?counId."+
				   "?counId <http://www.w3.org/2000/01/rdf-schema#label> \"" + country +"\"."+
				"}";			 
		try{
			conn = UserController.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					Sensor sensor = new Sensor();
					sensor.setId(rs.getString(1));
					sensor.setSource(rs.getString(2));
					sensor.setSensorType(sensorType);
					sensor.setSourceType(rs.getString(3));
					Place place = placeManager.getPlaceWithPlaceId(rs.getString("place"));
					sensor.setPlace(place);							
					sensors.add(sensor);
				}
				UserController.attemptClose(rs);				
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return sensors.size()>0?sensors:null;
	}
	
	
	/*search*/
	@SuppressWarnings("unchecked")
	public List<String> getAllSensorTypesWithOneSpecifiedLatLng(double lat,double lng) {
		List<String> sensorTypes = new ArrayList<String>();
		String sql = "sparql select distinct ?sensorType "+
				" from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> " + 
				"where{ "+
				   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+				   
				   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> ?sensorType."+				   
				   "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> "+lat+";" +
				   "<http://www.w3.org/2003/01/geo/wgs84_pos#long> "+lng+"." +
				"}";			 
		try{
			conn = UserController.getConnectionPool().getConnection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					sensorTypes.add(rs.getString(1));					
				}
				UserController.attemptClose(rs);				
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return sensorTypes;
	}
	

	public ArrayList<List> getAllSensorHasLatLongWithSpatialCriteria(String sensorType,String spatialOperator,double lng,double lat,double distance){		
		ArrayList<List> lst = new ArrayList<List>(3);
		List<String> l1= new ArrayList<String>();
		List<String> l2 = new ArrayList<String>();
		List<Double> l3 = new ArrayList<Double>();
		
		String sql = "sparql select distinct(?sensor) ?city ?country <bif:st_distance>(?geo,<bif:st_point>("+
			lng+","+lat+")) as ?distance "+		
				" from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> " + 			
				"where {"+			
				"?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
				"?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
				"?typeId <http://www.w3.org/2000/01/rdf-schema#label> \""+sensorType+"\"."+							
				"?sensor <"+ VirtuosoConstantUtil.sensorHasPlacePrefix+"> ?place. "+
				"?place <http://lsm.deri.ie/ont/lsm.owl#is_in_city> ?cityId."+
				"?cityId <http://www.w3.org/2000/01/rdf-schema#label> ?city."+
				"?place <http://linkedgeodata.org/property/is_in_country> ?counId."+
				"?counId <http://www.w3.org/2000/01/rdf-schema#label> ?country."+
				"?place geo:geometry ?geo."+
				"filter (<bif:"+ spatialOperator +">(?geo,<bif:st_point>("+
						lng+","+lat+"),"+distance+"))." +
				"} order by ?distance";
		try{
			conn = UserController.getConnectionPool().getConnection();			
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();		
			while(rs.next()){
				l1.add(rs.getString(1));
				l2.add(rs.getString(2)+", "+rs.getString(3));
				l3.add(rs.getDouble(4));
			}
			lst.add(l1);
			lst.add(l2);
			lst.add(l3);
			UserController.attemptClose(rs);
			UserController.attemptClose(ps);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return l1.size()>0?lst:null;
	}
	
	public ArrayList<List> getAllSensorHasLatLongWithSpatialCriteria(String spatialOperator,
			double lng,double lat,double distance){		
		ArrayList<List> lst = new ArrayList<List>(3);
		List<String> l1= new ArrayList<String>();
		List<String> l2 = new ArrayList<String>();
		List<Double> l3 = new ArrayList<Double>();
		
		String sql = "sparql select distinct(?sensor) ?city ?country <bif:st_distance>(?geo,<bif:st_point>("+
				lng+","+lat+")) as ?distance "+		
					" from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> " + 			
					"where {"+			
					"?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+												
					"?sensor <"+ VirtuosoConstantUtil.sensorHasPlacePrefix+"> ?place. "+
					"?place <http://lsm.deri.ie/ont/lsm.owl#is_in_city> ?cityId."+
					"?cityId <http://www.w3.org/2000/01/rdf-schema#label> ?city."+
					"?place <http://linkedgeodata.org/property/is_in_country> ?counId."+
					"?counId <http://www.w3.org/2000/01/rdf-schema#label> ?country."+
					"?place geo:geometry ?geo."+
					"filter (<bif:"+ spatialOperator +">(?geo,<bif:st_point>("+
							lng+","+lat+"),"+distance+"))." +
					"} order by ?distance";		
		try{
			conn = UserController.getConnectionPool().getConnection();			
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();		
			while(rs.next()){
				l1.add(rs.getString(1));
				l2.add(rs.getString(2)+", "+rs.getString(3));
				l3.add(rs.getDouble(4));
			}
			lst.add(l1);
			lst.add(l2);
			lst.add(l3);
			UserController.attemptClose(rs);
			UserController.attemptClose(ps);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return lst;
	}
			
	@SuppressWarnings("unchecked")
	public ArrayList<Sensor> getAllSpecifiedSensorAroundPlace(String sensorType,String lat,String lng,String distance){		
		ArrayList<Sensor> sensors = new ArrayList<Sensor>();
		PlaceManager placeManager = new PlaceManager();
		
		String sql = "sparql select distinct(?sensor) ?source ?sourceType ?place "+	
			" from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> " +
			"where {"+			  
			"?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
			"?sensor <http://purl.org/net/provenance/ns#PerformedBy> ?source."+
			"?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
			"?typeId <http://www.w3.org/2000/01/rdf-schema#label> \""+sensorType+"\"."+	
			"?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+
			"?sensor <"+ VirtuosoConstantUtil.sensorHasPlacePrefix+"> ?place. "+
		     "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat;" +
		     "<http://www.w3.org/2003/01/geo/wgs84_pos#long> ?long;" +
		     "geo:geometry ?geo."+
		     "filter (<bif:st_intersects>(?geo,<bif:st_point>("+
				lng+","+lat+"),"+distance+"))." +
		"} order by ?distance";		
		try{
			conn = UserController.getConnectionPool().getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();		
			while(rs.next()){
				Sensor sensor = new Sensor();
				sensor.setId(rs.getString("sensor"));
				sensor.setSource(rs.getString("source"));
				sensor.setSensorType(sensorType);
				sensor.setSourceType(rs.getString("sourceType"));
				Place place = placeManager.getPlaceWithPlaceId(rs.getString("place"));
				sensor.setPlace(place);							
				sensors.add(sensor);
			}
			UserController.attemptClose(rs);
			UserController.attemptClose(ps);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return sensors.size()>0?sensors:null;
	}
		
	
	public String getNearestLocationIdFromGraph(String linkedgeoId) {
		// TODO Auto-generated method stub
		String nearbyId = "";		
		String sql = "sparql select distinct(?near) "+	
			" from <"+ VirtuosoConstantUtil.linkedgeodataGraphURI +"> " +
			"where {"+			  
			"<"+linkedgeoId+"> <"+ VirtuosoConstantUtil.lnkedgeodataSameAsPrefix +"> ?near." +		     
			"}";		
		try{
			conn = UserController.getConnectionPool().getConnection();			
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();		
			while(rs.next()){
				nearbyId = rs.getString(1);
			}
			UserController.attemptClose(rs);
			UserController.attemptClose(ps);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return nearbyId;
	}
	
	public String getNearestLocationWithSensorIdURL(String id){
		String nearbyId = "";		
		String sql = "sparql select distinct(?near) "+	
			" from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> \n" +
			"where {"+			  
			"<"+id+"> <"+ VirtuosoConstantUtil.sensorHasNearestLocation +"> ?near." +		     
			"}";		
		try{
			conn = UserController.getConnectionPool().getConnection();			
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();		
			while(rs.next()){
				nearbyId = rs.getString(1);
			}
			UserController.attemptClose(rs);
			UserController.attemptClose(ps);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return nearbyId;
	}


		
	//**********************sensor table***************************/
	public Sensor getSpecifiedSensorWithSource(String source){
		PlaceManager placeManager = new PlaceManager();
//		PlaceManager placeManager = new PlaceManager(conn);		
		Sensor sensor = null;
		String sql = "sparql select ?sensor ?sensorType ?sourceType ?place ?userId "+
					"from <http://lsm.deri.ie/sensormeta#> "+
					"where{ "+
					   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
					   "?sensor <http://purl.org/net/provenance/ns#PerformedBy> <"+source+">."+
					   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
					   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
					   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> ?sensorType."+
					   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+
					   "?sensor <http://lsm.deri.ie/ont/lsm.owl#isAddedBy> ?userId."+
					"}";			 
			try{
				conn = UserController.getConnectionPool().getConnection();				
				Statement st = conn.createStatement();
				if(st.execute(sql)){
					ResultSet rs = st.getResultSet();
					while(rs.next()){
						sensor = new Sensor();
						sensor.setId(rs.getString(1));
						sensor.setSensorType(rs.getString(2));
						sensor.setSource(source);
						sensor.setSourceType(rs.getString(3));
						Place place = placeManager.getPlaceWithPlaceId(rs.getString("place"));
						sensor.setPlace(place);
					}
					UserController.attemptClose(rs);				
				}
				UserController.attemptClose(st);
				UserController.attemptClose(conn);
			}catch(Exception e){
				e.printStackTrace();
				UserController.attemptClose(conn);
			}		
			return sensor;
	}
	
	@SuppressWarnings("unchecked")
	public Sensor getSpecifiedSensorWithPlaceId(String placeId){
		Sensor sensor = null;
		PlaceManager placeManager = new PlaceManager();		
		String sql = "sparql select ?sensor ?sensorType ?source ?sourceType ?place ?userId "+
				" from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> \n" +
				"where{ "+
				   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
				   "?sensor <http://purl.org/net/provenance/ns#PerformedBy> ?source."+
				   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> <"+placeId+">."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> ?sensorType."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#isAddedBy> ?userId." +				  
				"}";			 
		try{
			conn = UserController.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					sensor = new Sensor();					
					sensor.setId(rs.getString("sensor"));
					sensor.setSource(rs.getString("source"));
					sensor.setSensorType(rs.getString("sensorType"));
					sensor.setSourceType(rs.getString("sourceType"));
					Place place = placeManager.getPlaceWithPlaceId(placeId);
					sensor.setPlace(place);								
				}
				UserController.attemptClose(rs);				
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return sensor;
	}
	

	public Sensor getSpecifiedSensorWithSensorId(String id){		
		Sensor sensor = null;
		
		String sql = "sparql select ?name ?sensorType ?source ?sourceType ?place ?userId "+
				" from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> \n" +
				"where{ "+
				   "<"+id+"> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
				   "<"+id+"> <http://purl.org/net/provenance/ns#PerformedBy> ?source."+
				   "<"+id+"> <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
				   "<"+id+"> <http://www.w3.org/2000/01/rdf-schema#label> ?name."+
				   "<"+id+"> <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> ?sensorType."+
				   "<"+id+"> <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+
				   "<"+id+"> <http://lsm.deri.ie/ont/lsm.owl#isAddedBy> ?userId." +				  
				"}";			 
		try{
			conn = UserController.getConnectionPool().getConnection();			
			PlaceManager placeManager = new PlaceManager();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					sensor = new Sensor();					
					sensor.setId(id);
					sensor.setSource(rs.getString("source"));
					sensor.setSensorType(rs.getString("sensorType"));
					sensor.setSourceType(rs.getString("sourceType"));
					sensor.setName(rs.getString("name"));
					Place place = placeManager.getPlaceWithPlaceId(rs.getString("place"));
					sensor.setPlace(place);
				}
				UserController.attemptClose(rs);				
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return sensor;
	}
	

	public Sensor getSpecifiedSensorWithLatLng(double lat, double lng) {		
		Sensor sensor = null;
		String sql = "sparql select ?sensor ?sensorType ?source ?sourceType ?place "+
				" from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> \n" +
				"where{ "+
				   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
				   "?sensor <http://purl.org/net/provenance/ns#PerformedBy> ?source."+
				   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> ?sensorType."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+				  
				   "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> "+lat+";" +
				   "<http://www.w3.org/2003/01/geo/wgs84_pos#long> "+lng+"." +
				"}";			 
		try{
			conn = UserController.getConnectionPool().getConnection();
			PlaceManager placeManager = new PlaceManager();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					sensor = new Sensor();
					sensor.setId(rs.getString("sensor"));
					sensor.setSource(rs.getString("source"));
					sensor.setSensorType(rs.getString("sensorType"));
					sensor.setSourceType(rs.getString("?sourceType"));
					Place place = placeManager.getPlaceWithPlaceId(rs.getString("place"));
					sensor.setPlace(place);
				}
				UserController.attemptClose(rs);				
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return sensor;
	}
	
	
	//**********************observation table***************************/
	public Observation getNewestObservationForOneSensor(String sensorId) {
		Observation observation = null;
		String sql = "sparql select ?obs ?time ?foi"+
				" from <"+ VirtuosoConstantUtil.sensormasherDataGraphURI+"> \n" +
				"where{ "+
				   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy> <"+sensorId+">."+
				   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time." +
				   "?obs <http://purl.oclc.org/NET/ssnx/ssn#featureOfInterest> ?foi."+
				"}order by desc(?time) limit 1";			 
		try{
			conn = UserController.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					observation = new Observation();
					observation.setId(rs.getString(1));
					observation.setSensor(sensorId);
					observation.setTimes(DateUtil.string2Date(rs.getString(2),"yyyy-MM-dd HH:mm:ss.SSS"));		
					observation.setFeatureOfInterest(rs.getString(3));
				}
				UserController.attemptClose(rs);				
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return  observation;
	}
	
	public List<String> getObservationsWithTimeCriteria(String sensorId,
			String dateOperator, Date fromTime, Date toTime) {
		// TODO Auto-generated method stub				
		String sql;
		
		if(toTime!=null){
			sql= "sparql select ?obs"+
					" from <"+ VirtuosoConstantUtil.sensormasherDataGraphURI+"> "+
					"where{ "+
					   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorId+">."+					   
					   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+		
					   "filter (?time "+dateOperator+" \""+DateUtil.date2StandardString(fromTime)+"\"^^xsd:dateTime"+" && ?time <= \""+DateUtil.date2StandardString(toTime)+"\"^^xsd:dateTime)" +
					"}";
		}else{
			sql= "sparql select ?obs"+
					" from <"+ VirtuosoConstantUtil.sensormasherDataGraphURI+"> "+
					"where{ "+
					   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorId+">."+					   
					   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+		
					   "filter (?time "+dateOperator+" \""+DateUtil.date2StandardString(fromTime)+"\"^^xsd:dateTime)" +
					"}";
		}
		List<String> observations = new ArrayList<String>();		
		try{
			conn = UserController.getConnectionPool().getConnection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					observations.add(rs.getString(1));
				}
				UserController.attemptClose(rs);				
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return observations;
	}
	
	public List<Observation> getObservationsForOneSensor(String sensorId) {
		// TODO Auto-generated method stub		
		List<Observation> observations = new ArrayList<Observation>();
		String sql = "sparql select ?obs ?time ?foi"+
				" from <"+ VirtuosoConstantUtil.sensormasherDataGraphURI +"> " + 
				"where{ "+
				   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy> <"+sensorId+">."+
				   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time." +
				   "?obs <http://purl.oclc.org/NET/ssnx/ssn#featureOfInterest> ?foi."+
				"}order by desc(?time)";			 
		try{
			conn = UserController.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					Observation observation = new Observation();
					observation.setId(rs.getString(1));
					observation.setSensor(sensorId);
					observation.setTimes(DateUtil.string2Date(rs.getString(2),"yyyy-MM-dd HH:mm:ss.SSS"));		
					observation.setFeatureOfInterest(rs.getString(3));
					observations.add(observation);
				}
				UserController.attemptClose(rs);				
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return  observations;
	}
	
	public List<String> getObservationsForNonSpatialCriteria(
			String sensorId,String timeOper, String dateTime, String readingType,
			String oper, String value) {		
		// TODO Auto-generated method stub
		Date date = DateUtil.standardString2Date(dateTime);
		String sql;
		if(timeOper.equals("latest")){
			if(value!=null){
				sql= "sparql select ?obs"+
						" from <"+ VirtuosoConstantUtil.sensormasherDataGraphURI +"> " + 
					"where{ "+
					   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorId+">."+
					   "?sign <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?obs."+
					   "?sign rdf:type ?type." +
					   "?sign <http://lsm.deri.ie/ont/lsm.owl#value> ?value."+
					   "?sign <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+					  
					   " filter regex(?type,'"+readingType+"','i')"+
					   " filter (?value" + oper + value +")" +
					"}order by desc(?time) limit 1";
			}else{
				sql= "sparql select ?obs"+
						" from <"+ VirtuosoConstantUtil.sensormasherDataGraphURI +"> " + 
						"where{ "+
						   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorId+">."+						   
						   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+	
						"}order by desc(?time) limit 1";
			}
		}else{
			if(value!=null){
				sql= "sparql select ?obs"+
						" from <"+ VirtuosoConstantUtil.sensormasherDataGraphURI +"> " + 
						"where{ "+
						   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorId+">."+
						   "?sign <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?obs."+
						   "?sign rdf:type ?type." +
						   "?sign <http://lsm.deri.ie/ont/lsm.owl#value> ?value."+
						   "?sign <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+					  
						   " filter regex(?type,'"+readingType+"','i')"+
						   " filter (?value" + oper + value +" && ?time "+timeOper+" \""+dateTime+"\"^^xsd:dateTime)" +
						"}";
			}else{
				sql= "sparql select ?obs"+
						" from <"+ VirtuosoConstantUtil.sensormasherDataGraphURI +"> " + 
						"where{ "+
						   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorId+">."+
						   "?sign <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?obs."+
						   "?sign rdf:type ?type." +
						   "?sign <http://lsm.deri.ie/ont/lsm.owl#value> ?value."+
						   "?sign <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+					  
						   " filter regex(?type,'"+readingType+"','i')"+
						   " filter (?time "+timeOper+" \""+dateTime+"\"^^xsd:dateTime)" +
						"}";
				
			}
		}
		List<String> observations = new ArrayList<String>();		
		try{
			conn = UserController.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					observations.add(rs.getString(1));
				}
				UserController.attemptClose(rs);				
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return observations;
	}
	

	public AbstractProperty getSpecifiedReadingForOneObservation(String observationId,	String readingType) {		
		// TODO Auto-generated method stub
		AbstractProperty abs = null;
		String sql = "sparql select ?value ?unit ?time"+
				" from <"+ VirtuosoConstantUtil.sensormasherDataGraphURI +"> " + 
				"where{ "+
				   "?sign <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> <"+observationId+">."+
				   "?sign rdf:type ?type." +
				   "?sign <http://lsm.deri.ie/ont/lsm.owl#value> ?value."+
				   "?sign <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+
				   "OPTIONAL{?sign <http://lsm.deri.ie/ont/lsm.owl#unit> ?unit.}" +
				   " filter regex(?type,'"+readingType+"','i')"+
				"}";			 
		try{
			conn = UserController.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					abs = new AbstractProperty();
					abs.setValue(rs.getString("value"));
					abs.setUnit(rs.getString("unit"));
					abs.setValue(rs.getString("value"));
					abs.setPropertyName(readingType);
					abs.setObservedURL(observationId);	
					abs.setTimes(DateUtil.string2Date(rs.getString("time"),"yyyy-MM-dd HH:mm:ss.SSS"));
				}
				UserController.attemptClose(rs);				
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return abs;
	}


	//***********************************check*********************
	public ArrayList<List> getAllSensorPropertiesForSpecifiedSensorType(String type) {
		// TODO Auto-generated method stub
		ArrayList<List> list = new ArrayList<List>(2);
		List list1 = new ArrayList<String>();
		List list2 = new ArrayList<String>();
		String sql = "sparql select ?x ?type "+
				" from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> " + 
					  "where{ "+
						   "{"+
						     "select ?sensor "+
						     "where{ "+
						         "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
						         "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?type."+
						   "?type <http://www.w3.org/2000/01/rdf-schema#label> \""+type+"\"."+
						    " }limit 1"+
						   "}"+
						   "?sensor <http://purl.oclc.ie/NET/ssnx/ssn#observes> ?x."+
						   "?x rdf:type ?type."+
						"}";			 
		try{
			conn = UserController.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){					
					list1.add(rs.getString(1));
					list2.add(rs.getString(2).substring(rs.getString(2).lastIndexOf("#")+1));
//					list2.add(rs.getString(2));
				}
				UserController.attemptClose(rs);				
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		list.add(list1);
		list.add(list2);
		return list;
	}


	public List<ArrayList> getReadingDataOfObservation(String observationId) {		
		// TODO Auto-generated method stub
		List<ArrayList> list = new ArrayList<ArrayList>();
		String sql = "sparql select ?type ?value ?uni ?name "+
				" from <"+ VirtuosoConstantUtil.sensormasherDataGraphURI+"> "+
				"where{ "+
				   "?sign <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> <"+observationId+">."+
				   "?sign rdf:type ?type." +
				   "?sign <http://lsm.deri.ie/ont/lsm.owl#value> ?value."+
				   "OPTIONAL{?sign <http://lsm.deri.ie/ont/lsm.owl#unit> ?unit.}" +				  
				   "OPTIONAL{?sign <http://www.w3.org/2000/01/rdf-schema#label> ?name.}"+
				"}";			 
		try{
			conn = UserController.getConnectionPool().getConnection();	
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					ArrayList<String> arr = new ArrayList<String>();
					arr.add(rs.getString("type"));
					arr.add(rs.getString("value"));
					arr.add(rs.getString("uni"));
					arr.add(rs.getString("name"));
					list.add(arr);
				}				
				UserController.attemptClose(rs);				
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return list;
	}


	public Sensor getSpecifiedSensorWithObservationId(String obsId) {
		// TODO Auto-generated method stub 
		Sensor sensor = null;
		PlaceManager placeManager = new PlaceManager();
		String sql = "sparql select ?sensor ?source ?sourceType ?sensorType ?place ?userId "+
				" from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> " +
				"where{ "+
				   "{select ?sensor from <"+VirtuosoConstantUtil.sensormasherDataGraphURI +"> " +
				   " where{ <"+obsId+"> <http://purl.oclc.org/NET/ssnx/ssn#observedBy> ?sensor.}"+
				   "}"+
				   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
				   "?sensor <http://purl.org/net/provenance/ns#PerformedBy> ?source."+
				   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> ?sensorType."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#isAddedBy> ?userId." +				  
				"}";			 
		try{
			conn = UserController.getConnectionPool().getConnection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					sensor = new Sensor();
					sensor.setId(rs.getString("sensor"));
					sensor.setSource(rs.getString("source"));
					sensor.setSensorType(rs.getString("sensorType"));
					sensor.setSourceType(rs.getString("sourceType"));
					Place place = placeManager.getPlaceWithPlaceId(rs.getString("place"));
					sensor.setPlace(place);
				}
				UserController.attemptClose(rs);				
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return sensor;
	}
	
	private List<String> data2List(Observation observation){
		String source = observation.getSensor();//the reference of more data of the specified weather
		String sign;
		List<String> list = new ArrayList<String>();
		List<ArrayList> readings = getReadingDataOfObservation(observation.getId());
		for(ArrayList reading : readings){
			sign = reading.get(0).toString().substring(reading.get(0).toString().lastIndexOf("#")+1);
			list.add(sign);						
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
			try {				
				if(content == null){
					list.add("not_supported" + ConstantsUtil.useful_data_sign);
				}else if(NumberUtil.isDouble(content)){
					double data = Double.parseDouble(content);
					if(data != ConstantsUtil.weather_defalut_value){
						list.add(String.valueOf(data) + " unit:(" + unit + ")" + ConstantsUtil.useful_data_sign + source + "," + sign.toString());
					}else{
						list.add("not_supported" + ConstantsUtil.useful_data_sign);
					}
				}else{
					String data = content;
					if(!data.trim().equals("") ){
						list.add(data + " unit:(" + unit + ")" + ConstantsUtil.useful_data_sign + source + "," + sign.toString());
					}else{
						list.add("not_supported" + ConstantsUtil.useful_data_sign);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	public ArrayList getSensorHistoricalData(String sensorURL, Date fromTime) {
		// TODO Auto-generated method stub
		LinkedHashMap<String, String> reading = new LinkedHashMap<>();
		ArrayList arr = new ArrayList<>();
		String query = "sparql select ?s ?type ?name ?value ?time "+
				" from <"+ VirtuosoConstantUtil.sensormasherDataGraphURI +"> " +
					"where{ "+
					  "{ "+
					   "select ?observation where "+
					     "{ "+
					       "?observation <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorURL+">. "+
					       "?observation <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time. "+
					    "filter( ?time >\""+DateUtil.date2StandardString(fromTime)+"\"^^xsd:dateTime).} "+
					  "} "+ 
					  "?s <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?observation. "+
					  "?s <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> ?type. "+
					  "?s <http://lsm.deri.ie/ont/lsm.owl#value> ?value."+
					  "?s <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time. "+
					  "OPTIONAL{?s <http://www.w3.org/2000/01/rdf-schema#label> ?name.}"+
					"}";			 
		try{
			conn = UserController.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			String sign = "";
			if(st.execute(query)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){				
					reading = new LinkedHashMap<>();					
					if(rs.getString("name")==null)
						sign = rs.getString("type");
					else sign = rs.getString("name");
					reading.put("property",sign.substring(sign.lastIndexOf("#")+1));
					reading.put("value", rs.getString("value"));
					reading.put("time", rs.getString("time"));
					arr.add(reading);
				}			
				UserController.attemptClose(rs);				
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();			
			UserController.attemptClose(conn);
		}
//		return json;
		return arr;
	}

	
	public ArrayList getAllFlightsWithSpecifiedDeparture(String airportCode){
		// TODO Auto-generated method stub
		ArrayList results = new ArrayList<>();
		ArrayList arr = new ArrayList<>();
		String query = "select CallSign,FlightNumber,Departure,Transit,Destination from deri.dba.flightroutecsv where Departure='"+airportCode+"'";								 
		try{
			conn = UserController.getConnectionPool().getConnection();			
			Statement st = conn.createStatement();
			if(st.execute(query)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					arr = new ArrayList<>();
					arr.add(rs.getString("CallSign"));
					arr.add(rs.getString("FlightNumber"));
					arr.add(rs.getString("Departure")+"-"+rs.getString("Destination"));
					results.add(arr);
				}	
				UserController.attemptClose(rs);
			}			
			UserController.attemptClose(st);
			UserController.attemptClose(conn);			
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}		
		return results;
	}
	
	public ArrayList getAllFlightsWithSpecifiedDestination(String airportCode){
		// TODO Auto-generated method stub
		ArrayList results = new ArrayList<>();
		ArrayList arr = new ArrayList<>();
		String query = "select CallSign,FlightNumber,Departure,Transit,Destination from deri.dba.flightroutecsv where Destination='"+airportCode+"'";								 
		try{
			conn = UserController.getConnectionPool().getConnection();
			Statement st = conn.createStatement();
			if(st.execute(query)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					arr = new ArrayList<>();
					arr.add(rs.getString("CallSign"));
					arr.add(rs.getString("FlightNumber"));
					arr.add(rs.getString("Departure")+"-"+rs.getString("Destination"));
					results.add(arr);
				}				
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);		
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}		
		return results;
	}

	public boolean addSSCView(JSONObject json) {
		// TODO Auto-generated method stub
		String userId = json.getString("userId");		
		String viewName = json.getString("viewname");
		String sql = "insert into SSC.DBA.userview values('"+System.nanoTime()+"','"+userId+"','"+viewName+"','"+json.toString()+"')";
		try{
			conn = UserController.getConnectionPool().getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			boolean i = ps.execute(sql);
			UserController.attemptClose(ps);
			UserController.attemptClose(conn);	
			return true;
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return false;
	}
	
	public JSONObject loadSSCView(JSONObject json) {
		// TODO Auto-generated method stub
		String userId = json.getString("userId");		
		String viewName = json.getString("viewname");
		JSONObject jLoad = new JSONObject();
		String sql = "select userId,viewname,viewJSONContent from SSC.DBA.userview where userId = '"+userId+"' and viewname='"+viewName+"'";
		try{
			conn = UserController.getConnectionPool().getConnection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					jLoad.put("success", true);
					jLoad.put("userId", rs.getString("userId"));
					jLoad.put("viewname", rs.getString("viewname"));
					jLoad.put("view", rs.getString("viewJSONContent"));					
				}				
				UserController.attemptClose(rs);				
			}else jLoad.put("success", false);
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
			jLoad.put("success", false);
		}		
		return jLoad;
	}

	public boolean addSSCSocket(JSONObject json) {
		// TODO Auto-generated method stub
		String userId = json.getString("userId");		
		String socketId = json.getString("socketId");
		String template = json.getString("socketTemplate");
		String description = json.getString("description");
		String sql = "insert into SSC.DBA.socket values('"+socketId+"','"+userId+"','"+template+"','"+description+"')";
		try{
			conn = UserController.getConnectionPool().getConnection();
			PreparedStatement ps = conn.prepareStatement(sql);
			boolean i = ps.execute(sql);
			UserController.attemptClose(ps);
			UserController.attemptClose(conn);	
			return true;
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return false;
	}

	public String loadSocketContent(String socketId) {
		// TODO Auto-generated method stub
		String json ="";
		String sql = "select content from SSC.DBA.socket where id = '"+socketId+"'";
		try{
			conn = UserController.getConnectionPool().getConnection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					json = rs.getString("content");	
				}				
				UserController.attemptClose(rs);				
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return json;
	}

	public JSONObject loadSSCViewNameWithSpecifiedUserId(String userId) {
		// TODO Auto-generated method stub
		JSONObject json = new JSONObject();
		ArrayList viewArr = new ArrayList<>();
		String sql = "select viewname from SSC.DBA.userview where userId = '"+userId+"'";
		try{
			conn = UserController.getConnectionPool().getConnection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					viewArr.add(rs.getString("viewname"));	
				}				
				UserController.attemptClose(rs);				
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		json.put("views", viewArr);
		return json;
	}
	

}
