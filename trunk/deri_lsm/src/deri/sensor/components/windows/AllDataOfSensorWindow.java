package deri.sensor.components.windows;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import deri.sensor.database.ServiceLocator;
import deri.sensor.javabeans.Observation;
import deri.sensor.manager.SensorManager;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class AllDataOfSensorWindow extends Window {
	private Textbox txtTriple;
	private String source ;
	private Button cmdDownload;
	private String type;	
	private StringBuffer strBuff;
	private SensorManager sensormanager = ServiceLocator.getSensorManager();

	public AllDataOfSensorWindow(){
		super();		
		init();
	}
	
	public String getSource() {
		return source;
	}


	public void setSource(String source) {
		this.source = source;
	}


	public StringBuffer getStrBuff() {
		return strBuff;
	}


	public void setStrBuff(StringBuffer strBuff) {
		this.strBuff = strBuff;
	}


	public void init(){
		this.setId("AllDataSensor");
		this.setBorder("none");
		this.setSizable(true);
		this.setWidth("30%");
		this.setHeight("40%");
		this.setPosition("top,center");
		this.setClosable(true);
		this.setTitle("All data of sensor");
		
		txtTriple = new Textbox();
		txtTriple.setMultiline(true);
		txtTriple.setRows(10);
		txtTriple.setReadonly(true);
		txtTriple.setHeight("80%");
		txtTriple.setWidth("100%");
		txtTriple.setParent(this);
		
		Vbox vb = new Vbox();
		vb.setParent(this);
		vb.setAlign("center");
		vb.setHeight("100%");
		vb.setWidth("100%");

		cmdDownload = new Button("Download data","/imgs/Button/button_download.png");		
		cmdDownload.setParent(vb);
		cmdDownload.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				try{
					Filedownload fdl = new Filedownload();
					Filedownload.save(strBuff.toString(),"text/html",type+".n3");
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}	
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public void addContent(String sensorId){
		try{
			strBuff = new StringBuffer();
			addSensorMeatadata(strBuff,sensorId);
			Observation newestObs = sensormanager.getNewestObservationForOneSensor(sensorId);
			List rs = sensormanager.getReadingTriplesOfObservation(newestObs.getId());
			addContentFromResultSet(strBuff, rs);			
			txtTriple.setText(strBuff.toString());
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void addSensorMeatadata(StringBuffer bffer,String id){
		//List ls = sensormanager.getSensorMetadataWithSensorId(id);
	}
	
	public void addContentFromResultSet(StringBuffer bffer, List rs){
		Iterator iter = rs.iterator();
		while(iter.hasNext()){
			ArrayList arr = (ArrayList)iter.next();
			bffer.append(arr.get(0)+ " " + arr.get(1)+ " " + arr.get(2)+ "\n");
		}
	}
}
