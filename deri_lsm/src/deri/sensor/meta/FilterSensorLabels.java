package deri.sensor.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import deri.sensor.javabeans.WeatherSignShown;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class FilterSensorLabels {
	public static String weatherLabel = "Weather";
	public static String webcamLabel = "WebCam";
	public static String satelliteLabel = "Satellite";
	public static String snowdepthLabel = "SnowDepth";
	public static String snowfallLabel = "SnowFall";
	public static String cosmLabel = "Cosm";
	public static String sealevelLabel = "SeaLevel";
	public static String radarLabel = "Radar";
	public static String trafficcamLabel = "Traffic";
	public static String roadactivityLabel = "Road Activity";
	public static String bikehireLabel = "Bike Station";
	public static String railwaystationLabel = "Railway";
	public static String adsbHubLabel = "Flight";
	public static String airportLabel = "Airport";

	//public static List<String> labels_first_level = new ArrayList<String>();
	public static Map<String,String> firstLabel2SensorType = new  HashMap<String,String>();
	public static Map<String,String> sensorType2FirstLabel = new  HashMap<String,String>();
	
	//public static Map<String,String> toolbarLabel2SensorLabel = new HashMap<String, String>();
	
	static{
//		initialize_labels_first_level();
		initialize_label2SensorType();
		initialize_sensorType2Label();
	}
	
//	private static void initialize_labels_first_level(){
//		
//		labels_first_level.add(floodLabel);
//		labels_first_level.add(trafficcamLabel);
//		labels_first_level.add(satelliteLabel);
//		labels_first_level.add(snowdepthLabel);
//		labels_first_level.add(snowfallLabel);
//		labels_first_level.add(sealevelLabel);			
//		labels_first_level.add(radarLabel);
//		labels_first_level.add(weatherLabel);
//		labels_first_level.add(webcamLabel);
//		labels_first_level.add(roadactivityLabel);
//		labels_first_level.add(bikehireLabel);
//		labels_first_level.add(railwaystationLabel);
//		labels_first_level.add(adsbHubLabel);
//	}
	
	
	private static void initialize_sensorType2Label(){
//		for(String label : firstLabel2SensorType.keySet()){
//			sensorType2FirstLabel.put(firstLabel2SensorType.get(label), label);
//		}
		sensorType2FirstLabel.put(SensorTypeEnum.weather.toString(),FilterSensorLabels.weatherLabel);
		sensorType2FirstLabel.put(SensorTypeEnum.traffic.toString(),FilterSensorLabels.trafficcamLabel);
		sensorType2FirstLabel.put(SensorTypeEnum.roadactivity.toString(),FilterSensorLabels.roadactivityLabel);
		sensorType2FirstLabel.put(SensorTypeEnum.webcam.toString(),FilterSensorLabels.webcamLabel);
		sensorType2FirstLabel.put(SensorTypeEnum.satellite.toString(),FilterSensorLabels.satelliteLabel);
		sensorType2FirstLabel.put(SensorTypeEnum.snowdepth.toString(),FilterSensorLabels.snowdepthLabel);
		sensorType2FirstLabel.put(SensorTypeEnum.snowfall.toString(),FilterSensorLabels.snowfallLabel);
		sensorType2FirstLabel.put(SensorTypeEnum.cosm.toString(),FilterSensorLabels.cosmLabel);
		sensorType2FirstLabel.put(SensorTypeEnum.sealevel.toString(),FilterSensorLabels.sealevelLabel);
		sensorType2FirstLabel.put(SensorTypeEnum.radar.toString(),FilterSensorLabels.radarLabel);
		sensorType2FirstLabel.put(SensorTypeEnum.bikehire.toString(),FilterSensorLabels.bikehireLabel);
		sensorType2FirstLabel.put(SensorTypeEnum.railwaystation.toString(),FilterSensorLabels.railwaystationLabel);
		sensorType2FirstLabel.put(SensorTypeEnum.airport.toString(),FilterSensorLabels.airportLabel);
		sensorType2FirstLabel.put(SensorTypeEnum.ADSBHub.toString(),FilterSensorLabels.adsbHubLabel);
		sensorType2FirstLabel.put("all","All");
		
	}

	private static void initialize_label2SensorType(){
		firstLabel2SensorType.put(FilterSensorLabels.weatherLabel, "weather");
		firstLabel2SensorType.put(FilterSensorLabels.trafficcamLabel, "traffic");
		firstLabel2SensorType.put(FilterSensorLabels.roadactivityLabel, "roadactivity");
		firstLabel2SensorType.put(FilterSensorLabels.webcamLabel, "webcam");
		firstLabel2SensorType.put(FilterSensorLabels.satelliteLabel, "satellite");
		firstLabel2SensorType.put(FilterSensorLabels.snowdepthLabel, "snowdepth");
		firstLabel2SensorType.put(FilterSensorLabels.snowfallLabel, "snowfall");
		firstLabel2SensorType.put(FilterSensorLabels.cosmLabel, "cosm");
		firstLabel2SensorType.put(FilterSensorLabels.sealevelLabel, "sealevel");
		firstLabel2SensorType.put(FilterSensorLabels.radarLabel, "radar");
		firstLabel2SensorType.put(FilterSensorLabels.bikehireLabel, "bikehire");
		firstLabel2SensorType.put(FilterSensorLabels.railwaystationLabel, "railwaystation");
		firstLabel2SensorType.put(FilterSensorLabels.airportLabel, "airport");
		firstLabel2SensorType.put(FilterSensorLabels.adsbHubLabel, "ADSBHub");
		firstLabel2SensorType.put("All", "all");
	}
	
	public static String getSensorType(String label){
		return firstLabel2SensorType.get(label);
	}
	
	public static String getLabelForSensorType(String type){
		return sensorType2FirstLabel.get(type);
	}

}
