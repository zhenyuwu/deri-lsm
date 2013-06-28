package deri.sensor.components.user.datamanagent;

import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tabpanel;

import deri.sensor.database.ServiceLocator;
import deri.sensor.javabeans.User;
import deri.sensor.manager.SensorManager;
import deri.sensor.meta.SensorTypeEnum;
import deri.sensor.wrapper.LinkValueWrapper;
import deri.sensor.wrapper.WebServiceURLRetriever;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class UserUpdateDataTab extends Tabpanel {
	private Grid grdSensor = new Grid();	
	private SensorManager sensorManager;
	private Listbox lstSensorType;	
	
	private User user;
	private Button cmdUpdate;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public void init(){	
		sensorManager = ServiceLocator.getSensorManager();
		initGrdSensor();
		addEventListener();
	}
	
	private void initGrdSensor() {
		// TODO Auto-generated method stub
		grdSensor.setParent(this);
		grdSensor.setId("grdUpdateSensor");
		grdSensor.setMold("paging");
		grdSensor.setPageSize(6);
		
		Columns columns = new Columns();
		columns.setParent(grdSensor);
		Column column = new Column();
		column.setParent(columns);
		column.setLabel("Properties"); 
		column.setWidth("20%");
		column = new Column();
		column.setParent(columns);
		column.setLabel("Criteria");
		Rows rows = new Rows();
		rows.setParent(grdSensor);
		
		//row 1 add sensor type combobox and sensor filed combobox
		Row r1 = new Row();
		r1.appendChild(new Label("Sensor type"));		
		
		lstSensorType= new Listbox();		
		lstSensorType.setParent(r1);	
		lstSensorType.setMold("select");	
		rows.appendChild(r1);
		initialize_lstSensorType();		
		
		Row r2 = new Row();
		r2.appendChild(new Label(" "));
		r2.setAlign("center");
		
		cmdUpdate = new Button("Update");
		cmdUpdate.setParent(r2);		
		rows.appendChild(r2);		
	}

	private void addEventListener() {
		// TODO Auto-generated method stub	
		cmdUpdate.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				if(lstSensorType.getSelectedItem().getLabel().equals(SensorTypeEnum.traffic.toString())){
					String url = "http://www.tfl.gov.uk/tfl/businessandpartners/syndication/feed.aspx?email=nmqhoan@gmail.com&feedId=3";
					String xml = WebServiceURLRetriever.RetrieveFromURL(url);
					xml = xml.trim().replaceFirst("^([\\W]+)<","<");
//					LondonTrafficCamParser.getTrafficElementFromUrl(xml);
					System.out.println("London Traffic cameras Update update is finished");
					
					url = "http://www.buckeyetraffic.org/services/Cameras.aspx";
					xml = WebServiceURLRetriever.RetrieveFromURL(url);
					xml = xml.trim().replaceFirst("^([\\W]+)<","<");
//					BuckeyeTrafficXMLParser.getTrafficElementFromUrl(xml);
					System.out.println("Buckeye traffic camera update is finished");
				}if(lstSensorType.getSelectedItem().getLabel().equals(SensorTypeEnum.webcam.toString())){
					LinkValueWrapper lwrapper = new LinkValueWrapper();
					lwrapper.update();
				}
			}
		});
	}	

	private void initialize_lstSensorType() {
		// TODO Auto-generated method stub
		List<String> lstType = sensorManager.getAllSensorType();
		ListModelList lm2 = new ListModelList(lstType);
		lm2.addSelection(lm2.get(0));
		lstSensorType.setModel(lm2);		
	}
}
