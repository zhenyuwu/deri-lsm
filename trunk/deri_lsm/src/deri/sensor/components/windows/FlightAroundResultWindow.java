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
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class FlightAroundResultWindow extends Window {
	private Grid grid = new Grid();
	private Rows rows = new Rows();
	private ArrayList<List> resultList;

	
	public FlightAroundResultWindow () {
		super();
	}
	
	
	public ArrayList<List> getResultList() {
		return resultList;
	}

	public void setResultList(ArrayList<List> resultList) {
		this.resultList = resultList;
	}

	public void init(){
		this.setId("FlightAroundResultsWindow");
		this.setTitle("Flights information");
		this.setBorder("normal");
		this.setWidth("50%");
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
				
		Columns columns = new Columns();
		columns.setSizable(true);
		columns.setParent(grid);		
		
		Column column = new Column();
		column.setParent(columns);
		column.setLabel("Registration");
		column.setAlign("center");
		column.setWidth("25%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Model");
		column.setAlign("center");
		column.setWidth("25%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Callsign");
		column.setAlign("center");
		column.setWidth("25%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Route");
		column.setAlign("center");
		column.setWidth("40%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Latitude");
		column.setAlign("center");
		column.setWidth("40%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Longitude");
		column.setAlign("center");
		column.setWidth("40%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Distance (km)");
		column.setAlign("center");
		column.setWidth("40%");
		rows.setParent(grid);
	}
	
	
	public void addContent(){
		if(resultList.size()>0)
		for(List lstFlightInfo:resultList){
			Row row = new Row();
			row.setParent(rows);
			for(int i=0;i<lstFlightInfo.size();i++)
				row.appendChild(new Label(lstFlightInfo.get(i).toString()));
		}
			
	}
}
