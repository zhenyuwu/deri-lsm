package deri.sensor.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import deri.sensor.dao.SensorDAO;
import deri.sensor.javabeans.AbstractProperty;
import deri.sensor.javabeans.Airport;
import deri.sensor.javabeans.Altitude;
import deri.sensor.javabeans.CallSign;
import deri.sensor.javabeans.Departure;
import deri.sensor.javabeans.Destination;
import deri.sensor.javabeans.FlightCIAO;
import deri.sensor.javabeans.FlightRoute;
import deri.sensor.javabeans.Latitude;
import deri.sensor.javabeans.Longitude;
import deri.sensor.javabeans.Observation;
import deri.sensor.javabeans.ObservedProperty;
import deri.sensor.javabeans.Place;
import deri.sensor.javabeans.Sensor;
import deri.sensor.javabeans.SensorSourceType;
import deri.sensor.javabeans.Speed;
import deri.sensor.javabeans.SystemSensorType;
import deri.sensor.javabeans.UserFeed;
import deri.sensor.javabeans.Wrapper;

/**
 * @author Hoan Nguyen Mau Quoc
 */

public class SensorManagerImpl implements SensorManager{
	private SensorDAO sensorDao;

	public SensorManagerImpl() {
		super();
	}

	public SensorManagerImpl(SensorDAO markerDao) {
		super();
		this.sensorDao = markerDao;
	}

	@Override
	public List<String> getAllSourcesWithSpecifiedLatLngSensorType(double lat,
			double lng, String sensorType) {
		return this.sensorDao.getAllSourcesWithSpecifiedLatLngSensorType(lat, lng, sensorType);
	}	

	@Override
	public List<Sensor> getAllSensorWithSpecifiedLatLngSensorType(double lat,
			double lng, String sensorType) {
		return this.sensorDao.getAllSensorWithSpecifiedLatLngSensorType(lat, lng, sensorType);
	}
	
	@Override
	public List<Object[]> getMarkersNearby(String sensorType, int level,
			String regionType, List<String> regions, double lat, double lng,
			double neLat, double neLng, double swLat, double swLng) {
		return this.sensorDao.getMarkersNearby(sensorType, level, regionType, regions, lat, lng, neLat, neLng, swLat, swLng);
	}
	
	@Override
	public List<Object[]> getFlightMarkersNearby(int level,
			String regionType, List<String> regions, double lat, double lng,
			double neLat, double neLng, double swLat, double swLng) {
		return this.sensorDao.getFlightMarkersNearby(level, regionType, regions, lat, lng, neLat, neLng, swLat, swLng);
	}

	@Override
	public List<Object[]> getMarkersShown(String sensorType, int level,
			String regionType, List<String> regions, double neLat,
			double neLng, double swLat, double swLng,String userId) {
		return this.sensorDao.getMarkersShown(sensorType, level, regionType, regions, neLat, neLng, swLat, swLng,userId);
	}

	@Override
	public List<Object[]> getFlightMarkersShown(int level,
			String regionType, List<String> region, double neLat, double neLng,
			double swLat, double swLng, String userId){
		return this.sensorDao.getFlightMarkersShown(level, regionType, region, neLat, neLng, swLat, swLng,userId);
	}
	
	public SensorDAO getSensorDao() {
		return sensorDao;
	}

	public void setSensorDao(SensorDAO sensorDao) {
		this.sensorDao = sensorDao;
	}

	
	@Override
	public void addObject(Object object) {
		this.sensorDao.addObject(object);
	}

	@Override
	public void deleteObject(Object object) {
		this.sensorDao.deleteObject(object);
	}

	@Override
	public void deleteAllObservationsForSpecifiedSensor(Sensor sensor) {
		// TODO Auto-generated method stub
		this.sensorDao.deleteAllObservationsForSpecifiedSensor(sensor);
	}

	@Override
	public void deleteAllObservationsWithTimeCriteria(Sensor sensor,
			String dateOperator, Date fromDate, Date toDate) {
		// TODO Auto-generated method stub
		this.sensorDao.deleteAllObservationsWithTimeCriteria(sensor,dateOperator,fromDate,toDate);
	}
	
	@Override
	public void updateObject(Object object) {
		this.sensorDao.updateObject(object);
	}

	@Override
	public void excuteSQL(String sql) {
		// TODO Auto-generated method stub
		this.sensorDao.excuteSQL(sql);
	}
	
	@Override
	public void excuteSQLUpdate(String sql) {
		// TODO Auto-generated method stub
		this.sensorDao.excuteSQLUpdate(sql);
	}
	@Override
	public List<String> getAllSensorTypesWithOneSpecifiedLatLng(
			double lat, double lng) {
		return this.sensorDao.getAllSensorTypesWithOneSpecifiedLatLng(lat, lng);
	}


	/*------------------------- to create spatial index -------------------------*/
	@Override
	public void runSpatialIndex(){
		this.sensorDao.runSpatialIndex();
	}
	
	/*------------------------- add delete update graph -------------------------*/
	
	@Override
	public ArrayList<List> getAllSensorHasLatLongWithSpatialCriteria(String sensorType,String spatialOperator,double lng,double lat,double distance){
		return this.sensorDao.getAllSensorHasLatLongWithSpatialCriteria(sensorType, spatialOperator, lng, lat, distance);
	}
	
	@Override
	public ArrayList<List> getAllSpecifiedSensorAndLatLongAroundPlace(String sensorType,String spatialOperator,double lng,double lat,double distance){
		return this.sensorDao.getAllSpecifiedSensorAndLatLongAroundPlace(sensorType, spatialOperator, lng, lat, distance);
	}
	
	@Override
	public ArrayList<List> getAllSensorHasLatLongWithSpatialCriteria(String spatialOperator,double lng,double lat,double distance){
		return this.sensorDao.getAllSensorHasLatLongWithSpatialCriteria(spatialOperator, lng, lat, distance);
	}
	/*------------------------- to process the sensor metadata grph-------------------------*/
	
	@Override
	public void addNewPlaceToGraph(Place place) {
		// TODO Auto-generated method stub
		this.sensorDao.addNewPlaceToGraph(place);
	}
		
	@Override
	public void addExtraMetadataToGraph(HashMap<String, List<ArrayList>> extraPropertiesMap) {
		// TODO Auto-generated method stub
		this.sensorDao.addExtraMetadataToGraph(extraPropertiesMap);
	}
	
	@Override
	public String getNearestLocationIdFromGraph(String linkedgeoId) {
		// TODO Auto-generated method stub
		return this.sensorDao.getNearestLocationIdFromGraph(linkedgeoId);
	}
	
	@Override
	public String getNearestLocationWithSensorIdURL(String id){
		return this.sensorDao.getNearestLocationWithSensorIdURL(id);
	}
	
	@Override
	public void insertTriplesToGraph(String graphName,String triples) {
		// TODO Auto-generated method stub
		this.sensorDao.insertTriplesToGraph(graphName,triples);
	}
	
	/*------------------------- to run sparql end point-------------------------*/
	@Override
	public List runSparqlEndPoint(String sparqlInput){
		return this.sensorDao.runSparqlEndPoint(sparqlInput);
	}
	
	/*----------------------process sensormasher data quad storage---------------------------------*/
	@Override
	public ArrayList getReadingTriplesOfObservation(String obsId){
		return this.sensorDao.getReadingTriplesOfObservation(obsId);
	}
	
	/***********************************************************************************************/
	/*------------------------- process the sensor table -------------------------*/
	@Override
	public Sensor getSpecifiedSensorWithSource(String source) {
		return this.sensorDao.getSpecifiedSensorWithSource(source);
	}
	@Override
	public Sensor getSpecifiedSensorWithPlaceId(String id) {
		return this.sensorDao.getSpecifiedSensorWithPlaceId(id);
	}
	@Override
	public Sensor getSpecifiedSensorWithSensorId(String id){
		return this.sensorDao.getSpecifiedSensorWithSensorId(id);
	}
	@Override
	public Sensor getSpecifiedSensorWithLatLng(double lat, double lng) {
		// TODO Auto-generated method stub
		return this.sensorDao.getSpecifiedSensorWithLatLng(lat,lng);
	}
	@Override
	public List<String> getAllSensorSourceWithSpecifiedSensorType(String sensorType){
		return this.sensorDao.getAllSensorSourceWithSpecifiedSensorType(sensorType);
	}
	@Override
	public List<Sensor> getAllSensorWithSpecifiedSensorType(String sensorType) {
		return this.sensorDao.getAllSensorWithSpecifiedSensorType(sensorType);
	}
	@Override
	public List<Object[]> getAllCityWithSpecifiedSensorType(String sensorType) {
		return this.sensorDao.getAllCityWithSpecifiedSensorType(sensorType);
	}
	
	@Override
	public List<Sensor> getAllSensorWithSpecifiedCitySensorType(String city,
			String country, String type){
		return this.sensorDao.getAllSensorWithSpecifiedCitySensorType(city,country,type);
	}
	@Override
	public List<Sensor> getAllSensorWithSpecifiedUser(String id) {
		// TODO Auto-generated method stub
		return this.sensorDao.getAllSensorWithSpecifiedUser(id);
	}
	
	@Override
	public List<Sensor> getAllSensorWithSpecifiedSensorTypeAndUser(
			String sensorType,String userId) {
		// TODO Auto-generated method stub
		return this.sensorDao.getAllSensorWithSpecifiedSensorTypeAndUser(sensorType,userId);
	}
	@Override
	public List<String> getAllSensorSourceWithSpecifiedSourceType(
			String sourceType) {
		return this.sensorDao.getAllSensorSourceWithSpecifiedSourceType(sourceType);
	}
	
	@Override
	public List<String> getAllSensorSourceWithSpecifiedUser(String id,
			int from, int size) {
		// TODO Auto-generated method stub
		return this.sensorDao.getAllSensorSourceWithSpecifiedUser(id,from,size);
	}
	
	@Override
	public List<String> getAllSensorSourceWithSpecifiedSourceType(String sourceType,
			int from, int size) {
		// TODO Auto-generated method stub
		return this.sensorDao.getAllSensorSourceWithSpecifiedSourceType(sourceType,from,size);
	}

	@Override
	public int getCountForSpecifiedSourceType(String sourceType) {
		// TODO Auto-generated method stub
		return this.sensorDao.getCountForSpecifiedSourceType(sourceType);
	}
	
	@Override
	public List<Sensor> getAllSensorFromDB() {
		// TODO Auto-generated method stub
		return this.sensorDao.getAllSensorFromDB();
	}
	
	@Override
	public List<String> getAllSourceTypeWithSpecifiedSensorType(String sensorType){
		return this.sensorDao.getAllSourceTypeWithSpecifiedSensorType(sensorType);
	}
	
	@Override
	public List<Sensor> getAllSensorForSpecfiedUserAndType(String sensorType,String id) {
		// TODO Auto-generated method stub
		return this.sensorDao.getAllSensorForSpecfiedUserAndType(sensorType,id);
	}

	@Override
	public List<Sensor> getAllSensorWithSpecifiedSensorTypeAndUserAndSubName(
			String sensorType, String id, String subName) {
		// TODO Auto-generated method stub
		return this.sensorDao.getAllSensorWithSpecifiedSensorTypeAndUserAndSubName(
				sensorType,id,subName);
	}

	/*------------------------- process the observation table -------------------------*/
	@Override
	public Observation getNewestObservationForOneSensor(String sensorId){
		return this.sensorDao.getNewestObservationForOneSensor(sensorId);
	}
	
	@Override
	public AbstractProperty getSpecifiedReadingForOneObservation(String observationId,String readingType) {
		// TODO Auto-generated method stub		
		return this.sensorDao.getSpecifiedReadingForOneObservation(observationId,readingType);
	}

	@Override
	public List<Observation> getObservationsForOnePlace(double lat, double lng,
			String type) {
		// TODO Auto-generated method stub
		return this.sensorDao.getObservationsForOnePlace(lat,lng,type);
	}

	@Override
	public List<String> getObservationsForNonSpatialCriteria(
			String sensorId,String timeOperator, String date2StandardString, String tbl,
			String oper, String parseDouble) {
		// TODO Auto-generated method stub
		return this.sensorDao.getObservationsForNonSpatialCriteria(sensorId,timeOperator, date2StandardString, tbl,
		oper, parseDouble);
	}

	@Override
	public List<String> getObservationsWithTimeCriteria(String sensorId,
			String dateOperator, Date fromDate, Date toDate){
		return this.sensorDao.getObservationsWithTimeCriteria(sensorId,dateOperator,fromDate,toDate);
	}
	
	@Override
	public CallSign getNewestCallSignWithCallSignValue(String planeCallSign){
		return this.sensorDao.getNewestCallSignWithCallSignValue(planeCallSign);
	}
	@Override
	public CallSign getFlightCallSignWithOneLocation(double lat, double lng){
		return this.sensorDao.getFlightCallSignWithOneLocation(lat,lng);
	}
	@Override
	public FlightCIAO getFlightCIAOForOneObservation(String obsId){
		return this.sensorDao.getFlightCIAOForOneObservation(obsId);
	}
	@Override
	public Latitude getFlightLatitudeForOneObservation(String obsId){
		return this.sensorDao.getFlightLatitudeForOneObservation(obsId);
	}
	@Override
	public Longitude getFlightLongitudeForOneObservation(String obsId){
		return this.sensorDao.getFlightLongitudeForOneObservation(obsId);
	}
	@Override
	public Latitude getFlightLatitudeWithCurrentLat(double lat) {
		// TODO Auto-generated method stub
		return this.sensorDao.getFlightLatitudeWithCurrentLat(lat);
	}
	@Override
	public CallSign getFlightCallSignForOneObservation(String obsId) {
		// TODO Auto-generated method stub
		return this.sensorDao.getFlightCallSignForOneObservation(obsId);
	}

	@Override
	public FlightCIAO getNewestFlightCIAOWithCIAOValue(String ciao) {
		// TODO Auto-generated method stub
		return this.sensorDao.getNewestFlightCIAOWithCIAOValue(ciao);
	}
	
	@Override
	public Observation getObservationForOneFlightWithLatLng(double lat,double lng){
		return this.sensorDao.getObservationForOneFlightWithLatLng(lat,lng);
	}
	
	@Override
	public Altitude getFlightAltitudeForOneObservation(String obsId) {
		// TODO Auto-generated method stub
		return this.sensorDao.getFlightAltitudeForOneObservation(obsId);
	}

	@Override
	public Speed getFlightSpeedForOneObservation(String obsId) {
		// TODO Auto-generated method stub
		return this.sensorDao.getFlightSpeedForOneObservation(obsId);
	}
	
	@Override
	public Departure getFlightDepartureForOneObservation(String id) {
		// TODO Auto-generated method stub
		return this.sensorDao.getFlightDepartureForOneObservation(id);
	}

	@Override
	public Destination getFlightDestinationForOneObservation(String id) {
		// TODO Auto-generated method stub
		return this.sensorDao.getFlightDestinationForOneObservation(id);
	}
	

	@Override
	public List<Departure> getAllFlightDepaturesWithSpecifiedDeparture(
			String airportCode) {
		// TODO Auto-generated method stub
		return this.sensorDao.getAllFlightDepaturesWithSpecifiedDeparture(airportCode);
	}

	@Override
	public List<Destination> getAllFlightDestinationsWithSpecifiedDestination(
			String airportCode) {
		// TODO Auto-generated method stub
		return this.sensorDao.getAllFlightDestinationsWithSpecifiedDestination(airportCode);
	}
	
	@Override
	public List<Observation> getObservationsForOneSensor(String sensorId) {
		// TODO Auto-generated method stub
		return this.sensorDao.getObservationsForOneSensor(sensorId);
	}

	@Override
	public List<String> getAllSensorType() {
		// TODO Auto-generated method stub
		return this.sensorDao.getAllSensorType();
	}

	@Override
	public ArrayList<List> getAllSensorPropertiesForSpecifiedSensorType(String type) {
		// TODO Auto-generated method stub
		return this.sensorDao.getAllSensorPropertiesForSpecifiedSensorType(type);
	}

	@Override
	public List<String> getAllSensorSystemProperties() {
		// TODO Auto-generated method stub
		return this.sensorDao.getAllSensorSystemProperties();
	}

	/*------------------------- process observed property table -------------------------*/
	@Override
	public ObservedProperty getClassURIForObservedPropertyID(String obsProId) {
		// TODO Auto-generated method stub
		return this.sensorDao.getClassURIForObservedPropertyID(obsProId);
	}

	@Override
	public List getAllObservedProperty() {
		// TODO Auto-generated method stub
		return this.sensorDao.getAllObservedProperty();
	}

	@Override
	public ObservedProperty getObservedPropertyWithClassURI(String classURI) {
		// TODO Auto-generated method stub
		return this.sensorDao.getObservedPropertyWithClassURI(classURI);
	}

	/*------------------------- process wrapper table -------------------------*/
	@Override
	public String getWrapperIdWithSensorType(String sensorType) {
		// TODO Auto-generated method stub
		return this.sensorDao.getWrapperIdWithSensorType(sensorType);
	}

	@Override
	public Wrapper getWrapperWithId(String wrapperId) {
		// TODO Auto-generated method stub
		return this.sensorDao.getWrapperWithId(wrapperId);
	}
	

	/*------------------------- process sensorsourcetype table -------------------------*/
	@Override
	public SensorSourceType getXMLMapForSpecifiedSourceType(String sourceType) {
		// TODO Auto-generated method stub
		return this.sensorDao.getXMLMapForSpecifiedSourceType(sourceType);
	}

	@Override
	public List<String> getAllUserDefinedSourceTypeWithSpecifiedSensorType(
			String type) {
		// TODO Auto-generated method stub
		return this.sensorDao.getAllUserDefinedSourceTypeWithSpecifiedSensorType(type);
	}

	@Override
	public List<SensorSourceType> getAllSensorSourceTypeForSpecifiedSensorType(
			String type) {
		// TODO Auto-generated method stub
		return this.sensorDao.getAllSensorSourceTypeForSpecifiedSensorType(type);
	}

	
	/*------------------------- processing for railwaystation -------------------------*/
	@Override
	public Sensor getRailwayStationWithSpecifiedStationCode(String stationCode) {
		// TODO Auto-generated method stub
		return this.sensorDao.getRailwayStationWithSpecifiedStationCode(stationCode);
	}

	@Override
	public FlightRoute getFlightRouteForOneFlight(String callsign) {
		// TODO Auto-generated method stub
		return this.sensorDao.getFlightRouteForOneFlight(callsign);
	}

	@Override
	public Airport getAirportWithAirportCode(String code) {
		// TODO Auto-generated method stub
		return this.sensorDao.getAirportWithAirportCode(code);
	}

	@Override
	public List<Airport> getAllAirports() {
		// TODO Auto-generated method stub
		return this.sensorDao.getAllAirports();
	}
	
	@Override
	public List<FlightRoute> getAllFlightsWithSpecifiedDeparture(
			String airportCode) {
		// TODO Auto-generated method stub
		return this.sensorDao.getAllFlightsWithSpecifiedDeparture(airportCode);
	}

	@Override
	public List<FlightRoute> getAllFlightsWithSpecifiedArrival(
			String airportCode) {
		// TODO Auto-generated method stub
		return this.sensorDao.getAllFlightsWithSpecifiedArrival(airportCode);
	}


	/*------------------------- process user feed table -------------------------*/
	@Override
	public String getQueryWithFeedId(String feedId) {
		// TODO Auto-generated method stub
		return this.sensorDao.getQueryWithFeedId(feedId);
	}

	@Override
	public UserFeed getUserFeedWithFeedId(String feedId) {
		// TODO Auto-generated method stub
		return this.sensorDao.getUserFeedWithFeedId(feedId);
	}

	
	/*------------------------- new -------------------------*/
	@Override
	public List<ArrayList> getReadingDataOfObservation(String observationId) {
		// TODO Auto-generated method stub
		return this.sensorDao.getReadingDataOfObservation(observationId);
	}

	@Override
	public Sensor getSpecifiedSensorWithObservationId(String obsId) {
		// TODO Auto-generated method stub
		return this.sensorDao.getSpecifiedSensorWithObservationId(obsId);
	}

	@Override
	public void deleteSensor(Sensor removeSensor) {
		// TODO Auto-generated method stub
		this.sensorDao.deleteSensor(removeSensor);
	}

	@Override
	public void deleteSensorType(String type) {
		// TODO Auto-generated method stub
		this.sensorDao.deleteSensorType(type);
	}

	@Override
	public AbstractProperty getSpecifiedCosmReadingForOneObservation(String obsId,
			String sign) {
		// TODO Auto-generated method stub
		return this.sensorDao.getSpecifiedCosmReadingForOneObservation(obsId,sign);
	}

























}
