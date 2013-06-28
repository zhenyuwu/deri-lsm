package deri.sensor.manager;


import java.util.List;

import deri.sensor.dao.PlaceDAO;
import deri.sensor.javabeans.Place;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class PlaceManagerImpl implements PlaceManager {

	private PlaceDAO placeDao;
	
	public PlaceManagerImpl(){
		super();
	}

	public PlaceDAO getPlaceDao() {
		return placeDao;
	}

	public void setPlaceDao(PlaceDAO placeDao) {
		this.placeDao = placeDao;
	}
	
	@Override
	public void addPlace(Place place) {
		this.placeDao.addPlace(place);
	}

	@Override
	public void deletePlace(Place place) {
		this.placeDao.deletePlace(place);
	}

	@Override
	public void updatePlace(Place place) {
		this.placeDao.updatePlace(place);
	}
	
	@Override
	public Place getPlaceWithSpecifiedLatLng(double lat, double lng) {
		return this.placeDao.getPlaceWithSpecifiedLatLng(lat, lng);
	}

	@Override
	public List<Place> getAllPlaces() {
		return this.placeDao.getAllPlaces();
	}

	@Override
	public List<Place> getAllPlacesWithinOneCity(String city) {
		return this.placeDao.getAllPlacesWithinOneCity(city);
	}

	@Override
	public List<List<String>> getALlPlaceMetadataWithPlaceId(String id) {
		// TODO Auto-generated method stub
		return this.placeDao.getALlPlaceMetadataWithPlaceId(id);
	}

	@Override
	public Place getPlaceWithPlaceId(String id) {
		// TODO Auto-generated method stub
		return this.placeDao.getPlaceWithPlaceId(id);
	}
}
