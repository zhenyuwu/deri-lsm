package deri.sensor.components.user.annotation;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Vbox;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class AddNewSensorTypeWindow extends Window {
	private Textbox txtNewSensorType;
	private Button cmdAdd;
	private Button cmdCancel;


	
	public AddNewSensorTypeWindow(){
		super();
		init();
	}
	
	public void init(){
		this.setTitle("Add new sensor type");
		this.setId("AddNewSensorTypeWizard");
		this.setBorder("normal");
		this.setStyle("overflow:auto");
		this.setHeight("110px");
		this.setWidth("350px");
		this.setClosable(true);
		this.setSizable(true);
		this.setPosition("center,center");
		Vbox vbox = new Vbox();
		vbox.setParent(this);
		vbox.setSpacing("8px");
		
		Hbox hb1 = new Hbox();
		hb1.setSpacing("10px");
		hb1.setParent(vbox);
		
		Label lb1 = new Label("New sensor type");
		lb1.setParent(hb1); 
		
		txtNewSensorType = new Textbox();
		txtNewSensorType.setParent(hb1);
		
		Hbox hboxWButton = new Hbox();
		hboxWButton.setParent(vbox);
		hboxWButton.setSpacing("10px");
		hboxWButton.setStyle("padding-left:50%");
		
		cmdAdd = new Button("Add","/imgs/Button/button_ok.png");
		cmdAdd.setParent(hboxWButton);
		cmdAdd.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				try{
					setNewSensorType();					
				}catch(Exception e){
					
				}
			}
		});
		
		cmdCancel = new Button("Cancel","/imgs/Button/button_cancel.png");
		cmdCancel.setParent(hboxWButton);
		cmdCancel.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				AddNewSensorTypeWindow.this.detach();
			}
		});
	}

	public void setNewSensorType() throws InterruptedException{
		//SensorTypeWizardWindow s = (SensorTypeWizardWindow)this.getParent();		
		SensorTypeWizardWindow s = (SensorTypeWizardWindow)this.getPreviousSibling().getFellowIfAny("ChooseSensorTypeWizard");
		if(txtNewSensorType.getValue()!=""){
			s.addNewSensorType(txtNewSensorType.getValue());
			AddNewSensorTypeWindow.this.detach();
		} else  
			Messagebox.show("Sensor type is invalid. Please input again.", "Error", Messagebox.OK, Messagebox.ERROR);				
	}
}
