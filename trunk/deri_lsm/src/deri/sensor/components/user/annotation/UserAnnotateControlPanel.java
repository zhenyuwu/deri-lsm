package deri.sensor.components.user.annotation;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import deri.sensor.components.Map.UserFisheyeBar;
import deri.sensor.javabeans.User;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class UserAnnotateControlPanel extends Window {
	private Tabbox tbbAnCtr;
	private Tab wizardTab;
	private Tab configTab;
	private Tab showSensorTab; 
	private User user;
	private Checkbox chkOnlyShowMySensor;
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserAnnotateControlPanel(){
		super();
	}
	
	public boolean isOnlyShowMySensorOnMap(){
		return chkOnlyShowMySensor.isChecked();
	}
	
	public void setOnlyShowMySensorOnMap(boolean t){
		chkOnlyShowMySensor.setChecked(t);
	}
	
	public void init(){
		 this.setId("UserAnnotateControlPanel");
		 this.setPosition("center,center");
		 this.setTitle("User Annotation Control Panel");
		 this.setWidth("50%");
		 this.setHeight("30%");
		 this.setBorder("normal");
		 this.setContentStyle("overflow:auto");		
		 this.setClosable(true);
		 this.setSizable(true);
		 
		 tbbAnCtr = new Tabbox();
		 tbbAnCtr.setWidth("100%");		 
		 tbbAnCtr.setParent(this);
		 initTab();
	 }
	
	public void initTab(){		
		wizardTab = new Tab("User annotation wizard");
		configTab = new Tab("User configuration");
		showSensorTab = new Tab("Show sensor");	
				
		tbbAnCtr.appendChild(new Tabs());			
		tbbAnCtr.appendChild(new Tabpanels());			
		tbbAnCtr.getTabs().appendChild(wizardTab);
		tbbAnCtr.getTabs().appendChild(configTab);
		tbbAnCtr.getTabs().appendChild(showSensorTab);
		initWizardTab();
		initConfigTab();
		initShowSensorTab();
	}
	
	
	public void initShowSensorTab(){
		Tabpanel tpShowSensor = new Tabpanel();
		tpShowSensor.setHeight("100%");
		tbbAnCtr.getTabpanels().appendChild(tpShowSensor);
		chkOnlyShowMySensor = new Checkbox("Show all my sensor on Map");
		chkOnlyShowMySensor.setParent(tpShowSensor);
		
		final UserFisheyeBar userF = (UserFisheyeBar)this.getParent().getFellowIfAny("userFishEyeBar");		
		chkOnlyShowMySensor.setChecked(userF.isOnlyShowMySensor());	
		chkOnlyShowMySensor.addEventListener(Events.ON_CHECK, new EventListener() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub								
				userF.setOnlyShowMySensor(chkOnlyShowMySensor.isChecked());
			}
		});
	}
	
	private void initConfigTab() {
		// TODO Auto-generated method stub
		UserAnnotateConfigurationTab confTabpanel = new UserAnnotateConfigurationTab();
		confTabpanel.setUser(user);
		confTabpanel.setHeight("100%");
		confTabpanel.init();
		tbbAnCtr.getTabpanels().appendChild(confTabpanel);
	}

	public void initWizardTab(){
		Tabpanel tpWizard = new Tabpanel();
		tpWizard.setHeight("100%");
		tbbAnCtr.getTabpanels().appendChild(tpWizard);		
		
		Label lblWizard = new Label();
		lblWizard.setParent(tpWizard);
		lblWizard.setHeight("100%");
		lblWizard.setWidth("100%");
		lblWizard.setMultiline(true);		
		lblWizard.setValue("Use this wizard to annotate new sensor.\n \t \t Please click Start button to annotate.");
		Div div = new Div();

		div.setParent(tpWizard);
		div.setAlign("right");
		Button cmdStart = new Button("Start","/imgs/Button/start.png");
		cmdStart.setParent(div);
		cmdStart.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				if(UserAnnotateControlPanel.this.getParent().getFellowIfAny("UserAnnotateWizard") != null){
					UserAnnotateControlPanel.this.getParent().getFellowIfAny("UserAnnotateWizard").detach();
				}				
				UserAnnotateWizard annotateWizard = new UserAnnotateWizard();
				annotateWizard.setParent(UserAnnotateControlPanel.this.getParent());				
				annotateWizard.setUser(user);
				annotateWizard.init();
				//annotateWizard.doOverlapped();
			}
		});
	}
}
