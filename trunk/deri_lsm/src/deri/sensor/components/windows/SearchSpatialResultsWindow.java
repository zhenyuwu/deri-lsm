package deri.sensor.components.windows;


import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.VirtuosoConstantUtil;
import deri.sensor.javabeans.Observation;
import deri.sensor.javabeans.Sensor;
import deri.sensor.manager.SensorManager;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class SearchSpatialResultsWindow extends Window {
	private static final long serialVersionUID = -3900618895663270180L;
	private String type;
	private Grid grid = new Grid();
	private Rows rows = new Rows();
	private ArrayList<List> spatialResultList;
	private SensorManager sensorManager = ServiceLocator.getSensorManager();
	private boolean isQuickSearch = false;
	public SearchSpatialResultsWindow() {
		super();
		this.setId("spatialResult");
	}
	
	public SearchSpatialResultsWindow(String title) {
		this();
		this.setTitle(title);
		
	}
			
	public ArrayList<List> getSpatialResultList() {
		return spatialResultList;
	}

	public void setSpatialResultList(ArrayList<List> spatialResultList) {
		this.spatialResultList = spatialResultList;
	}
	
	public boolean isQuickSearch() {
		return isQuickSearch;
	}

	public void setQuickSearch(boolean isQuickSearch) {
		this.isQuickSearch = isQuickSearch;
	}

	public void init(){
		this.setId("SearchSpatialResultsWindow");
		this.setBorder("normal");
		this.setWidth("50%");
		this.setHeight("50%");
		this.setPosition("top,left");
		this.setContentStyle("overflow:auto");		
		this.setClosable(true);
		this.setSizable(true);
		initGrid();
	}
	
	public void initGrid(){
		grid.setParent(this);
		grid.setId("grid");
		grid.setMold("paging");		
		grid.setVflex(true);		
		grid.setAutopaging(true);
		//grid.setPageSize(10);
		//grid.setStyle("color:green");
		
		Columns columns = new Columns();
		columns.setSizable(true);
		columns.setParent(grid);
		
		Column column = new Column();
		column.setParent(columns);
		column.setLabel("Sensor");
		column.setWidth("40%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("City");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Distance (km)");
		if(!isQuickSearch){
			column = new Column();
			column.setParent(columns);
			column.setLabel("Data");
		}
		
		rows.setParent(grid);
	}
	
	public void addContent(){	
		try{
			for(int i=0;i<spatialResultList.get(0).size();i++){
				String source = (String) spatialResultList.get(0).get(i);
				String cty = (String) spatialResultList.get(1).get(i);
				double d = (Double) spatialResultList.get(2).get(i);
				addOneRowForOneSource(source, cty, d);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
			
	}
		
	private void addOneRowForOneSource(final String source,String city,double dis){
		final Row row = new Row();
		row.setParent(rows);
				
		Toolbarbutton bar = new Toolbarbutton();
		bar.setParent(row);
		bar.setLabel(source);
		bar.setStyle("color:blue;text-decoration:underline");
		bar.setTooltiptext("Click to load sensor metadata");
		bar.addEventListener(Events.ON_CLICK, new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				showSensorMetadataWindow(source);
			}
		});
		
		bar = new Toolbarbutton();
		bar.setParent(row);
		bar.setStyle("color:blue;text-decoration:underline");
		bar.setTooltiptext("Click here to load place metadata");
		bar.setLabel(city);
		bar.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				showLocationMetadataFromSparqlService(source);
			}			
		});
		
		row.appendChild(new Label(Double.toString(dis)));
		if(isQuickSearch) return;
		bar = new Toolbarbutton();
		bar.setParent(row);
		bar.setStyle("color:blue");
		bar.setTooltiptext("Click here to load sensor reading data");
		bar.setLabel("Data");		
		bar.addEventListener(Events.ON_CLICK, new EventListener() {			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				int i = grid.getRows().getChildren().indexOf(row);
				createDataDetailWindows(i);				
			}
		});	
	}	

	private void showLocationMetadataFromSparqlService(String id) {
		// TODO Auto-generated method stub		
		Sensor sensor = sensorManager.getSpecifiedSensorWithSensorId(id);
		String placeId = sensor.getPlace().getId();
		String sameAsId = sensorManager.getNearestLocationWithSensorIdURL(placeId);
		
		if(this.getRoot().getFellowIfAny("SparqlEndPointResult") != null){
			this.getRoot().getFellowIfAny("SparqlEndPointResult").detach();
		}
		
		SparqlResultWindow spWindow = new SparqlResultWindow();
		spWindow.setParent(this.getParent());
		spWindow.addLocationMetaFromSparqlService(placeId,sameAsId);
		spWindow.doOverlapped();
	}
	
	protected void showSensorMetadataWindow(String source) {
		// TODO Auto-generated method stub
		if(this.getRoot().getFellowIfAny("SparqlEndPointResult") != null){
			this.getRoot().getFellowIfAny("SparqlEndPointResult").detach();
		}
		SparqlResultWindow sWindow = new SparqlResultWindow();
		sWindow.setParent(this.getParent());
		String sql = "select * "+
        "from <" +VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> "+
		  "where{ "+
		     "<"+source+"> ?p ?o.}";		     
		sWindow.setSparql(sql);
		sWindow.addContent("default");
		sWindow.doOverlapped();
	}

	@SuppressWarnings("unchecked")
	public void createDataDetailWindows(int idx) throws InterruptedException{
		try{
			if(this.getRoot().getFellowIfAny("SensorDataDetail") != null){
				this.getRoot().getFellowIfAny("SensorDataDetail").detach();
			}
			SensorDataDetail ssDetail = new SensorDataDetail();
			ssDetail.setParent(this.getParent());
			ssDetail.setSensorType(this.type);
			ssDetail.setListDataResult((List<String>) spatialResultList.get(3).get(idx));
			ssDetail.init();		
			ssDetail.doOverlapped();
		}catch(Exception e){
			Messagebox.show("There are no data for this time criteria.",
					"Information", Messagebox.OK,
					Messagebox.INFORMATION);
		}
	}
	
	public void clearContent(){
		while(this.getFirstChild() != null){
			this.getFirstChild().detach();
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
