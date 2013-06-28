package deri.sensor.dao;

import java.util.List;

import deri.sensor.javabeans.Place;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public interface PlaceDAO {
	public void addPlace(Place place);
	public void deletePlace(Place place);
	public void updatePlace(Place place);

	public Place getPlaceWithSpecifiedLatLng(double lat, double lng);
	public List<Place> getAllPlaces();	
	public List<Place> getAllPlacesWithinOneCity(String city);
	
	public List<List<String>> getALlPlaceMetadataWithPlaceId(String id);
	public Place getPlaceWithPlaceId(String id);
	
}
