package deri.sensor.components.search;

import java.io.IOException;
import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Toolbarbutton;

import deri.sensor.components.windows.SensorWindow;
import deri.sensor.database.ServiceLocator;
import deri.sensor.javabeans.AbstractProperty;
import deri.sensor.javabeans.Observation;
import deri.sensor.javabeans.Picture;
import deri.sensor.javabeans.Sensor;
import deri.sensor.manager.SensorManager;
import deri.sensor.meta.FilterSensorLabels;
import deri.sensor.utils.DateUtil;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class RowViewTabPannel extends Tabpanel{
	private Grid grid = new Grid();
	private Rows rows = new Rows();
	private List<String> spatialResultList;
	private String type;
	private SensorManager sensorManager = ServiceLocator.getSensorManager();

	public void init(){	
		grid.setParent(this);
		grid.setId("grid");		
		grid.setMold("paging");
		grid.setVflex(true);		
		//grid.setAutopaging(true);
		grid.setPageSize(5);
		
		Columns columns = new Columns();
		columns.setSizable(true);		
		columns.setParent(grid);
		
		Column column = new Column();
		column.setParent(columns);
		column.setLabel("Sensor");
		column.setAlign("center");
		column.setWidth("40%");
		
		List<String> st = sensorManager.getAllSensorPropertiesForSpecifiedSensorType(type).get(1);
		for(String tbl:st){
			tbl = tbl.substring(tbl.lastIndexOf("#")+1);
			column = new Column();
			column.setParent(columns);
			column.setLabel(tbl);
			column.setAlign("center");
		}
		column = new Column();
		column.setParent(columns);
		column.setAlign("center");
		column.setLabel("Times");		
		rows.setParent(grid);
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
		try{			
			for(int i = 0;i<spatialResultList.size();i++)
				addOneRowForOneObservation(spatialResultList.get(i));
		}catch(Exception e){
			e.printStackTrace();
		}			
	}

	private void addOneRowForOneObservation(String obs) {
		// TODO Auto-generated method stub
		Row row = new Row();
		row.setParent(rows);
		AbstractProperty absPro = null;
		Sensor sensor = sensorManager.getSpecifiedSensorWithObservationId(obs);
		row.appendChild(new Label(sensor.getPlace().getSimpleContent()));
		List<String> st = sensorManager.getAllSensorPropertiesForSpecifiedSensorType(type).get(1);		
		for(String tbl: st){
			tbl = tbl.substring(tbl.lastIndexOf("#")+1);
			absPro = sensorManager.getSpecifiedReadingForOneObservation(obs,tbl);
			if(absPro!=null&&tbl.contains("WebcamSnapShot")){
				final String imagePath = absPro.getValue();
				Toolbarbutton bar = new Toolbarbutton();
				bar.setParent(row);
				bar.setImage("/imgs/Button/32x32/button_image.png");
				bar.setHflex(imagePath);
				//bar.setStyle("color:blue;text-decoration:underline");
				bar.addEventListener(Events.ON_CLICK, new EventListener(){
					@Override
					public void onEvent(Event event) throws Exception {
						showImage(imagePath);				
					}
				});
			}else
				row.appendChild(new Label(absPro==null?"not supported":absPro.getValue()));
		}
		
		row.appendChild(new Label(DateUtil.date2StandardString(absPro.getTimes())));
	}

	public void showImage(String url) throws IOException{
		if(this.getRoot().getFellowIfAny("sensorWindow") != null){
			this.getRoot().getFellowIfAny("sensorWindow").detach();
		}		
		SensorWindow sensorWindow = new SensorWindow(FilterSensorLabels.sensorType2FirstLabel.get(type)
								+ " sensor data", type);
		sensorWindow.setParent(this.getRoot());
		sensorWindow.addImageContentFromFile(url);
		sensorWindow.doOverlapped();
	}
}
