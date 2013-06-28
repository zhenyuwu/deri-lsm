package deri.sensor.components.windows;

import java.util.Random;

import org.zkoss.zk.ui.Session;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Label;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.West;
import org.zkoss.zul.Window;

import deri.sensor.components.Map.GMaps;
import deri.sensor.javabeans.User;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class MainWindow extends Window {
	private static final long serialVersionUID = -8547976241378758987L;
	private Hbox hbox;
	public MainWindow(){
		super();
	}

	public MainWindow(String title){
		this();
		this.setTitle(title);
	}
	
	public void onCreate(){
		Caption caption = new Caption();
		caption.setId("caption");
		caption.setParent(this);		
		initializeCaption(caption);
	}
	
	public void initializeCaption(Caption caption){
		User user = (User)this.getDesktop().getSession().getAttribute("user");
		if(user != null){
			resetCaptionWithSession();
		}else{
			hbox = new Hbox();
			hbox.setParent(caption);
			hbox.setWidth("100%");
			Html htm = new Html();
			htm.setWidth("80%");
			htm.setContent("<P ALIGN=left><MARQUEE WIDTH=95% HEIGHT=2 BEHAVIOR=scroll SCROLLAMOUNT=\"3\"><font face=\"arial\">"+
						setStatusbarContent()+"</font></MARQUEE>");		
			htm.setParent(hbox);
			
//			Label label = new Label("    ");
//			label.setParent(hbox);
			Toolbarbutton login = new Toolbarbutton("Login / Register");
			login.setParent(hbox);
			login.addEventListener(Events.ON_CLICK, new EventListener(){
				@Override
				public void onEvent(Event event) throws Exception {
					LoginWindow loginWindow = new LoginWindow("Login");
					loginWindow.setParent(getPage().getFirstRoot());
					loginWindow.init();
					loginWindow.doModal();
				}
			});
		}
	}
	
	public void clearCaption(Caption caption){
		while(caption.getFirstChild() != null){
			caption.getFirstChild().detach();
		}
	}
	
	public void resetCaptionWithSession(){
		Caption caption = (Caption)this.getFellowIfAny("caption");
		clearCaption(caption);
		Session session = this.getDesktop().getSession();
		
		hbox = new Hbox();
		hbox.setParent(caption);
		hbox.setWidth("100%");
		Html htm = new Html();
		htm.setWidth("80%");
		htm.setContent("<P ALIGN=left><MARQUEE WIDTH=95% HEIGHT=2 BEHAVIOR=scroll SCROLLAMOUNT=\"3\"><font face=\"arial\">"+
					setStatusbarContent()+"</font></MARQUEE>");		
		htm.setParent(hbox);
		
		User user = (User)session.getAttribute("user");
		Label label = new Label(user.getNickname());
		label.setParent(hbox);
		//Space space = new Space();
		//space.setParent(caption);
		Toolbarbutton logoff = new Toolbarbutton("Log Off");
		logoff.setParent(hbox);
		logoff.addEventListener(Events.ON_CLICK, new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				getDesktop().getSession().removeAttribute("user");
				Caption caption = (Caption)((Toolbarbutton)event.getTarget()).getParent().getParent();
				clearCaption(caption);
				initializeCaption(caption);
				closeSaveViewFilter();
			}
		});
	}
	public void showUserPanel(boolean isShow){
		West userPanel = (West)this.getFellowIfAny("west");				
		userPanel.setVisible(true);
	}
	private void closeSaveViewFilter(){
		GMaps map = (GMaps)this.getFellowIfAny("map");
		map.closeSaveViewFilter();
	}
	
	public String setStatusbarContent(){
		Random generator = new Random();
		int number = generator.nextInt(4000)+1000;
		
		String htmlContent="";
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Total: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">over 110.000 sensors  </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Weather: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">82365 sensors  </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Webcam: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">24570 sensors  </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Traffic: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">469 sensors  </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Roadactivity: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">575 sensors  </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Railwaystation: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">251 railways </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Airport: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">2794 aiports  </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Flight: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">more than 3000 flights  </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Snowfall: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">7525 sensors  </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Snowdepth: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">more than 615 sensors  </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Sea level: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">more than 2500 sensors  </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Radar: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">1 sensors  </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Satellite: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">12 sensors  </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Bike hire: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">421 sensors  </font>";
		
		htmlContent+="<font size=\"1\" color=\"red\">";
		htmlContent+="<b>Update rate: "+"<b></font>";
		htmlContent+="<font size=\"1\" color=\"green\">"+number+" triples/s  </font>";
		
		return htmlContent;
	}
}
