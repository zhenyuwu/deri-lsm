package deri.sensor.components.user.notification;

import java.util.Date;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Space;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class UserNotificationEmailWindow extends Window {
	private Grid mailGrid = new Grid();
	private Rows rows = new Rows();
	private Button cmdNext;	
	private Button cmdBack;
	private Vbox vbox;	
	private Textbox txtContent;	
	private Textbox txtUserMail;
	private Textbox txtSubject;
	private Intbox txtTimeUpdateOccurence;
	private Intbox txtTimeDeleteOccurence;
	private Listbox lsbTimeUpdateUnit;
	private Listbox lsbTimeDeleteUnit;
	private Checkbox chksendData;
	String host = "smtp.gmail.com";
	private int order = 1;
	private InternetAddress[] addressTo;
	private Checkbox chkMailConfig;
	private Datebox txtStartDate;
	private Datebox txtExpiredDate;
	
	public UserNotificationEmailWindow(){
		super();
		init();
	}
	
	public boolean isSendMail(){
		//return chkMailConfig.isChecked();
		return true;
	}
	
	public int getOrder() {
		return order;
	}


	public void setOrder(int order) {
		this.order = order;
	}

	public InternetAddress[] getUserMailAdress(){
		setAddressTo();
		return addressTo;
	}
	
	public String getMailSubject(){
		return txtSubject.getValue();
	}
	
	public String getMailDescription(){
		return txtContent.getValue();
	}
	
	public int getTimeUpdateValue(){
		return txtTimeUpdateOccurence.getValue();
	}
	
	public String getTimeUpdateUnit(){
		return lsbTimeUpdateUnit.getSelectedItem().getLabel().toString();
	}
	
	public boolean isAttachFile(){
		return chksendData.isChecked();
	}
	
	public Date getExpiredDate(){
		return txtExpiredDate.getValue();
	}
	
	public Date getStartDate(){
		return txtStartDate.getValue();
	}
	
	private void init(){
		this.setTitle("Step 2 - Set mail information");
		this.setId("UserMailWizard");
		this.setPosition("center,center");
		this.setContentStyle("overflow:auto");
		this.setClosable(true);		
		this.setStyle("border-top: 1px solid #AAB");
		this.setSizable(true);
		//this.setMinimizable(true);		
		this.setWidth("430px");
		this.setHeight("420px");
		vbox = new Vbox();
		vbox.setParent(this);
		vbox.setHeight("100%");
		vbox.setWidth("100%");
		
		initGroupbox();
		initMailGrid();
		
		Hbox hboxWButton = new Hbox();
		hboxWButton.setParent(vbox);
		hboxWButton.setSpacing("10px");
		hboxWButton.setStyle("padding-left:55%");		
		
		cmdBack = new Button("Back","/imgs/Button/button_back.png");
		cmdBack.setParent(hboxWButton);
		cmdBack.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				backToPreviousStep();
			}
		});
		
		cmdNext = new Button("Next","/imgs/Button/button_next.png");
		cmdNext.setParent(hboxWButton);		
		cmdNext.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				moveToNextStep();
			}
		});
	}
	
	public void initGroupbox(){
		Groupbox grpServer = new Groupbox();
		grpServer.setParent(vbox);
		grpServer.setWidth("95%");
		Caption capSer = new Caption("User inbox setup");
		capSer.setParent(grpServer);
		
		Hbox hbox1 = new Hbox();
		hbox1.setParent(grpServer);
		
		Label lblNotificate = new Label("Update notification every");
		lblNotificate.setParent(hbox1);
		
		txtTimeUpdateOccurence = new Intbox();
		txtTimeUpdateOccurence.setParent(hbox1);		
		
		lsbTimeUpdateUnit= new Listbox();
		lsbTimeUpdateUnit.setParent(hbox1);
		lsbTimeUpdateUnit.setMold("select");
		initialize_lsbTimeUnit();
		lsbTimeUpdateUnit.addEventListener(Events.ON_SELECT, new EventListener() {			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				
			}			
		});		
		
		hbox1 = new Hbox();
		hbox1.setParent(grpServer);
		hbox1.setStyle("padding-left:23%");
		lblNotificate = new Label("Start date");
		lblNotificate.setParent(hbox1);
		
		txtStartDate = new Datebox(new Date());		
		txtStartDate.setFormat("M/d/yy KK:mm a");		
		txtStartDate.setParent(hbox1);
						
		hbox1 = new Hbox();
		hbox1.setParent(grpServer);
		hbox1.setStyle("padding-left:18%");
		lblNotificate = new Label("Expired date");
		lblNotificate.setParent(hbox1);
		
		txtExpiredDate = new Datebox(new Date());		
		txtExpiredDate.setFormat("M/d/yy KK:mm a");		
		txtExpiredDate.setParent(hbox1);
		
		
		Hbox hbox2 = new Hbox();
		hbox2.setParent(grpServer);
		
//		Label lblDeleteNotificate = new Label("Delete notification after");
//		lblDeleteNotificate.setParent(hbox2);
//		Space space = new Space();
//		space.setWidth("3px");
//		hbox2.appendChild(space);
//		txtTimeDeleteOccurence = new Intbox();
//		txtTimeDeleteOccurence.setParent(hbox2);		
//		
//		lsbTimeDeleteUnit= new Listbox();
//		lsbTimeDeleteUnit.setParent(hbox2);
//		lsbTimeDeleteUnit.setMold("select");
//		initialize_lsbTimeUnit();
//		lsbTimeDeleteUnit.addEventListener(Events.ON_SELECT, new EventListener() {			
//			@Override
//			public void onEvent(Event arg0) throws Exception {
//				// TODO Auto-generated method stub
//				
//			}			
//		});
//		
//		chkMailConfig = new Checkbox("Send to mail");
//		chkMailConfig.setParent(grpServer);
//		chkMailConfig.setChecked(false);
//		chkMailConfig.addEventListener(Events.ON_CHECK, new EventListener() {
//			
//			@Override
//			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
//				if(chkMailConfig.isChecked())
//					mailGrid.setVisible(true);
//				else
//					mailGrid.setVisible(false);
//			}
//		});
		
	}
	public void initMailGrid(){
		
		Auxhead aux = new Auxhead();
		aux.setParent(mailGrid);
		Auxheader auxhder = new Auxheader("Email");
		auxhder.setColspan(2);
		auxhder.setStyle("color:red");
		auxhder.setParent(aux);
		
		mailGrid.setParent(vbox);
		mailGrid.setId("mailGrid");
		mailGrid.setMold("paging");
		mailGrid.setPageSize(10);
		mailGrid.setParent(vbox);
		mailGrid.setWidth("100%");
		//mailGrid.setVisible(false);
		
		Columns columns = new Columns();
		columns.setParent(mailGrid);
		columns.setSizable(true);
		
		Column column = new Column();
		column.setParent(columns);
		column.setWidth("30%");
		
		column = new Column();
		column.setWidth("70%");
		column.setParent(columns);		
		
		rows.setParent(mailGrid);
		
		Row r1 = new Row();
		r1.appendChild(new Label("User email(separate individual addresses with commas)"));		
		txtUserMail = new Textbox();
		txtUserMail.setParent(r1);
		rows.appendChild(r1);
		
		Row r2 = new Row();
		r2.appendChild(new Label("Subject"));
		txtSubject = new Textbox();
		txtSubject.setParent(r2);
		rows.appendChild(r2);
		
		Row r4 = new Row();
		r4.appendChild(new Label("Include description"));
		txtContent = new Textbox();
		txtContent.setParent(r4);
		txtContent.setRows(5);
		rows.appendChild(r4);
		
		Row r5 = new Row();
		r5.setAlign("center");
		r5.appendChild(new Label(""));
		chksendData = new Checkbox("Send data file");
		chksendData.setParent(r5);
		rows.appendChild(r5);		
	}

	private void initialize_lsbTimeUnit() {
		// TODO Auto-generated method stub
		//lsbTimeUpdateUnit.appendChild(new Listitem("seconds"));
		lsbTimeUpdateUnit.appendChild(new Listitem("minutes"));
		lsbTimeUpdateUnit.appendChild(new Listitem("hours"));
		lsbTimeUpdateUnit.appendChild(new Listitem("days"));
		lsbTimeUpdateUnit.setSelectedIndex(0);
		
		//lsbTimeDeleteUnit.appendChild(new Listitem("seconds"));
//		lsbTimeDeleteUnit.appendChild(new Listitem("minutes"));
//		lsbTimeDeleteUnit.appendChild(new Listitem("hours"));
//		lsbTimeDeleteUnit.appendChild(new Listitem("days"));
//		lsbTimeDeleteUnit.setSelectedIndex(0);
	}
	
	private void backToPreviousStep(){
		UserNotificationWizard u = (UserNotificationWizard) this.getParent();
		u.moveToStep(this.getOrder()-1);
	}
	
	private void moveToNextStep(){		
		try{
			UserNotificationWizard u = (UserNotificationWizard) this.getParent();
			u.moveToStep(this.getOrder()+1);
			u.startMappingProcess();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setAddressTo(){
		String st = txtUserMail.getValue();
		String[] addArr = st.split(",");
		addressTo = new InternetAddress[addArr.length]; 
		for(int i=0;i<addArr.length;i++){
			try {
				addressTo[i] = new InternetAddress(addArr[i]);
			}catch (AddressException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}
		}
	}
}
