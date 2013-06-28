package deri.sensor.components.windows;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import deri.sensor.components.Map.GMaps;
import deri.sensor.database.ServiceLocator;
import deri.sensor.flight.libhomeradar.FlightAroundXMLParser;
import deri.sensor.javabeans.Altitude;
import deri.sensor.javabeans.CallSign;
import deri.sensor.javabeans.Departure;
import deri.sensor.javabeans.Destination;
import deri.sensor.javabeans.FlightCIAO;
import deri.sensor.javabeans.FlightRoute;
import deri.sensor.javabeans.Latitude;
import deri.sensor.javabeans.Longitude;
import deri.sensor.javabeans.Observation;
import deri.sensor.javabeans.Sensor;
import deri.sensor.javabeans.Speed;
import deri.sensor.javabeans.User;
import deri.sensor.manager.SensorManager;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class AirportFlightScheduleWindow extends Window {
	private Tabbox tbbAirportSchedule;
	private Tab arrivalTab;
	private Tab departureTab;		
	private Tab onAirTab;
	private Tab liveContactTab;
	private SensorManager sensorManager;
	private Sensor sensor;
	private GMaps map;
	
	public AirportFlightScheduleWindow(){
		super();
	}
	
	public AirportFlightScheduleWindow(Sensor sensor){
		super();
		this.sensor = sensor;
	}
	
	public void init(){
		 this.setId("AirportFlightScheduleWindow");
		 this.setPosition("center,center");
		 this.setTitle(sensor.getCode() + " Airport Data");
		 this.setWidth("40%");
		 this.setHeight("40%");
		 this.setBorder("normal");
		 this.setContentStyle("overflow:auto");		
		 this.setClosable(true);
		 this.setSizable(true);
		 
		 sensorManager = ServiceLocator.getSensorManager();
		 map = (GMaps)this.getParent().getRoot().getFellowIfAny("map");
		 
		 tbbAirportSchedule = new Tabbox();
		 tbbAirportSchedule.setWidth("100%");		 
		 tbbAirportSchedule.setParent(this);
		 initTab();
	 }
	
	private void initTab(){
		tbbAirportSchedule.appendChild(new Tabs());			
		tbbAirportSchedule.appendChild(new Tabpanels());
		
		departureTab = new Tab("Departure");							
		tbbAirportSchedule.getTabs().appendChild(departureTab);			
		initDepartureTab();
		
		arrivalTab = new Tab("Arrival");
		tbbAirportSchedule.getTabs().appendChild(arrivalTab);			
		initArrivalTab();
		
//		onAirTab = new Tab("On Air");
//		tbbAirportSchedule.getTabs().appendChild(onAirTab);
//		initOnAirTab();
		
		liveContactTab = new Tab("In radar contact");
		tbbAirportSchedule.getTabs().appendChild(liveContactTab);
		initLiveContactTab();
	}


	private void initLiveContactTab() {
		// TODO Auto-generated method stub
		Tabpanel liveTabpanel = new Tabpanel();
		liveTabpanel.setHeight("100%");
		tbbAirportSchedule.getTabpanels().appendChild(liveTabpanel);
		
		Grid grdLiveContact = new Grid();
		grdLiveContact.setParent(liveTabpanel);
		grdLiveContact.setId("grdLiveContact");
		grdLiveContact.setMold("paging");
		grdLiveContact.setPageSize(6);
				
		Columns columns = new Columns();
		columns.setSizable(true);
		columns.setParent(grdLiveContact);		
		
		Column column = new Column();
		column.setParent(columns);
		column.setLabel("Registration");
		column.setAlign("center");
		column.setWidth("25%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Model");
		column.setAlign("center");
		column.setWidth("25%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Callsign");
		column.setAlign("center");
		column.setWidth("25%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Route");
		column.setAlign("center");
		column.setWidth("40%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Latitude");
		column.setAlign("center");
		column.setWidth("40%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Longitude");
		column.setAlign("center");
		column.setWidth("40%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Distance (km)");
		column.setAlign("center");
		column.setWidth("40%");
		Rows rows = new Rows();
		rows.setParent(grdLiveContact);		
		try{
			ArrayList<List> findFlights = FlightAroundXMLParser.getFlightAroundInformation(sensor.getPlace().getLat(),sensor.getPlace().getLng());
			if(findFlights==null) return;
			for(List lstFlightInfo:findFlights){
				Row row = new Row();
				row.setParent(rows);
				for(int i=0;i<lstFlightInfo.size();i++)
					row.appendChild(new Label(lstFlightInfo.get(i).toString()));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}


	private void initOnAirTab() {
		// TODO Auto-generated method stub
		Tabpanel onAirTabpanel = new Tabpanel();
		onAirTabpanel .setHeight("100%");
		tbbAirportSchedule.getTabpanels().appendChild(onAirTabpanel);
		
		Grid grdOnAir= new Grid();
		grdOnAir.setParent(onAirTabpanel);
		grdOnAir.setId("grdOnAir");
		grdOnAir.setMold("paging");
		grdOnAir.setPageSize(6);
		
		Columns columns = new Columns();
		columns.setSizable(true);
		columns.setParent(grdOnAir);
		Column column = new Column();
		
		column.setParent(columns);
		column.setLabel("Flight CIAO");
		column.setAlign("center");
		column.setWidth("20%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Flight");
		column.setAlign("center");
		column.setWidth("15%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Latest location");
		column.setAlign("center");
		column.setWidth("30%");
		
		column = new Column();
		column.setParent(columns);
		column.setAlign("center");
		column.setLabel("Altitude");
		column.setWidth("15%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Speed");
		column.setAlign("center");
		column.setWidth("10%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Route");
		column.setAlign("center");
		column.setWidth("30%");
		
		Rows rows = new Rows();
		rows.setParent(grdOnAir);
		
		List<Departure> lstDepatures = sensorManager.getAllFlightDepaturesWithSpecifiedDeparture(sensor.getCode());
		List<Destination> lstArrivals = sensorManager.getAllFlightDestinationsWithSpecifiedDestination(sensor.getCode());
		if(lstDepatures!=null)
		for(int i=0;i<lstDepatures.size();i++){
			String id = lstDepatures.get(i).getObservation().getId();
			FlightCIAO ciao = sensorManager.getFlightCIAOForOneObservation(id);
			CallSign callSign = sensorManager.getFlightCallSignForOneObservation(id);
			final Latitude latitude = sensorManager.getFlightLatitudeForOneObservation(id);
			final Longitude longitude = sensorManager.getFlightLongitudeForOneObservation(id);
			Altitude altitude = sensorManager.getFlightAltitudeForOneObservation(id);
			Speed speed = sensorManager.getFlightSpeedForOneObservation(id);
			Destination destination = sensorManager.getFlightDestinationForOneObservation(id);
			
			Row row = new Row();
			row.appendChild(new Label(ciao.getValue()));
			row.appendChild(new Label(callSign.getValue()));	
			row.appendChild(new Label(latitude.getValue()+","+longitude.getValue()));
//			Toolbarbutton location = new Toolbarbutton();
//			location.setParent(row);
//			location.setStyle("color:blue");
//			location.setTooltiptext("Click here to locate this flight on Map");
//			location.setLabel(latitude.getValue()+","+longitude.getValue());
//			location.addEventListener(Events.ON_CLICK, new EventListener() {
//				
//				@Override
//				public void onEvent(Event event) throws Exception {
//					// TODO Auto-generated method stub
//					map.setCenter(latitude.getValue(),longitude.getValue());
//					map.setZoom(9);
//				}
//			});
			
			row.appendChild(new Label(altitude.getValue()+""));
			row.appendChild(new Label(speed.getValue()+""));
			row.appendChild(new Label(sensor.getCode()+"-"+destination.getValue()));
			rows.appendChild(row);
		}
		if(lstArrivals!=null)
		for(int i=0;i<lstArrivals.size();i++){
			String id = lstArrivals.get(i).getObservation().getId();
			FlightCIAO ciao = sensorManager.getFlightCIAOForOneObservation(id);
			CallSign callSign = sensorManager.getFlightCallSignForOneObservation(id);
			final Latitude latitude = sensorManager.getFlightLatitudeForOneObservation(id);
			final Longitude longitude = sensorManager.getFlightLongitudeForOneObservation(id);
			Altitude altitude = sensorManager.getFlightAltitudeForOneObservation(id);
			Speed speed = sensorManager.getFlightSpeedForOneObservation(id);
			Departure departure = sensorManager.getFlightDepartureForOneObservation(id);
			
			Row row = new Row();
			row.appendChild(new Label(ciao.getValue()));
			row.appendChild(new Label(callSign.getValue()));
			row.appendChild(new Label(latitude.getValue()+","+longitude.getValue()));
//			Toolbarbutton location = new Toolbarbutton();
//			location.setParent(row);
//			location.setStyle("color:blue");
//			location.setTooltiptext("Click here to locate this flight on Map");
//			location.setLabel(latitude.getValue()+","+longitude.getValue());
//			location.addEventListener(Events.ON_CLICK, new EventListener() {
//				
//				@Override
//				public void onEvent(Event event) throws Exception {
//					// TODO Auto-generated method stub
//					map.setCenter(latitude.getValue(),longitude.getValue());
//					map.setZoom(9);
//				}
//			});
			row.appendChild(new Label(altitude.getValue()+""));
			row.appendChild(new Label(speed.getValue()+""));
			row.appendChild(new Label(departure.getValue()+"-"+sensor.getCode()));
			rows.appendChild(row);
		}
	}


	private void initDepartureTab() {
		// TODO Auto-generated method stub
		Tabpanel departureTabpanel = new Tabpanel();
		departureTabpanel.setHeight("100%");
		tbbAirportSchedule.getTabpanels().appendChild(departureTabpanel);
		
		Grid grdDeparture = new Grid();
		grdDeparture.setParent(departureTabpanel);
		grdDeparture.setId("grdDeparture");
		grdDeparture.setMold("paging");
		grdDeparture.setPageSize(6);
		
		Columns columns = new Columns();
		columns.setParent(grdDeparture);
		
		Column column = new Column();
		column.setParent(columns);
		column.setLabel("Call sign"); 
		column.setWidth("20%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Flight number"); 
		column.setWidth("20%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Route"); 
		column.setWidth("20%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Status"); 
		column.setWidth("20%");
				
		Rows rows = new Rows();
		rows.setParent(grdDeparture);
		try{
			String airportCode = sensor.getName().substring(sensor.getName().lastIndexOf("(")+1,sensor.getName().lastIndexOf(")"));
			List<FlightRoute> lstFlightRoutes = sensorManager.getAllFlightsWithSpecifiedDeparture(airportCode);
			if(lstFlightRoutes==null) return;
			for(FlightRoute flightRoute:lstFlightRoutes){
				Row row = new Row();
				row.appendChild(new Label(flightRoute.getCallSign()));
				row.appendChild(new Label(flightRoute.getFlightNumber()));
				row.appendChild(new Label(flightRoute.getDeparture()+"-"+flightRoute.getDestination()));
				row.appendChild(new Label("unknown"));
				rows.appendChild(row);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}


	private void initArrivalTab() {
		// TODO Auto-generated method stub
		Tabpanel arrivalTabpanel = new Tabpanel();
		arrivalTabpanel.setHeight("100%");
		tbbAirportSchedule.getTabpanels().appendChild(arrivalTabpanel);
		
		Grid grdArrival = new Grid();
		grdArrival.setParent(arrivalTabpanel);
		grdArrival.setId("grdArrival");
		grdArrival.setMold("paging");
		grdArrival.setPageSize(6);
		
		Columns columns = new Columns();
		columns.setParent(grdArrival);
		
		Column column = new Column();
		column.setParent(columns);
		column.setLabel("Call sign"); 
		column.setWidth("20%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Flight number"); 
		column.setWidth("20%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Route"); 
		column.setWidth("20%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Status"); 
		column.setWidth("20%");
				
		Rows rows = new Rows();
		rows.setParent(grdArrival);
		try{
			String airportCode = sensor.getName().substring(sensor.getName().lastIndexOf("(")+1,sensor.getName().lastIndexOf(")"));
			List<FlightRoute> lstFlightRoutes = sensorManager.getAllFlightsWithSpecifiedArrival(airportCode);
			if(lstFlightRoutes==null) return;			
			for(FlightRoute flightRoute:lstFlightRoutes){
				Row row = new Row();
				row.appendChild(new Label(flightRoute.getCallSign()));
				row.appendChild(new Label(flightRoute.getFlightNumber()));
				row.appendChild(new Label(flightRoute.getDeparture()+"-"+flightRoute.getDestination()));
				row.appendChild(new Label("unknow"));
				rows.appendChild(row);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
