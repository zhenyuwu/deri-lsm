package deri.sensor.components.user.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.zkoss.zul.Auxhead;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import deri.sensor.hashmaps.util.MappingWizardUtil;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class SensorMetaPropertiesWizardWindow extends Window {
	private Grid grdSensorMeta;
	private Grid grdPlaceMeta;
	private Button cmdNext;
	private Button cmdBack;
	private final int order = 4;
	private String type;
	private Vbox vbox;

	private ListModelList sensorModelList;
	private ListModelList placeModelList;
	private List sensorXMLTagList;
	private EditRowWithRdButtonRenderer sensorRowRenderer;
	private EditRowWithRdButtonRenderer placeRowRenderer;
	
	private HashMap xml2SensorPro;
	private HashMap xml2PlacePro;
	public SensorMetaPropertiesWizardWindow(){
		super();
		init();
	}
	
	
	public List getSensorXMLTagList() {
		return sensorXMLTagList;
	}


	public void setSensorXMLTagList(List sensorXMLTagList) {
		this.sensorXMLTagList = sensorXMLTagList;
	}


	private void init(){
		this.setTitle("Step 4 - Set your sensor meatadata properties");
		this.setId("SensorMetaPropertiesWizard");
		this.setBorder("normal");
		this.setPosition("center,center");
		this.setContentStyle("overflow:auto");
		this.setSizable(true);
		this.setClosable(true);
		this.setStyle("border-top: 1px solid #AAB");
		this.setStyle("overflow:auto");
		this.setHeight("550px");
		this.setWidth("800px");
 
		xml2SensorPro = new LinkedHashMap<String, List>();
		xml2PlacePro = new LinkedHashMap<String, List>();
		cloneHashMap(MappingWizardUtil.SensorMetaAnnotate, xml2SensorPro);
		cloneHashMap(MappingWizardUtil.PlaceMetaAnnotate, xml2PlacePro);
		
		vbox = new Vbox();
		vbox.setParent(this);
		vbox.setHeight("100%");
		vbox.setWidth("100%");
		vbox.setStyle("overflow:auto");
		
		Hbox hb1 = new Hbox();
		hb1.setParent(vbox);
		hb1.setSpacing("5px");
		
		Button cmdReload = new Button("Reload");
		cmdReload.setParent(hb1);
		cmdReload.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				reloadSensorMetaData();
			}
		});
		Button cmdAddSensorPro = new Button("Add new");
		cmdAddSensorPro.setParent(hb1);
		cmdAddSensorPro.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				NewPropertyWizardWindow newPro = new NewPropertyWizardWindow(true);
				newPro.setParent(SensorMetaPropertiesWizardWindow.this);
				newPro.doOverlapped();
			}
		});
		
		initSensorMetaGrid();
		hb1 = new Hbox();
		hb1.setParent(vbox);
		hb1.setSpacing("5px");
		
		cmdReload = new Button("Reload");
		cmdReload.setParent(hb1);
		cmdReload.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				reloadPlaceMetaData();
			}
		});
		
		Button cmdAddPlacePro = new Button("Add new");
		cmdAddPlacePro.setParent(hb1);
		cmdAddPlacePro.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				NewPropertyWizardWindow newPro = new NewPropertyWizardWindow(false);
				newPro.setParent(SensorMetaPropertiesWizardWindow.this);
				newPro.doOverlapped();
			}
		});
		
		initPlaceMetaGrid();
		//addContent();
		
		Hbox hboxWButton = new Hbox();
		hboxWButton.setParent(vbox);
		hboxWButton.setSpacing("10px");
		hboxWButton.setStyle("padding-left:60%");
		hboxWButton.setHeight("100%");
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
	
	public int getOrder(){
		return order;
	}
	
	
	public HashMap getXml2SensorPro() {
		return xml2SensorPro;
	}


	public void setXml2SensorPro(HashMap xml2SensorPro) {
		this.xml2SensorPro = xml2SensorPro;
	}


	public HashMap getXml2PlacePro() {
		return xml2PlacePro;
	}


	public void setXml2PlacePro(HashMap xml2PlacePro) {
		this.xml2PlacePro = xml2PlacePro;
	}


	private void initSensorMetaGrid(){		
		grdSensorMeta = new Grid();
		grdSensorMeta.setId("metagrdSensorMeta");
		grdSensorMeta.setMold("paging");
		grdSensorMeta.setPageSize(4);
		grdSensorMeta.setWidth("100%");
		grdSensorMeta.setParent(vbox);
		
		Auxhead aux = new Auxhead();
		aux.setParent(grdSensorMeta);
		Auxheader auxhder = new Auxheader("Please click Edit Button if you want to modify sensor source metadata");
		auxhder.setColspan(4);
		auxhder.setStyle("color:red");
		auxhder.setParent(aux);
		
		Columns clms = new Columns();
		clms.setSizable(true);
		clms.setParent(grdSensorMeta);
		
		Column column = new Column();
		column.setParent(clms);
		column.setLabel("Metadata properties");
		column.setWidth("10%");
		column = new Column();
		column.setParent(clms);
		column.setLabel("Ontology annotation");
		column.setWidth("30%");
		column = new Column();
		column.setParent(clms);
		column.setLabel("Value");
		column.setWidth("40%");
		column = new Column("Edit");
		column.setWidth("10%");
		column.setParent(clms);
	}
	
	private void initPlaceMetaGrid(){
		grdPlaceMeta = new Grid();
		grdPlaceMeta.setId("grdPlaceMeta");
		grdPlaceMeta.setMold("paging");
		grdPlaceMeta.setPageSize(4);
		grdPlaceMeta.setWidth("100%");
		grdPlaceMeta.setParent(vbox);
		
		Auxhead aux = new Auxhead();
		aux.setParent(grdPlaceMeta);
		Auxheader auxhder = new Auxheader("Please click Edit Button if you want to modify sensor place metadata");
		auxhder.setColspan(4);
		auxhder.setStyle("color:red");
		auxhder.setParent(aux);
		
		Columns clms = new Columns();
		clms.setSizable(true);
		clms.setParent(grdPlaceMeta);
		
		Column column = new Column();
		column.setParent(clms);
		column.setLabel("Metadata properties");
		column.setWidth("10%");
		column = new Column();
		column.setParent(clms);
		column.setLabel("Ontology annotation");
		column.setWidth("30%");
		column = new Column();
		column.setParent(clms);
		column.setLabel("Value");
		column.setWidth("40%");
		column = new Column("Edit");
		column.setWidth("10%");
		column.setParent(clms);
	}
	
	public void addContent(){
				
		setUneditableProperties();
		
		sensorModelList = new ListModelList(xml2SensorPro.entrySet());
		//sensorRowRenderer = new EditRowBtnRenderer();
		sensorRowRenderer = new EditRowWithRdButtonRenderer();
				
		sensorRowRenderer.setLstXMLValueTag(sensorXMLTagList);			
		
		sensorRowRenderer.setRowList(sensorModelList.getInnerList());
		sensorRowRenderer.setParentHashMap(xml2SensorPro);
		grdSensorMeta.setRowRenderer(sensorRowRenderer);
		grdSensorMeta.setModel(sensorModelList);
		
		placeModelList = new ListModelList(xml2PlacePro.entrySet());
		placeRowRenderer = new EditRowWithRdButtonRenderer();
		placeRowRenderer.setLstXMLValueTag(sensorXMLTagList);
		placeRowRenderer.setRowList(placeModelList.getInnerList());
		grdPlaceMeta.setRowRenderer(placeRowRenderer);
		grdPlaceMeta.setModel(placeModelList);
	}
	
	private void backToPreviousStep(){
		UserAnnotateWizard u = (UserAnnotateWizard) this.getParent();
		u.moveToStep(this.getOrder()-1);
		//System.out.println(MappingWizardUtil.SensorMetaAnnotate.values());
	}
	private void moveToNextStep(){		
		UserAnnotateWizard u = (UserAnnotateWizard) this.getParent();
		u.moveToStep(this.getOrder()+1);
	}
	
	public void reloadSensorMetaData(){
		cloneHashMap(MappingWizardUtil.SensorMetaAnnotate, xml2SensorPro);
		sensorModelList = new ListModelList(xml2SensorPro.entrySet());
		sensorRowRenderer.setRowList(sensorModelList.getInnerList());
		grdSensorMeta.setModel(sensorModelList);
	}
	
	public void reloadPlaceMetaData(){		
		cloneHashMap(MappingWizardUtil.PlaceMetaAnnotate, xml2PlacePro);
		placeModelList = new ListModelList(xml2PlacePro.entrySet());
		placeRowRenderer.setRowList(placeModelList.getInnerList());
		grdPlaceMeta.setModel(placeModelList);
	}
	
	@SuppressWarnings("unchecked")
	public static void cloneHashMap(HashMap<String,List> source, HashMap<String,List> des){
		des.clear();
		Set set = source.entrySet();
		Iterator iter = set.iterator();
		List temp2;
		while(iter.hasNext()){
			Map.Entry me = (Map.Entry)iter.next();
			List temp = (List)me.getValue();
			temp2 = new ArrayList();
			for(Object st:temp){
				String t=st.toString();
				temp2.add(st.toString());
			}
			des.put(me.getKey().toString(), temp2);
		}
	}

	public void setUneditableProperties(){
		SensorTypeWizardWindow sensorT = (SensorTypeWizardWindow ) this.getParent().getFellowIfAny("ChooseSensorTypeWizard");
		((List)xml2SensorPro.get("sensorType")).set(3, sensorT.getType().toString());
		((List)xml2SensorPro.get("source")).set(3, sensorT.getSourceURL());
		((List)xml2SensorPro.get("sourceType")).set(3, sensorT.getSourceType());
	}
	
	public static void main(String[] agrs){
		HashMap des = new HashMap<String, List>();
		cloneHashMap(MappingWizardUtil.SensorMetaAnnotate, des);
	}
	
	public void addNewSensorMetaProperty(String name,String newPro){
		List lst = new ArrayList();
		lst.add(newPro);
		lst.add(1);
		lst.add("This property is defined by the user");
		lst.add("please input data");
		xml2SensorPro.put(name, lst);
		sensorModelList = new ListModelList(xml2SensorPro.entrySet());
		grdSensorMeta.setModel(sensorModelList);
		
	}
	
	public void addNewPlaceMetaProperty(String name,String newPro){
		List lst = new ArrayList();
		lst.add(newPro);
		lst.add(1);
		lst.add("This property is defined by the user");
		lst.add("please input data");
		xml2PlacePro.put(name, lst);
		placeModelList = new ListModelList(xml2PlacePro.entrySet());
		grdPlaceMeta.setModel(placeModelList);
		
	}
}
