package deri.sensor.components.search;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;


import deri.sensor.components.Map.GMaps;
import deri.sensor.components.windows.AdvanceSearchWindow;
import deri.sensor.components.windows.FlightAroundResultWindow;
import deri.sensor.components.windows.SearchSpatialResultsWindow;
import deri.sensor.database.ServiceLocator;
import deri.sensor.flight.libhomeradar.FlightAroundXMLParser;
import deri.sensor.hashmaps.util.FieldSearchConstainsUtil;
import deri.sensor.javabeans.Place;
import deri.sensor.manager.SensorManager;
import deri.sensor.meta.SensorTypeEnum;
import deri.sensor.places.yahoo.YahooWhereURLXMLParser;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class DataSearchTabPannel extends Tabpanel{
	private Listbox lstSensorType;	
	private Textbox txtLocation;
	private Hbox hboxDataSearch;
	private Groupbox grbSensorType;
	private Groupbox grbLocation;
	private Decimalbox txtLat;
	private Decimalbox txtLong;
	private Decimalbox txtDistance;	
	private GMaps map;	
	private ArrayList<List> listSpatialResult;
	private SensorManager sensormanager = ServiceLocator.getSensorManager();
	private Checkbox chkShowOnMap;

	
	public DataSearchTabPannel(Component parent){
		super();
		this.setParent(parent);
		init();	 
	}
		

	public boolean isShowOnMap(){
		return chkShowOnMap.isChecked();
	}

	public void setLocation(double lat,double lng){
		txtLat.setValue(BigDecimal.valueOf(lat));
		txtLong.setValue(BigDecimal.valueOf(lng));
	}
	
	public void init(){
		this.setStyle("overflow:auto");
		this.setId("QuickSearchTabPanel");
		hboxDataSearch = new Hbox();
		hboxDataSearch.setParent(this);
		hboxDataSearch.setWidth("80%");
		createGrbSensorType();
		create_grbLocation();
		create_searchButton();
		map = (GMaps)this.getParent().getParent().getFellowIfAny("map");
	}
	
	public void createGrbSensorType(){
		grbSensorType = new Groupbox();
		grbSensorType.setId("grbSensorType");
		grbSensorType.setParent(hboxDataSearch);
		
		Caption capSensorType = new Caption("Choose sensor");
		capSensorType.setParent(grbSensorType);
		
		Hbox hb1 = new Hbox();
		hb1.setSpacing("10px");
		hb1.setParent(grbSensorType);
		
		Label lb1 = new Label("Sensor");
		lb1.setParent(hb1);
		lstSensorType= new Listbox();
		lstSensorType.setParent(hb1);
		lstSensorType.setMold("select");
		initialize_lstSensorType();
		
		grbSensorType.appendChild(new Space());	
		
		lstSensorType.addEventListener(Events.ON_SELECT, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub				
//				reset_cmbCity(lstSensorType.getSelectedItem().getLabel().toLowerCase());
//				if(lstSensorType.getSelectedItem().getLabel().equals("flight"))
//					txtDistance.setVisible(true);
//				else txtDistance.setVisible(false);
			}
		});
	}
	
	private void create_grbLocation(){
		grbLocation = new Groupbox();
		grbLocation.setId("grbLocation");
		grbLocation.setParent(hboxDataSearch);
		grbLocation.setWidth("80%");
		Caption capSensorType = new Caption("Location");
		capSensorType.setParent(grbLocation);
		
		Hbox hb1 = new Hbox();
		hb1.setSpacing("5px");
		hb1.setParent(grbLocation);
		
		Label lb1 = new Label("City");
		lb1.setParent(hb1);
		txtLocation= new Textbox();
		txtLocation.setParent(hb1);
		hb1.appendChild(new Label("Please input your location or you can click on the map"));
		
		Space sp = new Space();
		sp.setHeight("5px");
		grbLocation.appendChild(sp);
		Hbox hb2 = new Hbox();
		hb2.setSpacing("7px");
		hb2.setParent(grbLocation);
		hb2.setWidth("95%");
		Label lb2 = new Label("Lat");
		lb2.setParent(hb2);
		
		txtLat = new Decimalbox();
		txtLat.setWidth("80%");
		txtLat.setParent(hb2);
		
		Label lb3 = new Label("Long");
		lb3.setParent(hb2);
		
		txtLong = new Decimalbox();
		txtLong.setWidth("80%");
		txtLong.setParent(hb2);
		
		txtLocation.addEventListener(Events.ON_OK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				String address = txtLocation.getValue();
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
//		hb2.appendChild(new Label("Within"));
//		txtDistance = new Decimalbox();
//		txtDistance.setWidth("40%");
//		txtDistance.setParent(hb2);
//		txtDistance.setVisible(false);
//		hb2.appendChild(new Label("km"));
	}
	
	
	private void create_searchButton(){
		Vbox vbox = new Vbox();
		vbox.setParent(hboxDataSearch);
		
		Hbox hboxButton = new Hbox();		
		hboxButton.setParent(vbox);
		final Button jbtSearch = new Button("Search");
		jbtSearch.setTooltiptext("Find all sensors within 1 km");
		jbtSearch.setParent(hboxButton);
		//hboxButton.setSpacing("20px");
		Button jbtSearchAdv = new Button("Advanced search");
		jbtSearchAdv.setParent(hboxButton);
		//jbtSearchAdv.setStyle("background:#04B4AE");
		chkShowOnMap = new Checkbox("Show results on map");
		//chkShowOnMap.setChecked(true);
		chkShowOnMap.setParent(vbox);
		
		jbtSearch.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				try{
					if(lstSensorType.getSelectedItem().getLabel().equals("flight"))
						showFlightsAroundResult();
					else{
						getSearchResults();
						showResults();
					}
				}catch(Exception e){
					e.printStackTrace();
					Messagebox.show("Invalid data input",
							"Error", Messagebox.OK,
							Messagebox.ERROR);
				}
			}
		});
		
		jbtSearchAdv.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				showAdvanceSearchWindow();				
			}
		});	
		
		chkShowOnMap.addEventListener(Events.ON_CHECK,new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				if(!chkShowOnMap.isChecked()) map.detachCircle();
				else if(map.getCircleLat()!=0){					
					map.drawCircle();
					map.setZoom(13);
					map.setCenter(map.getCircleLat(), map.getCircleLng());
				}
			}
		});
	}
	
	protected void showFlightsAroundResult() {
		// TODO Auto-generated method stub
		try{
			if(this.getRoot().getFellowIfAny("FlightAroundResultsWindow") != null){
				this.getRoot().getFellowIfAny("FlightAroundResultsWindow").detach();
			}
			ArrayList<List> findFlights = FlightAroundXMLParser.getFlightAroundInformation(txtLat.doubleValue(), txtLong.doubleValue());
			FlightAroundResultWindow searchResult = new FlightAroundResultWindow ();			
			searchResult.setTitle("All flights around");
			searchResult.setParent(map.getRoot());
			searchResult.setResultList(findFlights);
			searchResult.init();
			searchResult.addContent();
			searchResult.doOverlapped();
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	private void initialize_lstSensorType(){
		List<String> lstSensor = sensormanager.getAllSensorType();
		Iterator iter = lstSensor.iterator();
		while(iter.hasNext()){
			String type = iter.next().toString();
			if(!type.equals(SensorTypeEnum.ADSBHub.toString()))
				lstSensorType.appendChild(new Listitem(type));
		}
		lstSensorType.appendChild(new Listitem("flight"));		
		lstSensorType.setSelectedIndex(0);
	}


	public void getSearchResults(){	
		try{
			listSpatialResult = sensormanager.getAllSensorHasLatLongWithSpatialCriteria(lstSensorType.getSelectedItem().getLabel().toLowerCase(), FieldSearchConstainsUtil.spatialOperator.get(0), 
					Double.parseDouble(txtLong.getText()), Double.parseDouble(txtLat.getText()),
					1); 			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void showResults(){
		try{
			if(this.getRoot().getFellowIfAny("SearchSpatialResultsWindow") != null){
				this.getRoot().getFellowIfAny("SearchSpatialResultsWindow").detach();
			}
			SearchSpatialResultsWindow searchResult = new SearchSpatialResultsWindow();
			searchResult.setType(lstSensorType.getSelectedItem().getLabel().toLowerCase());
			searchResult.setTitle("Data results");
			searchResult.setParent(map.getRoot());
			searchResult.setSpatialResultList(listSpatialResult);
			searchResult.setQuickSearch(true);
			searchResult.init();
			searchResult.addContent();
			searchResult.doOverlapped();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void showAdvanceSearchWindow(){
		if(this.getRoot().getFellowIfAny("AdvanceSearchWindow") != null){
			this.getRoot().getFellowIfAny("AdvanceSearchWindow").detach();
		}		
		AdvanceSearchWindow advSearchWindow = new AdvanceSearchWindow();
		advSearchWindow.setParent(map.getRoot());
		advSearchWindow.init();
		advSearchWindow.doOverlapped();
	}
}
