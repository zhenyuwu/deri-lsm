package deri.sensor.components.user.notification;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.zkoss.zul.Div;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import deri.sensor.components.user.annotation.CompleteAnnotateWizardWindow;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class UserNotificationWizard extends Div {
	private Vbox vboxPanel;		
	private UserNotificationCriteriaWindow criteriaWindow;
	private UserNotificationEmailWindow mailWindow;
	private CompleteAnnotateWizardWindow compPanel;
	private List<Window> lstWizardPnl;
	private String type;
	private String userID;
	private List spatialPara;
	
	public UserNotificationWizard(){
		super();
		init();		
	}
	
	public void init(){
		this.setId("UserNotificationWizard");
		this.setStyle("overflow:auto");	
		lstWizardPnl = new ArrayList<Window>();

		this.addWizardFirstStep();

	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	
	public List getSpatialPara() {
		return spatialPara;
	}

	public void setSpatialPara(List spatialPara) {
		this.spatialPara = spatialPara;
	}

	public void addWizardFirstStep(){
		criteriaWindow = new UserNotificationCriteriaWindow();
		criteriaWindow.setParent(this);
		criteriaWindow.doOverlapped();
		criteriaWindow.setPosition("center,center");
		lstWizardPnl.add(criteriaWindow);
		
		mailWindow = new UserNotificationEmailWindow();
		mailWindow.setParent(this);
		mailWindow.setVisible(false);
		mailWindow.setPosition("center,center");
		lstWizardPnl.add(mailWindow);	

	}

	public void moveToStep(int step){
		for(Window pn:lstWizardPnl){
			if(lstWizardPnl.indexOf(pn)==step){
				pn.setVisible(true);
				pn.doOverlapped();
			}
			else pn.setVisible(false);
		}
	}
	
	public void println(int i){
		System.out.println(i);
	}
	
	public void startMappingProcess(){
		try{
			criteriaWindow.setSpatialPara();
			this.spatialPara = criteriaWindow.getSpatialPara();
			UserNotificationClientThread mailThread = new UserNotificationClientThread();
			mailThread.setType((String)spatialPara.get(0));
			mailThread.setSpatialOperator((String)spatialPara.get(1));
			mailThread.setLng((Double)spatialPara.get(2));
			mailThread.setLat((Double)spatialPara.get(3));
			mailThread.setDistance((Double)spatialPara.get(4));
			mailThread.setSensorField((String)spatialPara.get(5));
			mailThread.setValueOperator((String)spatialPara.get(6));
			mailThread.setValueCondition((String)spatialPara.get(7));
			mailThread.getSensorIdfromSpatialQuery();
			
			
			mailThread.setMailContentDescription(mailWindow.getMailDescription());
			//mailThread.settimeUpdateOccurence(mailWindow.getTimeUpdateValue());
			//mailThread.setTimeUnit(mailWindow.getTimeUpdateUnit());
			mailThread.setName("Use notification thread");
			
			if(mailWindow.isSendMail()){
				mailThread.setUserEmailAdress(mailWindow.getUserMailAdress());
				mailThread.setMailSubject(mailWindow.getMailSubject());
				mailThread.setAttachedFile(mailWindow.isAttachFile());
			}
			mailThread.setSendMail(mailWindow.isSendMail());
			mailThread.init();
			
			Date startDate = mailWindow.getStartDate();
			long l = startDate.getTime() - (new Date()).getTime();
			long end = mailWindow.getExpiredDate().getTime() -  startDate.getTime();
			ScheduledExecutorService scheduler =
			      Executors.newSingleThreadScheduledExecutor();		
			long t = l/(60*1000);
			long con = 0;
			if(mailWindow.getTimeUpdateUnit().equals("minutes"))
				con = mailWindow.getTimeUpdateValue();
			else if(mailWindow.getTimeUpdateUnit().equals("hours"))
				con = mailWindow.getTimeUpdateValue()*60;
			else if(mailWindow.getTimeUpdateUnit().equals("days"))
				con = mailWindow.getTimeUpdateValue()*60*24;
			final ScheduledFuture<?> timeHandle =
			      scheduler.scheduleAtFixedRate(mailThread, t, con, TimeUnit.MINUTES);
			    // Schedule the event, and run for 1 hour (60 * 60 seconds)
			    scheduler.schedule(new Runnable() {
			      @Override
				public void run() {
			        timeHandle.cancel(false);
			      }
			    }, end/(60*1000), TimeUnit.MINUTES);		    
			this.detach();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}