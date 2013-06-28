package deri.sensor.components.windows;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Window;


import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.engine.http.QueryEngineHTTP;

import deri.sensor.database.ServiceLocator;
import deri.sensor.manager.PlaceManager;
import deri.sensor.manager.SensorManager;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class SparqlResultWindow extends Window {
	private Grid grid = new Grid();
	private Rows rows = new Rows();
	private String sparql;
	private List<List<String>> rowsList = new ArrayList<List<String>>();
	private List<String> columnLabelsList = new ArrayList<String>();
	private static Logger LOGGER = Logger.getLogger(SparqlResultWindow.class);
	private SensorManager sensorManager;
	private PlaceManager placeManager;
	private Button cmdDownload;
	private StringBuffer strBuff;
	
	public SparqlResultWindow(){
		super();		
		this.setTitle("RDF data");
		init();
	}
	

	public String getSparql() {
		return sparql;
	}

	public void setSparql(String sparql) {
		this.sparql = sparql;
	}
	
	
	public List<String> getColumnLabelsList() {
		return columnLabelsList;
	}


	public void setColumnLabelsList(List<String> columnLabelsList) {
		this.columnLabelsList = columnLabelsList;
	}


	public List<List<String>> getRowsList() {
		return rowsList;
	}


	public void setRowsList(List<List<String>> rowsList) {
		this.rowsList = rowsList;
	}

	public SparqlResultWindow(String title){
		this.setTitle(title);
		init();
	}
	
	public void init(){		
		this.setId("SparqlEndPointResult");
		this.setBorder("normal");
		this.setWidth("70%");
		this.setSizable(true);
		this.setClosable(true);
		this.setPosition("top,left");
		this.setContentStyle("overflow:auto");
		this.setClosable(true);
		
		sensorManager = ServiceLocator.getSensorManager();
		placeManager = ServiceLocator.getPlaceManager();
	}
	
	@SuppressWarnings("unchecked")
	public void addContent(String service){
		try{			
			sparql = sparql.replace("'", "\"");
			if(service.equals("default")){
				List result = sensorManager.runSparqlEndPoint(sparql);
				columnLabelsList = (List<String>) result.get(0);
				rowsList = (List<List<String>>) result.get(1);
			}else{
				QueryExecution vqe = new QueryEngineHTTP(service, sparql);
				ResultSet results = vqe.execSelect();
				columnLabelsList = new ArrayList();
				while(results.hasNext()){
					QuerySolution qs = results.nextSolution();	
					if(columnLabelsList.size()==0){
						Iterator<String> names;
						names = qs.varNames();					
						while (names.hasNext()) 
							columnLabelsList.add(names.next().toString());						
					}					
					List<String> temp= new ArrayList();
					for (String var : columnLabelsList) {						
						if (qs.get(var).isLiteral()) {
							temp.add("Lit: " + qs.getLiteral(var));
						}else 
							temp.add(qs.getResource(var).toString());						
					}
					rowsList.add(temp);
				}
			}				
			initGrid();
		}catch(Exception e){
			try {
				Messagebox.show(
						"Can not excute this query. May be the callsign of result is too large or query syntax is wrong.",
						"Error", Messagebox.OK,
						Messagebox.ERROR);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}		
	}
	
	
	public void addLocationMetaFromSparqlService(String placeId,String sameAsId){
		try{
			rowsList = new ArrayList<List<String>>();
			rowsList = placeManager.getALlPlaceMetadataWithPlaceId(placeId);
			if(sameAsId!=""){
				String service ="http://linkedgeodata.org/sparql";
				String queryString = " select * " 
					 			+"From <http://linkedgeodata.org> "
					 			+"where { "
					 			+"<"+sameAsId+"> ?p ?o."				 			
					 			+"}";
				QueryExecution vqe = new QueryEngineHTTP(service, queryString);
				ResultSet results = vqe.execSelect(); 
				while(results.hasNext()){
					QuerySolution qs = (QuerySolution)results.next();
					List<String> temp= new ArrayList();
					temp.add("<"+sameAsId+">");
					temp.add(qs.get("p").toString());
					temp.add(qs.get("o").toString());
					rowsList.add(temp);
				}			
			}
			columnLabelsList = new ArrayList();
			columnLabelsList.add("Subject");
			columnLabelsList.add("Predicate");
			columnLabelsList.add("Object");			
			initGrid();
		}catch(Exception e){
			try {
				Messagebox.show(
						"Can not excute this query. May be the callsign of result is too large or query syntax is wrong.",
						"Error", Messagebox.OK,
						Messagebox.ERROR);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	public void initGrid(){
		grid.setParent(this);
		grid.setWidth("100%");
		grid.setHeight("87%");
		grid.setMold("paging");
		grid.setVflex(true);	
		grid.setPageSize(5);
		grid.setAutopaging(true);
		Columns columns = new Columns();
		columns.setParent(grid);
		columns.setSizable(true);
		for(String clmName: columnLabelsList){
			Column clm = new Column(clmName);
			clm.setParent(columns);
		}
		
		rows.setParent(grid);
		
		Iterator iter = rowsList.iterator();
		while(iter.hasNext()){
			Row row = new Row();			
			row.setParent(rows);
			List<String> rowValue =(List<String>)iter.next();
			for(String value:rowValue){				
				row.appendChild(new Label(value));				
			}
		}		
		
		Hbox hbox = new Hbox();
		hbox.setParent(this);
		hbox.setWidth("100%");
		hbox.setAlign("center");
		hbox.setStyle("padding-left:40%");
		cmdDownload = new Button("Download data","/imgs/Button/button_download.png");		
		cmdDownload.setParent(hbox);
		cmdDownload.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				try{
					Filedownload fdl = new Filedownload();
					convertDatatoText();
					Filedownload.save(strBuff.toString(),"text/html","data.n3");
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}
	
	private void convertDatatoText(){
		strBuff = new StringBuffer();
		Iterator iter = rowsList.iterator();
		while(iter.hasNext()){
			List<String> arr =(List<String>)iter.next();
			strBuff.append(arr.get(0)+ " " + arr.get(1)+ " " + arr.get(2)+ "\n");
		}
	}
	
}
