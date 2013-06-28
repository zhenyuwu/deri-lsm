package deri.sensor.manager;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import deri.sensor.javabeans.*;

/**
 * @author Hoan Nguyen Mau Quoc
 */

public interface SensorManager{
	/*------------------------- for the map to show markers -------------------------*/
	public List<Object[]> getMarkersShown(String sensorType, int level, String regionType, List<String> regions, double neLat, double neLng, double swLat, double swLng,String userId);
	public List<Object[]> getFlightMarkersShown(int level,
			String regionType, List<String> region, double neLat, double neLng,
			double swLat, double swLng, String userId);
	public List<Object[]> getMarkersNearby(String sensorType, int level, String regionType, List<String> regions, double lat, double lng, double neLat, double neLng, double swLat, double swLng);
	public List<Object[]> getFlightMarkersNearby(int level, String regionType, List<String> regions, double lat, double lng, double neLat, double neLng, double swLat, double swLng);
	public List<String> getAllSourcesWithSpecifiedLatLngSensorType(double lat, double lng, String sensorType);
	public List<Sensor> getAllSensorWithSpecifiedLatLngSensorType(double lat, double lng, String sensorType);
	
	
	/*------------------------- add delete update object -------------------------*/
	public void addObject(Object object);
	public void deleteObject(Object object);
	public void updateObject(Object object);
	public void deleteAllObservationsForSpecifiedSensor(Sensor sensor);
	public void deleteAllObservationsWithTimeCriteria(Sensor sensor,
			String dateOperator, Date fromDate, Date toDate);
	public void excuteSQL(String string);
	public void excuteSQLUpdate(String sql);
		
	/*------------------------- to help the search module -------------------------*/
	public List<String> getAllSensorTypesWithOneSpecifiedLatLng(double lat, double lng);
	
	/*------------------------- to create spatial index -------------------------*/
	public void runSpatialIndex();

	public ArrayList<List> getAllSensorHasLatLongWithSpatialCriteria(String sensorType,String spatialOperator,double lng,double lat,double distance);
	public ArrayList<List> getAllSpecifiedSensorAndLatLongAroundPlace(String sensorType,String spatialOperator,double lng,double lat,double distance);
	public ArrayList<List> getAllSensorHasLatLongWithSpatialCriteria(String spatialOperator,double lng,double lat,double distance);
	
	/*------------------------- to process the sensor metadata graph-------------------------*/
	public void addExtraMetadataToGraph(HashMap<String, List<ArrayList>> extraPropertiesMap);
	public String getNearestLocationIdFromGraph(String linkedgeoId);
	public String getNearestLocationWithSensorIdURL(String id);
	public void insertTriplesToGraph(String graphName,String triples);
	
	/*------------------------- to run sparql end point-------------------------*/
	public List runSparqlEndPoint(String sparqlInput);
	
	/*----------------------process sensormasher data quad storage---------------------------------*/
	public ArrayList<String> getReadingTriplesOfObservation(String obsId);
	public void addNewPlaceToGraph(Place place);
	/*------------------------- process the sensor table -------------------------*/
	public Sensor getSpecifiedSensorWithSource(String source);
	public Sensor getSpecifiedSensorWithPlaceId(String id);
	public Sensor getSpecifiedSensorWithSensorId(String id);
	public List<Sensor> getAllSensorFromDB();
	public List<Sensor> getAllSensorWithSpecifiedSensorType(String sensorType);
	public List<Sensor> getAllSensorForSpecfiedUserAndType(String sensorType,String id);
	public List<Sensor> getAllSensorWithSpecifiedUser(String id);
	public List<Sensor> getAllSensorWithSpecifiedSensorTypeAndUser(
			String sensorType,String userId);
	public Sensor getSpecifiedSensorWithLatLng(double lat, double lng);
	public List<Sensor> getAllSensorWithSpecifiedCitySensorType(String city,
			String country, String type);
	
	public List<String> getAllSensorSourceWithSpecifiedSensorType(String sensorType);
	public List<String> getAllSensorSourceWithSpecifiedSourceType(String sourceType);
	public List<String> getAllSensorSourceWithSpecifiedUser(String id,
			int from, int size);
	public List<String> getAllSensorSourceWithSpecifiedSourceType(String string,
			int from, int size);
	public List<String> getAllSourceTypeWithSpecifiedSensorType(String sensorType);		
	public int getCountForSpecifiedSourceType(String string);	
	public List<Object[]> getAllCityWithSpecifiedSensorType(String sensorType);
	public List<Sensor> getAllSensorWithSpecifiedSensorTypeAndUserAndSubName(
			String sensorType, String id, String subName);
	
	/*------------------------- process the observation table -------------------------*/
	public Observation getNewestObservationForOneSensor(String sensorId);
	public List<Observation> getObservationsForOnePlace(double lat, double lng,String type);
	public List<Observation> getObservationsForOneSensor(String sensorId);
	public AbstractProperty getSpecifiedReadingForOneObservation(String id,String tblName);	
	public List<String> getObservationsForNonSpatialCriteria(
			String sensorId, String timeOper, String date2StandardString, String tbl,
			String oper, String value);
	public List<String> getObservationsWithTimeCriteria(String sensorId,
			String dateOperator, Date fromDate, Date toDate);
	
	/*------------------------- process properties table -------------------------*/	
	public CallSign getNewestCallSignWithCallSignValue(String planeCallSign);
	public FlightCIAO getFlightCIAOForOneObservation(String id);
	public FlightCIAO getNewestFlightCIAOWithCIAOValue(String ciao);
	public CallSign getFlightCallSignWithOneLocation(double lat, double lng);
	public Latitude getFlightLatitudeForOneObservation(String id);
	public Longitude getFlightLongitudeForOneObservation(String id);
	public Latitude getFlightLatitudeWithCurrentLat(double lat);
	public Altitude getFlightAltitudeForOneObservation(String id);
	public Speed getFlightSpeedForOneObservation(String id);
	public Observation getObservationForOneFlightWithLatLng(double lat,
			double lng);
	public CallSign getFlightCallSignForOneObservation(String id);
	public Departure getFlightDepartureForOneObservation(String id);
	public Destination getFlightDestinationForOneObservation(String id);
	public List<Departure> getAllFlightDepaturesWithSpecifiedDeparture(
			String code);
	public List<Destination> getAllFlightDestinationsWithSpecifiedDestination(
			String code);
	/*------------------------- process system sensor type table -------------------------*/
	public List<String> getAllSensorType();
	public ArrayList<List> getAllSensorPropertiesForSpecifiedSensorType(String string);
	public List<String> getAllSensorSystemProperties();

	
	/*------------------------- process observed property table -------------------------*/
	public ObservedProperty getClassURIForObservedPropertyID(String obsProId);
	public List getAllObservedProperty();
	public ObservedProperty getObservedPropertyWithClassURI(String classURI);
	public String getWrapperIdWithSensorType(String sensorType);
	
	/*------------------------- process wrapper table -------------------------*/
	public Wrapper getWrapperWithId(String wrapperId);

	
	/*------------------------- process sensorsourcetype table -------------------------*/
	public SensorSourceType getXMLMapForSpecifiedSourceType(String sourceType);
	public List<String> getAllUserDefinedSourceTypeWithSpecifiedSensorType(
			String type);
	public List<SensorSourceType> getAllSensorSourceTypeForSpecifiedSensorType(
			String type);	
	public Sensor getRailwayStationWithSpecifiedStationCode(String stationCode);
	
	/*------------------------- process flightroute table -------------------------*/
	public FlightRoute getFlightRouteForOneFlight(String callsign);
	public Airport getAirportWithAirportCode(String code);
	public List<FlightRoute> getAllFlightsWithSpecifiedDeparture(
			String airportCode);
	public List<FlightRoute> getAllFlightsWithSpecifiedArrival(
			String airportCode);

	public List<Airport> getAllAirports();
	
	/*------------------------- process user feed table -------------------------*/
	public String getQueryWithFeedId(String feedId);
	public UserFeed getUserFeedWithFeedId(String feedId);
	
	/*------------------------- new -------------------------*/
	public List<ArrayList> getReadingDataOfObservation(String observationId);
	public Sensor getSpecifiedSensorWithObservationId(String string);
	public void deleteSensor(Sensor removeSensor);
	public void deleteSensorType(String type);
	public AbstractProperty getSpecifiedCosmReadingForOneObservation(String id,
			String sign);


	
	
	


	
	
	
	
	
	
	
	
	

	
	
	

	

	
	
	
	
	
	
	
	
	

	
	
	
		

	
	

}
