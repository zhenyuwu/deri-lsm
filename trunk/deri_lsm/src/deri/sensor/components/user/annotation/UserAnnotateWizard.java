package deri.sensor.components.user.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.zkoss.zul.Div;
import org.zkoss.zul.Window;

import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.WrapperStatus;
import deri.sensor.javabeans.SensorSourceType;
import deri.sensor.javabeans.SystemSensorType;
import deri.sensor.javabeans.User;
import deri.sensor.javabeans.Wrapper;
import deri.sensor.manager.SensorManager;
import deri.sensor.meta.SensorType;
import deri.sensor.utils.XMLUtil;
import deri.sensor.wrapper.UserSensorWrapper;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class UserAnnotateWizard  extends Div{

	private SensorTypeWizardWindow sensorWindow;
	private SensorDataPropertiesWizardWindow dataWindow;
	private SensorMappingXML2TableWizardWindow mapping2OntoWindow;
	private SensorMetaPropertiesWizardWindow metaWindow;
	private WrapperMetaPropertiesWizardWindow wrapperWindow;
	private CompleteAnnotateWizardWindow compWindow;
	private List<Window> lstWizardPnl;
	private User user;
	private UserSensorWrapper uWrapper;
	private SensorManager sensorManager;
	
	
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public UserAnnotateWizard(){
		super();
		//init();		
	}
	
	public void init(){
		//this.setTitle("User annotate wizard");
		this.setId("UserAnnotateWizard");
		//this.setBorder("normal");
		this.setStyle("overflow:auto");
		//this.setContentStyle("overflow:auto");
		//this.setSizable(true);
		//this.setHeight("350px");
		lstWizardPnl = new ArrayList<Window>();
		
		this.addWizardFirstStep();
		sensorManager = ServiceLocator.getSensorManager();
	}

	public void addWizardFirstStep(){
		sensorWindow = new SensorTypeWizardWindow();
		sensorWindow.setParent(this);
		sensorWindow.setPosition("center,center");
		sensorWindow.init();
		sensorWindow.doOverlapped();
		lstWizardPnl.add(sensorWindow);
		
		wrapperWindow = new WrapperMetaPropertiesWizardWindow();
		wrapperWindow.setParent(this);
		wrapperWindow.setVisible(false);
		wrapperWindow.setPosition("center,center");
		lstWizardPnl.add(wrapperWindow);
		
		dataWindow = new SensorDataPropertiesWizardWindow();
		dataWindow.setParent(this);
		dataWindow.setVisible(false);
		dataWindow.setPosition("center,center");
		lstWizardPnl.add(dataWindow);
		
		mapping2OntoWindow = new SensorMappingXML2TableWizardWindow();
		mapping2OntoWindow.setParent(this);
		mapping2OntoWindow.setVisible(false);
		mapping2OntoWindow.setPosition("center,center");
		lstWizardPnl.add(mapping2OntoWindow);
		
		metaWindow = new SensorMetaPropertiesWizardWindow();
		metaWindow.setParent(this);
		metaWindow.setVisible(false);
		metaWindow.setPosition("center,center");
		lstWizardPnl.add(metaWindow);
		
		compWindow = new CompleteAnnotateWizardWindow();
		compWindow.setParent(this);
		compWindow.setVisible(false);
		compWindow.setPosition("center,center");
		lstWizardPnl.add(compWindow);		
		
	}

	public void moveToStep(int step){
		if(step<0){
			this.detach();
			return;
		}
		for(Window pn:lstWizardPnl){
			if(lstWizardPnl.indexOf(pn)==step){
				pn.setVisible(true);
				pn.doOverlapped();
				//this.invalidate();
				//this.setWidth(pn.getWidth());
				//this.setHeight(pn.getHeight());				
			}
			else pn.setVisible(false);
		}
	}
	
	public void println(int i){
		System.out.println(i);
	}
	
	@SuppressWarnings("unchecked")
	public void startWrappingDataProcess(){
		if(!SensorType.getTypeList().contains(sensorWindow.getType())){
			sensorManager = ServiceLocator.getSensorManager();
			SystemSensorType sp = new SystemSensorType();
			sp.setSensorType(sensorWindow.getType());
			String temp="";
			Iterator iter = mapping2OntoWindow.getHsMapSign_SensorAnnotate().values().iterator();
			int i;
			while(iter.hasNext()){
				List l = (List)iter.next();
				temp+= l.get(0) +",";
			}			
			//System.out.println(temp.substring(0, temp.length()-1));
			sp.setSensorProperties(temp.substring(0, temp.length()-1));
			sp.setUser(user);
			
			Wrapper newWrapper = new Wrapper();
			newWrapper.setCurrentStatus(WrapperStatus.stopped.toString());
			newWrapper.setDataFormat(wrapperWindow.getWrapperDataFormat());
			newWrapper.setTimeToUpdate(wrapperWindow.getTimeUpdate());
			newWrapper.setTimeToUpdateUnit(wrapperWindow.getTimeUpdateUnit());
			
			sp.setWrapper(newWrapper);			
			sensorManager.addObject(sp);			
		}
		if(sensorWindow.isNewSourceType()){
			SensorSourceType sourceObj = new SensorSourceType();
			sourceObj.setsensorType(sensorWindow.getType());
			sourceObj.setUser(user);
			sourceObj.setSourceType(sensorWindow.getSourceType());
			String xmlContent = getSourceXMLMapContent();
			sourceObj.setSourceXMLContent(xmlContent);
			sensorManager.addObject(sourceObj);
			
			uWrapper = new UserSensorWrapper(sensorWindow.getType(),user);		
			uWrapper.addNewUserSensor(sensorWindow.getSourceURL(), sensorWindow.getSourceType());
		}	

		this.detach();
	}
		
	private String getSourceXMLMapContent(){
		Document document = XMLUtil.createDocument();
		Element root = XMLUtil.addRootToDocument(document, XMLUtil.sensorsource_sign_ROOT, null);
					
//		XMLUtil.addElementToElement(root, XMLUtil.sensorsource_sign_SENSORTYPE, null, sensorWindow.getType());
//		XMLUtil.addElementToElement(root, XMLUtil.sensorsource_sign_SOURCETYPE, null, sensorWindow.getSourceType());
//		XMLUtil.addElementToElement(root, XMLUtil.sensorsource_sign_SOURCEURL, null, sensorWindow.getSourceURL());
			
		//add sensor meta
		Element sensorEl = XMLUtil.addElementToElement(root, XMLUtil.sensorsource_sign_SENSOR, null, null);
		Iterator iter = getHashMapDataForUserSensorParser(metaWindow.getXml2SensorPro()).entrySet().iterator();
		while(iter.hasNext()){
			Element subSensormap = XMLUtil.addElementToElement(sensorEl, XMLUtil.sensorsource_sign_MAP, null, null);
			Map.Entry me = (Map.Entry) iter.next();
			List l = (List)me.getValue();
			XMLUtil.addElementToElement(subSensormap, XMLUtil.sensorsource_sign_PROPERTY, null, me.getKey().toString());
			XMLUtil.addElementToElement(subSensormap, XMLUtil.sensorsource_sign_ONTOPRO, null, l.get(0).toString());
			XMLUtil.addElementToElement(subSensormap, XMLUtil.sensorsource_sign_VALUETAG, null, l.get(1).toString());
		}
		
		//add place metadata
		Element placeEl = XMLUtil.addElementToElement(root, XMLUtil.sensorsource_sign_PLACE, null, null);
		iter = getHashMapDataForUserSensorParser(metaWindow.getXml2PlacePro()).entrySet().iterator();
		while(iter.hasNext()){
			Element subPlacemap = XMLUtil.addElementToElement(placeEl, XMLUtil.sensorsource_sign_MAP, null, null);
			Map.Entry me = (Map.Entry) iter.next();
			List l = (List)me.getValue();
			XMLUtil.addElementToElement(subPlacemap, XMLUtil.sensorsource_sign_PROPERTY, null, me.getKey().toString());
			XMLUtil.addElementToElement(subPlacemap, XMLUtil.sensorsource_sign_ONTOPRO, null, l.get(0).toString());
			XMLUtil.addElementToElement(subPlacemap, XMLUtil.sensorsource_sign_VALUETAG, null, l.get(1).toString());
		}
		
		//add reading data
		Element map = XMLUtil.addElementToElement(root, XMLUtil.sensorsource_sign_TAGMAP, null, null);
		HashMap<String, List> signMap = mapping2OntoWindow.getHsMapSign_SensorAnnotate();
		iter = signMap.entrySet().iterator();
		while(iter.hasNext()){		
			Element submap = XMLUtil.addElementToElement(map, XMLUtil.sensorsource_sign_MAP, null, null);
			Map.Entry me = (Map.Entry) iter.next();
			List l = (List)me.getValue();
			XMLUtil.addElementToElement(submap, XMLUtil.sensorsource_sign_VALUETAG, null, me.getKey().toString());
			XMLUtil.addElementToElement(submap, XMLUtil.sensorsource_sign_PROPERTY, null, l.get(0).toString());
			XMLUtil.addElementToElement(submap, XMLUtil.sensorsource_sign_UNIT, null, l.get(1).toString());
		}		
		return document.asXML();
	}
	
	public HashMap getHashMapDataForUserSensorParser(HashMap hmInput){
		HashMap<String, List> newHM = new HashMap<String, List>();
		Iterator iter = hmInput.entrySet().iterator();
		while(iter.hasNext()){
			Map.Entry me = (Map.Entry)iter.next();
			String key = me.getKey().toString();
			List valueList = new ArrayList();
			valueList.add(((ArrayList)me.getValue()).get(0));
			valueList.add(((ArrayList)me.getValue()).get(3));
			newHM.put(key, valueList);
		}		
		return newHM;
	}	

}
