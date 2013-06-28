package deri.sensor.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import deri.sensor.hashmaps.util.VirtuosoConstantUtil;
import deri.sensor.javabeans.Place;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class PlaceDAOImpl extends HibernateDaoSupport  implements PlaceDAO {

	@Override
	public void addPlace(Place place) {
		if(this.getPlaceWithSpecifiedLatLng(place.getLat(), place.getLng()) == null){
			this.getHibernateTemplate().save(place);
		}
	}

	@Override
	public void deletePlace(Place place) {
		this.getHibernateTemplate().delete(place);
	}

	@Override
	public void updatePlace(Place place) {
		this.getHibernateTemplate().update(place);
	}
	
	@Override
	public Place getPlaceWithSpecifiedLatLng(double lat, double lng) {
		Place place = null;
		String sql = "sparql select ?place ?lat ?lng ?city ?province ?country ?continent "+
					"from <http://lsm.deri.ie/metadata#> " +
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
			Connection conn = this.getSession().connection();
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
				conn.close();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
			
//		return places.size()>0?places.get(0):null;
		return place;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Place> getAllPlaces() {
		String hql = "from Place";
		List<Place> list = this.getSession(true).createQuery(hql).list();
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Place> getAllPlacesWithinOneCity(String city) {
//		String hql = "from Place p where p.city = '"+city+"'";
//		List<Place>places = this.getSession(true).createQuery(hql).list();
		List<Place> places = new ArrayList<Place>();
		String sql = "sparql select ?place ?lat ?lng "+
					"from <"+VirtuosoConstantUtil.sensormasherMetadataGraphURI+"> " +
					"where{ "+
					  "?place <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.loa-cnr.it/ontologies/DUL.owl#Place>."+
					  "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat."+
					  "?place <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?lng."+
					  "?place <http://lsm.deri.ie/ont/lsm.owl#is_in_city> ?cityId."+
					  "?cityId <http://www.w3.org/2000/01/rdf-schema#label> \"" + city +"\"."+					  
					"}";			 
		try{
			Connection conn = this.getSession().connection();
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
				conn.close();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return places;
	}


	@Override
	public List<List<String>> getALlPlaceMetadataWithPlaceId(String id) {
		// TODO Auto-generated method stub
		List<List<String>> lst = new ArrayList();		
		String sql = "sparql select ?p ?o "+		
			"from <"+VirtuosoConstantUtil.sensormasherMetadataGraphURI+"> " +
			"where{ \n" +
			   " <"+id+"> ?p ?o." +			   
			  "}";			 
		try{
			Connection conn = this.getSession().connection();
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
				conn.close();	
			}
		}catch(Exception e){			
			return lst;
		}		
		return lst;
	}

	public Place getPlaceWithPlaceId(String id){
		Place place = null;		
		String sql = "sparql select ?place ?lat ?lng ?city ?province ?country ?continent "+
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
			Connection conn = this.getSession().connection();
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
				conn.close();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return place;
	}

}
