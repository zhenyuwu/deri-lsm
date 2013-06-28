package deri.sensor.components.search;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Hbox;

import deri.sensor.components.Map.GMaps;
import deri.sensor.components.Map.SensorMarker;
import deri.sensor.database.ServiceLocator;
import deri.sensor.javabeans.Place;
import deri.sensor.manager.PlaceManager;
import deri.sensor.manager.SensorManager;
import deri.sensor.meta.SensorType;
import deri.sensor.places.yahoo.YahooWhereURLXMLParser;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class MapSearchTabPanel extends Tabpanel{
	private GMaps map;
	private Hbox hbox;
	
	private Textbox search = new Textbox();	
	private Textbox txtLat = new Textbox();
	private Textbox txtLong = new Textbox();
	private Label lblInput = new Label("Input the city ");
	private Label lblLat = new Label("Latitude");
	private Label lblLong = new Label("Longitude");
	
	private Checkbox only = new Checkbox();
	private PlaceManager placeManager = ServiceLocator.getPlaceManager();
	private SensorManager sensorManager = ServiceLocator.getSensorManager();
	private Set<String> searched_cities = new HashSet<String>();
	
	public MapSearchTabPanel(){
		
	}
	
	public MapSearchTabPanel(Component parent){
		super();
		this.setId("searchPanel");
		this.setParent(parent);		
		this.setId("searchMap");
		this.initMap();
		//this.setStyle("color:blue");
	}
	
	private void initMap(){
		map = (GMaps)this.getParent().getParent().getFellowIfAny("map");
	}
	
	public void addSearchFunction(){
		hbox = new Hbox();
		hbox.setSpacing("10px");
		hbox.setParent(this);
				
		//lblInput.setParent(this);
		lblInput.setParent(hbox);
		
		//search.setParent(this);
		search.setParent(hbox);
		search.setWidth("200px");
		search.setText("Search the map");
		search.setTooltiptext("Press enter to start searching");
		search.addEventListener(Events.ON_OK, new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
//				String city_input = search.getText();
//				searched_cities = new HashSet<String>();
//				if(city_input != null && !city_input.trim().equals("")){
//					String[] city_input_arr = city_input.split("\\,");
//					String city_not_supported = "";
//					for(String city : city_input_arr){
//						List<Place> places = placeManager.getAllPlacesWithinOneCity(city.trim());
//						if(places == null || places.size() == 0){
//							city_not_supported += city;
//						}else{
//							searched_cities.add(city);
//						}
//					}
//					deleteSearchResultOnTheMap();
//					showSearchResult();
//					if(!city_not_supported.trim().equals("")){
//						Messagebox.show("Do not support:"+city_not_supported, "Information", Messagebox.OK, Messagebox.INFORMATION);
//					}
//				}
				
				String address = search.getValue();
				String locationPra = 
						(address.trim().equals("")?  ""  : (address + ", "))
						+ txtLat.getValue()+","+txtLong.getValue();
				Place place = YahooWhereURLXMLParser.placeStr2PlaceObj(locationPra);
				if(place!=null){					
					txtLat.setValue(Double.toString(place.getLat()));
					txtLong.setValue(Double.toString(place.getLng()));
					map.setCenter(place.getLat(),place.getLng());
					map.setZoom(8);
				}
			}
		});
				
		search.addEventListener(Events.ON_FOCUS, new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				search.setText("");
				search.setFocus(true);
				
			}
		});
		
		lblLat.setParent(hbox);
		txtLat.setParent(hbox);
		txtLat.setReadonly(true);
		lblLong.setParent(hbox);
		txtLong.setParent(hbox);
		txtLong.setReadonly(true);
		
		only.setLabel("Search Only");
		//only.setParent(this);
		only.addEventListener(Events.ON_CHECK, new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				if(only.isChecked()){
					map.deleteSpecifiedSensorTypeMarkersOrAll(SensorType.all);
					showSearchResult();
				}else{
					map.reshowMarkersWith3Filters();
				}
			}
		});
	}
	
	public Set<String> getSearchedCities(){
		return this.searched_cities;
	}
	
	public void setSearchedCities(Set<String> searched_cities){
		this.searched_cities = searched_cities;
	}
	
	public void setSearchedCities(String cities){
		String[] city_arr = cities.split("\\,");
		if(city_arr != null){
			for(String city : city_arr){
				if(!city.trim().equals("")){
					this.searched_cities.add(city.trim());
				}
			}
		}
	}
	
	public void showSearchResult(){
		if(this.searched_cities != null && this.searched_cities.size() > 0){
			for(String city : searched_cities){
				if(!city.trim().equals("")){
					List<Place> places = placeManager.getAllPlacesWithinOneCity(city.trim());
					showSearchResult(places);
				}
			}
		}
	}
	
	//rewrite
	public void showSearchResult(List<Place> places){
		if(places != null){
			for(Place place : places){
				double lat = place.getLat();
				double lng = place.getLng();
				
				txtLat.setText(Double.toString(lat));
				txtLong.setText(Double.toString(lng));
				map.setCenter(lat, lng);
				map.setZoom(6);
				List<String> sensorTypes = sensorManager.getAllSensorTypesWithOneSpecifiedLatLng(lat, lng);
				if(sensorTypes != null && sensorTypes.size() > 0){
					for(String type : sensorTypes){
						showMarkerWithLatLngSensorType(lat,lng,type);
					}
				}
			}
		}
	}
	

	
	private void showMarkerWithLatLngSensorType(double lat, double lng, String type){
		int zoom = map.getZoom();
		double neLat = map.getNeLat();
		double neLng = map.getNeLng();
		double swLat = map.getSwLat();
		double swLng = map.getSwLng();
		SensorMarker searchMarker = new SensorMarker(type,1,lat,lng,zoom,neLat,neLng,swLat,swLng);
		searchMarker.setParent(map);
		searchMarker.init();
	}
	
	private void deleteSearchResultOnTheMap(){
		if(this.searched_cities != null && this.searched_cities.size() > 0){
			for(String city : this.searched_cities){
				List<Place> places = placeManager.getAllPlacesWithinOneCity(city.trim());
				if(places != null){
					for(Place place : places){
						double lat = place.getLat();
						double lng = place.getLng();
						map.deleteOneMarkerWithLatLng(lat, lng);
					}
				}
			}
		}
	}
	
	private void deleteSearchResultSet(){
		this.searched_cities.clear();
	}
	
	public void onOpen(){
		if(this.isSelected()){
			search.setFocus(true);
		}else{
			deleteSearchResultOnTheMap();
			deleteSearchResultSet();
			search.setValue("");
		}
	}
	
	public boolean isSearchEnabled(){
		return this.isSelected();
	}

	public void setSearchText(String cities){
		search.setValue(cities);
	}
	
	public void setSearchOnly(boolean isOnly){
		only.setChecked(isOnly);
	}
	
	public boolean isSearchOnly(){
		return only.isChecked();
	}
}
