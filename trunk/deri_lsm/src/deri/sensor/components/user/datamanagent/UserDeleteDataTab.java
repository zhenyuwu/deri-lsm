package deri.sensor.components.user.datamanagent;

import java.util.ArrayList;
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
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tabpanel;


import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.FieldSearchConstainsUtil;
import deri.sensor.javabeans.Sensor;
import deri.sensor.javabeans.SensorSourceType;
import deri.sensor.javabeans.SystemSensorType;
import deri.sensor.javabeans.User;
import deri.sensor.javabeans.Wrapper;
import deri.sensor.manager.SensorManager;
import deri.sensor.meta.SensorTypeEnum;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class UserDeleteDataTab extends Tabpanel {
	private Grid grdSensor = new Grid();	
	private SensorManager sensorManager;
	private Listbox lstSensorType;	
	
	private Button cmdSave;
	private User user;
	private List<Sensor> lstSensor;
	private Button cmdDelete;
	private Image imgDeleteType;
	private Button cmdDeleteAllSensor;
	private Image imgDeleteOneSource;
	
	private ListModelList lstSensorModel;
	private Datebox txtStartDate;
	private Datebox txtExpiredDate;
	private Listbox lstDateOperator;
	private Label lblToDate;
	private Checkbox chkSpecifiedSensor;
	private Combobox cmbSensorLocate;
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}


	public void init(){	
		sensorManager = ServiceLocator.getSensorManager();
		initGrdSensor();
		addEventListener();
	}

	
	private void initGrdSensor() {
		// TODO Auto-generated method stub
		grdSensor.setParent(this);
		grdSensor.setId("grdSensor");
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
		
		//row 1 add sensor type combobox and sensor filed combobox
		Row r1 = new Row();
		r1.appendChild(new Label("Sensor type"));		
		
		Hbox hbss = new Hbox();
		hbss.setParent(r1);
		hbss.setSpacing("10px");
		lstSensorType= new Listbox();		
		lstSensorType.setParent(hbss);	
		lstSensorType.setMold("select");	
		rows.appendChild(r1);
		initialize_lstSensorType();
		imgDeleteType = new Image("/imgs/Button/delete.png");
		imgDeleteType.setParent(hbss);
		if(user.getUsername().equals("admin"))
			imgDeleteType.setVisible(true);
		else imgDeleteType.setVisible(false);
	
		//row 2 add city combobox
		Row r2 = new Row();				
		chkSpecifiedSensor = new Checkbox("Specify sensor");		
		chkSpecifiedSensor.setParent(r2);
		chkSpecifiedSensor.setChecked(false);
		chkSpecifiedSensor.addEventListener(Events.ON_CHECK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				cmbSensorLocate.setDisabled(!cmbSensorLocate.isDisabled());			
				imgDeleteOneSource.setVisible(!imgDeleteOneSource.isVisible());	
			}
		});
		hbss = new Hbox();
		hbss.setParent(r2);
		hbss.setSpacing("10px");
		cmbSensorLocate = new Combobox();
		cmbSensorLocate.setParent(hbss);		
		cmbSensorLocate.setDisabled(true);
		
		imgDeleteOneSource = new Image("/imgs/Button/delete.png");
		imgDeleteOneSource.setParent(hbss);
		imgDeleteOneSource.setVisible(false);
		rows.appendChild(r2);
				
		Row r3 = new Row();
		r3.appendChild(new Label("Time criteria"));
		Hbox hbox1 = new Hbox();
		hbox1.setParent(r3);
		
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
		rows.appendChild(r3);
		
		Row r4 = new Row();
		r4.appendChild(new Label(" "));
		r4.setAlign("center");	
		hbox1 = new Hbox();
		hbox1.setParent(r4);
		
		cmdDeleteAllSensor = new Button("Delete sensors","/imgs/Button/delete.png");
		cmdDeleteAllSensor.setParent(hbox1);		
		
		cmdDelete = new Button("Delete data","/imgs/Button/delete.png");
		cmdDelete.setParent(hbox1);		
		rows.appendChild(r4);
		
	}

	private void addEventListener() {
		// TODO Auto-generated method stub
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
		
		cmdDelete.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				try {			
					Messagebox.show("This function will delete all data based on time creterias. Do you want to coninue?","Prompt", Messagebox.YES|Messagebox.NO,
							Messagebox.QUESTION,
						    new EventListener() {
						        @Override
								public void onEvent(Event evt) {
						            switch (((Integer)evt.getData()).intValue()) {
						            case Messagebox.YES: doYes(); break; //the Yes button is pressed
						            case Messagebox.NO: break; //the No button is pressed
						            }
						        }

								private void doYes() {
									// TODO Auto-generated method stub
									String type = lstSensorType.getSelectedItem().getLabel();
									Date toDate = null;
									List<Sensor> sensors = null;
									String dateOperator;
									Date fromDate = txtStartDate.getValue();
									if(lstDateOperator.getSelectedItem().getLabel().toString().equals("from")){ 
										toDate = txtExpiredDate.getValue();
										dateOperator=">=";
									}else 
										dateOperator = FieldSearchConstainsUtil.timeOperator.get(
											lstDateOperator.getSelectedItem().getLabel().toString());
									if(chkSpecifiedSensor.isChecked()){
										int index = lstSensorModel.indexOf(cmbSensorLocate.getSelectedItem().getValue());
										Sensor removeSensor = lstSensor.get(index);
										sensorManager.deleteAllObservationsWithTimeCriteria(removeSensor,dateOperator,
												fromDate,toDate);
									}else{					
										if(!user.getUsername().equals("admin"))
											sensors = sensorManager.getAllSensorForSpecfiedUserAndType(type, user.getId());
										else
											sensors = sensorManager.getAllSensorWithSpecifiedSensorType(type);
										for(Sensor sensor:sensors){
											sensorManager.deleteAllObservationsWithTimeCriteria(sensor,dateOperator,
																	fromDate,toDate);
										}
									}
								}					
						    }
					);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}	
			}
		});
		
		cmdDeleteAllSensor.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				String type = lstSensorType.getSelectedItem().getLabel();
				try {			
					Messagebox.show("This function will delete all sensors which have type "+type+
							". Do you want to coninue?","Prompt", Messagebox.YES|Messagebox.NO,
							Messagebox.QUESTION,
						    new EventListener() {
						        @Override
								public void onEvent(Event evt) {
						            switch (((Integer)evt.getData()).intValue()) {
						            case Messagebox.YES: doYes(); break; //the Yes button is pressed
						            case Messagebox.NO: break; //the No button is pressed
						            }
						        }

								private void doYes() {
									// TODO Auto-generated method stub
									for(Sensor sensor:lstSensor){
										sensorManager.deleteAllObservationsForSpecifiedSensor(sensor);
										sensorManager.deleteSensor(sensor);
									}
									lstSensorModel.clear();
									cmbSensorLocate.getChildren().clear();
								}					
						    }
					);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}	
			}
		});
		
		imgDeleteType.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event event) throws Exception {				
				// TODO Auto-generated method stub
				final String type = lstSensorType.getSelectedItem().getLabel();
				try {			
					Messagebox.show("This function will delete this type of sensor and also delete all sensors which have type "+type+
							". Do you want to coninue?","Prompt", Messagebox.YES|Messagebox.NO,
							Messagebox.QUESTION,
						    new EventListener() {
						        @Override
								public void onEvent(Event evt) {
						            switch (((Integer)evt.getData()).intValue()) {
						            case Messagebox.YES: doYes(); break; //the Yes button is pressed
						            case Messagebox.NO: break; //the No button is pressed
						            }
						        }

								private void doYes() {
									// TODO Auto-generated method stub
									for(Sensor sensor:lstSensor){
										sensorManager.deleteAllObservationsForSpecifiedSensor(sensor);
										sensorManager.deleteSensor(sensor);
									}
									lstSensorModel.clear();
									cmbSensorLocate.getChildren().clear();
									
									sensorManager.deleteSensorType(type);
									lstSensorType.removeItemAt(lstSensorType.getSelectedIndex());
								}					
						    }
					);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		});
		
		imgDeleteOneSource.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				try {			
					Messagebox.show("This function will delete this sensor and its data. Do you want to coninue?","Prompt", Messagebox.YES|Messagebox.NO,
							Messagebox.QUESTION,
						    new EventListener() {
						        @Override
								public void onEvent(Event evt) {
						            switch (((Integer)evt.getData()).intValue()) {
						            case Messagebox.YES: doYes(); break; //the Yes button is pressed
						            case Messagebox.NO: break; //the No button is pressed
						            }
						        }

								private void doYes() {
									// TODO Auto-generated method stub
									
//									System.out.println(cmbSensorLocate.getSelectedItem());
									int index = lstSensorModel.indexOf(cmbSensorLocate.getSelectedItem().getValue());
									Sensor removeSensor = lstSensor.get(index);
									lstSensor.remove(index);
									sensorManager.deleteAllObservationsForSpecifiedSensor(removeSensor);
									sensorManager.deleteSensor(removeSensor);
								}					
						    }
					);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		cmbSensorLocate.addEventListener(Events.ON_OK, new EventListener() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				initialize_cmbSensorLocate(lstSensorType.getSelectedItem().getLabel(), 
						cmbSensorLocate.getValue());
			}
		});
		
		lstSensorType.addEventListener(Events.ON_SELECT, new EventListener() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				if(!lstSensorType.getSelectedItem().getLabel().equals(SensorTypeEnum.weather.toString()))
					initialize_cmbSensorLocate(lstSensorType.getSelectedItem().getLabel());
			}
		});
	}
	
	
	private void initialize_lstSensorType() {
		// TODO Auto-generated method stub
		List<String> lstType = sensorManager.getAllSensorType();
		ListModelList lm2 = new ListModelList(lstType);
		lm2.addSelection(lm2.get(0));
		lstSensorType.setModel(lm2);		
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
	
	@SuppressWarnings("unused")
	private void initialize_cmbSensorLocate(String sensorType,String subName) {
		// TODO Auto-generated method stub
		lstSensor  = sensorManager.getAllSensorWithSpecifiedSensorTypeAndUserAndSubName(sensorType,user.getId(),subName);		
		List city = new ArrayList<String>();
		for(Sensor sensor:lstSensor){			
			city.add(sensor.getName()+","+sensor.getPlace().getCity()+","+sensor.getPlace().getCountry());			
		}				
		lstSensorModel = new ListModelList(city);
		cmbSensorLocate.setModel(lstSensorModel);
	}
	
	private void initialize_cmbSensorLocate(String sensorType) {
		// TODO Auto-generated method stub
		lstSensor  = sensorManager.getAllSensorWithSpecifiedSensorTypeAndUser(sensorType,user.getId());		
		List city = new ArrayList<String>();
		for(Sensor sensor:lstSensor){			
			city.add(sensor.getName()+","+sensor.getPlace().getCity()+","+sensor.getPlace().getCountry());			
		}				
		lstSensorModel = new ListModelList(city);
		cmbSensorLocate.setModel(lstSensorModel);
		cmbSensorLocate.setRows(1);
	}
}
