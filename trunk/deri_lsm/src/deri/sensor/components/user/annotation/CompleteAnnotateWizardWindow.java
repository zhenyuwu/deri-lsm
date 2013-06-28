package deri.sensor.components.user.annotation;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class CompleteAnnotateWizardWindow extends Window {
	private Button cmdNext;
	private Button cmdBack;
	private final int order = 5;
	
	public CompleteAnnotateWizardWindow(){
		super();
		init();
	}
	
	public void init(){
		this.setTitle("Step 5 - Compelete");
		this.setId("FinishMappingWizard");
		this.setBorder("normal");
		
		this.setStyle("overflow:auto");		
		this.setPosition("center");
		this.setHeight("200px");
		this.setWidth("300px");
		this.setClosable(true);
		Vbox vbox = new Vbox();
		vbox.setWidth("100%");
		vbox.setHeight("70%");
		vbox.setAlign("center");
		vbox.setParent(this);
		
		
		Label lbl = new Label("Annotation process is complete.");	
		lbl.setStyle("padding-top:60%");		
		lbl.setParent(vbox);
		
		Hbox hboxWButton = new Hbox();
		hboxWButton.setParent(this);
		hboxWButton.setWidth("40%");
		//hboxWButton.setSpacing("10px");
		hboxWButton.setStyle("padding-left:60%");
		cmdBack = new Button("Back");
		cmdBack.setParent(hboxWButton);
		cmdBack.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				backToPreviousStep();
			}
		});
		
		cmdNext = new Button("Finish");
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
		

	private void backToPreviousStep(){
		UserAnnotateWizard u = (UserAnnotateWizard) this.getParent();
		u.moveToStep(this.getOrder()-1);
	}
	private void moveToNextStep(){		
		UserAnnotateWizard u = (UserAnnotateWizard) this.getParent();
		this.detach();
		u.startWrappingDataProcess();		
	}
	
}
