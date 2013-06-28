package deri.sensor.components.user.annotation;

import java.util.Iterator;
import java.util.List;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import deri.sensor.database.ServiceLocator;
import deri.sensor.manager.SensorManager;
import deri.sensor.meta.SensorType;
import deri.sensor.meta.SensorTypeEnum;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class SensorTypeWizardWindow extends Window {
	private Listbox lstSensorType;
	private Button cmdNext;
	private Button cmdCancel;
	private final int order = 0;
	private String type;
	private Textbox txtSourceURL;
	private SensorManager sensorManager;
	private boolean isNewSensorType;
	private Combobox cmbSourceType;
	private boolean isNewSourceType;
	private List<String> lstSourceType;
	public SensorTypeWizardWindow(){
		super();
		//init();
	}
		
	public boolean isNewSensorType() {
		return isNewSensorType;
	}	

	
	public boolean isNewSourceType() {
		return isNewSourceType;
	}

	public void setNewSourceType(boolean isNewSourceType) {
		this.isNewSourceType = isNewSourceType;
	}

	public void setNewSensorType(boolean isNewSensorType) {
		this.isNewSensorType = isNewSensorType;
	}


	public void init(){
		this.setTitle("Step 1 - Choose your sensor type");
		this.setId("ChooseSensorTypeWizard");
		this.setBorder("normal");
		this.setStyle("overflow:auto");
		this.setContentStyle("overflow:auto");
		this.setHeight("210px");
		this.setWidth("380px");
		this.setSizable(true);
		this.setClosable(true);
//		((Window)this.getParent()).setWidth("310px");
		//((Window)this.getParent()).setHeight("220px");
		//((Window)this.getParent()).invalidate();
		
		sensorManager = ServiceLocator.getSensorManager();
		Groupbox grbSensorType = new Groupbox();
		grbSensorType.setId("grbSensorType");
		grbSensorType.setParent(this);
		
		Caption capSensorType = new Caption("Choose sensor");
		capSensorType.setParent(grbSensorType);
		
		Vbox vbox = new Vbox();
		vbox.setParent(grbSensorType);
		vbox.setSpacing("4px");
		
		Hbox hb1 = new Hbox();
		hb1.setSpacing("5px");
		hb1.setParent(vbox);
				
		Label lb1 = new Label("Sensors");
		lb1.setParent(hb1);
		hb1.appendChild(new Space());
		
		lstSensorType= new Listbox();
		lstSensorType.setParent(hb1);
		lstSensorType.setMold("select");		
		lstSensorType.addEventListener(Events.ON_SELECT, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				type = lstSensorType.getSelectedItem().getLabel();
				SensorTypeWizardWindow.this.setType(type);
				initialize_cmbSourceType();
			}			
		});
		Button cmdAddNewSensorType = new Button("New...");
		cmdAddNewSensorType.setParent(hb1);
		cmdAddNewSensorType.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				showNewSensorTypeWindow();
			}
		});
		
		Hbox hbSType = new Hbox();
		hbSType.setParent(vbox);
		
		hbSType.appendChild(new Label("Source type"));
		cmbSourceType = new Combobox();
		cmbSourceType.setParent(hbSType);
		initialize_lstSensorType();
		
		Hbox hb2 = new Hbox();		
		hb2.setParent(vbox);
		
		Label lb2 = new Label("Source URL");
		lb2.setParent(hb2);		
//		Space space = new Space();
//		space.setParent(hb2);
//		space.setWidth("4px");
//				
		txtSourceURL = new Textbox();
		txtSourceURL.setParent(hb2);
		txtSourceURL.setWidth("100%");
		
		
		Hbox hboxWButton = new Hbox();
		hboxWButton.setParent(this);
		hboxWButton.setSpacing("10px");
		hboxWButton.setStyle("padding-left:60%");
		cmdCancel = new Button("Cancel");
		cmdCancel.setParent(hboxWButton);
		cmdCancel.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				backToPreviousStep();
			}
		});
		
		cmdNext = new Button("Next");
		cmdNext.setParent(hboxWButton);		
		cmdNext.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				moveToNextStep();
			}
		});
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

	private void setSensorTypeforWizard() {
		// TODO Auto-generated method stub
		this.type = lstSensorType.getSelectedItem().getLabel().toLowerCase(); 
		SensorDataPropertiesWizardWindow sp = (SensorDataPropertiesWizardWindow) this.getParent().getFellowIfAny("ChooseSensorPropertiesWizard");
		sp.setType(this.type);
		sp.setSourceURL(this.txtSourceURL.getValue());
		sp.addContent();
	}
	
	private void backToPreviousStep(){
		UserAnnotateWizard u = (UserAnnotateWizard) this.getParent().getFellowIfAny("UserAnnotateWizard");
		u.moveToStep(this.getOrder()-1);
	}
	private void moveToNextStep() throws InterruptedException{
		List<String> lstUserSensorSource = sensorManager.getAllUserDefinedSourceTypeWithSpecifiedSensorType(type);			
		isNewSourceType = !lstUserSensorSource.contains(cmbSourceType.getValue());
//		isNewSourceType = !(lstSourceType.contains(cmbSourceType.getValue())||lstUserSensorSource.contains(cmbSourceType.getValue()));
		
		setSensorTypeforWizard();
		UserAnnotateWizard u = (UserAnnotateWizard) this.getParent();		
		if(isNewSensorType){
			if(isNewSensorType==false){
				Messagebox.show("This sensor source type already exists. Please input again.", "Error", Messagebox.OK, Messagebox.ERROR);
				return;
			}
			isNewSensorType = true;
			u.moveToStep(this.getOrder()+1);
		}
		else{		
			if(isNewSourceType)
				u.moveToStep(this.getOrder()+2);
			else u.moveToStep(this.getOrder()+5);
		}
	}
	
	private void initialize_lstSensorType(){
		List<String> lstSensor = sensorManager.getAllSensorType();
		Iterator iter = lstSensor.iterator();
		while(iter.hasNext()){
			String type = iter.next().toString();
			if(!(type.equals(SensorTypeEnum.ADSBHub.toString())||type.equals(SensorTypeEnum.cosm.toString())))
				lstSensorType.appendChild(new Listitem(type));
		}
		lstSensorType.setSelectedIndex(0);
		this.setType(lstSensorType.getSelectedItem().getLabel());
		initialize_cmbSourceType();
	}
	
	private void initialize_cmbSourceType(){		
		lstSourceType = sensorManager.getAllSourceTypeWithSpecifiedSensorType(type);
		if(lstSourceType.size()<=0)
			lstSourceType = sensorManager.getAllUserDefinedSourceTypeWithSpecifiedSensorType(type);
		ListModelList sourceModel = new ListModelList(lstSourceType);
		if(sourceModel.size()!=0) sourceModel.addSelection(sourceModel.get(0));
		cmbSourceType.setModel(sourceModel);
	}
	
	public void showNewSensorTypeWindow(){
		AddNewSensorTypeWindow newSensorWindow = new AddNewSensorTypeWindow();
		newSensorWindow.setParent(this.getParent().getParent());
		newSensorWindow.doOverlapped();
	}

	public void addNewSensorType(String type){
		isNewSensorType = !SensorType.getTypeList().contains(type);
		if(isNewSensorType)
			lstSensorType.appendChild(new Listitem(type));
	}

	public String getSourceURL() {
		// TODO Auto-generated method stub
		return txtSourceURL.getValue();
	}
	public String getSourceType(){
		return cmbSourceType.getValue();
	}
}
