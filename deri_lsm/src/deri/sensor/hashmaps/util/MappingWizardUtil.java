package deri.sensor.hashmaps.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class MappingWizardUtil {	
	public static HashMap<String,List> SensorMetaAnnotate = new LinkedHashMap<String, List>();
	public static HashMap<String,List> PlaceMetaAnnotate = new LinkedHashMap<String, List>();
	public static HashMap<String,List> WrapperMetaAnnotate = new LinkedHashMap<String, List>();
	public static HashMap<String,HashMap> type2SensorProAnnotate = new HashMap<String, HashMap>();
	
	static{
		initialize();
	}

	private static void initialize() {
		// TODO Auto-generated method stub
		initializeSensorMetaAnnotate();
		initializePlaceMetaAnnotate();
		initializedWrapperMetaAnnotate();
	}

	
	/**
	 *  O: uneditable
	 *  1: editable
	 *  2: will not be chosen for mapping
	 */			
	
	@SuppressWarnings("unchecked")
	public static void initializedWrapperMetaAnnotate(){
		List lst = new ArrayList();
				
		lst.add("http://purl.oclc.org/NET/ssnx/ssn#hasInputDataFormat");	
		lst.add(1);
		lst.add("Information about input data format for wrapper ");
		lst.add("please input data");
		WrapperMetaAnnotate.put("inputDataFormat", lst);
		
		lst = new ArrayList();
		lst.add("http://purl.oclc.org/NET/ssnx/ssn#hasTimeUpdateInterval");		
		lst.add(1);
		lst.add("interal time for wrapper to update");
		lst.add("please input data");
		WrapperMetaAnnotate.put("timeUpdateInterval",lst);
		
		lst = new ArrayList();
		lst.add("http://purl.oclc.org/NET/ssnx/ssn#hasSlideWindowSize");	
		lst.add(1);
		lst.add("Slide window size of each updates");
		lst.add("please input data");
		WrapperMetaAnnotate.put("slideWindowSize", lst);
		
		lst = new ArrayList();
		lst.add("http://purl.oclc.org/NET/ssnx/ssn#hasURLSource");	
		lst.add(1);
		lst.add("Source URL for receiving data");
		lst.add("please input data");
		WrapperMetaAnnotate.put("dataURL", lst);		
	}
	@SuppressWarnings("unchecked")
	public static void initializeSensorMetaAnnotate(){
		List lst = new ArrayList();
		lst.add("http://purl.oclc.org/NET/ssnx/ssn#hasName");
		lst.add(1);
		lst.add("Name of sensor");
		lst.add("please input data");
		SensorMetaAnnotate.put("name",lst);
		
		lst = new ArrayList();
		lst.add("http://purl.oclc.org/NET/ssnx/ssn#hasSensorType");
		lst.add(0);
		lst.add("Type of sensor. For example: weather, traffic, snow, radar...");
		lst.add("please input data");
		SensorMetaAnnotate.put("sensorType", lst);
		
		lst = new ArrayList();
		lst.add("http://purl.oclc.org/NET/ssnx/ssn#hasSource");
		lst.add(0);
		lst.add("The source service which publish sensor data");
		lst.add("please input data");
		SensorMetaAnnotate.put("source", lst);
		
		lst = new ArrayList();
		lst.add("http://purl.oclc.org/NET/ssnx/ssn#hasSourceType");
		lst.add(0);
		lst.add("Specified source type service");
		lst.add("please input data");
		SensorMetaAnnotate.put("sourceType", lst);
		
		lst = new ArrayList();
		lst.add("http://purl.oclc.org/NET/ssnx/ssn#hasPlace");
		lst.add(0);
		lst.add("This is id of sensor position");
		lst.add("please input data");
		//SensorMetaAnnotate.put("placeID", lst);
		
		lst = new ArrayList();
		lst.add("http://purl.oclc.org/NET/ssnx/ssn#hasInfor");
		lst.add(1);
		lst.add("information about sensor");
		lst.add("please input data");
		SensorMetaAnnotate.put("infor", lst);
		
		lst = new ArrayList();
		lst.add("http://purl.oclc.org/NET/ssnx/ssn#hasTimes");
		lst.add(1);
		lst.add("Time when sensor was activated");
		lst.add("please input data");
		SensorMetaAnnotate.put("times", lst);
	}
	
	@SuppressWarnings("unchecked")
	public static void initializePlaceMetaAnnotate(){
		List lst = new ArrayList();
		lst.add("http://purl.oclc.org/NET/ssnx/ssn#hasWoeid");
		lst.add(1);
		lst.add("Woeid for place");
		lst.add("please input data");
		PlaceMetaAnnotate.put("woeid",lst);
		
		lst = new ArrayList();
		lst.add("http://purl.oclc.org/NET/ssnx/ssn#hasGeonameid");
		lst.add(1);
		lst.add("Geoname id for place");
		lst.add("please input data");
		PlaceMetaAnnotate.put("geonameid", lst);
		
		lst = new ArrayList();
		lst.add("http://purl.oclc.org/NET/ssnx/ssn#hasZipcode");
		lst.add(1);
		lst.add("Zipcode for place");
		lst.add("please input data");
		PlaceMetaAnnotate.put("zipcode", lst);
		
		lst = new ArrayList();
		lst.add("http://purl.oclc.org/NET/ssnx/ssn#hasStreet");
		lst.add(1);
		lst.add("Street name where sensor is located");
		lst.add("please input data");
		PlaceMetaAnnotate.put("street", lst);
		
		lst = new ArrayList();
		lst.add("http://purl.oclc.org/NET/ssnx/ssn#hasCity");
		lst.add(1);
		lst.add("City name where sensor is located");
		lst.add("please input data");
		PlaceMetaAnnotate.put("city", lst);
		
		lst = new ArrayList();
		lst.add("http://purl.oclc.org/NET/ssnx/ssn#hasProvince");
		lst.add(1);
		lst.add("Province where sensor is located");
		lst.add("please input data");
		PlaceMetaAnnotate.put("province", lst);
		
		lst = new ArrayList();
		lst.add("http://purl.oclc.org/NET/ssnx/ssn#hasCountry");
		lst.add(1);
		lst.add("Conutry where sensor is located");
		lst.add("please input data");
		PlaceMetaAnnotate.put("country", lst);
		
		lst = new ArrayList();
		lst.add("http://purl.oclc.org/NET/ssnx/ssn#hasContinent");
		lst.add(1);
		lst.add("Continent where sensor is located");
		lst.add("please input data");
		PlaceMetaAnnotate.put("continent", lst);
		
//		lst = new ArrayList();
//		lst.add("http://purl.oclc.org/NET/ssnx/ssn#hasInfor");
//		lst.add(1);
//		lst.add("information about place");
//		lst.add("please input data");
//		PlaceMetaAnnotate.put("infor", lst);
		
//		lst = new ArrayList();
//		lst.add("http://purl.oclc.org/NET/ssnx/ssn#hasAuthor");
//		lst.add(1);
//		lst.add("information about user");
//		lst.add("please input data");
//		PlaceMetaAnnotate.put("author", lst);
		
		lst = new ArrayList();
		lst.add("http://www.w3.org/2003/01/geo/wgs84_pos#long");
		lst.add(1);
		lst.add("longitue of sensor location");
		lst.add("please input data");
		PlaceMetaAnnotate.put("lng", lst);
		
		lst = new ArrayList();
		lst.add("http://www.w3.org/2003/01/geo/wgs84_pos#lat");
		lst.add(1);
		lst.add("latitue of sensor location");
		lst.add("please input data");
		PlaceMetaAnnotate.put("lat", lst);
		
//		lst = new ArrayList();
//		lst.add("http://purl.oclc.org/NET/ssnx/ssn#hasTimes");
//		lst.add(1);
//		lst.add("Time when sensor was located");
//		lst.add("please input data");
//		PlaceMetaAnnotate.put("times", lst);
	}
}
