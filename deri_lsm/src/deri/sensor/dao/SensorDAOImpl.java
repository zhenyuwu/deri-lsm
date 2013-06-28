package deri.sensor.dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import virtuoso.jdbc4.VirtuosoExtendedString;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;

import deri.sensor.components.Map.MapZoomUtil;
import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.VirtuosoConstantUtil;
import deri.sensor.javabeans.AbstractProperty;
import deri.sensor.javabeans.Airport;
import deri.sensor.javabeans.Altitude;
import deri.sensor.javabeans.CallSign;
import deri.sensor.javabeans.Departure;
import deri.sensor.javabeans.Destination;
import deri.sensor.javabeans.FlightCIAO;
import deri.sensor.javabeans.FlightRoute;
import deri.sensor.javabeans.Latitude;
import deri.sensor.javabeans.Longitude;
import deri.sensor.javabeans.Observation;
import deri.sensor.javabeans.ObservedProperty;
import deri.sensor.javabeans.Place;
import deri.sensor.javabeans.Sensor;
import deri.sensor.javabeans.SensorSourceType;
import deri.sensor.javabeans.Speed;
import deri.sensor.javabeans.User;
import deri.sensor.javabeans.UserFeed;
import deri.sensor.javabeans.Wrapper;
import deri.sensor.manager.PlaceManager;
import deri.sensor.manager.UserActiveManager;
import deri.sensor.meta.SensorTypeEnum;
import deri.sensor.utils.ConstantsUtil;
import deri.sensor.utils.DateUtil;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class SensorDAOImpl extends HibernateDaoSupport implements SensorDAO {

	/*basic add delete update for rl database*/
	@Override
	public void addObject(Object object) {
		this.getHibernateTemplate().save(object);
	}

	@Override
	public void deleteObject(Object object) {
		this.getHibernateTemplate().delete(object);
	}

	@Override
	public void updateObject(Object object) {
		this.getHibernateTemplate().update(object);
	}
	
	@Override
	public void deleteAllObservationsForSpecifiedSensor(Sensor sensor) {
		// TODO Auto-generated method stub
		String sql = "sparql delete from <http://lsm.deri.ie/data#> {?s ?p ?o} "+
						"where{ "+
							"{ "+
								"?observation <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensor.getId()+">."+    
								"?s <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?observation."+
								"?s ?p ?o."+
							"}"+
						"union{ "+
							"?s <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensor.getId()+">."+
							"?s ?p  ?o."+
						"}"+
					"}";
		try{
			Connection conn = this.getSession().connection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.executeQuery();		
			conn.close();
			System.out.println("All triples were deleted");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void deleteAllObservationsWithTimeCriteria(Sensor sensor,
			String dateOperator, Date fromTime, Date toTime) {
		// TODO Auto-generated method stub
		String sql = "";				
		if(toTime!=null){
			sql = "sparql delete from <"+ VirtuosoConstantUtil.sensormasherDataGraphURI+"> {?s ?p ?o} "+
				"where{ "+
					"{ {"+
							"?observation <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensor.getId()+">."+  						
							"?observation <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+
						    "filter( ?time "+dateOperator+" \""+DateUtil.date2StandardString(fromTime)+"\"^^xsd:dateTime && "+
						    "?time <= \""+DateUtil.date2StandardString(toTime)+"\"^^xsd:dateTime)."+
						 "}"+
						"?s <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?observation."+
						"?s ?p ?o."+
					"}"+
				"union{ "+
					"?s <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensor.getId()+">."+
					"?s ?p  ?o."+
				"}"+
			"}";
		}else{
			sql = "sparql delete from <"+ VirtuosoConstantUtil.sensormasherDataGraphURI+"> {?s ?p ?o} "+
					"where{ "+
						"{ {"+
								"?observation <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensor.getId()+">."+  						
								"?observation <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+
							    "filter( ?time "+dateOperator+" \""+DateUtil.date2StandardString(fromTime)+"\"^^xsd:dateTime)."+
							 "}"+
							"?s <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> ?observation."+
							"?s ?p ?o."+
						"}"+
					"union{ "+
						"?s <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensor.getId()+">."+
						"?s ?p  ?o."+
					"}"+
				"}";					
		}
		try{
			Connection conn = this.getSession().connection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.executeQuery();		
			conn.close();
			System.out.println("All triples were deleted");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void deleteSensor(Sensor removeSensor){
		String sql = "sparql delete from <" + VirtuosoConstantUtil.sensormasherMetadataGraphURI + 
				"> {<"+removeSensor.getId()+"> ?p ?o} "+
				"where{ "+
					"<"+removeSensor.getId()+"> ?p  ?o."+
				"}"+
			"}";
		try{
			Connection conn = this.getSession().connection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.executeQuery();		
			conn.close();
			System.out.println("Sensor was deleted");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void deleteSensorType(String type){
		String sql = "sparql delete "+
				  "from <" + VirtuosoConstantUtil.sensormasherMetadataGraphURI + "> {" +				  
					 " ?typeId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lsm.deri.ie/ont/lsm.owl#SensorType>."+
					 " ?typeId <http://www.w3.org/2000/01/rdf-schema#label> \""+type+"\"."+
				   "}";
		try{
			Connection conn = this.getSession().connection();
			Statement st = conn.createStatement();
			st.execute(sql);
			conn.close();				
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void excuteSQL(String sql){
		try{
			Connection conn = this.getSession().connection();
			PreparedStatement ps = conn.prepareStatement(sql);
			boolean i = ps.execute(sql);
			if(i) System.out.println("Excute "+sql+" succeed");
			conn.close();			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void excuteSQLUpdate(String sql){
		try{
			Connection conn = this.getSession().connection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.execute(sql);
			System.out.println("update "+sql+" succeed");
			conn.close();			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@Override
	public void insertTriplesToGraph(String graphName, String triples) {
		// TODO Auto-generated method stub
		try{									
			Connection conn = this.getSession().connection();
			String sql = "sparql insert into graph <" + graphName + ">{" + triples +"}";
			PreparedStatement ps = conn.prepareStatement(sql);
			boolean i = ps.execute(sql);
			System.out.println("Insert triples to graph "+graphName);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
				

	@Override
	public void addNewPlaceToGraph(Place place) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		String content="";
		String sql = "sparql define input:storage "+ VirtuosoConstantUtil.placeQuadStorageURI + " \n"+
		" select ?p ?o "+
			"where{ "+
			  "<"+VirtuosoConstantUtil.placeObjectDataPrefix+place.getId()+">"+" ?p ?o."+
			"}";
		
		try{
			Connection conn = this.getSession().connection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();	
				ResultSetMetaData data = rs.getMetaData();				
				Node s = Node.createURI(VirtuosoConstantUtil.placeObjectDataPrefix+place.getId());
				while(rs.next()){
					ArrayList<String> strNode = new ArrayList();
					for(int i = 1;i <= data.getColumnCount();i++){
						 Node n=null;
						 Object obj = rs.getObject(i);
						 String format="";
						 if (obj instanceof Double)
							n = Node.createLiteral(obj.toString(), null, XSDDatatype.XSDdouble);
						 else if (obj instanceof Date)
							n = Node.createLiteral(obj.toString(), null, XSDDatatype.XSDdateTime);
						 else if (obj instanceof VirtuosoExtendedString) // String representing an IRI
						  {
							 	VirtuosoExtendedString vs = (VirtuosoExtendedString) obj;
					            n = Node.createURI(vs.str);
					            if(obj.toString().contains("/")||obj.toString().contains(":"))
					            	format="<" +n.getURI()+">";
					            else
					            	format="\"" +n.getURI()+"\"";
						  }						 
						 else
							 n = Node.createLiteral(obj.toString(), null, XSDDatatype.XSDstring);
						 if(n!=null&&n.toString().contains("^")){
							 int indx = n.toString().lastIndexOf("^");
							 format = n.toString().substring(0, indx+1)+"<"+n.toString().substring(indx+1)+">";
						 }						 
						 strNode.add(format);
				    }
					content+="<"+s.toString()+ "> " + strNode.get(0) + " " + strNode.get(1) + ". ";	
				}
				String sparql2sql = "sparql insert in graph <" + VirtuosoConstantUtil.sensormasherMetadataGraphURI + ">{";			
				sparql2sql+=content+"}";
				PreparedStatement ps = conn.prepareStatement(sparql2sql);
				boolean j = ps.execute(sparql2sql);
				if(j) System.out.println("Insert triples of one place into graph");
			}
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public void addExtraMetadataToGraph(HashMap<String, List<ArrayList>> extraPropertiesMap) {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
		String content="";		
		Object obj = null;Node n=null;
		String sparql2sql = "sparql insert in graph <" + VirtuosoConstantUtil.sensormasherMetadataGraphURI + ">{";
		try{
			Connection conn = this.getSession().connection();
			Iterator iter = extraPropertiesMap.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry me = (Map.Entry)iter.next();
				String id = me.getKey().toString();
				List l = (List)me.getValue();
				for(int i=0;i<l.size();i++){	
					ArrayList arr = (ArrayList)l.get(i);
					Node s = Node.createURI(id);
					Node p = Node.createURI(arr.get(0).toString());
					obj = arr.get(1);
					String o="";
					 if (obj instanceof Double)
						n = Node.createLiteral(obj.toString(), null, XSDDatatype.XSDdouble);
					 else if (obj instanceof Date)
						n = Node.createLiteral(obj.toString(), null, XSDDatatype.XSDdateTime);
					 else if (obj instanceof VirtuosoExtendedString) // String representing an IRI
					  	{
						    VirtuosoExtendedString vs = (VirtuosoExtendedString) obj;
				            n = Node.createURI(vs.str);
				            if(obj.toString().contains("/")||obj.toString().contains(":"))
				            	o="<" +n.getURI()+">";
				            else
				            	o="\"" +n.getURI()+"\"";
						  }
					 else 
						 n = Node.createLiteral(obj.toString(), null, XSDDatatype.XSDanyURI);
					 if(n!=null&&n.toString().contains("^")){
						 int indx = n.toString().lastIndexOf("^");
						 o = n.toString().substring(0, indx+1)+"<"+n.toString().substring(indx+1)+">";
					 }
					sparql2sql+="<"+s+"> <" + p + "> "+ o + ". ";
				}
			}			
			sparql2sql+="}";
			PreparedStatement ps = conn.prepareStatement(sparql2sql);
			boolean j = ps.execute(sparql2sql);
			if(j) System.out.println("Insert extra metadata triples of one sensor into graph");
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	/*------------------------- to create spatial index -------------------------*/
	@Override
	public void runSpatialIndex(){
		String sql = "DB.DBA.RDF_GEO_FILL()";
		try{
			Connection conn = this.getSession().connection();
			PreparedStatement ps = conn.prepareStatement(sql);
			boolean i = ps.execute(sql);
			if(i) System.out.println("create spatial index succed");
			conn.close();			
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getMarkersNearby(String sensorType, int level,
			String regionType, List<String> regions, double lat, double lng,
			double neLat, double neLng, double swLat, double swLng) {
		String sql = "";
		String manual = this.list2SQLManual(regionType, regions);
		
		double[] area = this.getCloseArea(lat, lng, neLat, neLng, swLat, swLng);
		neLat = area[0];
		neLng = area[1];
		swLat = area[2];
		swLng = area[3];

		if(level >= ConstantsUtil.marker_show_level_exactly_one){
			sql = "sparql select ?lat,?lng "+
				  	 " from <" + VirtuosoConstantUtil.sensormasherMetadataGraphURI+"> " +
				  	 	"where{ "+
				  	 		"?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
					        "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
					        "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?type."+
					        "?type <http://www.w3.org/2000/01/rdf-schema#label> \""+sensorType+"\"."+
					        "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat."+
					        "?place <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?lng."+ manual +						        
					        "filter(?lat = " + lat + " and ?lng = " + lng +")"+
					     "}";
		}else{
			double divide = MapZoomUtil.getDivide(level);
			sql = "sparql select ?lat,?lng "+
					  	 " from <" + VirtuosoConstantUtil.sensormasherMetadataGraphURI+"> " +
					  	 	"where{ "+
					  	 		"?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
						        "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
						        "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?type."+
						        "?type <http://www.w3.org/2000/01/rdf-schema#label> \""+sensorType+"\"."+
						        "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat."+
						        "?place <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?lng."+ manual +						        
						        this.lastManual2(divide, lat, lng, neLat, neLng, swLat, swLng)+
						     "}";
		}		

		List<Object[]> list = new ArrayList<Object[]>(40);
		try{
			Connection conn = this.getSession().connection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();		
			while(rs.next()){
				Object[] objArr = new Object[2];
				objArr[0]  = rs.getObject(1);				
				objArr[1]  = rs.getObject(2);				
				list.add(objArr);
			}
		}catch(Exception e){
			e.printStackTrace();
			return list;
		}
		
//		List<Object[]> list = this.getSession(true).createSQLQuery(sql).list();
		return list;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getFlightMarkersNearby(int level,
			String regionType, List<String> regions, double lat, double lng,
			double neLat, double neLng, double swLat, double swLng) {
		String sql = "";
		String manual = this.list2SQLManual(regionType, regions);
		
		double[] area = this.getCloseArea(lat, lng, neLat, neLng, swLat, swLng);
		neLat = area[0];
		neLng = area[1];
		swLat = area[2];
		swLng = area[3];

		if(level >= ConstantsUtil.marker_show_level_exactly_one){
			sql = "select la.value, lo.value from " + ConstantsUtil.databaseName
					+ "latitude la, " + ConstantsUtil.databaseName
					+ "longitude lo " + "where la.observationid=lo.observationid "+manual
					+ " and la.value = " + lat + " and lo.value = " + lng;
		}else{
			double divide = MapZoomUtil.getDivide(level);
			sql = "select la.value, lo.value from " + ConstantsUtil.databaseName
					+ "latitude la, " + ConstantsUtil.databaseName
					+ "longitude lo " + "where la.observationid=lo.observationid "+manual
					+ this.lastFlightManual2(divide, lat, lng, neLat, neLng, swLat, swLng);
		}		

		List<Object[]> list = this.getSession(true).createSQLQuery(sql).list();
		return list;
	}

	public List<Object[]> getMarkersShown(String sensorType, int level, String regionType, List<String> regions, 
			double neLat, double neLng, double swLat, double swLng,String userId) {
		List<Object[]> result = new ArrayList<Object[]>(20);
		String sql = "";
		String manual = this.list2SQLManual(regionType, regions);
		
		if(swLng == -180 && neLng != 180){
			neLng = 180;
		}
		
		List<double[]> coordinates = this.divideNeSwCoordinate(neLat, neLng, swLat, swLng);
		for(double[] coordinate : coordinates){
			neLat = coordinate[0];
			neLng = coordinate[1];
			swLat = coordinate[2];
			swLng = coordinate[3];

			double divide = MapZoomUtil.getDivide(level);			
			sql = "sparql select count(*),avg(?lat),avg(?lng) "+
					  "where{ "+
					  "{ "+
					  	 "select ?lat,?lng,bif:floor(?lat / "+ divide +
					  	 ") as ?latitute,bif:floor(?lng /"+divide+") as ?longitute "+
					  	 " from <" + VirtuosoConstantUtil.sensormasherMetadataGraphURI+"> " +
					  	 	"where{ "+
					  	 		"?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
						        "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
						        "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?type."+
						        "?type <http://www.w3.org/2000/01/rdf-schema#label> \""+sensorType+"\"."+
						        "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat."+
						        "?place <http://www.w3.org/2003/01/geo/wgs84_pos#long> ?lng."+ manual +
						        this.lastManual1(2, neLat, neLng, swLat, swLng)+     
						  "}"+ 
						"}";
			
//			List<Object[]> list = this.getSession(true).createSQLQuery(sql).list();		
			List<Object[]> list = new ArrayList<Object[]>(40);
			try{
				Connection conn = this.getSession().connection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery();		
				while(rs.next()){
					Object[] objArr = new Object[3];
					objArr[0]  = rs.getObject(1);
					if(rs.getInt(1)==0) continue;
					objArr[1]  = rs.getObject(2);
					objArr[2]  = rs.getObject(3);
					list.add(objArr);
				}
				result.addAll(list);
			}catch(Exception e){
				continue;
			}
		}		
		return result;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> getFlightMarkersShown(int level, String regionType, List<String> regions, 
			double neLat, double neLng, double swLat, double swLng,String userId) {
		List<Object[]> result = new ArrayList<Object[]>(20);
		String sql = "";
		String manual = this.list2SQLManual(regionType, regions);
		
		userId = userId.substring(userId.lastIndexOf("/")+1);
		
		if(swLng == -180 && neLng != 180){
			neLng = 180;
		}
		List<double[]> coordinates = this.divideNeSwCoordinate(neLat, neLng, swLat, swLng);
		for(double[] coordinate : coordinates){
			neLat = coordinate[0];
			neLng = coordinate[1];
			swLat = coordinate[2];
			swLng = coordinate[3];

			double divide = MapZoomUtil.getDivide(level);
			sql = "select count(*), avg(lat), avg(lng) from " +
			"(select la.value as lat, lo.value as lng, floor(la.value / "+ divide
			+ ") as latitute, floor(lo.value /" + divide
			+ ") as longitute from " + ConstantsUtil.databaseName
				+ "sensor s, " + ConstantsUtil.databaseName
				+ "observation obs, "+ ConstantsUtil.databaseName +"latitude la, "
				+ ConstantsUtil.databaseName +"longitude lo "
				+"where s.id = obs.sensorId and la.observationId=obs.id and lo.observationId=obs.id and " +
				"s.userId='"+userId+"' and s.sensorType = '"+ SensorTypeEnum.ADSBHub +"' "+manual
			+ this.lastFlightManual(2, neLat, neLng, swLat, swLng);

			List<Object[]> list = this.getSession(true).createSQLQuery(sql).list();
			result.addAll(list);
		}
		return result;
	}
	
	/**
	 * for the onMapClick event
	 */
	@SuppressWarnings("unchecked")
	@Override
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
			Connection conn = this.getSession().connection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					list.add(rs.getString(1));					
				}
				conn.close();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Sensor> getAllSensorWithSpecifiedLatLngSensorType(double lat, double lng, String sensorType) {
		List<Sensor> sensors = new ArrayList<Sensor>();
		PlaceManager placeManager = ServiceLocator.getPlaceManager();
		UserActiveManager userManager = ServiceLocator.getUserActiveManager();
		String sql = "sparql select ?sensor ?name ?source ?sourceType ?place ?userId "+
				" from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> " +
				"where{ "+
				   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
				   "?sensor <http://purl.org/net/provenance/ns#PerformedBy> ?source."+
				   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
				   "?sensor <http://www.w3.org/2000/01/rdf-schema#label> ?name."+
				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> \""+sensorType+"\"."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#isAddedBy> ?userId." +
				   "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> "+lat+";" +
				   "<http://www.w3.org/2003/01/geo/wgs84_pos#long> "+lng+"." +
				"}";			 
		try{
			Connection conn = this.getSession().connection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					Sensor sensor = new Sensor();
					sensor.setId(rs.getString("sensor"));
					sensor.setSource(rs.getString("source"));
					sensor.setName(rs.getString("name"));
					sensor.setSensorType(sensorType);
					sensor.setSourceType(rs.getString("sourceType"));
					Place place = placeManager.getPlaceWithPlaceId(rs.getString("place"));
					sensor.setPlace(place);
					User user = userManager.getUserWithUserId(rs.getString("userId"));
					sensor.setUser(user);
					sensors.add(sensor);
				}
				conn.close();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return sensors.size()>0?sensors:null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Sensor> getAllSensorWithSpecifiedCitySensorType(String city, String country, String sensorType) {
		List<Sensor> sensors = new ArrayList<Sensor>();
		PlaceManager placeManager = ServiceLocator.getPlaceManager();
		UserActiveManager userManager = ServiceLocator.getUserActiveManager();
		String sql = "sparql select ?sensor ?name ?source ?sourceType ?place ?userId "+
				" from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> " +
				"where{ "+
				   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
				   "?sensor <http://purl.org/net/provenance/ns#PerformedBy> ?source."+
				   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
				   "?sensor <http://www.w3.org/2000/01/rdf-schema#label> ?name."+
				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> \""+sensorType+"\"."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#isAddedBy> ?userId." +
				   "?place <http://lsm.deri.ie/ont/lsm.owl#is_in_city> ?cityId."+
				   "?cityId <http://www.w3.org/2000/01/rdf-schema#label> \"" + city +"\"."+
				   "?place <http://linkedgeodata.org/property/is_in_country> ?counId."+
				   "?counId <http://www.w3.org/2000/01/rdf-schema#label> \"" + country +"\"."+
				"}";			 
		try{
			Connection conn = this.getSession().connection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					Sensor sensor = new Sensor();
					sensor.setId(rs.getString("sensor"));
					sensor.setSource(rs.getString("source"));
					sensor.setName(rs.getString("name"));				
					sensor.setSourceType(rs.getString("sourceType"));
					sensor.setSensorType(sensorType);					
					Place place = placeManager.getPlaceWithPlaceId(rs.getString("place"));
					sensor.setPlace(place);
					User user = userManager.getUserWithUserId(rs.getString("userId"));
					sensor.setUser(user);
					sensors.add(sensor);
				}
				conn.close();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return sensors.size()>0?sensors:null;
	}
	
	/****************************************************************************************/
	private String list2SQLManual(String regionType, List<String> regions){
		if(regionType.trim().equals("all")){
			return "";
		}
		
		if(regions != null && regions.size() > 0){
//			String manual = " and (";
//			for(String region : regions){
//				manual += ("p." + regionType + " = '"+region+"' or " );
//			}
			
			String manual = " ?place <http://linkedgeodata.org/property/is_in_"+regionType+"> ?con."+
							 "?con <http://www.w3.org/2000/01/rdf-schema#label> ?"+regionType+"." +
							 "filter(";
			for(String region : regions){
				manual += ("?" + regionType + " = '"+region+"' or " );
			}
			
			int index_last_or = manual.lastIndexOf("or");
			manual = manual.substring(0, index_last_or);
			manual += ")";
			return manual;
		}
		
		return "";
	}
	
	private String lastFlightManual(int order, double neLat, double neLng, double swLat, double swLng){
		String manual = "";
		switch(order){
		case 1:
			manual = " and la.value < " + neLat + " and lo.value < " + neLng + " and la.value >= " + swLat + " and lo.value >= " + swLng;
			break;
		case 2:
			manual = " and la.value < " + neLat + " and lo.value < " + neLng + " and la.value >= " + swLat + " and lo.value >= " + swLng
					+ ") as temp group by latitute, longitute";
			break;
		}
		return manual;
	}
	
	private String lastManual1(int order, double neLat, double neLng, double swLat, double swLng){
		String manual = "";
		switch(order){
		case 1:
//			manual = " and p.lat < " + neLat + " and p.lng < " + neLng + " and p.lat >= " + swLat + " and p.lng >= " + swLng;
			manual = "filter(?lat < " + neLat + " && ?lng < " + neLng + " && ?lat >= " + swLat + " && ?lng >= " + swLng + ")}";
			break;
		case 2:
//			manual = " and p.lat < " + neLat + " and p.lng < " + neLng + " and p.lat >= " + swLat + " and p.lng >= " + swLng
//					+ ") as temp group by latitute, longitute";
			manual = "filter(?lat < " + neLat + " && ?lng < " + neLng + " && ?lat >= " + swLat + " && ?lng >= " + swLng + ")}" +
					" group by ?latitute ?longitute";
			break;
		}
		return manual;
	}
	
	private String lastFlightManual2(double divide, double lat, double lng, double neLat, double neLng, double swLat, double swLng){
		String manual = " and la.value < " + neLat + " and lo.value < " + neLng
		+ " and la.value > " + swLat + " and lo.value > " + swLng
		+ " and floor(la.value / "+divide+") = floor(" + (lat / divide)
		+ ") and floor(lo.value / "+divide+") = floor(" + (lng / divide) + ")";
		return manual;
	} 
	
	private String lastManual2(double divide, double lat, double lng, double neLat, double neLng, double swLat, double swLng){
		String manual = " filter(?lat < " + neLat + " and ?lng < " + neLng
				+ " and ?lat > " + swLat + " and ?lng > " + swLng
				+ " and bif:floor(?lat / "+divide+") = bif:floor(" + (lat / divide)
				+ ") and bif:floor(?lng / "+divide+") = bif:floor(" + (lng / divide) + "))";
		return manual;
	} 

	/**
	 * no matter the relation between neLng and swLng
	 * @param neLat
	 * @param neLng
	 * @param swLat
	 * @param swLng
	 * @return
	 */
	private List<double[]> divideNeSwCoordinate(double neLat, double neLng, double swLat, double swLng){
		List<double[]> list = new ArrayList<double[]>();
		
		if(neLng >= swLng){
			list = this.divideNeSwCoordinateInner(neLat, neLng, swLat, swLng);
		}else{
			double rightLngWidth = 180 - Math.abs(neLng);
			neLng = 180 + rightLngWidth;
			List<double[]> list_before1 = this.divideNeSwCoordinateInner(neLat, neLng, swLat, swLng);
			List<double[]> list_before2 = new ArrayList<double[]>();
			for(double[] arr : list_before1){
				double neLat_arr = arr[0];
				double neLng_arr = arr[1];
				if(neLng_arr > 180){
					neLng_arr = -(180 - (neLng_arr - 180));
				}
				double swLat_arr = arr[2];
				double swLng_arr = arr[3];
				if(swLng_arr > 180){
					swLng_arr = -(180 - (swLng_arr - 180));
				}
				
				list_before2.add(new double[]{neLat_arr, neLng_arr, swLat_arr, swLng_arr});
			}
			
			for(double[] arr : list_before2){
				double neLat_arr = arr[0];
				double neLng_arr = arr[1];
				double swLat_arr = arr[2];
				double swLng_arr = arr[3];
				if(swLng_arr > neLng_arr){
					if(swLng_arr < 180){
						list.add(new double[]{neLat_arr, 180, swLat_arr, swLng_arr});
					}
					
					if(neLng_arr > -180){
						list.add(new double[]{neLat_arr, neLng_arr, swLat_arr, -180});
					}
				}else if(swLng_arr < neLng_arr){
					list.add(new double[]{neLat_arr, neLng_arr, swLat_arr, swLng_arr});
				}// if neLng_arr == swLng_arr, then it won't be added to the list
			}
		}
		return list;
	}
	
	/**
	 * neLng >= swLng
	 * @param neLat
	 * @param neLng
	 * @param swLat
	 * @param swLng
	 * @return
	 */
	private List<double[]> divideNeSwCoordinateInner(double neLat, double neLng, double swLat, double swLng){
		List<double[]> list = new ArrayList<double[]>(8);
		if(neLng >= swLng){
			double swLat1 = swLat + (neLat - swLat) / 2;
			double[] lats = new double[]{swLat, swLat1, neLat};
			
			double lng_width = (neLng - swLng) / 4;
			double neLng1 = swLng + lng_width * 1;
			double neLng2 = swLng + lng_width * 2;
			double neLng3 = swLng + lng_width * 3;
			double[] lngs = new double[]{swLng, neLng1, neLng2, neLng3, neLng};
			
			for(int i=0;i<lats.length - 1;i++){
				for(int j=0;j<lngs.length - 1;j++){
					double[] arr = new double[4];
					arr[0] = lats[i + 1];
					arr[1] = lngs[j + 1];
					arr[2] = lats[i];
					arr[3] = lngs[j];
					list.add(arr);
				}
			}
		}
		return list;
	}
	
	private double[] getCloseArea(double lat, double lng, double neLat, double neLng, double swLat, double swLng){
		double[] result = new double[4];
		List<double[]> areas = this.divideNeSwCoordinate(neLat, neLng, swLat, swLng);
		for(double[] area : areas){
			double neLatA = area[0];
			double neLngA = area[1];
			double swLatA = area[2];
			double swLngA = area[3];
			if(lat <= neLatA && lng <= neLngA && lat > swLatA && lng > swLngA){
				result = area;
				break;
			}
		}
		return result;
	}
	
	/*search*/
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getAllSensorTypesWithOneSpecifiedLatLng(double lat,double lng) {
		List<String> sensorTypes = new ArrayList<String>();
		String sql = "sparql select distinct ?sensorType "+
				"from <"+VirtuosoConstantUtil.sensormasherMetadataGraphURI+"> "+
				"where{ "+
				   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+				   
				   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> ?sensorType."+				   
				   "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> "+lat+";" +
				   "<http://www.w3.org/2003/01/geo/wgs84_pos#long> "+lng+"." +
				"}";			 
		try{
			Connection conn = this.getSession().connection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					sensorTypes.add(rs.getString(1));					
				}
				conn.close();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return sensorTypes;
	}
	
		
	@Override
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
			Connection conn = this.getSession().connection();
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
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return l1.size()>0?lst:null;
	}
	
	@Override
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
			Connection conn = this.getSession().connection();
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
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return lst;
	}
			
	@Override
	@SuppressWarnings("unchecked")
	public ArrayList<List> getAllSpecifiedSensorAndLatLongAroundPlace(String sensorType,String spatialOperator,double lng,double lat,double distance){		
		ArrayList<List> lst = new ArrayList<List>(3);
		List<String> l1= new ArrayList<String>();
		List<Double> l2 = new ArrayList<Double>();
		List<Double> l3 = new ArrayList<Double>();
		String sql = "sparql select distinct(?sensor) ?lat ?long "+	
			" from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> " +
			"where {"+			  
			"?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
			"?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
			"?typeId <http://www.w3.org/2000/01/rdf-schema#label> \""+sensorType+"\"."+							
			"?sensor <"+ VirtuosoConstantUtil.sensorHasPlacePrefix+"> ?place. "+
		     "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat;" +
		     "<http://www.w3.org/2003/01/geo/wgs84_pos#long> ?long;" +
		     "geo:geometry ?geo."+
		     "filter (<bif:"+ spatialOperator +">(?geo,<bif:st_point>("+
		     			lng+","+lat+"),"+distance+"))." +
			"}";		
		try{
			Connection conn = this.getSession().connection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();		
			while(rs.next()){
				l1.add(rs.getString(1));
				l2.add(rs.getDouble(2));
				l3.add(rs.getDouble(3));
			}
			lst.add(l1);
			lst.add(l2);
			lst.add(l3);
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return lst;
	}
		
	
	@Override
	public String getNearestLocationIdFromGraph(String linkedgeoId) {
		// TODO Auto-generated method stub
		String nearbyId = "";		
		String sql = "sparql select distinct(?near) "+	
			" from <"+ VirtuosoConstantUtil.linkedgeodataGraphURI +"> " +
			"where {"+			  
			"<"+linkedgeoId+"> <"+ VirtuosoConstantUtil.lnkedgeodataSameAsPrefix +"> ?near." +		     
			"}";		
		try{
			Connection conn = this.getSession().connection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();		
			while(rs.next()){
				nearbyId = rs.getString(1);
			}
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return nearbyId;
	}
	
	@Override
	public String getNearestLocationWithSensorIdURL(String id){
		String nearbyId = "";		
		String sql = "sparql select distinct(?near) "+	
			" from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> \n" +
			"where {"+			  
			"<"+id+"> <"+ VirtuosoConstantUtil.sensorHasNearestLocation +"> ?near." +		     
			"}";		
		try{
			Connection conn = this.getSession().connection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();		
			while(rs.next()){
				nearbyId = rs.getString(1);
			}
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return nearbyId;
	}

	/*------------------------- to run sparql end point-------------------------*/

	@Override
	public List runSparqlEndPoint(String sparqlInput){	
		List sparqlResult = new ArrayList();
		List<List<String>> rowsList = new ArrayList<List<String>>();
		List<String> columnLabelsList = new ArrayList<String>();
		String sql = "sparql " + sparqlInput ;		
		try{
			Connection conn = this.getSession().connection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();	
			
			ResultSetMetaData rsMetaData = rs.getMetaData();
		    int numberOfColumns = rsMetaData.getColumnCount();
		    for (int i = 1; i < numberOfColumns + 1; i++) {
		        String columnName = rsMetaData.getColumnName(i);
		        columnLabelsList.add(columnName);
		      }		    
		    
			while(rs.next()){
				ArrayList memberResultArr = new ArrayList();
				for (String var : columnLabelsList){
					memberResultArr.add(rs.getString(var));
				}				
				rowsList.add(memberResultArr);				
			}
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		sparqlResult.add(columnLabelsList);
		sparqlResult.add(rowsList);		
		return sparqlResult;
	}
	
	//***************************************************************************************************/
	@Override
	public ArrayList getReadingTriplesOfObservation(String obsId){
		ArrayList<List> lst = new ArrayList<List>(3);
		
		String sql = "sparql select ?s ?p ?o"+
			" from <" + VirtuosoConstantUtil.sensormasherDataGraphURI +"> " +
			"where{ \n" +
			   " ?s <" + VirtuosoConstantUtil.dataIsPropertyOfObservationPrefix +"> <"+obsId+">. \n" +
			   " ?s ?p ?o."+
			  "}";
		try{
			Connection conn = this.getSession().connection();
			Statement st = conn.createStatement();			
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				ResultSetMetaData data = rs.getMetaData();
				Object obj = null;
				while(rs.next()){
					ArrayList<String> strNode = new ArrayList();
					Node n = null;
					for(int i = 1;i <= data.getColumnCount();i++){						 
						 obj = rs.getObject(i);
						 String format="";
						 if (obj instanceof VirtuosoExtendedString) // String representing an IRI
						  {
						    VirtuosoExtendedString vs = (VirtuosoExtendedString) obj;
				            n = Node.createURI(vs.str);
				            if(obj.toString().contains("/")||obj.toString().contains(":"))
				            	format="<" +n.getURI()+">";
				            else
				            	format="\"" +n.getURI()+"\"";
						  }
						 else if (obj instanceof Double)
							n = Node.createLiteral(obj.toString(), null, XSDDatatype.XSDdouble);
						 else if (obj instanceof Date)
							n = Node.createLiteral(obj.toString(), null, XSDDatatype.XSDdateTime);
						 else
							 n = Node.createLiteral(obj.toString(), null, XSDDatatype.XSDanyURI);
						 if(n!=null&&n.toString().contains("^")){
							 int indx = n.toString().lastIndexOf("^");
							 format = n.toString().substring(0, indx+1)+"<"+n.toString().substring(indx+1)+">";
						 }
						 strNode.add(format);
				    }
					if(strNode.get(1).contains(VirtuosoConstantUtil.dataIsPropertyOfObservationPrefix)){
						n = Node.createURI(obj.toString());
						strNode.set(2,"<"+n+">");
					}else if(strNode.get(1).contains("http://purl.oclc.org/NET/ssnx/ssn#observedBy")){
						n = Node.createURI(obj.toString());
						strNode.set(2,"<"+n+">");
					}
					lst.add(strNode);					
				}
			}
			conn.close();	
		}catch(Exception e){
			e.printStackTrace();
		}		
		return lst;
	}
	
		
	//**********************sensor table***************************/
	@Override
	public Sensor getSpecifiedSensorWithSource(String source){
		PlaceManager placeManager = ServiceLocator.getPlaceManager();
		UserActiveManager userManager = ServiceLocator.getUserActiveManager();
		Sensor sensor = null;
		String sql = "sparql select ?sensor ?name ?sensorType ?sourceType ?place ?userId "+
					"from <"+VirtuosoConstantUtil.sensormasherMetadataGraphURI+"> "+
					"where{ "+
					   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
					   "?sensor <http://purl.org/net/provenance/ns#PerformedBy> <"+source+">."+
					   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
					   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
					   "?sensor <http://www.w3.org/2000/01/rdf-schema#label> ?name."+
					   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> ?sensorType."+
					   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+
					   "?sensor <http://lsm.deri.ie/ont/lsm.owl#isAddedBy> ?userId."+
					"}";			 
			try{
				Connection conn = this.getSession().connection();
				Statement st = conn.createStatement();
				if(st.execute(sql)){
					ResultSet rs = st.getResultSet();
					while(rs.next()){
						sensor = new Sensor();
						sensor.setId(rs.getString("sensor"));
						sensor.setSensorType(rs.getString("sensorType"));
						sensor.setSource(source);
						sensor.setName(rs.getString("name"));
						sensor.setSourceType(rs.getString("sourceType"));
						Place place = placeManager.getPlaceWithPlaceId(rs.getString("place"));
						sensor.setPlace(place);
						User user = userManager.getUserWithUserId(rs.getString("userId"));
						sensor.setUser(user);
					}

					conn.close();	
				}
			}catch(Exception e){
				e.printStackTrace();
			}		
			return sensor;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Sensor getSpecifiedSensorWithPlaceId(String placeId){
		Sensor sensor = null;
		PlaceManager placeManager = ServiceLocator.getPlaceManager();
		UserActiveManager userManager = ServiceLocator.getUserActiveManager();
		String sql = "sparql select ?sensor ?name ?sensorType ?source ?sourceType ?place ?userId "+
				" from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> \n" +
				"where{ "+
				   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
				   "?sensor <http://purl.org/net/provenance/ns#PerformedBy> ?source."+
				   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> <"+placeId+">."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
				   "?sensor <http://www.w3.org/2000/01/rdf-schema#label> ?name."+
				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> ?sensorType."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#isAddedBy> ?userId." +				  
				"}";			 
		try{
			Connection conn = this.getSession().connection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					sensor = new Sensor();					
					sensor.setId(rs.getString("sensor"));
					sensor.setSource(rs.getString("source"));
					sensor.setSensorType(rs.getString("sensorType"));
					sensor.setSourceType(rs.getString("sourceType"));
					sensor.setName(rs.getString("name"));
					Place place = placeManager.getPlaceWithPlaceId(placeId);
					sensor.setPlace(place);
					User user = userManager.getUserWithUserId(rs.getString("userId"));
					sensor.setUser(user);					
				}
				conn.close();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return sensor;
	}
	
	@Override
	public Sensor getSpecifiedSensorWithSensorId(String id){		
		Sensor sensor = null;
		PlaceManager placeManager = ServiceLocator.getPlaceManager();
		UserActiveManager userManager = ServiceLocator.getUserActiveManager();
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
			Connection conn = this.getSession().connection();
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
//					User user = userManager.getUserWithUserId(rs.getString("userId"));
					User user = userManager.getUser("admin");
					sensor.setUser(user);					
				}
				conn.close();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return sensor;
	}
	
	@Override
	public Sensor getSpecifiedSensorWithLatLng(double lat, double lng) {		
		Sensor sensor = null;
		PlaceManager placeManager = ServiceLocator.getPlaceManager();
		UserActiveManager userManager = ServiceLocator.getUserActiveManager();
		String sql = "sparql select ?sensor ?name ?sensorType ?source ?sourceType ?place ?userId "+
				" from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> \n" +
				"where{ "+
				   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
				   "?sensor <http://purl.org/net/provenance/ns#PerformedBy> ?source."+
				   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
				   "?sensor <http://www.w3.org/2000/01/rdf-schema#label> ?name."+
				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> ?sensorType."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#isAddedBy> ?userId." +
				   "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> "+lat+";" +
				   "<http://www.w3.org/2003/01/geo/wgs84_pos#long> "+lng+"." +
				"}";			 
		try{
			Connection conn = this.getSession().connection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					sensor = new Sensor();
					sensor.setId(rs.getString("sensor"));
					sensor.setSource(rs.getString("source"));
					sensor.setSensorType(rs.getString("sensorType"));
					sensor.setSourceType(rs.getString("sourceType"));
					sensor.setName(rs.getString("name"));
					Place place = placeManager.getPlaceWithPlaceId(rs.getString("place"));
					sensor.setPlace(place);
					User user = userManager.getUserWithUserId(rs.getString("userId"));
					sensor.setUser(user);					
				}
				conn.close();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return sensor;
	}
	
	@Override
	public List<Sensor> getAllSensorForSpecfiedUserAndType(String sensorType,String id) {
		// TODO Auto-generated method stub
		String hql = "from Sensor s where s.sensorType ='"+ sensorType +"' and s.user.id = '" + id + "'";
		List<Sensor> sensors = this.getSession(true).createQuery(
				hql).list();
		return sensors.size()>0?sensors:null;
	}


	@Override
	@SuppressWarnings("unchecked")
	public List<String> getAllSensorSourceWithSpecifiedSensorType(String sensorType){
		String sql = "select l.source from "+ ConstantsUtil.databaseName
						+ "sensor l where l.sensorType = '" + sensorType + "'";
		List<String> list = this.getSession(true).createSQLQuery(sql).list();
		return list;
	}
	
	@Override
	public List<Sensor> getAllSensorWithSpecifiedSensorType(String sensorType) {
		// TODO Auto-generated method stub
		Sensor sensor = null;
		List<Sensor> sensors = new ArrayList<Sensor>();
		PlaceManager placeManager = ServiceLocator.getPlaceManager();
		UserActiveManager userManager = ServiceLocator.getUserActiveManager();
		String sql = "sparql select ?sensor ?name ?source ?sourceType ?place ?userId "+
				" from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> \n" +
				"where{ "+
				   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
				   "?sensor <http://purl.org/net/provenance/ns#PerformedBy> ?source."+
				   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
				   "?sensor <http://www.w3.org/2000/01/rdf-schema#label> ?name."+
				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> \"" + sensorType+"\"."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#isAddedBy> ?userId." +
				   "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat;" +
				   "<http://www.w3.org/2003/01/geo/wgs84_pos#long> ?lng." +
				"}";			 
		try{
			Connection conn = this.getSession().connection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					sensor = new Sensor();
					sensor.setId(rs.getString("sensor"));
					sensor.setSource(rs.getString("source"));
					sensor.setSensorType(sensorType);
					sensor.setSourceType(rs.getString("sourceType"));
					sensor.setName(rs.getString("name"));
					Place place = placeManager.getPlaceWithPlaceId(rs.getString("place"));
					sensor.setPlace(place);
					User user = userManager.getUserWithUserId(rs.getString("userId"));
					sensor.setUser(user);
					sensors.add(sensor);
				}
				conn.close();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return sensors;
	}
	
	@Override
	public List<Object[]> getAllCityWithSpecifiedSensorType(String sensorType) {
		String sql = "select distinct(p.city),p.country,p.lat,p.lng from "+ ConstantsUtil.databaseName
		+ "sensor s," + ConstantsUtil.databaseName +"place p where s.placeId = p.id and " +
			"s.sensorType = '" + sensorType + "' order by (p.city)";
		List<Object[]> list = this.getSession(true).createSQLQuery(sql).list();
		return list;
	}
	
	@Override
	public List<String> getAllSensorSourceWithSpecifiedSourceType(
			String sourceType) {
		String hql = "select s.source from Sensor s where s.sourceType = '" + sourceType + "'";
		List<String> list = this.getSession(true).createQuery(hql).list();
		return list;
	}
	
	@Override
	public List<Sensor> getAllSensorFromDB() {
		// TODO Auto-generated method stub
		String hql = "from Sensor ";				
		List<Sensor> sensors = this.getSession(true).createQuery(hql).list();
		return  sensors;
	}

	
	@Override
	public List<String> getAllSensorSourceWithSpecifiedSourceType(String sourceType, int from, int size) {
		String sql = "select top "+from+","+size+" s.source from " + ConstantsUtil.databaseName
			+ "sensor s where s.sourceType = '"+sourceType+"'";
		List<String> list = this.getSession(true).createSQLQuery(sql).list();
		return list;
	}
	
	@Override
	public List<String> getAllSensorSourceWithSpecifiedUser(String id,
			int from, int size) {
		// TODO Auto-generated method stub
		String sql = "select top "+from+","+size+" s.source from " + ConstantsUtil.databaseName
		+ "sensor s where s.userID = '"+ id +"'";
		List<String> list = this.getSession(true).createSQLQuery(sql).list();
		return list;
	}
	@Override
	@SuppressWarnings("unchecked")
	public List<String> getAllSensorSourceWithSpecifiedSensorTypeAndSourceType(
			String sensorType, String sourceType) {
		String hql = "select s.source from " + ConstantsUtil.databaseName
						+ "sensor s where s.sensorType = '" + sensorType + "' and s.sourceType = '"+ sourceType +"'";
		List<String> list = this.getSession(true).createQuery(hql).list();
		return list;
	}

	@Override
	public List<Sensor> getAllSensorWithSpecifiedSensorTypeAndUser(
			String sensorType, String userId) {
		// TODO Auto-generated method stub
		Sensor sensor = null;
		List<Sensor> sensors = new ArrayList<Sensor>();
		PlaceManager placeManager = ServiceLocator.getPlaceManager();
		UserActiveManager userManager = ServiceLocator.getUserActiveManager();
		String sql = "sparql select ?sensor ?name ?source ?sourceType ?place "+
				" from <"+ VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> \n" +
				"where{ "+
				   "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
				   "?sensor <http://purl.org/net/provenance/ns#PerformedBy> ?source."+
				   "?sensor <http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation> ?place."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?typeId."+
				   "?sensor <http://www.w3.org/2000/01/rdf-schema#label> ?name."+
				   "?typeId <http://www.w3.org/2000/01/rdf-schema#label> \"" + sensorType+"\"."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSourceType> ?sourceType."+
				   "?sensor <http://lsm.deri.ie/ont/lsm.owl#isAddedBy> <"+userId+">." +
				   "?place <http://www.w3.org/2003/01/geo/wgs84_pos#lat> ?lat;" +
				   "<http://www.w3.org/2003/01/geo/wgs84_pos#long> ?lng." +
				"}";			 
		try{
			Connection conn = this.getSession().connection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					sensor = new Sensor();
					sensor.setId(rs.getString("sensor"));
					sensor.setSource(rs.getString("source"));
					sensor.setSensorType(sensorType);
					sensor.setSourceType(rs.getString("sourceType"));
					sensor.setName(rs.getString("name"));
					Place place = placeManager.getPlaceWithPlaceId(rs.getString("place"));
					sensor.setPlace(place);
					User user = userManager.getUserWithUserId(userId);
					sensor.setUser(user);
					sensors.add(sensor);
				}
				conn.close();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return sensors;
	}
	
	@Override
	public List<Sensor> getAllSensorWithSpecifiedUser(String id) {
		// TODO Auto-generated method stub
		String hql = "from Sensor s where s.user.id = '"+ id +"'";
		List<Sensor> list = this.getSession(true).createQuery(hql).list();
		return list;
	}
	
	@Override
	public List<Sensor> getAllSensorWithSpecifiedSensorTypeAndUserAndSubName(
			String sensorType, String id, String subName) {
		// TODO Auto-generated method stub
		String hql = "from Sensor s where s.sensorType = '" + sensorType + "' and s.user.id = '"+ id +"' "+
						"and s.place.city like '"+subName+"%'";
		List<Sensor> list = this.getSession(true).createQuery(hql).list();
		return list;
	}
	
	@Override
	public int getCountForSpecifiedSourceType(String sourceType) {		
		String sql = "select count(*) from " + ConstantsUtil.databaseName
						+ "sensor s where s.sourceType = '"+sourceType+"'";
		int count = ((Integer)this.getSession(true).createSQLQuery(sql).uniqueResult()).intValue();		
		return count;
	}
	
	@Override
	public List<String> getAllSourceTypeWithSpecifiedSensorType(String sensorType){
		String sql = "select distinct(s.sourceType) from " + ConstantsUtil.databaseName
		+ "sensor s where s.sensorType = '" + sensorType +"'";
		List<String> list = this.getSession(true).createSQLQuery(sql).list();
		return list;
	}
	
	//**********************observation table***************************/
	@Override
	public Observation getNewestObservationForOneSensor(String sensorId) {
		Observation observation = null;
		String sql = "sparql select ?obs ?time ?foi"+
				" from <"+ VirtuosoConstantUtil.sensormasherDataGraphURI+"> "+
				"where{ "+
				   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy> <"+sensorId+">."+
				   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time." +
				   "?obs <http://purl.oclc.org/NET/ssnx/ssn#featureOfInterest> ?foi."+
				"}order by desc(?time) limit 1";			 
		try{
			Connection conn = this.getSession().connection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					observation = new Observation();
					observation.setId(rs.getString(1));
					observation.setSensorId(sensorId);
//					System.out.println(rs.getString(2));
					observation.setTimes(DateUtil.string2Date(rs.getString(2),"yyyy-MM-dd HH:mm:ss"));		
					observation.setFeatureOfInterest(rs.getString(3));
				}
				conn.close();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return  observation;
	}
	
	@Override
	public List<Observation> getObservationsForOnePlace(double lat, double lng,
			String type) {
		String hql = "from Observation obs where obs.sensor.sensorType ='" + type + "' and" +
		" obs.sensor.place.lat = " + lat + " and obs.sensor.place.lng = " + lng;		
		List<Observation> observations = this.getSession(true).createQuery(hql).list();
		return  observations;
	}

	@Override
	public List<Observation> getObservationsForOneSensor(String sensorId) {
		// TODO Auto-generated method stub		
		List<Observation> observations = new ArrayList<Observation>();
		String sql = "sparql select ?obs ?time ?foi"+
				" from <"+ VirtuosoConstantUtil.sensormasherDataGraphURI+"> "+
				"where{ "+
				   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy> <"+sensorId+">."+
				   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time." +
				   "?obs <http://purl.oclc.org/NET/ssnx/ssn#featureOfInterest> ?foi."+
				"}order by desc(?time)";			 
		try{
			Connection conn = this.getSession().connection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					Observation observation = new Observation();
					observation.setId(rs.getString(1));
					observation.setSensorId(sensorId);
					observation.setTimes(DateUtil.string2Date(rs.getString(2),"yyyy-MM-dd HH:mm:ss.SSS"));		
					observation.setFeatureOfInterest(rs.getString(3));
					observations.add(observation);
				}
				conn.close();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return  observations;
	}
	
	@Override
	public List<String> getObservationsForNonSpatialCriteria(
			String sensorId,String timeOper, String dateTime, String readingType,
			String oper, String value) {		
		// TODO Auto-generated method stub
		Date date = DateUtil.standardString2Date(dateTime);
		String sql;
		if(timeOper.equals("latest")){
			if(value!=null){
				sql= "sparql select ?obs"+
					" from <"+ VirtuosoConstantUtil.sensormasherDataGraphURI+"> "+
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
						" from <"+ VirtuosoConstantUtil.sensormasherDataGraphURI+"> "+
						"where{ "+
						   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observedBy>  <"+sensorId+">."+						   
						   "?obs <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+	
						"}order by desc(?time) limit 1";
			}
		}else{
			if(value!=null){
				sql= "sparql select ?obs"+
						" from <"+ VirtuosoConstantUtil.sensormasherDataGraphURI+"> "+
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
						" from <"+ VirtuosoConstantUtil.sensormasherDataGraphURI+"> "+
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
			Connection conn = this.getSession().connection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					observations.add(rs.getString(1));
				}
				conn.close();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return observations;
	}
	
	@Override
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
			Connection conn = this.getSession().connection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					observations.add(rs.getString(1));
				}
				conn.close();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return observations;
	}
	
	@Override
	public Observation getObservationForOneFlightWithLatLng(double lat,
			double lng){
		// TODO Auto-generated method stub
		String sql = "select obs.* from " + ConstantsUtil.databaseName + "observation obs, "+
				ConstantsUtil.databaseName + "latitude la, " +
				ConstantsUtil.databaseName + "longitude lo " +				
				" where obs.id = la.observationid and la.observationid = lo.observationid and " +
				" la.value = " + lat + " and lo.value = " + lng;
		Query query = this.getSession().createSQLQuery(sql).addEntity(Observation.class);		
		Observation observation = (Observation)query.uniqueResult();
		return observation;
	}
	/*----------------------process sensor properties table---------------------------------*/
		
	@Override
	public CallSign getNewestCallSignWithCallSignValue(String planeCallSign){
		String hql = "from CallSign w where w.value = '" + planeCallSign + "'";
		List<CallSign> spLst = this.getSession(true).createQuery(hql).list();
		return  spLst.size()>0?spLst.get(0):null;
	}
	@Override
	public FlightCIAO getNewestFlightCIAOWithCIAOValue(String ciao) {
		// TODO Auto-generated method stub
		String hql = "from FlightCIAO w where w.value = '" + ciao + "'";
		List<FlightCIAO> spLst = this.getSession(true).createQuery(hql).list();
		return  spLst.size()>0?spLst.get(0):null;
	}
	@Override
	public FlightCIAO getFlightCIAOForOneObservation(String obsId) {
		// TODO Auto-generated method stub
		String hql = "from FlightCIAO w where w.observation.id = '" + obsId + "'";
		List<FlightCIAO> spLst = this.getSession(true).createQuery(hql).list();
		return  spLst.size()>0?spLst.get(0):null;
	}

	@Override
	public Latitude getFlightLatitudeForOneObservation(String obsId) {
		// TODO Auto-generated method stub
		String hql = "from Latitude w where w.observation.id = '" + obsId + "'";
		List<Latitude> spLst = this.getSession(true).createQuery(hql).list();
		return  spLst.size()>0?spLst.get(0):null;
	}

	@Override
	public Longitude getFlightLongitudeForOneObservation(String obsId) {
		// TODO Auto-generated method stub
		String hql = "from Longitude w where w.observation.id = '" + obsId + "'";
		List<Longitude> spLst = this.getSession(true).createQuery(hql).list();
		return  spLst.size()>0?spLst.get(0):null;
	}
	
	@Override
	public Latitude getFlightLatitudeWithCurrentLat(double lat) {
		// TODO Auto-generated method stub
		String hql = "from Latitude w where w.value = " + lat;
		List<Latitude> spLst = this.getSession(true).createQuery(hql).list();
		return  spLst.size()>0?spLst.get(0):null;
	}
	
	@Override
	public CallSign getFlightCallSignWithOneLocation(double lat, double lng) {
		// TODO Auto-generated method stub
		String sql = "select cs.* from " + ConstantsUtil.databaseName + "callsign cs, "+
				ConstantsUtil.databaseName + "latitude la, " +
				ConstantsUtil.databaseName + "longitude lo " +				
				" where cs.observationId = la.observationid and la.observationid = lo.observationid and " +
				" la.value = " + lat + " and lo.value = " + lng;
		Query query = this.getSession().createSQLQuery(sql).addEntity(CallSign.class);		
		CallSign callSign = (CallSign)query.uniqueResult();
		return callSign;
	}
	@Override
	public CallSign getFlightCallSignForOneObservation(String obsId){
		String hql = "from CallSign cs where cs.observation.id = '" + obsId + "'";
		List<CallSign> spLst = this.getSession(true).createQuery(hql).list();
		return  spLst.size()>0?spLst.get(0):null;
	}
	@Override
	public Altitude getFlightAltitudeForOneObservation(String obsId) {
		// TODO Auto-generated method stub
		String hql = "from Altitude al where al.observation.id = '" + obsId + "'";
		List<Altitude> spLst = this.getSession(true).createQuery(hql).list();
		return  spLst.size()>0?spLst.get(0):null;
	}

	@Override
	public Speed getFlightSpeedForOneObservation(String obsId) {
		// TODO Auto-generated method stub
		String hql = "from Speed sp where sp.observation.id = '" + obsId + "'";
		List<Speed> spLst = this.getSession(true).createQuery(hql).list();
		return  spLst.size()>0?spLst.get(0):null;
	}
	
	@Override
	public Departure getFlightDepartureForOneObservation(String id) {
		// TODO Auto-generated method stub
		String hql = "from Departure sp where sp.observation.id = '" + id + "'";
		List<Departure> spLst = this.getSession(true).createQuery(hql).list();
		return  spLst.size()>0?spLst.get(0):null;
	}

	@Override
	public Destination getFlightDestinationForOneObservation(String id) {
		// TODO Auto-generated method stub
		String hql = "from Destination sp where sp.observation.id = '" + id + "'";
		List<Destination> spLst = this.getSession(true).createQuery(hql).list();
		return  spLst.size()>0?spLst.get(0):null;
	}
	
	@Override
	public List<Destination> getAllFlightDestinationsWithSpecifiedDestination(
			String airportCode) {
		// TODO Auto-generated method stub
		String hql = "from Destination sp where sp.value = '" + airportCode + "'";
		List<Destination> spLst = this.getSession(true).createQuery(hql).list();
		return  spLst.size()>0?spLst:null;
	}

	@Override
	public List<Departure> getAllFlightDepaturesWithSpecifiedDeparture(
			String airportCode) {
		// TODO Auto-generated method stub
		String hql = "from Departure sp where sp.value = '" + airportCode + "'";
		List<Departure> spLst = this.getSession(true).createQuery(hql).list();
		return  spLst.size()>0?spLst:null;
	}

	
	@Override
	public AbstractProperty getSpecifiedReadingForOneObservation(String observationId,	String readingType) {		
		// TODO Auto-generated method stub
		AbstractProperty abs = null;
		String sql = "sparql select ?value ?unit ?time"+
				" from <"+ VirtuosoConstantUtil.sensormasherDataGraphURI+"> "+
				"where{ "+
				   "?sign <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> <"+observationId+">."+
				   "?sign rdf:type ?type." +
				   "?sign <http://lsm.deri.ie/ont/lsm.owl#value> ?value."+
				   "?sign <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+
				   "OPTIONAL{?sign <http://lsm.deri.ie/ont/lsm.owl#unit> ?unit.}" +
				   " filter regex(?type,'"+readingType+"','i')"+
				"}";			 
		try{
			Connection conn = this.getSession().connection();
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
				conn.close();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return abs;
	}
	
	/*----------------------process sensor properties table---------------------------------*/
	@Override
	public List<String> getAllSensorType() {
		List<String> list = new ArrayList<String>();
		String sql = "sparql select distinct ?type "+
					  "from <" + VirtuosoConstantUtil.sensormasherMetadataGraphURI + "> " +
					  "where{ "+
						 " ?typeId <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://lsm.deri.ie/ont/lsm.owl#SensorType>."+
						 " ?typeId <http://www.w3.org/2000/01/rdf-schema#label> ?type."+
					   "}";
		try{
			Connection conn = this.getSession().connection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){					
					list.add(rs.getString(1));					
				}
				conn.close();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}

	//***********************************check*********************
	@Override
	public ArrayList<List> getAllSensorPropertiesForSpecifiedSensorType(String type) {
		// TODO Auto-generated method stub
		ArrayList<List> list = new ArrayList<List>(2);
		List list1 = new ArrayList<String>();
		List list2 = new ArrayList<String>();
		String sql = "sparql select ?x ?type "+
					  "from <"+VirtuosoConstantUtil.sensormasherMetadataGraphURI+"> "+
					  "where{ "+
						   "{"+
						     "select ?sensor "+
						     "where{ "+
						         "?sensor <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#Sensor>."+
						         "?sensor <http://lsm.deri.ie/ont/lsm.owl#hasSensorType> ?type."+
						   "?type <http://www.w3.org/2000/01/rdf-schema#label> \""+type+"\"."+
						    " }limit 1"+
						   "}"+
						   "?sensor <http://purl.oclc.org/NET/ssnx/ssn#observes> ?x."+
						   "?x rdf:type ?type."+
						"}";			 
		try{
			Connection conn = this.getSession().connection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){					
					list1.add(rs.getString(1));
//					list2.add(rs.getString(2).substring(rs.getString(2).lastIndexOf("#")+1));
					list2.add(rs.getString(2));
				}
				conn.close();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		list.add(list1);
		list.add(list2);
		return list;
	}
	
	@Override
	public String getWrapperIdWithSensorType(String sensorType) {
		// TODO Auto-generated method stub
		String sql = "select wrapperID from "+ ConstantsUtil.databaseName
		+ "systemsensortype s where s.sensorType = '"+sensorType+"'";
		List<String> list = this.getSession(true).createSQLQuery(sql).list();
		return list.size()>0?list.get(0):null;
	}

	@Override
	public List<String> getAllSensorSystemProperties() {
		// TODO Auto-generated method stub
		String sql = "select distinct(sensorProperties) from "+ ConstantsUtil.databaseName
		+ "systemsensortype s ";
		List<String> list = this.getSession(true).createSQLQuery(sql).list();
		
		ArrayList lstPro = new ArrayList<String>();
		for(String st:list){
			String[] temp = st.split(",");
			for(String j:temp){
				if(!lstPro.contains(j))
					lstPro.add(j);
			}
		}
		lstPro.add("DoubleValue");
		lstPro.add("StringValue");
		lstPro.add("IntegerValue");
		Collections.sort(lstPro);
		return lstPro.size()>0?lstPro:null;
	}

	@Override
	public ObservedProperty getClassURIForObservedPropertyID(String obsProId) {
		// TODO Auto-generated method stub
		String hql = "from ObservedProperty s where s.url = '" + obsProId + "'";
		List<ObservedProperty> spLst = this.getSession(true).createQuery(hql).list();
		return  spLst.size()>0?spLst.get(0):null;
	}

	@Override
	public List getAllObservedProperty() {
		// TODO Auto-generated method stub
		String hql = "from ObservedProperty s ";
		List<ObservedProperty> spLst = this.getSession(true).createQuery(hql).list();
		return  spLst.size()>0?spLst:null;
	}

	@Override
	public ObservedProperty getObservedPropertyWithClassURI(String classURI) {
		// TODO Auto-generated method stub
		String hql = "from ObservedProperty s where s.classURI = '" + classURI + "'";
		List<ObservedProperty> spLst = this.getSession(true).createQuery(hql).list();
		return  spLst.size()>0?spLst.get(0):null;
	}

	/*------------------------- process wrapper table -------------------------*/
	@Override
	public Wrapper getWrapperWithId(String wrapperId) {
		// TODO Auto-generated method stub
		String hql = "from Wrapper w where w.id = '" + wrapperId + "'";
		List<Wrapper> spLst = this.getSession(true).createQuery(hql).list();
		return  spLst.size()>0?spLst.get(0):null;
	}
	
	@Override
	public SensorSourceType getXMLMapForSpecifiedSourceType(String sourceType) {
		// TODO Auto-generated method stub
		String hql = "from SensorSourceType s where s.sourceType = '"+sourceType+"'";
		List<SensorSourceType> list =this.getSession(true).createQuery(hql).list();
		return list.size()>0?list.get(0):null;
	}

	@Override
	public List<String> getAllUserDefinedSourceTypeWithSpecifiedSensorType(
			String type) {
		String sql = "select distinct(s.sourceType) from " + ConstantsUtil.databaseName
			+ "sensorsourcetype s where s.sensorType = '" + type +"'";
		List<String> list = this.getSession(true).createSQLQuery(sql).list();
		return list;		
	}

	@Override
	public List<SensorSourceType> getAllSensorSourceTypeForSpecifiedSensorType(
			String type) {
		// TODO Auto-generated method stub
		String hql = "from SensorSourceType s where s.sensorType = '"+type+"'";
		List<SensorSourceType> list =this.getSession(true).createQuery(hql).list();
		return list.size()>0?list:null;
	}

	/*------------------------- processing for railwaystation -------------------------*/
	@Override
	public Sensor getRailwayStationWithSpecifiedStationCode(String stationCode) {
		// TODO Auto-generated method stub
		String hql = "from Sensor s where s.code = '"+stationCode+"'";
		List<Sensor> list =this.getSession(true).createQuery(hql).list();
		return list.size()>0?list.get(0):null;		
	}

	/*------------------------- process flight route table -------------------------*/
	@Override
	public FlightRoute getFlightRouteForOneFlight(String callsign) {
		// TODO Auto-generated method stub
		String hql = "from FlightRoute fl where fl.CallSign = '"+callsign+"'";
		List<FlightRoute> list =this.getSession(true).createQuery(hql).list();
		return list.size()>0?list.get(0):null;
	}

	@Override
	public Airport getAirportWithAirportCode(String code) {
		// TODO Auto-generated method stub
		String hql = "from Airport ap where ap.AirportCode = '"+code+"'";
		List<Airport> list =this.getSession(true).createQuery(hql).list();
		return list.size()>0?list.get(0):null;
	}

	@Override
	public List<Airport> getAllAirports() {
		// TODO Auto-generated method stub
		String hql = "from Airport ap ";
		List<Airport> list =this.getSession(true).createQuery(hql).list();
		return list;
	}
	
	@Override
	public List<FlightRoute> getAllFlightsWithSpecifiedDeparture(
			String airportCode) {
		// TODO Auto-generated method stub
		String hql = "from FlightRoute fl where fl.Departure = '"+airportCode+"'";
		List<FlightRoute> list =this.getSession(true).createQuery(hql).list();
		return list.size()>0?list:null;
	}

	@Override
	public List<FlightRoute> getAllFlightsWithSpecifiedArrival(
			String airportCode) {
		// TODO Auto-generated method stub
		String hql = "from FlightRoute fl where fl.Destination = '"+airportCode+"'";
		List<FlightRoute> list =this.getSession(true).createQuery(hql).list();
		return list.size()>0?list:null;
	}

	/*------------------------- process user feed table -------------------------*/
	@Override
	public String getQueryWithFeedId(String feedId) {
		// TODO Auto-generated method stub
		String query="";
		String sql = "select uf.query from " + ConstantsUtil.databaseName
				+ "userfeed uf where uf.id = '" + feedId +"'";
		try{
			Connection conn = this.getSession().connection();
			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			while(rs.next())
				query = rs.getString(1);
		}catch(SQLException e){
			e.printStackTrace();
		}
		return query;
	}

	@Override
	public UserFeed getUserFeedWithFeedId(String feedId) {
		// TODO Auto-generated method stub
		String hql = "from UserFeed ap where id = '"+feedId+"'";
		List<UserFeed> list =this.getSession(true).createQuery(hql).list();
		return list.size()>0?list.get(0):null;
	}

	@Override
	public List<ArrayList> getReadingDataOfObservation(String observationId) {		
		// TODO Auto-generated method stub
		List<ArrayList> list = new ArrayList<ArrayList>();
		String sql = "sparql select ?type ?value ?unit ?name"+
				" from <"+ VirtuosoConstantUtil.sensormasherDataGraphURI+"> "+
				"where{ "+
				   "?sign <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> <"+observationId+">."+
				   "?sign rdf:type ?type." +
				   "?sign <http://lsm.deri.ie/ont/lsm.owl#value> ?value."+
				   "OPTIONAL{?sign <http://lsm.deri.ie/ont/lsm.owl#unit> ?unit.}" +				  
				   "OPTIONAL{?sign <http://www.w3.org/2000/01/rdf-schema#label> ?name.}"+
				"}";			 
		try{
			Connection conn = this.getSession().connection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					ArrayList<String> arr = new ArrayList<String>();
					arr.add(rs.getString("type"));
					arr.add(rs.getString("value"));
					arr.add(rs.getString("unit"));
					arr.add(rs.getString("name"));
					list.add(arr);
				}
				conn.close();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return list;
	}

	@Override
	public Sensor getSpecifiedSensorWithObservationId(String obsId) {
		// TODO Auto-generated method stub 
		Sensor sensor = null;
		PlaceManager placeManager = ServiceLocator.getPlaceManager();
		UserActiveManager userManager = ServiceLocator.getUserActiveManager();
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
			Connection conn = this.getSession().connection();
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
					User user = userManager.getUserWithUserId(rs.getString("userId"));
					sensor.setUser(user);
				}
				conn.close();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return sensor;
	}

	@Override
	public AbstractProperty getSpecifiedCosmReadingForOneObservation(String obsId, String label) {
		// TODO Auto-generated method stub
		AbstractProperty abs = null;
		String sql = "sparql select ?value ?unit ?time"+
				" from <"+ VirtuosoConstantUtil.sensormasherDataGraphURI+"> "+
				"where{ "+
				   "?sign <http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf> <"+obsId+">."+
				   "?sign rdf:type <http://purl.oclc.org/NET/ssnx/ssn#Property>." +
				   "?sign <http://lsm.deri.ie/ont/lsm.owl#value> ?value."+
				   "?sign <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> ?time."+
				   "?sign <http://www.w3.org/2000/01/rdf-schema#label> \"" + label + "\".\n" +
				   "OPTIONAL{?sign <http://lsm.deri.ie/ont/lsm.owl#unit> ?unit.}" +				   
				"}";			 
		try{
			Connection conn = this.getSession().connection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					abs = new AbstractProperty();
					abs.setValue(rs.getString("value"));
					abs.setUnit(rs.getString("unit"));
					abs.setValue(rs.getString("value"));
					abs.setPropertyName(label);
					abs.setObservedURL(obsId);	
					abs.setTimes(DateUtil.string2Date(rs.getString("time"),"yyyy-MM-dd HH:mm:ss.SSS"));
				}
				conn.close();	
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return abs;
	}









	


}