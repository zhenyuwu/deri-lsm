package deri.sensor.components.user.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Auxheader;
import org.zkoss.zul.Button;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;


import deri.sensor.database.ServiceLocator;
import deri.sensor.manager.SensorManager;
import deri.virtuoso.mapping.RDFSchemaTemplate;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class SensorMappingXML2TableWizardWindow extends Window {
	private Grid grdSensorMap;	
	private Button cmdNext;
	private Button cmdBack;
	private final int order = 3;

	private Vbox vbox;	
	private HashMap<String, List> hsMapSign_SensorAnnotate;
	private ListModelList sensorModelList;
	private EditRowWithListboxRenderer sensorRowRenderer;
	private List propertiesList;
	private Listbox lstClass;

	private List<String> lstSensorSysPro;
	private List lstSign;
	private String ontURI;
	private RDFSchemaTemplate rdfSchm;	
	private SensorManager sensorManager;

		
	public SensorMappingXML2TableWizardWindow(){
		super();
		init();
	}
		
	public HashMap<String, List> getHsMapSign_SensorAnnotate() {
		return hsMapSign_SensorAnnotate;
	}

	public void setHsMapSign_SensorAnnotate(
			HashMap<String, List> hsMapSignSensorAnnotate) {
		hsMapSign_SensorAnnotate = hsMapSignSensorAnnotate;
	}

	private void init(){
		this.setTitle("Step 4 - Map XML tag to sensor properties");
		this.setId("SensorMappingPropertiesWizard");
		this.setBorder("normal");
		this.setStyle("overflow:auto");
		this.setHeight("350px");
		this.setWidth("600px");
		this.setSizable(true);
		this.setClosable(true);
		sensorManager = ServiceLocator.getSensorManager();
		lstSensorSysPro = sensorManager.getAllSensorSystemProperties();
		
		vbox = new Vbox();
		vbox.setParent(this);
		vbox.setHeight("100%");
		vbox.setWidth("100%");
		
		//initOntoGrid();			
		
		Button cmdReload = new Button("Reload");
		cmdReload.setParent(vbox);
		cmdReload.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				reload();
			}			
		});
		
		initgrdSensorMap();
		
		Hbox hboxWButton = new Hbox();
		hboxWButton.setParent(vbox);
		hboxWButton.setSpacing("10px");
		hboxWButton.setStyle("padding-left:60%");
		hboxWButton.setHeight("20%");
		cmdBack = new Button("Back");
		cmdBack.setParent(hboxWButton);
		cmdBack.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				backToPreviousStep();
			}
		});
		
		cmdNext = new Button("Next");
		cmdNext.setParent(hboxWButton);		
		cmdNext.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				moveToNextStep();
			}
		});
	}
	
	private void initialized_hsMapSensorAnnotate() {
		// TODO Auto-generated method stub
		lstSign = new ArrayList<String>();
		hsMapSign_SensorAnnotate = new LinkedHashMap<String, List>();
		
		SensorDataPropertiesWizardWindow sp = (SensorDataPropertiesWizardWindow) this.getParent().getFellowIfAny("ChooseSensorPropertiesWizard");
		List<String> temp = sp.getChosenDataList();
		for(String st: temp){		
			lstSign.add(st);
			List subArr = new ArrayList(2);
			subArr.add("");
			subArr.add("");
			//subArr.add("");
			hsMapSign_SensorAnnotate.put(st, subArr);
		}
//		System.out.println("lstSign:" + lstSign);
//		System.out.println("hash map Sign:" + hsMapSign_SensorAnnotate);
	}
	
	public int getOrder(){
		return order;
	}
	
	
	public List getPropertiesList() {
		return propertiesList;
	}

	public void setPropertiesList(List propertiesList) {
		this.propertiesList = propertiesList;
	}

	private void initgrdSensorMap(){		
		grdSensorMap = new Grid();
		grdSensorMap.setId("grdSensorMap");
		grdSensorMap.setMold("paging");
		grdSensorMap.setPageSize(4);
		grdSensorMap.setParent(vbox);		
		grdSensorMap.setWidth("100%");
		
		Auxhead aux = new Auxhead();
		aux.setParent(grdSensorMap);
		Auxheader auxhder = new Auxheader("Please chose suitable properties");
		auxhder.setColspan(4);
		auxhder.setStyle("color:red");
		auxhder.setParent(aux);
		
		Columns clms = new Columns();
		clms.setSizable(true);
		clms.setParent(grdSensorMap);
		
		Column column = new Column();
		column.setParent(clms);
		column.setLabel("Value");
		column.setWidth("25%");
		
		column = new Column();
		column.setParent(clms);
		column.setLabel("Sensor reading");
		column.setWidth("45%");	
		
		column = new Column();
		column.setParent(clms);
		column.setLabel("Unit");
		column.setWidth("13%");
		
		column = new Column();
		column.setParent(clms);
		column.setLabel("Edit");
		column.setWidth("10%");		

	}
			
	public void addContent(){
		initialized_hsMapSensorAnnotate();
		
		//SensorDataPropertiesWizardWindow sp = (SensorDataPropertiesWizardWindow) this.getParent().getFellowIfAny("ChooseSensorPropertiesWizard");
			
		//sensorModelList = new ListModelList(sp.getChosenDataList());
		//sensorModelList = new ListModelList(lstSign);		
		sensorModelList = new ListModelList(hsMapSign_SensorAnnotate.keySet());
		sensorRowRenderer = new EditRowWithListboxRenderer();
		sensorRowRenderer.setListSysProperties(lstSensorSysPro);
		sensorRowRenderer.setHsMapParentAnnotate(hsMapSign_SensorAnnotate);
		grdSensorMap.setRowRenderer(sensorRowRenderer);
		grdSensorMap.setModel(sensorModelList);		
	}
	
	private void backToPreviousStep(){
		UserAnnotateWizard u = (UserAnnotateWizard) this.getParent();
		u.moveToStep(this.getOrder()-1);
	}
	private void moveToNextStep(){		
		UserAnnotateWizard u = (UserAnnotateWizard) this.getParent();
		u.moveToStep(this.getOrder()+1);
	}
	

//	public void loadOntology(String ontName){
//		String dirPath =  "/root/Sensor Ontology/Sensormasher ont/";
//		//String classURI = txtOntURI.getValue() + lstClass.getSelectedItem().getLabel().toString();	 
//		ontURI = "http://purl.oclc.org/NET/ssnx/ssn#";
//		
//		rdfSchm = new RDFSchemaTemplate(ontURI);
//		rdfSchm.setBaseOnt(rdfSchm.loadMutilOntologyFromFile(dirPath));
//		loadLstOntologyClass();
//	}
//	
//	public void loadLstOntologyClass(){
//		OntModel ont = rdfSchm.getBaseOnt();
//		OntClass ontClass = ont.getOntClass("http://www.loa-cnr.it/ontologies/DUL.owl#PhysicalAttribute");
//		loadSubClass(ont, ontClass.getURI());
//	}
//	
//	public void loadSubClass(OntModel ont,String className){
//		OntClass ontClass = ont.getOntClass(className);
//		ExtendedIterator exiter = ontClass.listSubClasses(false);
//		if((!exiter.hasNext())&&!isContain(ontClass.getLocalName())){
//			lstClass.appendChild(new Listitem(ontClass.getLocalName()));
//		}
//		else
//			while(exiter.hasNext()){
//				OntClass subC = (OntClass)exiter.next();
//				loadSubClass(ont, subC.getURI());
//			}
//	}
	
//	private boolean isContain(String st){
//		Iterator iter = lstClass.getItems().iterator();
//		System.out.println("list box:" + lstClass.getItems().toString());
//		while(iter.hasNext()){
//			String name = ((Listitem)iter.next()).getLabel();
//			System.out.println(name);
//			if(name.equalsIgnoreCase(st))
//				return true;			
//		}
//		return false;
//	}
//	
//	
//	private void loadOntClassProperties(String className){				
//		rdfSchm.setClassURI(ontURI+className);
//		rdfSchm.setClassLocalName();
//		rdfSchm.setPropertiesLinkedList();
//	}
	
	private void reload() {
		// TODO Auto-generated method stub
		initialized_hsMapSensorAnnotate();		
		sensorModelList = new ListModelList(hsMapSign_SensorAnnotate.keySet());	
		grdSensorMap.setModel(sensorModelList);
	}
}
