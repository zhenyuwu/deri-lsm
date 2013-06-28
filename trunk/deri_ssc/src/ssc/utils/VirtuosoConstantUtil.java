package ssc.utils;

import java.util.HashMap;

public class VirtuosoConstantUtil {
	public final static String sensormasherMetadataGraphURI = "http://lsm.deri.ie/sensormeta#";
	public final static String sensormasherDataGraphURI = "http://lsm.deri.ie/sensordata#";
	
	public final static String sensormasherDataQuadURI = "<http://lsm.deri.ie/data/quad_storage/default>";
	public final static String sensormasherOntologyURI = "http://lsm.deri.ie/ont/lsm.owl#";
	public final static String SSNOntolotyURI ="http://purl.oclc.org/NET/ssnx/ssn#";
	
	public final static String sensorQuadStorageURI = "<http://lsm.deri.ie/sensor/quad_storage/default>"; 
	public final static String placeQuadStorageURI = "<http://lsm.deri.ie/place/quad_storage/default>";
	public final static String placeQuadGraphURI = "http://lsm.deri.ie/resource/place";
	
	public final static String sensorObservesReadingPrefix="http://purl.oclc.org/NET/ssnx/ssn#observes";
	public final static String sensorObjectDataPrefix ="http://lsm.deri.ie/resource/";
	public final static String sensorHasObservationPrefix = "http://lsm.deri.ie/ont/lsm.owl#hasObservation";
	public final static String sensorAddbyUserPrefix = "http://lsm.deri.ie/ont/lsm.owl#isAddedBy";
	public final static String sensorHasPlacePrefix = "http://www.loa-cnr.it/ontologies/DUL.owl#hasLocation";
	public final static String sensorHasWrapperPrefix = "http://lsm.deri.ie/ont/lsm.owl#hasWrapper";
	
	public final static String placeObjectDataPrefix ="http://lsm.deri.ie/resource/";
	
	public final static String observationObjectDataPrefix="http://lsm.deri.ie/data#";	
	public final static String observationIsObservedBySensorPrefix = "http://purl.oclc.org/NET/ssnx/ssn#observedBy";
	public final static String observationHasObservationResult = "http://purl.oclc.org/NET/ssnx/ssn#observationResult";
	
	public final static String dataIsPropertyOfObservationPrefix = "http://lsm.deri.ie/ont/lsm.owl#isObservedPropertyOf";	
	public final static String dataIsPropertyOfFeatureOfInterestPrefix="http://purl.oclc.org/NET/ssnx/ssn#isPropertyOf";
	
	public static HashMap<String,String> sensorType2Datasource = new HashMap<String, String>();
	public static HashMap<String,String> sensorType2tableName = new HashMap<String, String>();
	public static HashMap<String,String> sensorType2className = new HashMap<String, String>();
	
	public final static String linkedgeodataGraphURI = "http://lsm.deri.ie/linkedgeodata.com";
	public final static String lnkedgeodataSameAsPrefix ="http://www.w3.org/2002/07/owl#sameAs";
	public final static String sensorHasNearestLocation = "http://lsm.deri.ie/ont/lsm.owl#nearest";
	
	public final static String provenanceCreateby = "http://purl.org/net/provenance/ns#CreatedBy";
	public final static String provenancePerformedAt = "http://purl.org/net/provenance/ns#PerformedAt";
	public final static String provenancePerformedBy = "http://purl.org/net/provenance/ns#PerformedBy";
	
	public final static String FeatureOfInterest = "http://purl.oclc.org/NET/ssnx/ssn#FeatureOfInterest";
	public final static String ArrivingTrainAtStation = "http://lsm.deri.ie/ont/lsm.owl#ArrivingTrainAtStation";
	public final static String atStation = "http://lsm.deri.ie/ont/lsm.owl#atStation";
	public final static String RailwayStation = "http://lsm.deri.ie/ont/lsm.owl#RailwayStation";
	static{
		initilalize_sensorType2tableName();
	}

	
	private static void initilalize_sensorType2tableName(){
		sensorType2tableName.put("weather","weather");		
		sensorType2tableName.put("webcam", "linkvalue");
		sensorType2tableName.put("satellite", "linkvalue");
		sensorType2tableName.put("radar", "linkvalue");
		sensorType2tableName.put("flood", "linkvalue");
		sensorType2tableName.put("snowfall", "snowfall");
		sensorType2tableName.put("snowdepth", "snowdepth");
		sensorType2tableName.put("traffic", "traffic");
		sensorType2tableName.put("roadactivity", "roadactivity");
	}
	
	private static void initilalize_sensorType2className(){
		sensorType2className.put("weather","Weather");		
		sensorType2className.put("webcam", "linkvalue");
		sensorType2className.put("satellite", "WebcamSnapShot");
		sensorType2className.put("radar", "WebcamSnapShot");
		sensorType2className.put("flood", "WebcamSnapShot");
		sensorType2className.put("snowfall", "SnowFall");
		sensorType2className.put("snowdepth", "SnowDepth");
		sensorType2className.put("traffic", "WebcamSnapShot");
		sensorType2className.put("roadactivity", "RoadActivity");
	}
	
}
