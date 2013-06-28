package deri.sensor.components.windows;


import java.util.List;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class SearchResultsWindow extends Window {
	private static final long serialVersionUID = -3900618895663270180L;
	private String type;
	private Grid grid = new Grid();
	private Rows rows = new Rows();
	private List<String> resultList;

	
	public SearchResultsWindow() {
		super();
	}
	
	public SearchResultsWindow(String title) {
		this();
		this.setTitle(title);
	}
	
	public SearchResultsWindow(String type, String title) {
		this(title);
		this.type = type;
	}

	public SearchResultsWindow(String sensorType,String city,double lat,double lng,String date){
		this();
		this.type = sensorType;
		this.setTitle(sensorType +" data for"+ city + " city before " + date);
	}
	
	
	public List<String> getResultList() {
		return resultList;
	}

	public void setResultList(List<String> resultList) {
		this.resultList = resultList;
	}
	
	
	public void init(){
		this.setId("SearchResultsWindow");
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
		column.setLabel("Data");
		column.setAlign("center");
		column.setWidth("40%");
		rows.setParent(grid);
	}
	
	
	public void addContent(){	
		for(String url:resultList)
			addOneRowForOneSource(url);
	}
	
	public void addSpatialContent(){	
		for(String url:resultList)
			addOneRowForOneSource(url);
	}
	
	private void addOneRowForOneSource(final String source){
		Row row = new Row();
		row.setParent(rows);
		Toolbarbutton bar = new Toolbarbutton();
		bar.setParent(row);
		bar.setLabel(source);
		bar.addEventListener(Events.ON_CLICK, new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				//showSensorWindowFromDataSource(source);
			}
		});
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
