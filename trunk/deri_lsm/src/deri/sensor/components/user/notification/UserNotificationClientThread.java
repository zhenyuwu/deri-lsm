package deri.sensor.components.user.notification;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import deri.sensor.RabbitMQ.UserRabbitMQProducer;
import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.FieldSearchConstainsUtil;
import deri.sensor.javabeans.*;
import deri.sensor.manager.PlaceManager;
import deri.sensor.manager.SensorManager;
import deri.sensor.manager.UserActiveManager;
import deri.sensor.utils.ConstantsUtil;
import deri.sensor.utils.DateUtil;
import deri.sensor.utils.NumberUtil;
import deri.sensor.utils.StringUtil;
import deri.sensor.weather.weatherbug.WeatherBugXMLParser;
import deri.sensor.weather.wunderground.WUnderGroundXMLParser;
import deri.sensor.weather.yahoo.YahooWeatherXMLParser;
import deri.sensor.wrapper.WebServiceURLRetriever;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class UserNotificationClientThread extends Thread{
	private ArrayList<List> listSpatialResult;
	private String type;
	private String spatialOperator;
	private double lat;
	private double lng;
	private double distance;
	private SensorManager sensorManager;
	private PlaceManager placeManager;
	private UserActiveManager userManager;
	private InternetAddress[] userEmailAdress;
	private String mailSubject;
	private String mailContentDescription;
	private String host = "smtp.gmail.com";
	private String timeUnit;
	private String userName="nmqhoan";
	private String tempFilePath="";
	private String sensorField;
	private String valueOperator;
	private String valueCondition;
	private boolean isAttachedFile;
	private boolean isSendMail;
	private UserRabbitMQProducer userRProducer;
	
	public UserNotificationClientThread(){
		sensorManager = ServiceLocator.getSensorManager();	
		placeManager = ServiceLocator.getPlaceManager();
		userManager = ServiceLocator.getUserActiveManager();
		//init();
	}
	

	public boolean isSendMail() {
		return isSendMail;
	}


	public void setSendMail(boolean isSendMail) {
		this.isSendMail = isSendMail;
	}

	public String getTimeUnit() {
		return timeUnit;
	}


	public void setTimeUnit(String timeUnit) {
		this.timeUnit = timeUnit;
	}

	public String getSensorField() {
		return sensorField;
	}


	public void setSensorField(String sensorField) {
		this.sensorField = sensorField;
	}


	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSpatialOperator() {
		return spatialOperator;
	}

	public void setSpatialOperator(String spatialOperator) {
		this.spatialOperator = spatialOperator;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
		
	public String getValueOperator() {
		return valueOperator;
	}


	public void setValueOperator(String valueOperator) {
		this.valueOperator = valueOperator;
	}


	public String getValueCondition() {
		return valueCondition;
	}


	public void setValueCondition(String valueCondition) {
		this.valueCondition = valueCondition;
	}


	public ArrayList<List> getListSpatialResult() {
		return listSpatialResult;
	}

	public void setListSpatialResult(ArrayList<List> listSpatialResult) {
		this.listSpatialResult = listSpatialResult;
	}

	public InternetAddress[] getUserEmailAdress() {
		return userEmailAdress;
	}


	public void setUserEmailAdress(InternetAddress[] userEmailAdress) {
		this.userEmailAdress = userEmailAdress;
	}	

	public boolean isAttachedFile() {
		return isAttachedFile;
	}


	public void setAttachedFile(boolean isAttachedFile) {
		this.isAttachedFile = isAttachedFile;
	}

	
	public UserRabbitMQProducer getUserRProducer() {
		return userRProducer;
	}


	public void setUserRProducer(UserRabbitMQProducer userRProducer) {
		this.userRProducer = userRProducer;
	}


	public String getMailSubject() {
		return mailSubject;
	}

	public void setMailSubject(String mailSubject) {
		this.mailSubject = mailSubject;
	}

	public String getMailContentDescription() {
		return mailContentDescription;
	}

	public void setMailContentDescription(String mailContentDescription) {
		this.mailContentDescription = mailContentDescription;
	}
	
	public void init(){
//		userRProducer = new UserRabbitMQProducer();
//		UserRabbitMQ userRb = userManager.getUserRabbitMQFromUserName(userName);
//		if(userRb==null){
//			User user = userManager.getUser(userName);
//			userRb = new UserRabbitMQ();
//			userRb.setUser(user);
//			userRb.setQueue_name(user.getUsername());
//			userRb.setExchange_name(ConstantsUtil.rabbitMQExchangeName);			
//			userManager.addUserRabbitMQ(userRb);
//		}
//		userRProducer.setUserRabbit(userRb);
//		userRProducer.setRoutingKey(userRProducer.getUserRabbit().getUser().getId());
//		userRProducer.openConnection();
	}
	
	@Override
	public void run(){
		String message = "";		
		try {
			if(type.equals("weather"))					
				message = getLiveWeatherDataForNotification();
			else
				message = getLiveDataForNotification();	
			try{
//				sendToRabbitServer(message);
			}catch(Exception e){
				e.printStackTrace();
			}
			if(isSendMail){						
				sendMail(message);
			}				
		} catch (InterruptedException e){
		// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}
	
	private void sendToRabbitServer(String message) {
		// TODO Auto-generated method stub
		userRProducer.setMessage(message);
		userRProducer.publishMessage();
		
		
//		UserNotificationServerThread server = new UserNotificationServerThread("nmqhoan");
//		server.openConnection();
//		server.printMessage();
//		server.closeConnection();
	}


	public void getSensorIdfromSpatialQuery(){
		distance = distance==0?0.05:distance;
		listSpatialResult = sensorManager.getAllSpecifiedSensorAndLatLongAroundPlace(type.toString(), spatialOperator,lng, lat,distance);
		//System.out.println(listSpatialResult);
	}
	 
	public String getMaiPreContent(){
		StringBuffer strbuff = new StringBuffer();				
		return strbuff.toString();		
	}
	public void sendMail(String content) throws InterruptedException{
		
		String from = "nmqhoan@gmail.com";
		String senderPass = "quynhhoa1402";
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
		// Instantiatee a message 
		     Message msg = new MimeMessage(session); 
		      
		//Set message attributes 
		     msg.setFrom(new InternetAddress(from)); 
//		     InternetAddress[] address = {new InternetAddress(userEmailAdress)}; 
		     msg.setRecipients(Message.RecipientType.TO, userEmailAdress); 
		     msg.setSubject(mailSubject); 
		     msg.setSentDate(new Date()); 	            
		     		     
	         if(isAttachedFile){
	        	// attach the file to the message
	        	 MimeBodyPart messagePart = new MimeBodyPart();
			     messagePart.setText(content);
		         Multipart multipart = new MimeMultipart();
		         multipart.addBodyPart(messagePart);
		         
	        	 MimeBodyPart attachmentPart = new MimeBodyPart();
		         FileDataSource fds = new FileDataSource(tempFilePath);
		         attachmentPart.setDataHandler(new DataHandler(fds));
		         attachmentPart.setFileName(fds.getName());
	        	 multipart.addBodyPart(attachmentPart);
	        	 msg.setContent(multipart);
	         }else	         
		     // Set message content	         
	        	 msg.setText(content); 
		             
		     //Send the message 
		     Transport.send(msg); 	      
		     System.out.println("Message has been successfully send to " + userEmailAdress); 
		} 
		catch(Exception ee)  
		{ 
		     ee.printStackTrace();	            
		} 
	}

	
	public String getLiveWeatherDataForNotification(){
		String content="";		
		String tblName = StringUtil.toFirstUpperLetter(StringUtil.replaceAllBlankWith_(sensorField));
		
		for(int i=0;i<listSpatialResult.get(0).size();i++){
//			double lat = (Double) listSpatialResult.get(1).get(i);
//			double lng = (Double) listSpatialResult.get(2).get(i);
			String idURL = listSpatialResult.get(0).get(i).toString();
			//List<String> weatherSource = sensorManager.getAllSourcesWithSpecifiedLatLngSensorType(lat, lng, "weather");
			String id = idURL.substring(idURL.lastIndexOf("/")+1);
			Sensor sensor = sensorManager.getSpecifiedSensorWithSensorId(id);
			//for(String source: weatherSource){
				try{
					Date time = new Date();
					ArrayList weatherInfo = new ArrayList<AbstractProperty>();		
					//Sensor sensor = sensorManager.getSpecifiedSensorWithSource(source);
					String sourceType = sensor.getSourceType();
					String xml = WebServiceURLRetriever.RetrieveFromURL(sensor.getSource());	
					if(sourceType.equals("wunderground")){
						weatherInfo = WUnderGroundXMLParser.getWeatherElementsFromWUnderGroundXML(xml);
						time = WUnderGroundXMLParser.readerTime;
					}
					else if(sourceType.equals("weatherbug")){
						weatherInfo = WeatherBugXMLParser.getWeatherElementsFromWeatherBugXML(xml);
						time = WeatherBugXMLParser.readerTime;
					}
					else if(sourceType.equals("yahoo")){
						weatherInfo = YahooWeatherXMLParser.getWeatherElementsFromYahooXML(xml);
						time = YahooWeatherXMLParser.readerTime;
					}
					else if(sourceType.equals("australia")){						
						weatherInfo = YahooWeatherXMLParser.getWeatherElementsFromYahooXML(xml);
						time = YahooWeatherXMLParser.readerTime;
					}
					Object data = null;
					for(Object obj:weatherInfo)
						if(Class.forName("deri.sensor.javabeans." + tblName).isInstance(obj)){
							data = ((AbstractProperty)obj).getValue();
							break;
						}
					if(isSatisfied(data, valueOperator)){
						content+=" City:" + sensor.getPlace().getCity();						
						Observation newObs = new Observation();
						newObs.setTimes(time);
						newObs.setSensorId(sensor.getId());
						content+= setNewWeatherNotificationContent(weatherInfo, newObs);
//						sensorManager.addNewObservationToGraph(newObs);
						if(isAttachedFile){
							addContentForFile(newObs.getId());		
						}
					}					
				}catch(Exception e){
					e.printStackTrace();
					//continue;
				}
			//}
		}
//		if(isAttachedFile){			
//			saveFileToTemporaryStorage(strData.toString());
//		}
		return content;
	}
	
	public String getLiveDataForNotification(){
		if(type.equals("roadactivity")){
			String url = "http://www.buckeyetraffic.org/services/RoadActivity.aspx";
			String xml = WebServiceURLRetriever.RetrieveFromURL(url);
			xml = xml.trim().replaceFirst("^([\\W]+)<","<");			
		    //BuckeyeRoadActivityXMLParser.getRoadActivityFromUrl(xml);		    
		}else if(type.equals("traffic")){
			String url = "http://www.buckeyetraffic.org/services/RoadActivity.aspx";
			String xml = WebServiceURLRetriever.RetrieveFromURL(url);
			xml = xml.trim().replaceFirst("^([\\W]+)<","<");
		    //BuckeyeTrafficXMLParser.getTrafficElementFromUrl(xml);
		}
		
		String content="";
		//StringBuffer strData= new StringBuffer();
		try{			
			String tblName = sensorField!=""?StringUtil.toFirstUpperLetter(StringUtil.replaceAllBlankWith_(sensorField)):"";
			
			for(int i=0;i<listSpatialResult.get(0).size();i++){
				double lat = (Double) listSpatialResult.get(1).get(i);
				double lng = (Double) listSpatialResult.get(2).get(i);			
				try{				
					String sensorIdURL = listSpatialResult.get(0).get(i).toString();
					Observation newestObs = sensorManager.getNewestObservationForOneSensor(sensorIdURL);
					Object data = null;
					try{
						AbstractProperty absPro = sensorManager.getSpecifiedReadingForOneObservation(newestObs.getId(), tblName);
						data = absPro.getValue();
					}catch(Exception e){
						e.printStackTrace();
					}
					if(isSatisfied(data, valueOperator)){	
//						content+="\n City:" + newestObs.getSensor().getPlace().getCity();
						content+= setNewSensorNotificationContent(newestObs);
						if(isAttachedFile){
							addContentForFile(newestObs.getId());						
						}
					}						
						
				}catch(Exception e){
					e.printStackTrace();
					continue;				
				}
			}
//			if(isAttachedFile){			
//				saveFileToTemporaryStorage(strData.toString());
//			}
		}catch(Exception e){
			
		}
		return content;
	}
	
		
	private String setNewSensorNotificationContent(Observation newestObs) {		
		// TODO Auto-generated method stub
		List<ArrayList> arrs = sensorManager.getReadingDataOfObservation(newestObs.getId());
		String content="";
		for(ArrayList arr: arrs){
			try{				
				String pro = arr.get(0).toString().substring(arr.get(0).toString().lastIndexOf("#")+1);
				content+="\n "+pro+": "+ arr.get(1);
			}catch(Exception e){
				continue;
			}
		}
		content+="\nSensor update time: " + newestObs.getTimes();
		content+="\nSystem update time: " + (new Date()).toString();
		return content;
	}


	private void saveFileToTemporaryStorage(String st){
		tempFilePath = "/root/tomcat7/webapps/TempData/" + userName + 
		DateUtil.date2FormatString(new Date(), "ddMMyyyyhhmmss");
		String ext ="n3";
		try {
			File file = new File(tempFilePath + "." + ext);
			Writer output = null;			
			output = new BufferedWriter(new FileWriter(file));
			output.write(st);
			output.close();
			tempFilePath = file.getAbsolutePath();
		}catch(Exception e){
			
		}		
	}
	
	@SuppressWarnings("unchecked")
	private void addContentForFile(String obsId){
		tempFilePath = "/usr/local/tomcat7/webapps/SensorMiddlewareData/TempData/" + userName + 
		DateUtil.date2FormatString(new Date(), "ddMMyyyyhhmmss");
		String ext ="n3";
		try {
			File file = new File(tempFilePath + "." + ext);	
						
			FileWriter outFile = new FileWriter(file);
			PrintWriter out = new PrintWriter(outFile);
			
			List lstData = null;		
			lstData = sensorManager.getReadingTriplesOfObservation(obsId);
			Iterator iter = lstData.iterator();
			while(iter.hasNext()){
				ArrayList arr = (ArrayList)iter.next();				
				out.println(arr.get(0)+ " " + arr.get(1)+ " " + arr.get(2)+ "\n");
			}			
			tempFilePath = file.getAbsolutePath();
			out.close();
		}catch(Exception e){
			e.printStackTrace();		
		}
		
	}
	
	public boolean isSatisfied(Object data,String compareOperator){
		try{
			if(NumberUtil.isDouble(data.toString())){
				double d = Double.parseDouble(data.toString());
				double value = Double.parseDouble(valueCondition);
				if (FieldSearchConstainsUtil.valueSearchOperator.get(compareOperator)=="=")
					return (d==value)?true:false;
				if (FieldSearchConstainsUtil.valueSearchOperator.get(compareOperator)==">")
					return (d>value)?true:false;
				if (FieldSearchConstainsUtil.valueSearchOperator.get(compareOperator)==">=")
					return (d>=value)?true:false;
				if (FieldSearchConstainsUtil.valueSearchOperator.get(compareOperator)=="<=")
					return (d<=value)?true:false;
				if (FieldSearchConstainsUtil.valueSearchOperator.get(compareOperator)=="!=")
					return (d!=value)?true:false;
				if (FieldSearchConstainsUtil.valueSearchOperator.get(compareOperator)=="<")
					return (d<value)?true:false;
			}else if(data instanceof String){
				String d = (String)data;
				return d.equals(valueCondition);
			}	
		}catch(Exception e){
			
		}
		return true;
	}
	
	public String setNewWeatherNotificationContent(ArrayList<AbstractProperty> weatherInfo,Observation newObs){
		String content="";
		for(AbstractProperty obj:weatherInfo){			
			if(obj.getPropertyName().contains("Temperature")){
				content+="\n Temperature: " + obj.getValue();				
//				sensorManager.addObject(obj);
			}else if(obj.getPropertyName().contains("Windchill")){
				double v = Double.parseDouble(obj.getValue());
				if (v!= ConstantsUtil.weather_defalut_value)
					content+= "\n Wind chill: " + v;		
//				sensorManager.addObject(obj);
			}else if(obj.getPropertyName().contains("WindSpeed")){
				double v = Double.parseDouble(obj.getValue());
				if (v!= ConstantsUtil.weather_defalut_value)
					content+= "\n Wind speed: " + v;					
//				sensorManager.addObject(obj);
			}else if(obj.getPropertyName().contains("Direction")){
				content+="\n Direction: " + ((Direction)obj).getValue();				
//				sensorManager.addObject(obj);
			}else if(obj.getPropertyName().contains("AtmosphereHumidity")){
				double v = Double.parseDouble(obj.getValue());
				if (v!= ConstantsUtil.weather_defalut_value)
					content+= "\n Atmosphere Humidity: " + v;				
//				sensorManager.addObject(obj);
			}else if(obj.getPropertyName().contains("AtmospherePressure")){
				double v = Double.parseDouble(obj.getValue());
				if (v!= ConstantsUtil.weather_defalut_value)
					content+= "\n Atmosphere Pressure: " + v;				
//				sensorManager.addObject(obj);
			}else if(obj.getPropertyName().contains("AtmosphereVisibility")){
				double v = Double.parseDouble(obj.getValue());
				if (v!= ConstantsUtil.weather_defalut_value)
					content+= "\n Atmosphere Visibility: " + v;				
//				sensorManager.addObject(obj);
			}else if(obj.getPropertyName().contains("Status")){
				content+="\n Status: " + ((Status)obj).getValue();				
//				sensorManager.addObject(obj);
			}			
		}
		content+="\n Sensor time: " + ((AbstractProperty)weatherInfo.get(0)).getTimes();
		content+="\n System time: " + (new Date()).toString();
		return content;
	}
		
}
