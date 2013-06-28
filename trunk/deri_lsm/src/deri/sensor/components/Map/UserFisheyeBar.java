package deri.sensor.components.Map;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zkex.zul.Fisheye;
import org.zkoss.zkex.zul.Fisheyebar;
import org.zkoss.zul.Messagebox;

import deri.sensor.components.user.ContactUsWindow;
import deri.sensor.components.user.UserProfile;
import deri.sensor.components.user.annotation.UserAnnotateControlPanel;
import deri.sensor.components.user.datamanagent.UserDataManagementPanel;
import deri.sensor.components.user.notification.UserNotificationWizard;
import deri.sensor.components.windows.LinkToPachubeWindow;
import deri.sensor.components.windows.ManualWindow;
import deri.sensor.javabeans.User;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class UserFisheyeBar extends Fisheyebar {
	private User user;
	private boolean isOnlyShowMySensor=false;
	public UserFisheyeBar(){
		super();
		init();
	}
	 
	
	public boolean isOnlyShowMySensor() {
		return isOnlyShowMySensor;
	}


	public void setOnlyShowMySensor(boolean isOnlyShowMySensor) {
		this.isOnlyShowMySensor = isOnlyShowMySensor;
	}


	public void init(){
		this.setId("userFishEyeBar");
		
		Fisheye fsheUserNotifi = new Fisheye("User notification","/imgs/menu/user_notification.png");
		fsheUserNotifi.setParent(this);
		fsheUserNotifi.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				InitializedUserNotification();
			}
		});
		
		Fisheye fsheUserAnnotate= new Fisheye("User annotation","/imgs/menu/user_annotate.png");
		fsheUserAnnotate.setParent(this);	
		fsheUserAnnotate.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				InitializedUserAnnotate();
			}
		});
		
		Fisheye fsheUserProfile = new Fisheye("User profile","/imgs/menu/user_Info.png");
		fsheUserProfile.setParent(this);	
		fsheUserProfile.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				InitializedUserProfile();
			}
		});
		
//		Fisheye fsheMessageBox = new Fisheye("Message box","/imgs/menu/user_messagebox.png" );
//		fsheMessageBox.setParent(this);	
			
		Fisheye fsheDataManagement = new Fisheye("Data Management","/imgs/menu/data_management.png" );
		fsheDataManagement.setParent(this);	
		fsheDataManagement.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				InitializedUserManagementData();
			}
		});
		
//		Fisheye fsheSearchInfor = new Fisheye("Search Information","/imgs/menu/user_search_infor.png" );
//		fsheSearchInfor.setParent(this);
//		fsheSearchInfor.addEventListener(Events.ON_CLICK, new EventListener() {
//			
//			@Override
//			public void onEvent(Event event) throws Exception {
//				// TODO Auto-generated method stub
//				InitializedUserSearchLiveInfor();
//			}			
//		});
		
		Fisheye fsheDocumentation = new Fisheye("User manual","/imgs/menu/user_manual.png" );
		fsheDocumentation.setParent(this);	
		fsheDocumentation.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				try{
//					Filedownload fdl = new Filedownload();
//					FileInputStream input = new FileInputStream("/usr/local/tomcat7/webapps/SensorMiddlewareData/Document/lsmmanual.pdf");
//					Filedownload.save(input,"application/pdf","lsmmanual.pdf");
					if(UserFisheyeBar.this.getParent().getFellowIfAny("manualWindow") != null){
						UserFisheyeBar.this.getParent().getFellowIfAny("manualWindow").detach();
					}
					ManualWindow manualWindow = new ManualWindow();
					manualWindow.setParent(UserFisheyeBar.this.getParent());
					manualWindow.initContent();
					manualWindow.doOverlapped();
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
		
		Fisheye fsheContactUs = new Fisheye("Contact us","/imgs/menu/user_contactUs.png" );
		fsheContactUs.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				InitializedContactToUs();
			}
		});
		
		fsheContactUs.setParent(this);
		
		Fisheye fsheCosm = new Fisheye("Link to Pachube","/imgs/menu/cosm.png" );
		fsheCosm.setParent(this);
		fsheCosm.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				InitializedLinkToPachube();
			}
		});
	}
	
	protected void InitializedLinkToPachube() {
		// TODO Auto-generated method stub
		if(isCheckLogin()){
			if(this.getParent().getFellowIfAny("LinkToPachube") != null){
				this.getParent().getFellowIfAny("LinkToPachube").detach();
			}
			LinkToPachubeWindow pachube = new LinkToPachubeWindow("Link To Pachube");
			pachube.setParent(this.getParent());
			pachube.setUser(user);
			pachube.doOverlapped();
		} else
			try {
				Messagebox.show(
						"Please login.",
						"Information", Messagebox.OK,
						Messagebox.EXCLAMATION);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}


	protected void InitializedUserAnnotate() {
		// TODO Auto-generated method stub
		if(isCheckLogin()){
			if(this.getParent().getFellowIfAny("UserAnnotateControlPanel") != null){
				this.getParent().getFellowIfAny("UserAnnotateControlPanel").detach();
			}
			UserAnnotateControlPanel annotateCtrPanel = new UserAnnotateControlPanel();
			annotateCtrPanel.setParent(this.getParent());			
			annotateCtrPanel.setUser(user);
			annotateCtrPanel.init();
			annotateCtrPanel.doOverlapped();
		} else
			try {
				Messagebox.show(
						"Please login.",
						"Information", Messagebox.OK,
						Messagebox.EXCLAMATION);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	public void InitializedUserNotification(){
		if(isCheckLogin()){
			if(this.getParent().getFellowIfAny("UserNotificationWizard") != null){
				this.getParent().getFellowIfAny("UserNotificationWizard").detach();
			}
			UserNotificationWizard notiWizard = new UserNotificationWizard();
			notiWizard.setParent(this.getParent());
			//notiWizard.doOverlapped();
		} else
			try {
				Messagebox.show(
						"Please login.",
						"Information", Messagebox.OK,
						Messagebox.EXCLAMATION);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	public void InitializedUserManagementData(){
		if(isCheckLogin()){
			if(this.getParent().getFellowIfAny("DataManagementPanel") != null){
				this.getParent().getFellowIfAny("DataManagementPanel").detach();
			}
			UserDataManagementPanel pnlUserData = new UserDataManagementPanel();
			pnlUserData.setParent(this.getParent());
			pnlUserData.setUser(user);
			pnlUserData.init();			
			pnlUserData.doOverlapped();
		} else
			try {
				Messagebox.show(
						"Please login.",
						"Information", Messagebox.OK,
						Messagebox.EXCLAMATION);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public void InitializedContactToUs(){
		if(isCheckLogin()){
			if(this.getParent().getFellowIfAny("ContactUs") != null){
				this.getParent().getFellowIfAny("ContactUs").detach();
			}
			ContactUsWindow ctactUs = new ContactUsWindow();
			ctactUs.setParent(this.getParent());
			ctactUs.doOverlapped();
		} else
			try {
				Messagebox.show(
						"Please login.",
						"Information", Messagebox.OK,
						Messagebox.EXCLAMATION);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
	}
	
	private void InitializedUserProfile() {
		// TODO Auto-generated method stub
		if(isCheckLogin()){
			if(this.getParent().getFellowIfAny("UserProfile") != null){
				this.getParent().getFellowIfAny("UserProfile").detach();
			}
			UserProfile userProfile = new UserProfile();
			userProfile.setParent(this.getParent());
			userProfile.setUser(user);
			userProfile.init();
			userProfile.doOverlapped();
		} else
			try {
				Messagebox.show(
						"Please login.",
						"Information", Messagebox.OK,
						Messagebox.EXCLAMATION);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public boolean isCheckLogin(){
		user = (User)this.getDesktop().getSession().getAttribute("user");
		if(user != null)
			return true;
		return false;
	}
	
}
