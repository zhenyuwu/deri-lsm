package deri.sensor.components.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;

import deri.sensor.database.ServiceLocator;
import deri.sensor.javabeans.Observation;
import deri.sensor.manager.SensorManager;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class TripleViewTabPannel extends Tabpanel {
	private Textbox txtTriple;
	private List<String> spatialResultList;
	private Button cmdDownload;
	private String type;
	private StringBuffer strBuff;
	private SensorManager sensorManager;
	
	public TripleViewTabPannel(){
		
	}
	
	public void init(){		
		txtTriple = new Textbox();
		txtTriple.setMultiline(true);
		txtTriple.setRows(10);
		txtTriple.setReadonly(true);
		txtTriple.setHeight("80%");
		txtTriple.setWidth("100%");
		txtTriple.setParent(this);
		
		sensorManager = ServiceLocator.getSensorManager();
		
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

	public List<String> getSpatialResultList() {
		return spatialResultList;
	}

	public void setSpatialResultList(List<String> spatialResultList) {
		this.spatialResultList = spatialResultList;
	}

	
	public void addContent(){
		strBuff = new StringBuffer();
		for(int i=0;i<spatialResultList.size();i++){
			List temp = sensorManager.getReadingTriplesOfObservation(spatialResultList.get(i));
			addContentFromResultSet(strBuff, temp);			
		}		
		txtTriple.setText(strBuff.toString());
	}	
	
	public void addContentFromResultSet(StringBuffer bffer, List rs){
		Iterator iter = rs.iterator();
		while(iter.hasNext()){
			ArrayList arr = (ArrayList)iter.next();
			bffer.append(arr.get(0)+ " " + arr.get(1)+ " " + arr.get(2)+ "\n");
		}
	}
}
