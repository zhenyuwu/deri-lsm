package deri.sensor.components.windows;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Hbox;
import deri.sensor.components.Map.GMaps;
import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.FieldSearchConstainsUtil;
import deri.sensor.hashmaps.util.VirtuosoConstantUtil;
import deri.sensor.javabeans.Observation;
import deri.sensor.javabeans.Place;
import deri.sensor.manager.SensorManager;
import deri.sensor.meta.SensorType;
import deri.sensor.meta.SensorTypeEnum;
import deri.sensor.places.yahoo.YahooWhereURLXMLParser;
import deri.sensor.utils.DateUtil;
import deri.sensor.utils.StringUtil;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class AdvanceSearchWindow extends Window {
	
	private Textbox txtLocation;	
	private Grid grid = new Grid();
	private Rows rows = new Rows();
	private List<Object[]> lstCity;
	private Textbox txtCityLong;
	private Textbox txtCityLat;
	private Decimalbox txtDistance;
	private Textbox txtFromValue;
	//private Decimalbox txtToValue;
	
	private Listbox lstFromValueOperator;
	private Listbox lstSpatialOperator;
	private Listbox lstDateOperator;
	private Listbox lstSensorFiled;
	private Listbox lstSensorType;
	private Combobox cmbResutls;	
	private Button cmdSearch;
	private Button cmdShowData;
	private Datebox txtDate;
	private GMaps map;
	
	private ArrayList<List> listSpatialResult;
	private SensorManager sensormanager;
	private Checkbox chkSensorOnly;
	private String type="flood";
	private Row dateRow;
	private Row valueRow;
	
	public AdvanceSearchWindow(){
		super();
		this.setTitle("Advanced Search");
	}
	
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setLocation(double lat,double lng){
		txtCityLat.setValue(Double.toString(lat));
		txtCityLong.setValue(Double.toString(lng));
	}
	
	public void init(){
		this.setId("AdvanceSearchWindow");
		this.setBorder("normal");
		this.setWidth("500px");
		this.setPosition("center,center");
		this.setContentStyle("overflow:auto");
		this.setClosable(true);
		this.setStyle("border-top: 1px solid #AAB");	
		sensormanager = ServiceLocator.getSensorManager();
		map = (GMaps)this.getParent().getRoot().getFellowIfAny("map");
		initGrid();
		initMap();
		
	}
	
	private void initMap(){
		map = (GMaps)this.getParent().getFellowIfAny("map");
	}
	
	private void initGrid(){
		grid.setParent(this);
		grid.setId("grid");
		grid.setMold("paging");
		grid.setPageSize(20);
		
		Columns columns = new Columns();
		columns.setParent(grid);
		Column column = new Column();
		column.setParent(columns);
		column.setLabel("Properties");
		column.setWidth("20%");
		column = new Column();
		column.setParent(columns);
		column.setLabel("Criteria");
		//column.setAlign("center");
		rows.setParent(grid);
		
		//row 1 add sensor type combobox and sensor filed combobox
		Row r1 = new Row();
		r1.appendChild(new Label("Sensor type"));
		Hbox hbss = new Hbox();
		hbss.setParent(r1);
		hbss.setSpacing("20px");
		lstSensorType= new Listbox();		
		lstSensorType.setParent(hbss);	
		lstSensorType.setMold("select");		
		initialize_lstSensorType();
		
		lstSensorFiled = new Listbox();
		lstSensorFiled.setMold("select");
		lstSensorFiled.setParent(hbss);
		
		chkSensorOnly = new Checkbox("Sensor only");
		chkSensorOnly.setParent(hbss);
		r1.appendChild(hbss);
		rows.appendChild(r1);
		
		//row 2 add city combobox
		Row r2 = new Row();
		r2.appendChild(new Label("Location"));
		hbss = new Hbox();
		hbss.setParent(r2);
		
		txtLocation = new Textbox();		
		txtLocation.setParent(hbss);	
		txtLocation.setTooltiptext("Press Enter after input");
		hbss.appendChild(new Label("Please input your location or you can click on the map"));
		rows.appendChild(r2);
		
		//row 3 add latitude text box
		Row r3 = new Row();
		r3.appendChild(new Label("Lat"));
		txtCityLat = new Textbox();
		txtCityLat.setWidth("90%");		
		r3.appendChild(txtCityLat);
		rows.appendChild(r3);
		
		//row 4 add longitude text box
		Row r4 = new Row();
		r4.appendChild(new Label("Long"));
		txtCityLong = new Textbox();
		txtCityLong.setWidth("90%");		
		r4.appendChild(txtCityLong);
		rows.appendChild(r4);
		
		//row 5 add Date time
		dateRow = new Row();
		dateRow.appendChild(new Label("Date"));
		Hbox hb1 = new Hbox();
		hb1.setParent(dateRow);
		hb1.setSpacing("20px");
		lstDateOperator = new Listbox();
		lstDateOperator.setRows(1);
		lstDateOperator.setMold("select");
		lstDateOperator.setParent(hb1);
		initialize_lstDateOperator();
		
		txtDate = new Datebox(); 
		txtDate.setParent(hb1);
		txtDate.setWidth("100%");
		txtDate.setCols(16);
		txtDate.setFormat("yyyy-MM-dd hh:mm:ss");
		txtDate.setValue(new Date());		
		rows.appendChild(dateRow);
		
        //add value criteria
        valueRow = new Row();
		valueRow.appendChild(new Label("Value"));		
		Hbox hb2 = new Hbox();
		hb2.setParent(valueRow);
		hb2.setSpacing("10px");
		
		lstFromValueOperator = new Listbox();
		lstFromValueOperator.setMold("select");
		lstFromValueOperator.setMultiple(false);
		lstFromValueOperator.setParent(hb2);
		initialize_valueSearchOpertor();
		
		txtFromValue = new Textbox();
		txtFromValue.setWidth("70%");
		txtFromValue.setParent(hb2);
		
//		hb2.appendChild(new Label("To"));
//		
//		txtToValue = new Decimalbox();		
//		txtToValue.setParent(hb2);
//		txtToValue.setWidth("70%");
//		txtToValue.setReadonly(true);
		
		rows.appendChild(valueRow);
		
		//row 6 add spatial operator
		Row r6 = new Row();
		r6.appendChild(new Label("Spatial"));		
		lstSpatialOperator = new Listbox();
		lstSpatialOperator.setRows(1);		
		lstSpatialOperator.setMold("select");
		lstSpatialOperator.setMultiple(false);
		ListModelList lm2 = new ListModelList(FieldSearchConstainsUtil.spatialOperator);
        lm2.addSelection(lm2.get(0));
        lstSpatialOperator.setModel(lm2);
        lstSpatialOperator.setParent(r6);                
        rows.appendChild(r6);
        
		
		//add distance from city
		final Row r8 = new Row();
		r8.appendChild(new Label("Distance"));
		
		hb2 = new Hbox();
		hb2.setParent(r8);
		hb2.setWidth("80%");
		hb2.setSpacing("5px");
		
		txtDistance = new Decimalbox();
		txtDistance.setWidth("90%");
		txtDistance.setText("0");
		txtDistance.setParent(hb2);
		hb2.appendChild(new Label("km"));
		rows.appendChild(r8);
		
		//add result row
		Row r9 = new Row();
		r9.appendChild(new Label("Sensor"));
		cmbResutls = new Combobox();
		cmbResutls.setMold("rounded");
		cmbResutls.setAutodrop(true);		
		cmbResutls.setParent(r9);
		rows.appendChild(r9);
		
		//add Search button
		Row r10 = new Row();
		r10.setAlign("center");		
		r10.appendChild(new Label());
		
		hb2 = new Hbox();
		hb2.setParent(r10);
		hb2.setSpacing("10px");
		
		cmdSearch = new Button("Search","/imgs/Button/16x16/button_search.png");
		cmdSearch.setParent(hb2);
		
		cmdShowData = new Button("Details","/imgs/Button/16x16/button_showdata.png");
		cmdShowData.setParent(hb2);
		cmdShowData.setDisabled(true);
		rows.appendChild(r10);
		
		lstSpatialOperator.addEventListener(Events.ON_SELECT, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				if(lstSpatialOperator.getSelectedItem().getLabel().equals("st_contains"))
					r8.setVisible(false);
				else r8.setVisible(true);
			}
		});
		addEventListener();
	}
	
	private void addEventListener(){
		lstSensorType.addEventListener(Events.ON_SELECT , new EventListener() {
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub				
				setType(lstSensorType.getSelectedItem().getLabel().toLowerCase());
				if(type.equals("webcam")||type.equals("satellite")||type.equals("cosm")
					||type.equals("radar")||type.equals("traffic")||type.equals("all")){				
					valueRow.setVisible(false);
					lstSensorFiled.setVisible(false);
					dateRow.setVisible(true);
//				}else if(type.equals("railwaystation")){
//					valueRow.setVisible(false);
//					dateRow.setVisible(false);
//					lstSensorFiled.setVisible(false);
				}else if(type.equals("roadactivity")){
					valueRow.setVisible(true);
					txtFromValue.setVisible(false);
					dateRow.setVisible(true);
					lstSensorFiled.setVisible(true);
					initialize_lstSensorField(lstSensorType.getSelectedItem().getLabel().toLowerCase());
					initialize_valueSearchForRoadActivity(lstSensorFiled.getModel().getElementAt(0).toString());
				}else{				
					valueRow.setVisible(true);
					txtFromValue.setVisible(true);
					dateRow.setVisible(true);
					lstSensorFiled.setVisible(true);
					initialize_lstSensorField(lstSensorType.getSelectedItem().getLabel().toLowerCase());
					initialize_valueSearchOpertor();
				}
			}
		});
			
		lstSensorFiled.addEventListener(Events.ON_SELECT, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				if(type.equals("roadactivity"))
					initialize_valueSearchForRoadActivity(lstSensorFiled.getSelectedItem().getLabel());				
			}
		});
		
		txtLocation.addEventListener(Events.ON_OK, new EventListener() {			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				String address = txtLocation.getValue();
				String locationPra = 
						(address.trim().equals("")?  ""  : (address + ", "))
						+ txtCityLat.getValue()+","+txtCityLong.getValue();
				Place place = YahooWhereURLXMLParser.placeStr2PlaceObj(locationPra);
				if(place!=null){					
					txtCityLat.setValue(Double.toString(place.getLat()));
					txtCityLong.setValue(Double.toString(place.getLng()));
					map.setCenter(place.getLat(),place.getLng());
					map.setZoom(8);
				}
			}
		});
		
		cmdSearch.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				if(txtLocation.getValue().equals("")&&(txtCityLat.equals("")||txtCityLong.equals("")))
					Messagebox.show("Please input your location.", "Error", Messagebox.OK, Messagebox.ERROR);
				else{
					map.setCircleRadius(Double.parseDouble(txtDistance.getText()));
					map.setCircleLat(Double.parseDouble(txtCityLat.getText()));
					map.setCircleLng(Double.parseDouble(txtCityLong.getText()));
					getSearchResults();
					cmdShowData.setDisabled(false);
				}
			}
		});
		
		cmdShowData.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				showResultsData();
				cmdShowData.setDisabled(true);				
			}
		});
		//event for sensor only checkbox - when it is selected, the search function is only for the sensor,not sensor data
		chkSensorOnly.addEventListener(Events.ON_CHECK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				if(chkSensorOnly.isChecked()){					
					valueRow.setVisible(false);
					dateRow.setVisible(false);
				}else{
					if(type.equals("webcam")||type.equals("satellite")||type.equals("cosm")
							||type.equals("radar")||type.equals("traffic")||type.equals("all")){				
							valueRow.setVisible(false);							
					}else						
						valueRow.setVisible(true);
					dateRow.setVisible(true);
				}
			}
		});
	}
	
	private void initialize_lstSensorType(){
		List<String> lstSensor = sensormanager.getAllSensorType();
		Iterator iter = lstSensor.iterator();
		while(iter.hasNext()){
			String type = iter.next().toString();
			if(!(type.equals(SensorTypeEnum.ADSBHub.toString())||type.equals(SensorTypeEnum.cosm.toString())))
				lstSensorType.appendChild(new Listitem(type));			
		}
		lstSensorType.appendChild(new Listitem("all"));
		lstSensorType.setSelectedIndex(0);
	}
	
	private void initialize_lstSensorField(String sensorType){
		List<String> temp = sensormanager.getAllSensorPropertiesForSpecifiedSensorType(type).get(1);				
		List<String> fieldList = FieldSearchConstainsUtil.sensorFieldLabel.get(sensorType);
		if (fieldList==null){
			fieldList = new ArrayList<String>();
			for(String tbl:temp){
				tbl = tbl.substring(tbl.lastIndexOf("#")+1);
				fieldList.add(tbl);
			}			
		}
		ListModelList lm2 = new ListModelList(fieldList);
        lm2.addSelection(lm2.get(0));         
        lstSensorFiled.setModel(lm2);        
	}
	
	@SuppressWarnings("unchecked")
	private void initialize_lstDateOperator(){
		List lstOperator = new ArrayList<String>();
		lstOperator.add("latest");
		Set set = FieldSearchConstainsUtil.timeOperator.entrySet();
		Iterator iter = set.iterator();
		while(iter.hasNext()){
			Map.Entry me = (Map.Entry)iter.next();
			lstOperator.add(me.getKey());
		}		
		
		ListModelList lm2 = new ListModelList(lstOperator);
		lm2.addSelection(lm2.get(0));
		lstDateOperator.setModel(lm2);
        
        //lstDateOperator.setSelectedIndex(0);
	}
	
	@SuppressWarnings("unchecked")
	private void initialize_valueSearchOpertor(){
		List<String> lstOperator = new ArrayList<String>();
		Set set = FieldSearchConstainsUtil.valueSearchOperator.entrySet();
		Iterator iter = set.iterator();
		while(iter.hasNext()){
			Map.Entry me = (Map.Entry)iter.next();
			lstOperator.add((String) me.getKey());
		}		
		ListModelList lm2 = new ListModelList(lstOperator);
		lm2.addSelection(lm2.get(0));         
        lstFromValueOperator.setModel(lm2);
        //lstFromValueOperator.setSelectedIndex(0);
	}
	
	private void initialize_valueSearchForRoadActivity(String roadActivityField){
		@SuppressWarnings("unused")
		List lstOperator = new ArrayList<String>();
		ListModelList lm2 = new ListModelList(FieldSearchConstainsUtil.roadActivityField2ValueOperator
							.get(roadActivityField));      
		lm2.addSelection(lm2.get(0));
        lstFromValueOperator.setModel(lm2);
        //lstFromValueOperator.setSelectedIndex(0);
	}
	
	public void showResultsData(){		
		if(this.getRoot().getFellowIfAny("SearchSpatialResultsWindow") != null){
			this.getRoot().getFellowIfAny("SearchSpatialResultsWindow").detach();
		}
		SearchSpatialResultsWindow searchResult = new SearchSpatialResultsWindow();
		searchResult.setType(type);
		searchResult.setTitle("Data results");
		searchResult.setParent(this.getParent());
		searchResult.setSpatialResultList(listSpatialResult);
		searchResult.init();
		searchResult.addContent();
		searchResult.doOverlapped();
	}	
		
	public void showResults(){
		try{
			ListModelList resultsModel = new ListModelList(listSpatialResult.get(1));
			resultsModel.addSelection(resultsModel.get(0));
			cmbResutls.setModel(resultsModel);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void getSearchResults(){	
		try{
			double dis = Double.parseDouble(txtDistance.getText());
			dis = dis==0?0.1:dis;
			if(type.equals(SensorType.all))
				listSpatialResult = sensormanager.getAllSensorHasLatLongWithSpatialCriteria(lstSpatialOperator.getSelectedItem().getLabel(), 
						Double.parseDouble(txtCityLong.getText()), Double.parseDouble(txtCityLat.getText()),
						dis);
			else
				listSpatialResult = sensormanager.getAllSensorHasLatLongWithSpatialCriteria(lstSensorType.getSelectedItem().getLabel().toLowerCase(), lstSpatialOperator.getSelectedItem().getLabel(), 
					Double.parseDouble(txtCityLong.getText()), Double.parseDouble(txtCityLat.getText()),
					dis);
			if((!chkSensorOnly.isChecked())&&(listSpatialResult!=null)){				
				List<String> listSource = listSpatialResult.get(0);
				List<List> listDataResult = new ArrayList<List>();	
				List removeArry = new ArrayList();
				
				List<String> observations = null;
				for(String sensorId:listSource){
					String tbl="";
					//parse time operator
					String t = lstDateOperator.getSelectedItem().getLabel().toString();
					String timeOper = t.equals("latest")?"latest":FieldSearchConstainsUtil.timeOperator.get(t);
					
					String dateTime = DateUtil.date2StandardString(txtDate.getValue());
					String value = txtFromValue.getText();
					String operValue = FieldSearchConstainsUtil.valueSearchOperator.get(lstFromValueOperator.getSelectedItem().getLabel().toString());
					if(valueRow.isVisible()){
					   tbl = StringUtil.trimBlanksInner(lstSensorFiled.getSelectedItem().getLabel().toString());
					   if(FieldSearchConstainsUtil.valueSearchOperator.
								containsValue(operValue)){						
							observations = sensormanager.getObservationsForNonSpatialCriteria(
									sensorId,timeOper,dateTime,tbl,operValue,value);
						}else if(FieldSearchConstainsUtil.roadActivityFieldLabel.contains(lstSensorFiled.getSelectedItem().getLabel().toString())){
							observations = sensormanager.getObservationsForNonSpatialCriteria(
									sensorId,timeOper,dateTime,tbl,"=",lstFromValueOperator.getSelectedItem().getLabel().toString());
						}
					}
					else {
						tbl = "Picture";
						observations = sensormanager.getObservationsForNonSpatialCriteria(
								sensorId,timeOper,dateTime,"WebcamSnapShot",null,null);
					}
					
					if(observations.size()<=0)
						removeArry.add(sensorId);				
					else
						listDataResult.add(observations);
						
				}
				Iterator it = removeArry.iterator();
				while(it.hasNext()){
					int j = listSpatialResult.get(0).indexOf(it.next());
					listSpatialResult.get(0).remove(j);
					listSpatialResult.get(1).remove(j);
					listSpatialResult.get(2).remove(j);
				}				
				listSpatialResult.add(listDataResult);		
				showResults();
			}			
		}catch(Exception e){
			e.printStackTrace();
		}		
	}
}
