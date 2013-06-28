package deri.sensor.components.search;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;

import deri.sensor.components.windows.SparqlResultWindow;
import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.VirtuosoConstantUtil;
import deri.sensor.manager.SensorManager;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class SparqlQueryTabPannel extends Tabpanel {
	private Textbox txtSparqlInput;
	private SensorManager sensormanager;
	private Button cmdRunSparql;
	private Combobox cmbSparqlService;
	public SparqlQueryTabPannel(){
		
	}
		
	public SparqlQueryTabPannel(Component parent){
		super();
		this.setParent(parent);
		init();
		sensormanager = ServiceLocator.getSensorManager();
	}
	
	public void init(){
		this.setStyle("overflow:auto");
		
		Hbox hb = new Hbox();
		hb.setParent(this);
		hb.setAlign("center,left");
		hb.setSpacing("10px");
		
		cmbSparqlService = new Combobox();
		cmbSparqlService.setParent(hb);
		initialized_SparqlService();
		cmbSparqlService.addEventListener(Events.ON_SELECT, new EventListener() {
			
			@Override
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				String graph= cmbSparqlService.getSelectedItem().getLabel();
				graph = graph.replace("/sparql","");
				if(graph.equals("default"))
					txtSparqlInput.setText("select ?s ?p ?o from <" +VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> "+
						"where {?s ?p ?o.} limit 20");
				else
					txtSparqlInput.setText("select ?s ?p ?o from <" +graph +"> "+
							"where {?s ?p ?o.} limit 20");
			}
		});
		
		Button cmdClear = new Button("Clear");
		cmdClear.setParent(hb);
		cmdClear.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				txtSparqlInput.setText("");
				txtSparqlInput.setFocus(true);
			}
		});
		
		cmdRunSparql = new Button("Run Sparql");
		cmdRunSparql.setParent(hb);
		cmdRunSparql.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				callSparqlEngine();				
			}
		});
		
		
		txtSparqlInput = new Textbox();
		txtSparqlInput.setHeight("70%");
		txtSparqlInput.setWidth("100%");
		txtSparqlInput.setRows(6);
		txtSparqlInput.setMultiline(true);
		txtSparqlInput.setParent(this);
		txtSparqlInput.setText("select ?s ?p ?o from <" +VirtuosoConstantUtil.sensormasherMetadataGraphURI +"> "+
						"where {?s ?p ?o.} limit 20");
		
	}
	
	private void initialized_SparqlService() {
		// TODO Auto-generated method stub
		cmbSparqlService.appendItem("default");
		cmbSparqlService.appendItem("http://linkedgeodata.org/sparql");
		cmbSparqlService.appendItem("http://dbpedia.org/sparql");
		cmbSparqlService.setSelectedIndex(0);
	}

	private void callSparqlEngine() {
		if(this.getRoot().getFellowIfAny("SparqlEndPointResult") != null){
			this.getRoot().getFellowIfAny("SparqlEndPointResult").detach();
		}		
		try{
			SparqlResultWindow qrs = new SparqlResultWindow("Sparql endpoint result");
			qrs.setParent(this.getRoot());
			qrs.setSparql(txtSparqlInput.getText().trim());
			qrs.addContent(cmbSparqlService.getSelectedItem().getLabel());			
			qrs.doOverlapped();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
