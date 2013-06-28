package deri.sensor.wrapper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import deri.sensor.flight.libhomeradar.FlightLastSeenLocationXMLParser;
import deri.sensor.utils.ConstantsUtil;
import deri.sensor.utils.ThreadUtil;

public class LibhomeradarWrapper extends Thread{
	public Connection getVirtuosoConneciton(){
		try{
		    String username = "dba";
		    String password = "dba";
			Class.forName("virtuoso.jdbc4.Driver");
			Connection conn = DriverManager.getConnection(ConstantsUtil.urlHost,username,password);
			System.out.println("Load successfull");
			return conn;
		}catch (Exception e){
			System.out.println("Connection failed"+ e.getMessage());
			return null;
		}
	}
	
	public void run(){
		try{
			for(;;){
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
				System.out.println("Libhomeradar update is finished");
				connection.close();
				ThreadUtil.sleepForMinutes(15);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] agrs){
		LibhomeradarWrapper libhome = new LibhomeradarWrapper();
		libhome.start();
	}
}
