package deri.sensor.components.windows;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import deri.sensor.cosm.CosmSensorParser;
import deri.sensor.javabeans.User;
import deri.sensor.wrapper.WebServiceURLRetriever;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class LinkToPachubeWindow extends Window{
	private User user;
	
	public LinkToPachubeWindow(){
		super();
		init();
	}
	
	public LinkToPachubeWindow(String title) {
		this.setTitle(title);
		init();
	}
	
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void init(){
		this.setId("LinkToPachube");
		this.setPosition("center,center");
		this.setContentStyle("overflow:auto");		
		this.setClosable(true);
		this.setSizable(true);
		
		Hbox hb = new Hbox();
		hb.setParent(this);
		hb.setWidth("100%");
		hb.setHeight("100%");
		hb.appendChild(new Label("URL"));
		final Textbox txtURL = new Textbox();
		txtURL.setParent(hb);
		txtURL.setWidth("250px");
		Button cmdAdd= new Button("Add","/imgs/Button/cosm.png");
		cmdAdd.setParent(hb);
		cmdAdd.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				String xml = "";
				xml = WebServiceURLRetriever.RetrieveFromURLWithAuthentication(txtURL.getText(), "nmqhoan", "nmqhoan");
				CosmSensorParser.parseDataElementsFromCosmXML(xml, user);
			}
		});
		
		hb = new Hbox();
		hb.setParent(this);
		hb.setWidth("100%");
		hb.setHeight("100%");
		Checkbox chkOnyShow = new Checkbox("Show only my Pachube sensors");
		chkOnyShow.setParent(hb);
		chkOnyShow.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
}
