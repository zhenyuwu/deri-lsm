package deri.sensor.components.user.datamanagent;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Doublebox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;

import deri.sensor.components.user.UserSearchLiveInforResultWindow;
import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.VirtuosoConstantUtil;
import deri.sensor.javabeans.Place;
import deri.sensor.javabeans.Sensor;
import deri.sensor.javabeans.User;
import deri.sensor.manager.PlaceManager;
import deri.sensor.manager.SensorManager;
import deri.sensor.places.yahoo.YahooWhereURLXMLParser;
import deri.sensor.utils.ConstantsUtil;
import deri.sensor.wrapper.GoogleWeatherWrapper;
import deri.sensor.wrapper.TriplesDataRetriever;
import deri.sensor.wrapper.WebServiceURLRetriever;
import deri.sensor.wrapper.YahooWeatherWrapper;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class UserLiveDataTab extends Tabpanel {
	private Grid grdSensor = new Grid();

	private SensorManager sensorManager;
	private HashMap<String,Boolean> lstSensorType;	
	private User user;
	private Button cmdGetLive;	

	private Doublebox txtCityLat,txtCityLong;
	private Textbox txtAddress;
	private Textbox txtCountry,txtCity;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	
	public void setLocation(double lat,double lng){
		txtCityLat.setValue(lat);
		txtCityLong.setValue(lng);
	}
	
	public void init(){	
		this.setId("UserLiveData");
		sensorManager = ServiceLocator.getSensorManager();
		initUI();
		addEventListener();
	}
	
	private void addEventListener() {
		// TODO Auto-generated method stub
		cmdGetLive.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				showLiveResults();				
			}
		});			
	}

	private void initUI() {
		// TODO Auto-generated method stub
				
		grdSensor.setParent(this);
		grdSensor.setMold("paging");
		grdSensor.setVflex(true);
		grdSensor.setPageSize(7);
		
		Columns columns = new Columns();
		columns.setParent(grdSensor);
		Column column = new Column();
		column.setParent(columns);
		column.setLabel("Properties"); 
		column.setWidth("20%");
		column = new Column();
		column.setParent(columns);
		column.setLabel("Value");
		Rows rows = new Rows();
		rows.setParent(grdSensor);
		
		Row r0 = new Row();
		r0.appendChild(new Label("Address (Please use commas to separate the components)"));
		txtAddress = new Textbox();
		txtAddress.setWidth("80%");
		txtAddress.setParent(r0);
		txtAddress.setTooltip("For example: 701 First Ave., Sunnyvale, CA 94089");
		rows.appendChild(r0);
		
		Row r1 = new Row();
		r1.appendChild(new Label("City"));
		txtCity = new Textbox();
		txtCity.setParent(r1);
		rows.appendChild(r1);
		
		Row rCountry = new Row();
		rCountry.appendChild(new Label("Country"));
		txtCountry = new Textbox();
		txtCountry.setParent(rCountry);
		rows.appendChild(rCountry);
		
		//row 2 add latitude text box
		Row r2 = new Row();
		r2.appendChild(new Label("Lat"));
		txtCityLat = new Doublebox();
		txtCityLat.setWidth("40%");		
		r2.appendChild(txtCityLat);
		rows.appendChild(r2);
		
		//row 3 add longitude text box
		Row r3 = new Row();
		r3.appendChild(new Label("Long"));
		txtCityLong = new Doublebox();
		txtCityLong.setWidth("40%");		
		r3.appendChild(txtCityLong);
		rows.appendChild(r3);
				
		Row r5 = new Row();
		r5.appendChild(new Label(" "));
		r5.setAlign("center");	
		cmdGetLive = new Button("Get Live");
		cmdGetLive.setParent(r5);		
		rows.appendChild(r5);
		
	}
		
	private void showLiveResults(){
		try{
			String addresString = txtAddress.getValue().trim().replace(" ", "+");
			String locationPra = 
						(addresString.trim().equals("")?  ""  : (addresString + ", ")) 
					+ 	(txtCity.getValue().trim().equals("")? "" : (txtCity.getValue().trim()+", "))
					+	(txtCountry.getValue().trim().equals("")? "" : (txtCountry.getValue().trim()+", "))
					+	txtCityLat.getValue()+","+txtCityLong.getValue();
			
			Place place = YahooWhereURLXMLParser.placeStr2PlaceObj(locationPra);
			if(txtAddress.getValue()=="")
				txtAddress.setValue(place.getCity()+","+place.getCountry());
			txtCity.setValue(place.getCity());
			txtCountry.setValue(place.getCountry());
			txtCityLat.setValue(place.getLat());
			txtCityLong.setValue(place.getLng());
			
						
			List<Sensor> sensors = this.getLiveInforResults(place);			
			
			if(this.getRoot().getFellowIfAny("LiveInforResultWindow")!=null){
				this.getRoot().getFellowIfAny("LiveInforResultWindow").detach();
			}
			
			UserSearchLiveInforResultWindow liveInforResult = new UserSearchLiveInforResultWindow("Live results",place);
			liveInforResult.setParent(this.getRoot());
			liveInforResult.init();
			liveInforResult.setSensors(sensors);
			liveInforResult.addContent();
			liveInforResult.doOverlapped();
		
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private List<Sensor> getLiveInforResults(Place place){
		List<Sensor> sensors = new ArrayList();
		String type="weather";
		List<Sensor> s = sensorManager.getAllSensorWithSpecifiedCitySensorType(place.getCity(), place.getCountry(),
				type);
		if(s==null){
			Sensor sensor = new Sensor();
			sensor.setId("http://lsm.deri.ie/resource/"+System.nanoTime());
			sensor.setAuthor("admin");
			sensor.setPlace(place);
			sensor.setSensorType(type);
			sensor.setTimes(new Date());
			sensor.setUser(user);
			if(type.equals("weather")){
				String yahooweather = "http://weather.yahooapis.com/forecastrss?w=" + place.getWoeid();
				String xml =  WebServiceURLRetriever.RetrieveFromURL(yahooweather);					
				if(xml==null||xml.length()<ConstantsUtil.xmlParser_length_default_value){
					String location = YahooWhereURLXMLParser.trimWrongInputChars(place.getCity()+","
							+place.getCountry());
					String googleweather = "http://www.google.com/ig/api?weather="+location;
					System.out.println(googleweather);
					xml = WebServiceURLRetriever.RetrieveFromURL(googleweather);	
					if(xml!=null&&xml.length()>ConstantsUtil.xmlParser_length_default_value){
						sensor.setSource(googleweather);
						sensor.setSourceType("google");
						GoogleWeatherWrapper googleWrapper = new GoogleWeatherWrapper();
						String triples = TriplesDataRetriever.getSensorTripleMetadata(sensor);
						sensorManager.insertTriplesToGraph(VirtuosoConstantUtil.sensormasherMetadataGraphURI, triples);
						sensors.add(sensor);
					}
				}else{						
					sensor.setSource(yahooweather);
					sensor.setSourceType("yahoo");						
					String triples = TriplesDataRetriever.getSensorTripleMetadata(sensor);
					sensorManager.insertTriplesToGraph(VirtuosoConstantUtil.sensormasherMetadataGraphURI, triples);
					YahooWeatherWrapper yW = new YahooWeatherWrapper();
					yW.postWeatherElement(sensor.getSource());
					sensors.add(sensor);
				}
			}
		}else
			sensors.addAll(s);
		return sensors;
	}
}
