package deri.sensor.hashmaps.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import deri.sensor.meta.SensorTypeEnum;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class FieldSearchConstainsUtil {
	public static Map<String,List<String>> sensorFieldLabel = new HashMap<String,List<String>>();
	public static List<String> weatherFieldLabel = new ArrayList<String>();
	public static List<String> snowFieldLabel = new ArrayList<String>();
	public static List<String> seaLevelFieldLabel = new ArrayList<String>();
	public static List<String> roadActivityFieldLabel = new ArrayList<String>();
	public static List<String> railwayStationFieldLabel = new ArrayList<String>();
	
		
	public static List<String>spatialOperator = new ArrayList();
	public static Map<String,String>valueSearchOperator = new LinkedHashMap<String, String>();
	public static Map<String,String>timeOperator = new LinkedHashMap<String, String>();
	public static Map<String,List<String>> roadActivityField2ValueOperator = new LinkedHashMap<String, List<String>>();	
	public static List<String> roadActivityCategory = new ArrayList<String>();
	public static List<String> roadActivityState = new ArrayList<String>();
	public static List<String> roadActivityDirection = new ArrayList<String>();
	
	static{
		initialize_weather_field();
		initialize_snow_field();
		initialize_sea_field();
		initialize_sensor_field();
		initialize_roadActivity_field();
		initialize_railwaystation_field();
		
		initialize_spatialOperator();
		initialize_valueSearchOperator();
		initialize_timeOperator();
		initialize_roadActivityCategory();
		initialize_roadActivityState();
		initialize_roadActivityDirection();
		initialize_roadActivityField2ValueOperator();
	}
	
	public static void initialize_weather_field(){
		weatherFieldLabel.add("AirTemperature");
		weatherFieldLabel.add("State");
		weatherFieldLabel.add("Wind Speed");
		weatherFieldLabel.add("Wind Chill");
	}
	
	private static void initialize_railwaystation_field() {
		// TODO Auto-generated method stub
		railwayStationFieldLabel.add("TrainNumber");
		railwayStationFieldLabel.add("Destination");
	}

	private static void initialize_roadActivityField2ValueOperator() {
		// TODO Auto-generated method stub
		roadActivityField2ValueOperator.put("Category", roadActivityCategory);
		roadActivityField2ValueOperator.put("Direction", roadActivityDirection);
		roadActivityField2ValueOperator.put("Status", roadActivityState);
	}

	private static void initialize_roadActivity_field() {
		// TODO Auto-generated method stub
		roadActivityFieldLabel.add("Category");
		roadActivityFieldLabel.add("Direction");
		roadActivityFieldLabel.add("Status");		
	}

	private static void initialize_roadActivityState() {
		// TODO Auto-generated method stub
		roadActivityState.add("Open");
		roadActivityState.add("Restricted");
		roadActivityState.add("Closed");
	}
	
	private static void initialize_roadActivityDirection() {
		// TODO Auto-generated method stub
		roadActivityDirection.add("EASTBOUND");
		roadActivityDirection.add("NORTHBOUND");
		roadActivityDirection.add("SOUTHBOUND");
		roadActivityDirection.add("WESTBOUND");
		roadActivityDirection.add("BOTH DIRECTIONS");
	}

	private static void initialize_roadActivityCategory() {
		// TODO Auto-generated method stub
		roadActivityCategory.add("Accident");
		roadActivityCategory.add("Roadwork - Planned");
		roadActivityCategory.add("Roadwork - Unplanned");
		roadActivityCategory.add("Flooding");
		roadActivityCategory.add("Snow/ice");
		roadActivityCategory.add("Debris");
		roadActivityCategory.add("Disable vehicle");
		roadActivityCategory.add("Other");
	}

	public static void initialize_snow_field(){
		snowFieldLabel.add("Elevation");
		snowFieldLabel.add("Amount");		
	}
	
	public static void initialize_sea_field(){
		seaLevelFieldLabel.add("mean_level_value");
		seaLevelFieldLabel.add("missing_days");
	}
	
	public static void initialize_sensor_field(){
		sensorFieldLabel.put(SensorTypeEnum.weather.toString(), weatherFieldLabel);
		sensorFieldLabel.put(SensorTypeEnum.snowdepth.toString(), snowFieldLabel);
		sensorFieldLabel.put(SensorTypeEnum.snowfall.toString(), snowFieldLabel);
		sensorFieldLabel.put(SensorTypeEnum.sealevel.toString(), seaLevelFieldLabel);
		sensorFieldLabel.put(SensorTypeEnum.roadactivity.toString(), roadActivityFieldLabel);
		sensorFieldLabel.put(SensorTypeEnum.railwaystation.toString(), railwayStationFieldLabel);
	}
	
	public static void initialize_spatialOperator(){
		spatialOperator.add("st_intersects");
		spatialOperator.add("st_within");
		spatialOperator.add("st_contains");
	}
	
	public static void initialize_valueSearchOperator(){
		valueSearchOperator.put("equal","=");
		valueSearchOperator.put("does not equal","!=");
		valueSearchOperator.put("greater than",">");
		valueSearchOperator.put("greater than or equal",">=");
		valueSearchOperator.put("less than","<");
		valueSearchOperator.put("less than or equal","<=");
	}
	
	public static void initialize_timeOperator(){
		//timeOperator.put("newest","newest");
		timeOperator.put("equal","=");		
		timeOperator.put("sooner than or equal","<=");
		
	}
}
