package deri.sensor.components.user;

import java.util.Date;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Button;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class ContactUsWindow extends Window {
	private Grid mailGrid = new Grid();
	private Rows rows = new Rows();
	private Button cmdSend;
	private Button cmdCancel;
	private Vbox vbox;	
	private Textbox txtContent;	
	private Textbox txtUserMail;
	private Textbox txtSubject;
	private Textbox txtFullName;
	String host = "smtp.gmail.com";

	
	public ContactUsWindow(){
		super();
		init();
	}
		
	private void init(){
		this.setTitle("Contact us");
		this.setId("ContactUs");
		this.setPosition("center,center");
		this.setContentStyle("overflow:auto");
		this.setClosable(true);
		this.setSizable(true);
		this.setStyle("border-top: 1px solid #AAB");
		this.setWidth("400px");
		this.setHeight("260px");
		vbox = new Vbox();
		vbox.setParent(this);
		vbox.setHeight("100%");
		vbox.setWidth("100%");;

		initMailGrid();
		
		Hbox hboxWButton = new Hbox();
		hboxWButton.setParent(vbox);
		hboxWButton.setSpacing("10px");
		hboxWButton.setStyle("padding-left:45%");
	
		
		cmdSend = new Button("Send","/imgs/Button/mail_send.png");
		cmdSend.setParent(hboxWButton);		
		cmdSend.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				sendComment();
			}
		});
		cmdCancel = new Button("Cancel","/imgs/Button/button_cancel.png");
		cmdCancel.setParent(hboxWButton);		
		cmdCancel.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				ContactUsWindow.this.detach();
			}
		});
	}

	public void initMailGrid(){
		
		Auxhead aux = new Auxhead();
		aux.setParent(mailGrid);
		Auxheader auxhder = new Auxheader("Please input your comment");
		auxhder.setColspan(2);
		auxhder.setStyle("color:red");
		auxhder.setParent(aux);
		
		mailGrid.setParent(vbox);
		mailGrid.setId("mailGrid");
		mailGrid.setMold("paging");
		mailGrid.setPageSize(10);
		mailGrid.setParent(vbox);
		mailGrid.setWidth("100%");
		mailGrid.setHeight("90%");
				
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
		r1.appendChild(new Label("Your email"));		
		txtUserMail = new Textbox();
		txtUserMail.setParent(r1);
		rows.appendChild(r1);
		
		Row r2 = new Row();
		r2.appendChild(new Label("Full name"));		
		txtFullName = new Textbox();
		txtFullName.setParent(r2);
		rows.appendChild(r2);
		
		Row r3 = new Row();
		r3.appendChild(new Label("Subject"));
		txtSubject = new Textbox();
		txtSubject.setParent(r3);
		rows.appendChild(r3);
		
		Row r4 = new Row();
		r4.appendChild(new Label("Content"));
		txtContent = new Textbox();
		txtContent.setParent(r4);
		txtContent.setRows(5);
		rows.appendChild(r4);		
	}
	
	public void sendComment(){
				
		String senderPass = "050785";
		String from = "nmqhoan@gmail.com";
		//Properties file  contains host details 
		java.util.Properties props = System.getProperties(); 
		props.put("mail.smtp.user", from); 
		props.put("mail.smtp.host", host); 
		props.put("mail.smtp.port", "587"); 
		props.put("mail.smtp.starttls.enable","true"); 
		props.put("mail.smtp.auth", "true"); 
		props.put("mail.debug", "true"); 
		         
		String pwd = new String(senderPass); 
		Session session = Session.getInstance(props, new 
				deri.sensor.components.user.notification.MyAuth(from,pwd));         
		try  
		{
			InternetAddress[] userEmailAdress;
			userEmailAdress = new InternetAddress[1];
			userEmailAdress[0] = new InternetAddress("nmqhoan@gmail.com");
		// Instantiatee a message 
		    Message msg = new MimeMessage(session); 
		      
		//Set message attributes 
		    msg.setFrom(new InternetAddress(from));
		    msg.setRecipients(Message.RecipientType.TO, userEmailAdress); 
		    msg.setSubject(txtSubject.getValue()); 
		    msg.setSentDate(new Date());
	        msg.setText(txtContent.getValue());
		    //Send the message 
		    Transport.send(msg); 	      
		    System.out.println("Message has been successfully send to " + userEmailAdress); 
		} 
		catch(Exception ee)  
		{ 
		     ee.printStackTrace();		            
		} 
	}
}
