package deri.sensor.components.windows;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.SensorTypeToProperties;
import deri.sensor.javabeans.*;
import deri.sensor.london.railwaytation.PredictionDetailedXMLParser;
import deri.sensor.manager.SensorManager;
import deri.sensor.meta.FilterSensorLabels;
import deri.sensor.meta.SensorTypeEnum;
import deri.sensor.utils.ConstantsUtil;
import deri.sensor.utils.DateUtil;
import deri.sensor.utils.NumberUtil;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class SensorWithSourceWindow extends Window {
	private static final long serialVersionUID = -3900618895663270180L;
	private String type;
	private Grid grid = new Grid();
	private Rows rows = new Rows();
	private SensorManager sensorManager = ServiceLocator.getSensorManager();
	
	
	public SensorWithSourceWindow() {
		super();
	}
	
	public SensorWithSourceWindow(String title) {
		this();
		this.setTitle(title);
	}
	
	public SensorWithSourceWindow(String type, String title) {
		this(title);
		this.type = type;
	}

	public void init(){
		this.setId("sensorWithSourceWindow");
		this.setBorder("normal");
		this.setWidth("40%");
		this.setPosition("top,left");
		this.setContentStyle("overflow:auto");
		this.setClosable(true);
		this.setSizable(true);
		if(type.endsWith(SensorTypeEnum.ADSBHub.toString()))
			initFlightInforGrid();
		else
			initGrid();
	}
	
	private void initGrid(){
		grid.setParent(this);
		grid.setId("grid");
		grid.setMold("paging");
		grid.setPageSize(10);
		
		
		Columns columns = new Columns();
		columns.setParent(grid);
		columns.setSizable(true);
		
		Column column = new Column();
		column.setParent(columns);
		column.setLabel("Sensor Information");
		column.setWidth("40%");
		
//		column = new Column();
//		column.setParent(columns);
//		column.setLabel("Location");
//		column.setWidth("30%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Data");
		column.setAlign("center");
		rows.setParent(grid);
	}

	private void initFlightInforGrid(){
		grid.setParent(this);
		grid.setId("grid");
		grid.setMold("paging");
		grid.setPageSize(10);
		grid.setVflex(true);
		
		Columns columns = new Columns();
		columns.setParent(grid);
		columns.setSizable(true);
		
		Column column = new Column();
		column.setParent(columns);
		column.setLabel("Flight CIAO");
		column.setWidth("20%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Flight");
		column.setWidth("15%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Latest location");
		column.setWidth("30%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Altitude");
		column.setWidth("15%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Speed");
		column.setWidth("10%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Departure");
		column.setWidth("15%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Transit");
		column.setWidth("15%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Destination");
		column.setWidth("15%");
		
//		column = new Column();
//		column.setParent(columns);
//		column.setLabel("Time");
//		column.setWidth("30%");
		
		rows.setParent(grid);
	}

	public void addContent(double lat, double lng){
		try{
			if(type.equals(SensorTypeEnum.ADSBHub.toString())){
				Observation obs = sensorManager.getObservationForOneFlightWithLatLng(lat,lng);
				addOneRowForOneFlight(obs);
			}else{
				List<Sensor> lstSensor = sensorManager.getAllSensorWithSpecifiedLatLngSensorType(lat,lng,type);
				for(Sensor sensor: lstSensor)
					addOneRowForOneSensor(sensor);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void addOneRowForOneFlight(Observation obs){
		Row row = new Row();
		row.setParent(rows);
		try{
			FlightCIAO flightCIAO = sensorManager.getFlightCIAOForOneObservation(obs.getId());			
			row.appendChild(new Label(flightCIAO.getValue()));
			
			CallSign callSign = sensorManager.getFlightCallSignForOneObservation(obs.getId());			
			row.appendChild(new Label(callSign.getValue()));
			
			Latitude latitude = sensorManager.getFlightLatitudeForOneObservation(obs.getId());
			Longitude longitude = sensorManager.getFlightLongitudeForOneObservation(obs.getId());
			row.appendChild(new Label(latitude.getValue()+","+longitude.getValue()));
						
			Altitude altitude = sensorManager.getFlightAltitudeForOneObservation(obs.getId());
			row.appendChild(new Label(altitude.getValue()+" "+ altitude.getUnit()));
			
			Speed speed = sensorManager.getFlightSpeedForOneObservation(obs.getId());
			row.appendChild(new Label(speed.getValue()+" "+speed.getUnit()));
			
			final FlightRoute route = sensorManager.getFlightRouteForOneFlight(callSign.getValue());
			if(route!=null){
				Toolbarbutton code = new Toolbarbutton();
				code.setParent(row);
				code.setStyle("color:blue");
				code.setLabel(route.getDeparture());
				code.addEventListener(Events.ON_CLICK, new EventListener(){
					@Override
					public void onEvent(Event event) throws Exception {	
						Airport airport = sensorManager.getAirportWithAirportCode(route.getDeparture());
						showAirportInforWindow(airport);
					}
				});
				
				row.appendChild(new Label(route.getTransit()));
				
				code = new Toolbarbutton();
				code.setStyle("color:blue");
				code.setParent(row);
				code.setLabel(route.getDestination());
				code.addEventListener(Events.ON_CLICK, new EventListener(){
					@Override
					public void onEvent(Event event) throws Exception {
						Airport airport = sensorManager.getAirportWithAirportCode(route.getDestination());
						showAirportInforWindow(airport);
					}
				});
			}else{
				row.appendChild(new Label("unknow"));
				row.appendChild(new Label("unknow"));
				row.appendChild(new Label("unknow"));
			}
			
			//row.appendChild(new Label(callSign.getTimes().toString()));
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected void showAirportInforWindow(Airport airport) {
		// TODO Auto-generated method stub
		if(this.getRoot().getFellowIfAny("airportWindow") != null){
			this.getRoot().getFellowIfAny("airportWindow").detach();
		}		
		AirportInforWindow airportInfor = new AirportInforWindow();
		airportInfor.init();
		airportInfor.setParent(this.getRoot());
		airportInfor.addContent(airport);
		airportInfor.doOverlapped();
	}

	private void addOneRowForOneSensor(final Sensor sensor) {		
		// TODO Auto-generated method stub
		try{
			Row row = new Row();
			row.setParent(rows);
			String author = "";
			String stationid = "";
			String placeSimpleContent = "";
	
	
			boolean isShow = true;
	
			placeSimpleContent = sensor.getPlace().getSimpleContent();		

			Label label;
			if(FilterSensorLabels.getLabelForSensorType(sensor.getSensorType())!=null)
				label = new Label(FilterSensorLabels.getLabelForSensorType(sensor.getSensorType())
					+" sensor "+ sensor.getName() +" at "+placeSimpleContent);
			else 
				label = new Label(sensor.getSensorType()
						+" sensor "+ sensor.getName() +" at "+placeSimpleContent);
			label.setParent(row);

			Hbox hb1 = new Hbox();
			hb1.setParent(row);
			hb1.setSpacing("10px");
			if(type.equals(SensorTypeEnum.railwaystation.toString())){
				final boolean fisShow = isShow;
				Html htm = new Html();
				htm.setContent("<P ALIGN=left><MARQUEE WIDTH=100% HEIGHT=30 BEHAVIOR=scroll SCROLLAMOUNT=\"3\">"
							+getRailwayStationContent(sensor)+"</MARQUEE>");
				htm.setParent(hb1);
				htm.addEventListener(Events.ON_CLICK, new EventListener() {
					
					@Override
					public void onEvent(Event event) throws Exception {
						// TODO Auto-generated method stub
						showSensorWindow(sensor,fisShow);
					}
				});
			}else{
				final boolean fisShow = isShow;
				Toolbarbutton bar = new Toolbarbutton();
				bar.setParent(hb1);
				bar.setStyle("color:blue");
				bar.setImage("/imgs/Button/22x22/button_update.png");
				bar.setTooltiptext("Latest data");
				bar.addEventListener(Events.ON_CLICK, new EventListener(){
					@Override
					public void onEvent(Event event) throws Exception {
						if(sensor.getSensorType().equals(SensorTypeEnum.airport.toString()))
							showAirportScheduleWindow(sensor);
						else
							showSensorWindow(sensor,fisShow);
					}
				});
			}
			if(!sensor.getSensorType().equals(SensorTypeEnum.airport.toString())){
				Toolbarbutton allDatabar = new Toolbarbutton();
				allDatabar.setParent(hb1);
				allDatabar.setStyle("color:blue");			
				allDatabar.setTooltiptext("Triple data");
				allDatabar.setImage("/imgs/Button/button_rdf.png");
				final String sId = sensor.getId();
				allDatabar.addEventListener(Events.ON_CLICK, new EventListener(){
					@Override
					public void onEvent(Event event) throws Exception {				
						showAllSensorDataWindowFromSource(sId);
					}
				});
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected void showAirportScheduleWindow(Sensor sensor) {
		// TODO Auto-generated method stub
		if(this.getRoot().getFellowIfAny("AirportFlightScheduleWindow") != null){
			this.getRoot().getFellowIfAny("AirportFlightScheduleWindow").detach();
		}
		AirportFlightScheduleWindow airSche = new AirportFlightScheduleWindow(sensor);
		airSche.setParent(this.getRoot());		
		airSche.init();
		airSche.doOverlapped();
	}

	private void showAllSensorDataWindowFromSource(String source){		
		if(this.getRoot().getFellowIfAny("AllDataSensor") != null){
			this.getRoot().getFellowIfAny("AllDataSensor").detach();
		}
		
		AllDataOfSensorWindow allDataWindow = new AllDataOfSensorWindow();		
		allDataWindow.setParent(this.getRoot());
		allDataWindow.setType(type);
		allDataWindow.addContent(source);
		allDataWindow.doOverlapped();
	}
	
	
	private void showSensorWindow(Sensor sensor,boolean isShow){
		String author = sensor.getAuthor();
		if(this.getRoot().getFellowIfAny("sensorWindow") != null){
			this.getRoot().getFellowIfAny("sensorWindow").detach();
		}		
		SensorWindow sensorWindow = new SensorWindow(FilterSensorLabels.getLabelForSensorType(type) + " sensor "  + " data",type);
		sensorWindow.setParent(this.getRoot());
		sensorWindow.addContent(sensor);
		if(isShow)
			sensorWindow.doOverlapped();
	}
	
	private String getRailwayStationContent(Sensor sensor){
		String htmlContent="";
		PredictionDetailedXMLParser preParser = new PredictionDetailedXMLParser();
		preParser.getPredictionDetailedForOneStation(sensor);
		Observation observation = sensorManager.getNewestObservationForOneSensor(sensor.getId());
		List<String> observations = sensorManager.getObservationsWithTimeCriteria(sensor.getId(), "=", observation.getTimes(), null);
//		List<String> signs = SensorTypeToProperties.sensorTypeToTableProperties.get(type);
		
		for(String obs:observations){			
			String content=null;
			List<ArrayList> readings = sensorManager.getReadingDataOfObservation(obs);
			for(ArrayList reading : readings){
				String sign = reading.get(0).toString().substring(reading.get(0).toString().lastIndexOf("#")+1);			
				String unit=null;
				if(sign.equals("SecondToStation"))
					sign = "ArriveToNextStation";
				else if(sign.equals("TimeToStation"))
					sign = "ArriveToDestination";
				htmlContent+="<font size=\"2\" color=\"red\">";
				htmlContent+="<b>"+sign+": "+"<b></font>";
				try{
					content = reading.get(1).toString();
					unit = reading.get(2).toString();				
				}catch(Exception e){}
				if(unit == null){
					unit = "";
				}
				try {				
					htmlContent+="<font size=\"2\" color=\"green\">";
					if(content == null){
						htmlContent+="not_supported";
					}if(NumberUtil.isDouble(content)&&unit!=""){						
						int data =(int)Double.parseDouble(content);
						if(unit.equals("second"))
							htmlContent+=NumberUtil.secondsToHHMMSS(data);
						else
							htmlContent+=NumberUtil.minutesToHHMM(data);
					}else{
						htmlContent+=content;
					}
					htmlContent+=".</font>";
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return htmlContent;
	}
	public void clearContent(){
		while(this.getFirstChild() != null){
			this.getFirstChild().detach();
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
