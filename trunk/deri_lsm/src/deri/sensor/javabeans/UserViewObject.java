package deri.sensor.javabeans;

import java.util.List;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class UserViewObject {
	private double lat;
	private double lng;
	private int zoom;
	private String city = "";
	private boolean search_only = false;
	private List<String> filterTypes;
	private List<String> filterRegion;
	
	public UserViewObject(){
		super();
	}

	public UserViewObject(double lat, double lng, int zoom, String city,
			boolean searchOnly, List<String> filterTypes,
			List<String> filterRegion) {
		super();
		this.lat = lat;
		this.lng = lng;
		this.zoom = zoom;
		this.city = city;
		search_only = searchOnly;
		this.filterTypes = filterTypes;
		this.filterRegion = filterRegion;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public int getZoom() {
		return zoom;
	}

	public void setZoom(int zoom) {
		this.zoom = zoom;
	}

	public List<String> getFilterTypes() {
		return filterTypes;
	}

	public void setFilterTypes(List<String> filterTypes) {
		this.filterTypes = filterTypes;
	}

	public List<String> getFilterRegion() {
		return filterRegion;
	}

	public void setFilterRegion(List<String> filterRegion) {
		this.filterRegion = filterRegion;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public boolean isSearch_only() {
		return search_only;
	}

	public void setSearch_only(boolean searchOnly) {
		search_only = searchOnly;
	}
}
