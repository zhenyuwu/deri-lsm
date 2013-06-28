package deri.sensor.components.user.annotation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Button;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import deri.virtuoso.mapping.RDFSchemaTemplate;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class NewPropertyWizardWindow extends Window {
	private Vbox vbox;
	private Rows rows = new Rows();	
	private Listbox lstOnto;
	private Listbox lstClass;	
	private Textbox txtOntURI;
	private Textbox txtProName;
	private String ontURI;
	private RDFSchemaTemplate rdfSchm;
	private Grid ontoGrid;
	private Button cmdAdd;
	private Button cmdCancel;
	private Combobox cmbOntPro;
	private boolean isForSensor;
	
	public NewPropertyWizardWindow(boolean isForSensor){
		super();
		this.isForSensor = isForSensor;
		init();
	}
	
	
	private void init(){
		this.setTitle("Add new sensor meatadata property");
		this.setId("NewPropertyWizardWindow");
		this.setBorder("normal");
		this.setPosition("center,center");
		this.setContentStyle("overflow:auto");
		this.setSizable(true);
		this.setClosable(true);
		this.setStyle("border-top: 1px solid #AAB");
		this.setStyle("overflow:auto");
		this.setHeight("300px");
		this.setWidth("60%"); 
		
		vbox = new Vbox();
		vbox.setParent(this);
		vbox.setHeight("100%");
		vbox.setWidth("100%");
		vbox.setStyle("overflow:auto");
		
		initOntoGrid();	

		Hbox hboxWButton = new Hbox();
		hboxWButton.setParent(vbox);
		hboxWButton.setSpacing("10px");
		hboxWButton.setStyle("padding-left:50%");
		
		cmdAdd = new Button("Add","/imgs/Button/button_ok.png");
		cmdAdd.setParent(hboxWButton);
		cmdAdd.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				try{
					setNewProperty();					
				}catch(Exception e){
					
				}
			}
		});
		
		cmdCancel = new Button("Cancel","/imgs/Button/button_cancel.png");
		cmdCancel.setParent(hboxWButton);
		cmdCancel.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				NewPropertyWizardWindow.this.detach();
			}
		});

	}
	
	public void setNewProperty() throws InterruptedException {
		// TODO Auto-generated method stub
		SensorMetaPropertiesWizardWindow s = (SensorMetaPropertiesWizardWindow )this.getParent();
		if(!cmbOntPro.getText().equals("")){
			//System.out.println(cmbOntPro.getText());
			if(isForSensor)
				s.addNewSensorMetaProperty(txtProName.getValue(),cmbOntPro.getText());
			else
				s.addNewPlaceMetaProperty(txtProName.getValue(),cmbOntPro.getText());
			this.detach();
		}else  
			Messagebox.show("Sensor source is invalid. Please input again.", "Error", Messagebox.OK, Messagebox.ERROR);
	}

	public void initOntoGrid(){
		ontoGrid = new Grid();
		
		Auxhead aux = new Auxhead();
		aux.setParent(ontoGrid);
		Auxheader auxhder = new Auxheader("Set your Ontology configuration");
		auxhder.setColspan(2);
		auxhder.setStyle("color:red");
		auxhder.setParent(aux);
		
		ontoGrid.setParent(vbox);
		ontoGrid.setId("ontoGrid");
		ontoGrid.setMold("paging");
		ontoGrid.setPageSize(10);
		ontoGrid.setParent(vbox);
		ontoGrid.setWidth("100%");
		
		Columns columns = new Columns();
		columns.setParent(ontoGrid);
		columns.setSizable(true);
		
		Column column = new Column();
		column.setParent(columns);		
		column.setWidth("20%");
		
		column = new Column();
		column.setWidth("80%");
		column.setParent(columns);
		
		
		rows.setParent(ontoGrid);
		
		Row r1 = new Row();
		r1.appendChild(new Label("Ontology"));
		Hbox hbss = new Hbox();
		hbss.setParent(r1);
		hbss.setSpacing("20px");
		lstOnto= new Listbox();		
		lstOnto.setParent(hbss);	
		lstOnto.setMold("select");
		//lstOnto.appendChild(new Listitem("Sensormasher ontology"));
		lstOnto.appendChild(new Listitem(""));
		lstOnto.appendChild(new Listitem("LSM ontology"));		
		lstOnto.addEventListener(Events.ON_SELECT, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				loadOntology(lstOnto.getSelectedItem().getLabel().toString());
			}
		});
		
//		Button btnBrowser = new Button("Import...");		
//		btnBrowser.setParent(hbss);
		r1.setParent(rows);
				
		Row r2 = new Row();
		r2.appendChild(new Label("Ontology URI"));
		
		Hbox hb1 = new Hbox();
		hb1.setParent(r2);
		hb1.setWidth("100%");
		txtOntURI = new Textbox();
		txtOntURI.setWidth("90%"); 
		txtOntURI.setText("Input your ontology URI here");
		txtOntURI.addEventListener(Events.ON_FOCUS, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				txtOntURI.setText("");
			}
		});
		//txtOntURI.setParent(r2);
		txtOntURI.setParent(hb1);
		
		Button cmdLoad = new Button("Load");
		cmdLoad.setParent(hb1);
		cmdLoad.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				if(txtOntURI.getValue()!=""){
					loadOntologyFromURL(txtOntURI.getValue());
					loadAllOntologyClass();
				}
			}
		});
		r2.setParent(rows);
		
		Row r3 = new Row();
		r3.appendChild(new Label("Sensor class"));
		lstClass= new Listbox();
		lstClass.setMold("select");				
		lstClass.setParent(r3);		
		r3.setParent(rows);
		lstClass.addEventListener(Events.ON_SELECT, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				loadOntClassProperties(lstClass.getSelectedItem().getLabel().toString());
			}
		});
		
		Row r4 = new Row();
		r4.appendChild(new Label("Property name"));
		txtProName = new Textbox();
		txtProName.setWidth("30%");
		txtProName.setParent(r4);
		r4.setParent(rows);
		
		Row r5 = new Row();
		r5.appendChild(new Label("Sensor Properties"));
		cmbOntPro = new Combobox();
		cmbOntPro.setParent(r5);
		r5.setParent(rows);
		
	}
	
	public void loadOntology(String ontName){
		if(!ontName.equals("LSM ontology")) return;
		String dirPath =  "/usr/local/tomcat7/webapps/SensorMiddlewareData/Document/Ontology/";			 
		ontURI = "http://purl.oclc.org/NET/ssnx/ssn#";
		
		rdfSchm = new RDFSchemaTemplate(ontURI);
		rdfSchm.setBaseOnt(rdfSchm.loadMutilOntologyFromFile(dirPath));
		loadLstOntologyClass();
	}
	
	private void loadOntologyFromURL(String ontURL) {
		// TODO Auto-generated method stub
		ontURI = "http://purl.oclc.org/NET/ssnx/ssn#";		
		rdfSchm = new RDFSchemaTemplate(ontURI);
		
		rdfSchm.setBaseOnt(rdfSchm.loadOntologyFromURL(ontURL));
		loadLstOntologyClass();
	}
	
	public void loadLstOntologyClass(){
		OntModel ont = rdfSchm.getBaseOnt();
		OntClass ontClass = ont.getOntClass("http://www.loa-cnr.it/ontologies/DUL.owl#Quality");
		loadSubClass(ont, ontClass.getURI());
	}
	
	public void loadSubClass(OntModel ont,String className){
		OntClass ontClass = ont.getOntClass(className);
		ExtendedIterator exiter = ontClass.listSubClasses(false);
		if((!exiter.hasNext())&&!isContain(ontClass.getURI())){
			//lstClass.appendChild(new Listitem(ontClass.getLocalName()));
			lstClass.appendChild(new Listitem(ontClass.getURI()));
		}
		else
			while(exiter.hasNext()){
				OntClass subC = (OntClass)exiter.next();
				loadSubClass(ont, subC.getURI());
			}
	}
	
	public void loadAllOntologyClass(){
		OntModel ont = rdfSchm.getBaseOnt();
		ListModelList ontClassModel = new ListModelList(removeDuplicates(ont.listClasses().toList()));
		ontClassModel.addSelection(ontClassModel.get(0));
		lstClass.setModel(ontClassModel);
	}
	private boolean isContain(String st){
		Iterator iter = lstClass.getItems().iterator();
		//System.out.println("list box:" + lstClass.getItems().toString());
		while(iter.hasNext()){
			String name = ((Listitem)iter.next()).getLabel();			
			if(name.equalsIgnoreCase(st))
				return true;			
		}
		return false;
	}
	
	private void loadOntClassProperties(String className){				
		rdfSchm.setClassURI(className);
		rdfSchm.setPropertiesLinkedList();
		ListModelList model = new ListModelList(rdfSchm.getPropertiesLinkedList().keySet());
		model.addSelection(model.get(0));
		cmbOntPro.setModel(model);
	}
	
	public List removeDuplicates(List items) {
		Set set = new LinkedHashSet();
		set.addAll(items);
		return new ArrayList(set);
	}
}
