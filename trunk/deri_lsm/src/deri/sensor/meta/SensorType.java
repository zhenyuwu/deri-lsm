package deri.sensor.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import deri.sensor.database.ServiceLocator;
import deri.sensor.manager.SensorManager;
import deri.sensor.utils.ConstantsUtil;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class SensorType {
	public static String all="all";
	private static List<String> typeList = new ArrayList();
	private static List<String> sysTypeList = new ArrayList();
	private static List<String> sourceTypeList = new ArrayList();
	private static SensorManager sensorManager;
	
	private static Map<String,String> sensor_icon_map = new HashMap<String,String>();
	static{
		initialize_typeList();
		initialize_sysTypeList();
		initialize_sensor_icon_map();
	}

	
	private static void initialize_typeList() {
		// TODO Auto-generated method stub
		sensorManager = ServiceLocator.getSensorManager();
		typeList = sensorManager.getAllSensorType();		
		typeList.add("all");
	}

	private static void initialize_sysTypeList() {
		// TODO Auto-generated method stub
		sysTypeList.add("weather");
		sysTypeList.add("webcam");
		sysTypeList.add("satellite");
		sysTypeList.add("snowdepth");
		sysTypeList.add("snowfall");
		sysTypeList.add("cosm");
		sysTypeList.add("radar");
		sysTypeList.add("roadactivity");
		sysTypeList.add("traffic");
		sysTypeList.add("sealevel");
		sysTypeList.add("bikehire");
		sysTypeList.add("railwaystation");
		sysTypeList.add("airport");
		sysTypeList.add("ADSBHub");		
		sysTypeList.add("all");
	}

	private static void initialize_sensor_icon_map(){
		sensor_icon_map.put("weather", ConstantsUtil.gmarker_icon_weather);		
		sensor_icon_map.put("webcam", ConstantsUtil.gmarker_icon_webcam);
		sensor_icon_map.put("satellite", ConstantsUtil.gmarker_icon_satellite);
		sensor_icon_map.put("snowdepth", ConstantsUtil.gmarker_icon_snowdepth);
		sensor_icon_map.put("snowfall", ConstantsUtil.gmarker_icon_snowfall);
		sensor_icon_map.put("cosm", ConstantsUtil.gmarker_icon_cosm);
		sensor_icon_map.put("sealevel", ConstantsUtil.gmarker_icon_sealevel);
		sensor_icon_map.put("radar", ConstantsUtil.gmarker_icon_radar);
		sensor_icon_map.put("traffic", ConstantsUtil.gmarker_icon_trafficcam);
		sensor_icon_map.put("roadactivity", ConstantsUtil.gmarker_icon_roadactivity);
		sensor_icon_map.put("bikehire", ConstantsUtil.gmarker_icon_bikehire);
		sensor_icon_map.put("railwaystation", ConstantsUtil.gmarker_icon_railwaystation);		
		sensor_icon_map.put("airport", ConstantsUtil.gmarker_icon_airport);
		sensor_icon_map.put("ADSBHub", ConstantsUtil.gmarker_icon_plane);
		sensor_icon_map.put("other", ConstantsUtil.gmarker_icon_usersensor);
		sensor_icon_map.put("all", ConstantsUtil.gmarker_icon_all);
	}
	
	public static boolean contains(String test) {
		return typeList.contains(test);
	}
	
	public static String getIcon(String type){
		return sensor_icon_map.get(type);
	}
	
	public static List<String> getTypeList(){		
		return typeList;
	}
	
	public static List<String> getSystemTypeList(){
		return sysTypeList;
	}
	
	public static List<String> getSourceTypeList(String sensorType){
		return sensorManager.getAllUserDefinedSourceTypeWithSpecifiedSensorType(sensorType);
	}
	
//	public static List<SensorType> strList2SensorTypeList(List<String> sensorTypesStrs){
//		if(sensorTypesStrs == null || sensorTypesStrs.size() == 0){
//			return null;
//		}
//		List<SensorType> sensorType = new ArrayList<SensorType>();
//		for(String sensorTypesStr : sensorTypesStrs){
//			SensorType type = getSensorType(sensorTypesStr);
//			if(type != null){
//				sensorType.add(type);
//			}
//		}
//		return sensorType;
//	}
}