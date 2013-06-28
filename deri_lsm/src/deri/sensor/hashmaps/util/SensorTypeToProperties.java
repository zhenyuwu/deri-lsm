package deri.sensor.hashmaps.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import deri.sensor.database.ServiceLocator;
import deri.sensor.manager.SensorManager;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class SensorTypeToProperties {
	public static Map<String,List<String>> sensorTypeToTableProperties = new HashMap<String,List<String>>();
	public static Map<String,String> sensorTypeToWrapperDataFormat= new HashMap<String, String>();
	
	static{
		initialize_sensorTypeToTableProperties();
		initialize_sensorTypeToWrapperDataFormat();
	}

	private static void initialize_sensorTypeToTableProperties() {
		// TODO Auto-generated method stub
		initialized_LinkType();
		initialized_RoadActivitysType();
		initialized_SealevelType();
		initialized_SnowDepthType();
		initialized_SnowFallType();
		initialized_TrafficType();
		initialized_WeatherType();
		initialized_BikeHireType();
		initialized_RailwayStationType();
		initialized_ADSBHubType();
	}
	
	private static void initialized_BikeHireType() {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();		
		list.add("BikeAvailable");		
		list.add("BikeDockAvailable");
		sensorTypeToTableProperties.put("bikehire", list);
	}

	private static void initialize_sensorTypeToWrapperDataFormat() {
		// TODO Auto-generated method stub
		sensorTypeToWrapperDataFormat.put("weather", "XML/JSON");
		sensorTypeToWrapperDataFormat.put("traffic", "XML");
		sensorTypeToWrapperDataFormat.put("roadactivity", "XML");
		sensorTypeToWrapperDataFormat.put("snowfall", "Text");
		sensorTypeToWrapperDataFormat.put("snowdepth", "Text");
		sensorTypeToWrapperDataFormat.put("webcam", "XML");
		sensorTypeToWrapperDataFormat.put("radar", "XML");
		sensorTypeToWrapperDataFormat.put("satellite", "XML");
		sensorTypeToWrapperDataFormat.put("sealevel", "Text");
		sensorTypeToWrapperDataFormat.put("cosm", "XML");
		sensorTypeToWrapperDataFormat.put("bikehire", "XML");
		sensorTypeToWrapperDataFormat.put("railwaystation", "XML");
		sensorTypeToWrapperDataFormat.put("ADSBHub", "Stream");
	}

	private static void initialized_WeatherType(){
		List<String> list = new ArrayList<String>();
		list.add("Temperature");
		list.add("WindChill");
		list.add("WindSpeed");
		list.add("Picture");
		list.add("AtmosphereHumidity");
		list.add("AtmospherePressure");
		list.add("AtmosphereVisibility");
		list.add("Status");
		list.add("Direction");
		sensorTypeToTableProperties.put("weather", list);
	}
	
	private static void initialized_TrafficType(){
		List<String> list = new ArrayList<String>();
		list.add("Information");
		list.add("Picture");		
		list.add("Direction");
		sensorTypeToTableProperties.put("traffic", list);
	}
	
	private static void initialized_RoadActivitysType(){
		List<String> list = new ArrayList<String>();
		list.add("Name");
		list.add("Category");
		list.add("DateTimes");
		list.add("DistrictNumber");
		list.add("Description");
		list.add("Status");
		list.add("UserContact");
		list.add("Picture");		
		list.add("Direction");
		sensorTypeToTableProperties.put("roadactivity", list);
	}
	
	private static void initialized_SnowFallType(){
		List<String> list = new ArrayList<String>();
		list.add("Amount");
		list.add("Elevation");		
		list.add("Station");
		list.add("Duration");
		sensorTypeToTableProperties.put("snowfall", list);
		
	}
	
	private static void initialized_SnowDepthType(){
		List<String> list = new ArrayList<String>();
		list.add("Amount");
		list.add("Elevation");		
		list.add("Station");		
		sensorTypeToTableProperties.put("snowdepth", list);
	}
	
	private static void initialized_LinkType(){
		List<String> list = new ArrayList<String>();
		list.add("Information");
		list.add("Picture");
		sensorTypeToTableProperties.put("webcam", list);
		sensorTypeToTableProperties.put("radar", list);
		sensorTypeToTableProperties.put("satellite", list);		
		sensorTypeToTableProperties.put("cosm", list);
	}
	
	private static void initialized_SealevelType(){
		List<String> list = new ArrayList<String>();
		list.add("MissingDays");
		list.add("MeanLevelValue");
		list.add("SealevelTime");
		sensorTypeToTableProperties.put("sealevel", list);
	}
	
		
	private static void initialized_RailwayStationType(){
		List<String> list = new ArrayList<String>();
		list.add("StationPlatform");
		list.add("TrainNumber");
		list.add("SecondToStation");
		list.add("TimeToStation");
		list.add("Status");
		list.add("Destination");
		sensorTypeToTableProperties.put("railwaystation", list);
	}
	
	private static void initialized_ADSBHubType(){
		List<String> list = new ArrayList<String>();
		list.add("FlightCIAO");
		list.add("CallSign");
		list.add("Latitude");
		list.add("Longitude");		
		list.add("Altitude");
		list.add("Speed");
		list.add("Departure");
		list.add("Transit");
		list.add("Destination");
		sensorTypeToTableProperties.put("ADSBHub", list);
	}	
		
}
