package deri.sensor.components.user.annotation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;

import deri.sensor.database.ServiceLocator;
import deri.sensor.javabeans.Sensor;
import deri.sensor.javabeans.SensorSourceType;
import deri.sensor.javabeans.SystemSensorType;
import deri.sensor.javabeans.User;
import deri.sensor.javabeans.Wrapper;
import deri.sensor.manager.SensorManager;
import deri.sensor.wrapper.UserSensorWrapper;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class UserAnnotateConfigurationTab extends Tabpanel {
	private Grid grdSensor = new Grid();
	private Grid grdWrapper = new Grid();
	private SensorManager sensorManager;
	private Listbox lstSensorType;	
	private Checkbox chkShowSource;
	private Combobox cmbSensorLocate;
	private Intbox txtTimeUpdate;
	private Listbox lsbTimeUpdateUnit;
	private Button cmdSave;
	private User user;
	private List<Sensor> lstSensor;
	private Button cmdDeleteAllSource;
	private ListModelList lstSensorModel;
	private Textbox txtCurrentStatus;
	private Textbox txtDataFormat;
	private Datebox txtStartUpdate;
	private Button cmdStart;
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}


	public void init(){	
		sensorManager = ServiceLocator.getSensorManager();
		initGrdSensor();
		initGrdWrapper();
		addEventListener();
	}

	private void initGrdWrapper() {
		// TODO Auto-generated method stub
		grdWrapper.setParent(this);
		grdWrapper.setId("grdWrapper");
		grdWrapper.setMold("paging");
		grdWrapper.setPageSize(5);
		
		Columns columns = new Columns();
		columns.setParent(grdWrapper);
		Column column = new Column();
		column.setParent(columns);
		column.setLabel("Property");
		column.setWidth("20%");
		column = new Column();
		column.setParent(columns);
		column.setLabel("Value");
		Rows rows = new Rows();
		rows.setParent(grdWrapper);
		
		
		Row r1 = new Row();
		r1.appendChild(new Label("Data format"));		
		txtDataFormat = new Textbox();
		txtDataFormat.setParent(r1);
		rows.appendChild(r1);
		
		
		Row r2 = new Row();
		r2.appendChild(new Label("Time update"));
		
		
		Vbox vtime = new Vbox();
		vtime.setParent(r2);
		
		txtStartUpdate = new Datebox(new Date());		
		txtStartUpdate.setFormat("M/d/yy KK:mm a");		
		txtStartUpdate.setParent(vtime);
		
		Hbox hbss = new Hbox();
		hbss.setParent(vtime);
		hbss.setSpacing("10px");
		
		hbss.appendChild(new Label("Update every "));
		txtTimeUpdate = new Intbox();
		txtTimeUpdate.setParent(hbss);
		
		lsbTimeUpdateUnit = new Listbox();
		lsbTimeUpdateUnit.setParent(hbss);
		lsbTimeUpdateUnit.setMold("select");
		initialize_lsbTimeUnit();
		rows.appendChild(r2);
		
		Row r3 = new Row();
		r3.appendChild(new Label("Current status"));		
		txtCurrentStatus = new Textbox();
		txtCurrentStatus.setParent(r3);
		rows.appendChild(r3);
		
	
		Row r5 = new Row();
		r5.setAlign("center");
		r5.appendChild(new Label(""));
		Hbox hbSave = new Hbox();
		hbSave.setParent(r5);
		
		cmdSave = new Button("Save");
		cmdSave.setParent(hbSave);		
		cmdStart = new Button("Start update");
		cmdStart.setParent(hbSave);		
		rows.appendChild(r5);
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
		column.setWidth("20%");
		column = new Column();
		column.setParent(columns);		
		//column.setAlign("center");
		Rows rows = new Rows();
		rows.setParent(grdSensor);
		
		//row 1 add sensor type combobox and sensor filed combobox
		Row r1 = new Row();
		r1.appendChild(new Label("Sensor type"));		
				
		lstSensorType= new Listbox();		
		lstSensorType.setParent(r1);	
		lstSensorType.setMold("select");	
		rows.appendChild(r1);
		initialize_lstSensorType();		
		
		//row 2 add city combobox
		Row r2 = new Row();
		r2.appendChild(new Label("Sensor location"));
		Hbox hbss = new Hbox();
		hbss.setParent(r2);
		hbss.setSpacing("10px");
				
		chkShowSource = new Checkbox("Show source");
		chkShowSource.setParent(hbss);
		chkShowSource.addEventListener(Events.ON_CHECK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				cmbSensorLocate.setDisabled(!cmbSensorLocate.isDisabled());				
			}
		});
		
		cmbSensorLocate = new Combobox();
		cmbSensorLocate.setParent(hbss);		
		cmbSensorLocate.setDisabled(true);
		rows.appendChild(r2);
	
	}

	private void addEventListener() {
		// TODO Auto-generated method stub		
		
		lstSensorType.addEventListener(Events.ON_SELECT, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				String sensorType = lstSensorType.getSelectedItem().getLabel().toString();
				initialize_cmbSensorLocate(sensorType);
				initalize_wrapperInfor(sensorType);
			}
		});
		
		cmdStart.addEventListener(Events.ON_CLICK, new EventListener() {			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				Date date = txtStartUpdate.getValue();
				long l = date.getTime() - (new Date()).getTime();
				ScheduledExecutorService scheduler =
				      Executors.newSingleThreadScheduledExecutor();
				UserSensorWrapper usWrapper = new UserSensorWrapper(lstSensorType.getSelectedItem().getLabel(),
												user);
				long t = l/(60*1000);
				final ScheduledFuture<?> timeHandle =
				      scheduler.scheduleAtFixedRate(usWrapper, t, 1, TimeUnit.MINUTES);
			    // Schedule the event, and run for 1 hour (60 * 60 seconds)
//			    scheduler.schedule(new Runnable() {
//			      public void run() {
//			        timeHandle.cancel(false);
//			      }
//			    }, 160, SECONDS);
			}
		});
				
	}
	
	@SuppressWarnings("unchecked")
	private void initialize_cmbSensorLocate(String sensorType) {
		// TODO Auto-generated method stub
		lstSensor  = sensorManager.getAllSensorWithSpecifiedSensorTypeAndUser(sensorType,user.getId());		
		List city = new ArrayList<String>();
		for(Sensor sensor:lstSensor){			
			city.add(sensor.getPlace().getCity() +", "+sensor.getPlace().getCountry());			
		}				
		lstSensorModel = new ListModelList(city);
		cmbSensorLocate.setModel(lstSensorModel);
		cmbSensorLocate.setRows(1);
	}

	private void initialize_lstSensorType() {
		// TODO Auto-generated method stub
		List<String> lstSensor = sensorManager.getAllSensorType();
		ListModelList lm2 = new ListModelList(lstSensor);
		lm2.addSelection(lm2.get(0));
		lstSensorType.setModel(lm2);
	}
	
	private void initalize_wrapperInfor(String sensorType){
		String wrapperId = sensorManager.getWrapperIdWithSensorType(sensorType);
		Wrapper wrapper = sensorManager.getWrapperWithId(wrapperId);
		txtDataFormat.setValue(wrapper.getDataFormat());
		txtCurrentStatus.setValue(wrapper.getCurrentStatus());
		txtTimeUpdate.setValue(wrapper.getTimeToUpdate());		
	}
	
	
	private void initialize_lsbTimeUnit() {
		// TODO Auto-generated method stub
		lsbTimeUpdateUnit.appendChild(new Listitem("seconds"));
		lsbTimeUpdateUnit.appendChild(new Listitem("minutes"));
		lsbTimeUpdateUnit.appendChild(new Listitem("hours"));
		lsbTimeUpdateUnit.appendChild(new Listitem("days"));
		lsbTimeUpdateUnit.appendChild(new Listitem("weeks"));		
		lsbTimeUpdateUnit.setRows(1);
		lsbTimeUpdateUnit.setSelectedIndex(0);
	}

}
