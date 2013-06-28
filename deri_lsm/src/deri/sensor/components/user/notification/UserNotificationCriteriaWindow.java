package deri.sensor.components.user.notification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import deri.sensor.components.Map.GMaps;
import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.FieldSearchConstainsUtil;
import deri.sensor.javabeans.Place;
import deri.sensor.manager.SensorManager;
import deri.sensor.meta.SensorTypeEnum;
import deri.sensor.places.yahoo.YahooWhereURLXMLParser;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class UserNotificationCriteriaWindow extends Window{
	
	private Textbox txtLocation;	
	private Grid grid = new Grid();
	private Rows rows = new Rows();
	private double radius=0;
	private List<Object[]> lstCity;
	private Textbox txtCityLong;
	private Textbox  txtCityLat;
	private Decimalbox txtDistance;
	private Decimalbox txtFromValue;
//	private Decimalbox txtToValue;
	
	private Listbox lstFromValueOperator;
	private Listbox lstSpatialOperator;
	private Listbox lstSensorFiled;
	private Listbox lstSensorType;
	
	private Button cmdNext;
	private Button cmdCancel;
	private GMaps map;
	
	private SensorManager sensormanager;
	private String type;
	private Row dateRow;
	private Row valueRow;
	private Hbox fromValueHbox;
	private int order = 0;
	private List spatialPara;
	
	public UserNotificationCriteriaWindow(){
		super();
		init();
	}
	
		
	public void init(){
		this.setTitle("Step 1 - Set notification criteria");
		this.setId("NotificationCriteriasWizard");
		this.setPosition("center,center");
		this.setContentStyle("overflow:auto");
		this.setClosable(true);
		this.setStyle("border-top: 1px solid #AAB");
		this.setWidth("600px");
		sensormanager = ServiceLocator.getSensorManager();
		initGrid();		
	}
	
	public int getOrder(){
		return order;
	}
	
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private void initGrid(){
		grid.setParent(this);
		grid.setId("grid");
		grid.setMold("paging");
		grid.setPageSize(15);
		grid.setStyle("overflow:auto");
		
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
		rows.appendChild(r1);
		
		//row 2 add city combobox
		Row r2 = new Row();
		r2.appendChild(new Label("City"));
		txtLocation = new Textbox();
		txtLocation.setParent(r2);		
		r2.appendChild(txtLocation);
		rows.appendChild(r2);
		
		//row 3 add latitude text box
		Row r3 = new Row();
		r3.appendChild(new Label("Lat"));
		txtCityLat = new Textbox();
		txtCityLat.setWidth("100%");		
		r3.appendChild(txtCityLat);
		rows.appendChild(r3);
		
		//row 4 add longitude text box
		Row r4 = new Row();
		r4.appendChild(new Label("Long"));
		txtCityLong = new Textbox();
		txtCityLong.setWidth("100%");		
		r4.appendChild(txtCityLong);
		rows.appendChild(r4);
		
		//row 5 add Date time
		dateRow = new Row();
		dateRow.appendChild(new Label("Date"));
		Hbox hb1 = new Hbox();
		hb1.setParent(dateRow);
		hb1.setSpacing("10px");
		
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
		
		fromValueHbox = new Hbox();
		fromValueHbox.setParent(hb2);
		fromValueHbox.setSpacing("10px");
		txtFromValue = new Decimalbox();
		txtFromValue.setWidth("70%");
		txtFromValue.setParent(fromValueHbox);
		txtFromValue.setText("0");
		
//		fromValueHbox.appendChild(new Label("To"));
		
//		txtToValue = new Decimalbox();		
//		txtToValue.setParent(fromValueHbox);
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
		txtDistance = new Decimalbox();
		txtDistance.setWidth("90%");
		txtDistance.setText("0");
		r8.appendChild(txtDistance);
		rows.appendChild(r8);
		
		//add Search button
		Row r9 = new Row();
		r9.setAlign("center");		
		r9.appendChild(new Label());
		
		Hbox hboxWButton = new Hbox();
		hboxWButton.setParent(r9);
		
		cmdCancel = new Button("Cancel","/imgs/Button/22x22/button_cancel.png");
		cmdCancel.setParent(hboxWButton);
		cmdCancel.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				UserNotificationCriteriaWindow.this.getParent().detach();
			}
		});
		
		cmdNext = new Button("Next","/imgs/Button/button_next.png");
		cmdNext.setParent(hboxWButton);		
		rows.appendChild(r9);
		
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
						||type.equals("radar")||type.equals("traffic")){				
					valueRow.setVisible(false);
					lstSensorFiled.setVisible(false);
				}else if(type.equals("roadactivity")){				
					valueRow.setVisible(true);
					fromValueHbox.setVisible(false);
					lstSensorFiled.setVisible(true);
					initialize_lstSensorField(lstSensorType.getSelectedItem().getLabel().toLowerCase());
					initialize_valueSearchForRoadActivity(lstSensorFiled.getModel().getElementAt(0).toString());
				}else{				
					valueRow.setVisible(true);
					fromValueHbox.setVisible(true);
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
				}
			}
		});
		
		cmdNext.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				moveToNextStep();
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
	
	
	private void initialize_valueSearchOpertor(){
		List lstOperator = new ArrayList<String>();
		Set set = FieldSearchConstainsUtil.valueSearchOperator.entrySet();
		Iterator iter = set.iterator();
		while(iter.hasNext()){
			Map.Entry me = (Map.Entry)iter.next();
			lstOperator.add(me.getKey());
		}		
		ListModelList lm2 = new ListModelList(lstOperator);		       
		lm2.addSelection(lm2.get(0));   
        lstFromValueOperator.setModel(lm2);        
	}
	
	private void initialize_valueSearchForRoadActivity(String roadActivityField){		
		ListModelList lm2 = new ListModelList(FieldSearchConstainsUtil.roadActivityField2ValueOperator
							.get(roadActivityField));   
		lm2.addSelection(lm2.get(0));   
        lstFromValueOperator.setModel(lm2);        
	}
	
	
	@SuppressWarnings("unchecked")
	public void setSpatialPara(){
		spatialPara = new ArrayList();
		spatialPara.add(this.type);
		spatialPara.add(lstSpatialOperator.getSelectedItem().getLabel());
		spatialPara.add(Double.parseDouble(txtCityLong.getText()));
		spatialPara.add(Double.parseDouble(txtCityLat.getText()));
		spatialPara.add(Double.parseDouble(txtDistance.getText()));
		if(valueRow.isVisible()){
			spatialPara.add(lstSensorFiled.getSelectedItem().getLabel().toString());
			if(fromValueHbox.isVisible()){
				spatialPara.add(lstFromValueOperator.getSelectedItem().getLabel().toString());
				spatialPara.add(txtFromValue.getText());
			}else{
				spatialPara.add("equal");
				spatialPara.add(lstFromValueOperator.getSelectedItem().getLabel().toString());
			}
		}else{
			spatialPara.add("");
			spatialPara.add("");
			spatialPara.add("");
		}
	}
	
	public List getSpatialPara(){
		return spatialPara;
	}
	
	private void moveToNextStep(){		
		UserNotificationWizard u = (UserNotificationWizard) this.getParent();
		u.moveToStep(this.getOrder()+1);
	}
}
