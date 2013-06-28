package deri.sensor.components.user.datamanagent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.*;

import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.FieldSearchConstainsUtil;
import deri.sensor.javabeans.UserFeed;
import deri.sensor.manager.SensorManager;
import deri.sensor.meta.SensorTypeEnum;
import deri.sensor.utils.ConstantsUtil;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class UserDataFeedTab extends Tabpanel{

	private Combobox cmbCity;	
	private Grid grid = new Grid();
	private Rows rows = new Rows();
	private double radius=0;
	private List<Object[]> lstCity;
	private Checkbox chkQuery;
	private Textbox txtCityLong;
	private Textbox  txtCityLat;
	private Textbox txtEmail;
	private Textbox txtQuery;
	
	private Decimalbox txtFromValue;
	private Decimalbox txtToValue;
	
	private Listbox lstFromValueOperator;
	private Listbox lstSensorFiled;
	private Listbox lstSensorType;
	
	private Button cmdRegister;
	private SensorManager sensorManager;
	private String type;
	private Row valueRow;
	private Hbox fromValueHbox;
	private int order = 0;

	private Radiogroup rdGrpType;
	private Radio rdXML;
	private Radio rdHub;
	
	public UserDataFeedTab(){
		super();
	}
	
		
	public void init(){
		this.setId("DataFeedTabpanel");
		this.setStyle("border-top: 1px solid #AAB");
		this.setWidth("600px");
		sensorManager = ServiceLocator.getSensorManager();
		initGrid();		
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

	private void initGrid(){
		grid.setParent(this);
		grid.setId("grid");
		grid.setMold("paging");
		grid.setPageSize(15);
		grid.setWidth("100%");
		grid.setHeight("100%");
		grid.setStyle("overflow:auto");
		
		Columns columns = new Columns();
		columns.setParent(grid);
		Column column = new Column();
		column.setParent(columns);
		column.setLabel("Properties");
		column.setWidth("20%");
		column = new Column();
		column.setParent(columns);
		column.setLabel("Criteria");
		//column.setAlign("center");
		rows.setParent(grid);
		
		//row 1 add sensor type combobox and sensor filed combobox
//		Row r1 = new Row();
//		r1.appendChild(new Label("Sensor type"));
//		Hbox hbss = new Hbox();
//		hbss.setParent(r1);
//		hbss.setSpacing("20px");
//		lstSensorType= new Listbox();		
//		lstSensorType.setParent(hbss);	
//		lstSensorType.setMold("select");		
//		initialize_lstSensorType();
//		
//		lstSensorFiled = new Listbox();
//		lstSensorFiled.setMold("select");
//		lstSensorFiled.setParent(hbss);		
//		rows.appendChild(r1);
//		
//		//row 2 add city combobox
//		Row r2 = new Row();
//		r2.appendChild(new Label("City"));
//		cmbCity = new Combobox();
//		cmbCity.setParent(r2);		
//		r2.appendChild(cmbCity);
//		rows.appendChild(r2);
//		
//		//row 3 add latitude text box
//		Row r3 = new Row();
//		r3.appendChild(new Label("Lat"));
//		txtCityLat = new Textbox();
//		txtCityLat.setWidth("50%");		
//		r3.appendChild(txtCityLat);
//		rows.appendChild(r3);
//		
//		//row 4 add longitude text box
//		Row r4 = new Row();
//		r4.appendChild(new Label("Long"));
//		txtCityLong = new Textbox();
//		txtCityLong.setWidth("50%");		
//		r4.appendChild(txtCityLong);
//		rows.appendChild(r4);
//
//        //add value criteria
//        valueRow = new Row();
//		valueRow.appendChild(new Label("Value"));		
//		Hbox hb2 = new Hbox();
//		hb2.setParent(valueRow);
//		hb2.setSpacing("10px");
//		
//		lstFromValueOperator = new Listbox();
//		lstFromValueOperator.setMold("select");
//		lstFromValueOperator.setMultiple(false);
//		lstFromValueOperator.setParent(hb2);
//		initialize_valueSearchOpertor();
//		
//		fromValueHbox = new Hbox();
//		fromValueHbox.setParent(hb2);
//		fromValueHbox.setSpacing("10px");
//		txtFromValue = new Decimalbox();
//		txtFromValue.setWidth("70%");
//		txtFromValue.setParent(fromValueHbox);
//		
//		fromValueHbox.appendChild(new Label("To"));
//		
//		txtToValue = new Decimalbox();		
//		txtToValue.setParent(fromValueHbox);
//		txtToValue.setWidth("70%");
//		txtToValue.setReadonly(true);
//		
//		rows.appendChild(valueRow);
		
		//add sparql query
		final Row r7 = new Row();
		chkQuery = new Checkbox("CQELS query");
		chkQuery.setChecked(false);
		r7.appendChild(chkQuery);
		txtQuery = new Textbox();
		txtQuery.setWidth("99%");
		txtQuery.setMultiline(true);
		txtQuery.setRows(7);
		txtQuery.setDisabled(true);
		r7.appendChild(txtQuery);
		rows.appendChild(r7);
		
		//add sparql query
		final Row r6 = new Row();
		r6.appendChild(new Label("Type"));
		
		rdGrpType = new Radiogroup();
		rdHub = new Radio("Hub");
		rdHub.setParent(rdGrpType);
		rdXML = new Radio("XML feed");
		rdXML.setParent(rdGrpType);
		r6.appendChild(rdGrpType);
		rows.appendChild(r6);
			
		//add register mail
		final Row r8 = new Row();
		r8.appendChild(new Label("Email"));
		txtEmail = new Textbox();
		txtEmail.setWidth("90%");
		r8.appendChild(txtEmail);
		rows.appendChild(r8);
		
		//add Search button
		Row r9 = new Row();
		r9.setAlign("center");		
		r9.appendChild(new Label());
		
		cmdRegister = new Button("Register feed");
		cmdRegister.setParent(r9);		
		rows.appendChild(r9);
			
		addEventListener();
	}
	
	private void addEventListener(){
//		lstSensorType.addEventListener(Events.ON_SELECT , new EventListener() {
//			@Override
//			public void onEvent(Event arg0) throws Exception {
//				// TODO Auto-generated method stub				
//				setType(lstSensorType.getSelectedItem().getLabel().toLowerCase());
//				initialize_cmbCity(lstSensorType.getSelectedItem().getLabel().toLowerCase());
//				if(type.equals("webcam")||type.equals("satellite")||type.equals("flood")
//						||type.equals("radar")||type.equals("traffic")){				
//					valueRow.setVisible(false);
//					lstSensorFiled.setVisible(false);
//				}else if(type.equals("roadactivity")){				
//					valueRow.setVisible(true);
//					fromValueHbox.setVisible(false);
//					lstSensorFiled.setVisible(true);
//					initialize_lstSensorField(lstSensorType.getSelectedItem().getLabel().toLowerCase());
//					initialize_valueSearchForRoadActivity(lstSensorFiled.getModel().getElementAt(0).toString());
//				}else{				
//					valueRow.setVisible(true);
//					fromValueHbox.setVisible(true);
//					lstSensorFiled.setVisible(true);
//					initialize_lstSensorField(lstSensorType.getSelectedItem().getLabel().toLowerCase());
//					initialize_valueSearchOpertor();
//				}				
//			}
//		});
//		
//		lstSensorFiled.addEventListener(Events.ON_SELECT, new EventListener() {
//			
//			@Override
//			public void onEvent(Event arg0) throws Exception {
//				// TODO Auto-generated method stub
//				if(type.equals("roadactivity"))
//					initialize_valueSearchForRoadActivity(lstSensorFiled.getSelectedItem().getLabel());				
//			}
//		});
//		
//		cmbCity.addEventListener(Events.ON_CHANGE, new EventListener() {			
//			@Override
//			public void onEvent(Event arg0) throws Exception {
//				// TODO Auto-generated method stub
//				if(cmbCity.getSelectedIndex()>-1){
//					txtCityLat.setText(lstCity.get(cmbCity.getSelectedIndex())[2].toString());
//					txtCityLong.setText(lstCity.get(cmbCity.getSelectedIndex())[3].toString());
//				}
//			}
//		});
		
		chkQuery.addEventListener(Events.ON_CHECK, new EventListener() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				if(chkQuery.isChecked())
					txtQuery.setDisabled(false);
				else
					txtQuery.setDisabled(true);
			}
		});
		
		cmdRegister.addEventListener(Events.ON_CLICK,new EventListener() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				long l = System.nanoTime();
				UserFeed feed = new UserFeed();
				feed.setId(Long.toString(l));
				feed.setFeedURL(generateFeedURL(Long.toString(l)));	
				if(rdHub.isChecked())
					feed.setFeedType("Hub");
				else feed.setFeedType("XML");
				if(chkQuery.isChecked())
					feed.setQuery(txtQuery.getValue());
				else feed.setQuery(getQuery());
				sensorManager.addObject(feed);
				sendToMail(feed.getFeedURL());
			}
		});
	}
	
	public String setMailContent(UserFeed feed){
		String content="";
		if(feed.getFeedType().equals("XML"))
			content = "Your XML URL feed: "+feed.getFeedURL();
		else content = "Hub topic: "+"\n" +
						"Feed URL:" + feed.getFeedURL();
		return content;
	}
	public void sendToMail(String feedURL){
		String host = "smtp.gmail.com";
		String senderPass = "quynhhoa1402";
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
			userEmailAdress[0] = new InternetAddress(txtEmail.getValue());
		// Instantiatee a message 
		    Message msg = new MimeMessage(session); 
		      
		//Set message attributes 
		    msg.setFrom(new InternetAddress(from));
		    msg.setRecipients(Message.RecipientType.TO, userEmailAdress); 
		    msg.setSubject("Data feed"); 
		    msg.setSentDate(new Date());
	        msg.setText(feedURL);
		    //Send the message 
		    Transport.send(msg); 	      
		    showMessage("Your feed has been successfully send to " + txtEmail.getValue(),
		    		"Data feed Dialogue", Messagebox.OK, Messagebox.INFORMATION);		    
		} 
		catch(Exception ee)  
		{ 
		     System.out.println("Error : " + ee.toString()); 		            
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
	}
	
	private void initialize_lstSensorField(String sensorType){
		List<String> temp = sensorManager.getAllSensorPropertiesForSpecifiedSensorType(type).get(1);				
		List<String> fieldList = FieldSearchConstainsUtil.sensorFieldLabel.get(sensorType);
		if (fieldList==null){
			fieldList = new ArrayList<String>();
			for(String tbl:temp){
				tbl = tbl.substring(tbl.lastIndexOf("#")+1);
				fieldList.add(tbl);
			}			
		}
		ListModelList lm2 = new ListModelList(fieldList);
        lm2.addSelection(lm2.get(0));         
        lstSensorFiled.setModel(lm2);	        
	}
	
	private void initialize_cmbCity(String sensorType){
		lstCity  = sensorManager.getAllCityWithSpecifiedSensorType(sensorType);
		List city = new ArrayList<String>();		
		Iterator iter = lstCity.iterator();
		while(iter.hasNext()){
			Object[] obj = (Object[])iter.next();
			city.add(obj[0]+", "+obj[1]);			
		}				
		ListModelList lm2 = new ListModelList(city);
		lm2.addSelection(lm2.get(0));   
		cmbCity.setModel(lm2);		
		
	}
	
	private void initialize_valueSearchOpertor(){
		List lstOperator = new ArrayList<String>();
		Set set = FieldSearchConstainsUtil.valueSearchOperator.entrySet();
		Iterator iter = set.iterator();
		while(iter.hasNext()){
			Map.Entry me = (Map.Entry)iter.next();
			lstOperator.add(me.getKey());
		}		
		ListModelList lm2 = new ListModelList(lstOperator);		       
		lm2.addSelection(lm2.get(0));   
        lstFromValueOperator.setModel(lm2);        
	}
	
	private void initialize_valueSearchForRoadActivity(String roadActivityField){		
		ListModelList lm2 = new ListModelList(FieldSearchConstainsUtil.roadActivityField2ValueOperator
							.get(roadActivityField));   
		lm2.addSelection(lm2.get(0));   
        lstFromValueOperator.setModel(lm2);        
	}
	
	private String getQuery(){
		String query="";
		
		return query;
	}
	
	private String generateFeedURL(String id){
		String feedURL=ConstantsUtil.data_feed_prefix+"?api=xmlfeed&feedId="+id;
		return feedURL;
	}
	
	public void showMessage(String message, String title, int buttons, String icon){
		try {
			Messagebox.show(message, title, buttons, icon);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
