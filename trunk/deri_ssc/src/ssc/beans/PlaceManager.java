package ssc.beans;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import ssc.controller.UserController;
import ssc.utils.VirtuosoConstantUtil;
import virtuoso.jena.driver.VirtGraph;


public class PlaceManager {
	private Connection conn;
	public PlaceManager(){		
		
	}
	
	public PlaceManager(Connection conn){
		this.conn = conn;
	}
	
	public Place getPlaceWithSpecifiedLatLng(double lat, double lng) {
		Place place = null;
		String sql = "sparql select distinct ?place ?lat ?lng ?city ?province ?country ?continent "+
					"from <"+VirtuosoConstantUtil.sensormasherMetadataGraphURI+"> " +
					"where{ "+
					  "?place <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.loa-cnr.it/ontologies/DUL.owl#Place>."+
					  "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat."+
					  "?place <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?lng."+
					  "?place <http://lsm.deri.ie/ont/lsm.owl#is_in_city> ?cityId."+
					  "?cityId <http://www.w3.org/2000/01/rdf-schema#label> ?city."+
					  "?place <http://linkedgeodata.org/property/is_in_province> ?proId."+
					  "?proId <http://www.w3.org/2000/01/rdf-schema#label> ?province."+
					  "?place <http://linkedgeodata.org/property/is_in_country> ?counId."+
					  "?counId <http://www.w3.org/2000/01/rdf-schema#label> ?country."+
					  "?place <http://linkedgeodata.org/property/is_in_continent> ?conId."+
					  "?conId <http://www.w3.org/2000/01/rdf-schema#label> ?continent."+
					  "filter(?lat="+lat +" && ?lng="+lng+")"+
					"}";			 
		try{
			conn = UserController.getConnectionPool().getConnection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){					
					place = new Place();
					place.setId(rs.getString("place"));
					place.setCity(rs.getString("city"));
					place.setProvince(rs.getString("province"));
					place.setCountry(rs.getString("country"));
					place.setContinent(rs.getString("continent"));
					place.setLat(rs.getDouble("lat"));
					place.setLng(rs.getDouble("lng"));		
				}
				UserController.attemptClose(rs);				
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return place;
	}

	
	@SuppressWarnings("unchecked")
	public List<Place> getAllPlacesWithinOneCity(String city) {
		List<Place> places = new ArrayList<Place>();
		String sql = "sparql select distinct ?place ?lat ?lng "+
					"from <"+VirtuosoConstantUtil.sensormasherMetadataGraphURI+"> " +
					"where{ "+
					  "?place <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.loa-cnr.it/ontologies/DUL.owl#Place>."+
					  "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat."+
					  "?place <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?lng."+
					  "?place <http://lsm.deri.ie/ont/lsm.owl#is_in_city> ?cityId."+
					  "?cityId <http://www.w3.org/2000/01/rdf-schema#label> \"" + city +"\"."+					  
					"}";			 
		try{
			conn = UserController.getConnectionPool().getConnection();				
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){					
					Place place = new Place();
					place.setId(rs.getString("place"));				
					place.setLat(rs.getDouble("lat"));
					place.setLng(rs.getDouble("lng"));
					places.add(place);
				}
				UserController.attemptClose(rs);				
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return places;
	}


	public List<List<String>> getALlPlaceMetadataWithPlaceId(String id) {
		// TODO Auto-generated method stub
		List<List<String>> lst = new ArrayList();		
		String sql = "sparql select ?p ?o "+		
			"from <"+VirtuosoConstantUtil.sensormasherMetadataGraphURI+"> " +
			"where{ \n" +
			   " <"+id+"> ?p ?o." +			   
			  "}";			 
		try{
			conn = UserController.getConnectionPool().getConnection();				
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){										
					ArrayList strNode = new ArrayList();					
					strNode.add(id);	
					strNode.add(rs.getString(1));					
					strNode.add(rs.getString(2));
					lst.add(strNode);
				}
				UserController.attemptClose(rs);				
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
			return lst;
		}		
		return lst;
	}

	public Place getPlaceWithPlaceId(String id){
		Place place = null;		
		String sql = "sparql select distinct ?lat ?lng ?city ?province ?country ?continent "+
					"from <"+VirtuosoConstantUtil.sensormasherMetadataGraphURI+"> " +
					"where{ "+
					"<"+id+">" + " <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.loa-cnr.it/ontologies/DUL.owl#Place>."+
					"<"+id+">" + " <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat."+
					"<"+id+">" + " <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?lng."+
					"<"+id+">" + " <http://lsm.deri.ie/ont/lsm.owl#is_in_city> ?cityId."+
					  "?cityId <http://www.w3.org/2000/01/rdf-schema#label> ?city."+
					  "<"+id+">" + " <http://linkedgeodata.org/property/is_in_province> ?proId."+
					  "?proId <http://www.w3.org/2000/01/rdf-schema#label> ?province."+
					  "<"+id+">" + " <http://linkedgeodata.org/property/is_in_country> ?counId."+
					  "?counId <http://www.w3.org/2000/01/rdf-schema#label> ?country."+
					  "<"+id+">" + " <http://linkedgeodata.org/property/is_in_continent> ?conId."+
					  "?conId <http://www.w3.org/2000/01/rdf-schema#label> ?continent."+
					"}";			 
		try{
			conn = UserController.getConnectionPool().getConnection();				
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){					
					place = new Place();
					place.setId(id);
					place.setCity(rs.getString("city"));
					place.setProvince(rs.getString("province"));
					place.setCountry(rs.getString("country"));
					place.setContinent(rs.getString("continent"));
					place.setLat(rs.getDouble("lat"));
					place.setLng(rs.getDouble("lng"));		
				}
				UserController.attemptClose(rs);				
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();
			UserController.attemptClose(conn);
		}
		return place;
	}
}
