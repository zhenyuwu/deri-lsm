package deri.sensor.components.user.datamanagent;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;

import deri.sensor.components.windows.SparqlResultWindow;
import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.FieldSearchConstainsUtil;
import deri.sensor.javabeans.Observation;
import deri.sensor.javabeans.Place;
import deri.sensor.javabeans.Sensor;
import deri.sensor.javabeans.User;
import deri.sensor.manager.SensorManager;
import deri.sensor.meta.SensorTypeEnum;
import deri.sensor.places.yahoo.YahooWhereURLXMLParser;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class UserDataMashupTab extends Tabpanel {
	private Grid grdSensor = new Grid();
	private Groupbox grbSensorType;
	private SensorManager sensorManager;
	private HashMap<String,Boolean> lstSensorType;	
	private List<Checkbox> chkSensorTypes;
	private User user;
	private List<Sensor> lstSensor;
	private Button cmdMashup;
	private Textbox txtLocation;
	private Datebox txtStartDate;
	private Datebox txtExpiredDate;
	private Listbox lstDateOperator;
	private Label lblToDate;
	private Doublebox txtCityLat,txtCityLong;
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setLocation(double lat,double lng){
		txtCityLat.setValue(lat);
		txtCityLong.setValue(lng);
	}
	
	public void init(){	
		this.setId("DataMashupTabpanel");
		sensorManager = ServiceLocator.getSensorManager();
		initUI();
		addEventListener();
	}

	
	private void addEventListener() {
		
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
					txtCityLat.setValue(place.getLat());
					txtCityLong.setValue(place.getLng());
				}
			}
		});
		
		// TODO Auto-generated method stub
		cmdMashup.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				showMashupResults();				
			}
		});
		
		lstDateOperator.addEventListener(Events.ON_SELECT, new EventListener() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				if(lstDateOperator.getSelectedItem().getLabel().toString().equals("from")){
					lblToDate.setVisible(true);
					txtExpiredDate.setVisible(true);
				}else{
					lblToDate.setVisible(false);
					txtExpiredDate.setVisible(false);
				}
			}
		});
	}

	private void initUI() {
		// TODO Auto-generated method stub
		grbSensorType = new Groupbox();
		grbSensorType.setParent(this);
		Caption capSer = new Caption("Sensor type");
		capSer.setParent(grbSensorType);
		initialize_lstSensorType();
		for(String type:lstSensorType.keySet()){
			final Checkbox chkType = new Checkbox(type);
			chkType.setParent(grbSensorType);
			chkType.addEventListener(Events.ON_CHECK, new EventListener() {
				@Override
				public void onEvent(Event event) throws Exception {
					// TODO Auto-generated method stub
					updateTypeSelected(chkType.getLabel(),chkType.isChecked());
				}
			});
		}
		
		grdSensor.setParent(this);
		grdSensor.setId("grdMashupSensor");
		grdSensor.setMold("paging");
		grdSensor.setPageSize(6);
		
		Columns columns = new Columns();
		columns.setParent(grdSensor);
		Column column = new Column();
		column.setParent(columns);
		column.setLabel("Properties"); 
		column.setWidth("20%");
		column = new Column();
		column.setParent(columns);
		column.setLabel("Criteria");
		Rows rows = new Rows();
		rows.setParent(grdSensor);
		
		Row r1 = new Row();
		r1.appendChild(new Label("Location"));
		Hbox hb1 = new Hbox();
		hb1.setParent(r1);
		txtLocation = new Textbox();		
		txtLocation.setParent(hb1);		
		hb1.appendChild(new Label("Please input your location or you can click on the map"));
		rows.appendChild(r1);
		
		//row 2 add latitude text box
		Row r2 = new Row();
		r2.appendChild(new Label("Lat"));
		txtCityLat = new Doublebox();
		txtCityLat.setWidth("90%");		
		r2.appendChild(txtCityLat);
		rows.appendChild(r2);
		
		//row 3 add longitude text box
		Row r3 = new Row();
		r3.appendChild(new Label("Long"));
		txtCityLong = new Doublebox();
		txtCityLong.setWidth("90%");		
		r3.appendChild(txtCityLong);
		rows.appendChild(r3);
		
		Row r4 = new Row();
		r4.appendChild(new Label("Time criteria"));
		Hbox hbox1 = new Hbox();
		hbox1.setParent(r4);
		
		lstDateOperator = new Listbox();
		lstDateOperator.setRows(1);
		lstDateOperator.setMold("select");
		lstDateOperator.setParent(hbox1);
		initialize_lstDateOperator();
				
		txtStartDate = new Datebox(new Date());		
		txtStartDate.setFormat("M/d/yy KK:mm a");		
		txtStartDate.setParent(hbox1);
		
		lblToDate = new Label("to");
		lblToDate.setVisible(false);
		lblToDate.setParent(hbox1);
		
		txtExpiredDate = new Datebox(new Date());		
		txtExpiredDate.setFormat("M/d/yy KK:mm a");	
		txtExpiredDate.setVisible(false);
		txtExpiredDate.setParent(hbox1);
		rows.appendChild(r4);
		
		Row r5 = new Row();
		r5.appendChild(new Label(" "));
		r5.setAlign("center");	
		cmdMashup = new Button("Mashup");
		cmdMashup.setParent(r5);		
		rows.appendChild(r5);
		
	}
	
	private void initialize_lstSensorType() {
		// TODO Auto-generated method stub
		lstSensorType = new HashMap<String, Boolean>();
		List<String> temp = sensorManager.getAllSensorType();
		for(String type:temp){
			if(!(type.equals(SensorTypeEnum.ADSBHub.toString())||type.equals(SensorTypeEnum.cosm.toString())))
				lstSensorType.put(type, false);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void initialize_lstDateOperator(){
		List lstOperator = new ArrayList<String>();
		Set set = FieldSearchConstainsUtil.timeOperator.entrySet();
		Iterator iter = set.iterator();
		while(iter.hasNext()){
			Map.Entry me = (Map.Entry)iter.next();
			lstOperator.add(me.getKey());
		}		
		ListModelList lm2 = new ListModelList(lstOperator);
		lm2.add("from");
		lm2.addSelection(lm2.get(0));
		lstDateOperator.setModel(lm2);
		//lstDateOperator.appendChild(new Listitem("from"));
	}
	
	private void updateTypeSelected(String type,boolean isChecked){
		lstSensorType.put(type, isChecked);
	}
	
	private void showMashupResults(){
		try{
			List results = new ArrayList();
			Date toDate = null;
			ArrayList<List> sensors = null;
			String dateOperator;
			String spatialOperator = "st_intersects";		
			Date fromDate = txtStartDate.getValue();
			if(lstDateOperator.getSelectedItem().getLabel().toString().equals("from")){ 
				toDate = txtExpiredDate.getValue();
				dateOperator=">=";
			}else 
				dateOperator = FieldSearchConstainsUtil.timeOperator.get(
					lstDateOperator.getSelectedItem().getLabel().toString());
			Iterator iter = lstSensorType.entrySet().iterator();
			while(iter.hasNext()){
				Map.Entry<String, Boolean> me = (Map.Entry)iter.next();
				if(me.getValue()){
					sensors = sensorManager.getAllSpecifiedSensorAndLatLongAroundPlace(me.getKey(), spatialOperator, 
										txtCityLong.getValue(), txtCityLat.getValue(), 0.5);
					if(sensors.size()<=0) continue;
					List<String> sensorIds = sensors.get(0);
					for(String id:sensorIds){						
						List<String> observations = sensorManager.getObservationsWithTimeCriteria(id,
								dateOperator,fromDate,toDate);
						if(observations==null) continue;
						for(String obs:observations){
							List readings = sensorManager.getReadingTriplesOfObservation(obs);
							addToResults(results, readings);
						}					
					}
				}		
			}
			
			if(this.getRoot().getFellowIfAny("SparqlEndPointResult") != null){
				this.getRoot().getFellowIfAny("SparqlEndPointResult").detach();
			}					
			SparqlResultWindow qrs = new SparqlResultWindow("Data mashup");
			qrs.setParent(this.getRoot());
			qrs.setRowsList(results);
			
			ArrayList columnLabels = new ArrayList<String>();
			columnLabels.add("Subject");
			columnLabels.add("Predicate");
			columnLabels.add("Object");
			
			qrs.setColumnLabelsList(columnLabels);
			qrs.initGrid();
			qrs.doOverlapped();
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	private void addToResults(List<List<String>> results,List t){
		Iterator iter = t.iterator();
		while(iter.hasNext()){
			ArrayList arr = (ArrayList)iter.next();
			results.add(arr);
		}
	}
}
