package ssc.sensor.boston;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import ssc.beans.Place;
import ssc.beans.Sensor;
import ssc.beans.SensorManager;
import ssc.beans.User;
import ssc.beans.UserActiveManager;
import ssc.controller.UserController;
import ssc.utils.VirtuosoConstantUtil;
import ssc.wrapper.TriplesDataRetriever;

public class BostonStationDB {
	public static void initializedBostonStation(){
		
	}
	
static String airportURL = "http://www.libhomeradar.org/assets/lhr.api?n=DLPhuoc&p=GF3Seq6z&t=airport&r=";
	
	public static void initializeSubwaySensor(){
		Connection conn;
        UserActiveManager userManager = new UserActiveManager();
        User user = userManager.getUser("admin");
        SensorManager sensorManager = new SensorManager();
		String sql = "select latitude,longitude,station,line from SSC.DBA.bostonstation";
		try{
			conn = UserController.getConnectionPool().getConnection();
			Statement st = conn.createStatement();
			if(st.execute(sql)){
				ResultSet rs = st.getResultSet();
				while(rs.next()){
					String triples = "";					
					String source = "http://developer.mbta.com/lib/rthr/red.json";
					if(rs.getString("line").equals("O"))
						source = "http://developer.mbta.com/lib/rthr/orange.json";
					else if(rs.getString("line").equals("R"))
						source = "http://developer.mbta.com/lib/rthr/red.json";
					else if(rs.getString("line").equals("B"))
						source = "http://developer.mbta.com/lib/rthr/blue.json";
		        	Sensor sensor = new Sensor();
		        	sensor.setId("http://lsm.deri.ie/resource/"+System.nanoTime());
					sensor.setName(rs.getString("station"));
					sensor.setSensorType("railwaystation");
					sensor.setSource(source);
					sensor.setSourceType("bostonrailway");
					sensor.setInfor("");
					sensor.setTimes(new Date());
					sensor.setAuthor("admin");
					sensor.setUser(user);
					
					Place pl = new Place();
			    	pl.setWoeid("");
			    	pl.setId("http://lsm.deri.ie/resource/"+System.nanoTime());
			    	pl.setGeonameid("");
			    	pl.setZipcode("");
			    	pl.setStreet("");
			    	pl.setCity("Boston");
			    	pl.setProvince("Massachusetts");
			    	pl.setCountry("USA");
			    	pl.setContinent("America");
			    	pl.setLat(rs.getDouble("latitude"));
			    	pl.setLng(rs.getDouble("longitude"));
			    	pl.setInfor("subway location");
			    	pl.setAuthor("admin");
			    	pl.setTimes(new Date());
			    	sensor.setPlace(pl);
			    	
			    	triples = TriplesDataRetriever.getSensorTripleMetadata(sensor);
					System.out.println(triples);
					sensorManager.insertTriplesToGraph(VirtuosoConstantUtil.sensormasherMetadataGraphURI, triples);	
				}
			}
			UserController.attemptClose(st);
			UserController.attemptClose(conn);
		}catch(Exception e){
			e.printStackTrace();	
		}		
			
      
        
        
	}	
	
	public static void main(String[] agrs) throws SQLException{
//		AirportDB.testFlightSinal();
		BostonStationDB.initializeSubwaySensor();
	}
}
