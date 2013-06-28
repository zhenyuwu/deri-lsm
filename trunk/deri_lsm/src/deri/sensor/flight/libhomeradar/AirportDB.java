package deri.sensor.flight.libhomeradar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.VirtuosoConstantUtil;
import deri.sensor.javabeans.Airport;
import deri.sensor.javabeans.Place;
import deri.sensor.javabeans.Sensor;
import deri.sensor.javabeans.User;
import deri.sensor.manager.PlaceManager;
import deri.sensor.manager.SensorManager;
import deri.sensor.manager.UserActiveManager;
import deri.sensor.utils.ConstantsUtil;
import deri.sensor.wrapper.TriplesDataRetriever;
import deri.sensor.wrapper.WebServiceURLRetriever;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class AirportDB {
	static String airportURL = "http://www.libhomeradar.org/assets/lhr.api?n=usernamec&p=password=airport&r=";
	
	public static void initializeAirportSensor(){
		SensorManager sensorManager = ServiceLocator.getSensorManager();
        UserActiveManager userManager = ServiceLocator.getUserActiveManager();
        User user = userManager.getUser("admin");
        PlaceManager placeManager = ServiceLocator.getPlaceManager();
        String triples = "";
        List<Airport> lstAirport = sensorManager.getAllAirports();
        for(Airport airport:lstAirport){
        	String[] temp = airport.getCity().split(","); 
        	String name = temp[0].trim();
        	String city = temp.length>1?temp[1].trim():temp[0].trim();
        	
        	Sensor sensor = new Sensor();
        	sensor.setId("http://lsm.deri.ie/resource/"+System.nanoTime());
			sensor.setName(name+"("+airport.getAirportCode()+")");
			sensor.setSensorType("airport");
			sensor.setSource(airportURL+airport.getAirportCode());
			sensor.setSourceType("libhomeradar");
			sensor.setInfor("");
			sensor.setTimes(new Date());
			sensor.setAuthor("admin");
//			sensor.setCode(airport.getAirportCode());
			sensor.setUser(user);
			Place existPlace = placeManager.getPlaceWithSpecifiedLatLng(airport.getLatitude(), airport.getLongitude());
			if(existPlace!=null)
				sensor.setPlace(existPlace);
			else{
				Place pl = new Place();
		    	pl.setWoeid("");
		    	pl.setId("http://lsm.deri.ie/resource/"+System.nanoTime());
		    	pl.setGeonameid("");
		    	pl.setZipcode("");
		    	pl.setStreet("");
		    	pl.setCity(city);
		    	pl.setProvince("");
		    	pl.setCountry(airport.getCountry());
		    	pl.setContinent("");
		    	pl.setLat(airport.getLatitude());
		    	pl.setLng(airport.getLongitude());
		    	pl.setInfor("airport location");
		    	pl.setAuthor("admin");
		    	pl.setTimes(new Date());
		    	pl.setAgree(0);
		    	pl.setDisagree(0);	
		    	sensor.setPlace(pl);
			}
			triples = TriplesDataRetriever.getSensorTripleMetadata(sensor);
//			System.out.println(triples);
			sensorManager.insertTriplesToGraph(VirtuosoConstantUtil.sensormasherMetadataGraphURI, triples);
        }
	}
	
	public static Connection getVirtuosoConneciton(){
		try{
		    String username = "username";
		    String password = "password";
			Class.forName("virtuoso.jdbc4.Driver");
			Connection conn = DriverManager.getConnection(ConstantsUtil.urlHost,username,password);
			System.out.println("Load successfull");
			return conn;
		}catch (Exception e){
			System.out.println("Connection failed"+ e.getMessage());
			return null;
		}
	}
	
	public static void testFlightSinal() throws SQLException{
		
		Connection connection = getVirtuosoConneciton();
		String query = "select value from " + ConstantsUtil.databaseName
				+ "flightciao";
		ArrayList<String> para= new ArrayList();		
		Statement statement = connection.createStatement();
		java.sql.ResultSet resultSet = statement.executeQuery(query);
		String xml;
		FlightLastSeenLocationXMLParser flightParser = new FlightLastSeenLocationXMLParser();
		while(resultSet.next()){		
			//if(resultSet.getString(1).equals("3C4893"))
				flightParser.updateFlightInformation(resultSet.getString(1));
		}
		connection.close();
	}
	
	public static void main(String[] agrs) throws SQLException{
//		AirportDB.testFlightSinal();
		AirportDB.initializeAirportSensor();
	}
}
