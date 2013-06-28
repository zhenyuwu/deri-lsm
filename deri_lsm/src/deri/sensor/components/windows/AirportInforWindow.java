package deri.sensor.components.windows;

import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Window;

import deri.sensor.javabeans.Airport;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class AirportInforWindow extends Window {
	
	private Grid grid = new Grid();
	private Rows rows;
	
	public AirportInforWindow(){
		super();
		this.setTitle("Airport information");
		init();
	}
	
	public AirportInforWindow(String title){
		this();
		this.setTitle(title);
	}
	
	public void init(){
		this.setId("airportWindow");
		this.setBorder("normal");
		this.setSizable(true);
		this.setWidth("50%");
		this.setPosition("center,center");
		this.setClosable(true);
	}
	
	public void addContent(Airport airport){
		grid.setParent(this);
		grid.setId("grid");
		grid.setMold("paging");
		grid.setPageSize(1);
		grid.setVflex(true);
		
		Columns columns = new Columns();
		columns.setParent(grid);
		columns.setSizable(true);
		
		Column column = new Column();
		column.setParent(columns);
		column.setLabel("Code");
		column.setWidth("20%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("City");
		column.setWidth("15%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Country");
		column.setWidth("30%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Latitude");
		column.setWidth("15%");
		
		column = new Column();
		column.setParent(columns);
		column.setLabel("Longitude");
		column.setWidth("10%");		
		
		rows = new Rows();
		rows.setParent(grid);
		
		Row row = new Row();
		row.appendChild(new Label(airport.getAirportCode()));
		row.appendChild(new Label(airport.getCity()));
		row.appendChild(new Label(airport.getCountry()));
		row.appendChild(new Label(Double.toString(airport.getLatitude())));
		row.appendChild(new Label(Double.toString(airport.getLongitude())));
		row.setParent(rows);
	}
}
