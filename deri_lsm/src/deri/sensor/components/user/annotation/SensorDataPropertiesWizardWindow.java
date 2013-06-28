package deri.sensor.components.user.annotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;
import deri.sensor.database.ServiceLocator;
import deri.sensor.manager.SensorManager;
import deri.sensor.wrapper.WebServiceURLRetriever;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class SensorDataPropertiesWizardWindow extends Window {
	
	private Listbox candidateLb;
	private Listbox chosenLb;
	private ListModelList candidateModel;
    private ListModelList chosenDataModel;
    private List lstXMLTag;
	private Hbox hbox;
	private Button cmdNext;
	private Button cmdBack;
	private int order = 2;
	private String type;
	private String sourceURL;
	private Listbox lsbSystemPro;
	private Textbox txtXMLContent;
	private Label previewValue;
	private String xmlContent;
	public SensorDataPropertiesWizardWindow(){
		super();
		init();		
	}
	
	
	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}

	public String getSourceURL() {
		return sourceURL;
	}


	public void setSourceURL(String sourceURL) {
		this.sourceURL = sourceURL;
	}
	
	public void setOrder(int order) {
		this.order = order;
	}

	public void init(){
		this.setTitle("Step 3 - Choose your sensor properties");
		this.setId("ChooseSensorPropertiesWizard");
		this.setBorder("normal");
		this.setContentStyle("overflow:auto");
		this.setHeight("500px");
		this.setWidth("700px");
		this.setSizable(true);
		this.setClosable(true);
		
		Vbox vbox = new Vbox();
		vbox.setParent(this);
		vbox.setHeight("100%");
		vbox.setWidth("100%");
		vbox.setStyle("overflow:auto");
		
		txtXMLContent = new Textbox();
		txtXMLContent.setParent(vbox);
		txtXMLContent.setRows(10);
		txtXMLContent.setWidth("100%");	
		txtXMLContent.setHeight("70%");
		txtXMLContent.setStyle("overflow:auto");
		txtXMLContent.setReadonly(true);
		
		Button cmdReload = new Button("Reload");
		cmdReload.setParent(vbox);
		cmdReload.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				//System.out.println(SensorDataPropertiesWizardWindow.this.getHmChosenDataList());
			}
		});
		
		hbox = new Hbox();
		hbox.setHeight("40%");
		hbox.setWidth("100%");
		//hbox.setStyle("padding:5px");
		hbox.setParent(vbox);
		chosenDataModel = new ListModelList();		
		
		initLstCandidateSensorPro();
		
		initChooseListImage();
		initLstChooseSensorPro();	
		
		initOrderListButton();
		addContent();
				
		previewValue = new Label("Value");
		previewValue.setParent(vbox);
		
        Hbox hboxWButton = new Hbox();
		hboxWButton.setParent(vbox);
		hboxWButton.setSpacing("10px");
		hboxWButton.setStyle("padding-left:80%");
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
	
	public int getOrder(){
		return order;
	}
	
	public void addContent(){
		if(type==null) return;
		chosenLb.setModel(chosenDataModel);

		//sourceURL = "http://weather.yahooapis.com/forecastrss?p=15231";
		xmlContent = WebServiceURLRetriever.RetrieveFromURL(sourceURL);
		txtXMLContent.setValue(xmlContent);
		lstXMLTag = WebServiceURLRetriever.getXMLLeafNodeFromSourceURL(sourceURL);
		ListModelList lmdl = new ListModelList(lstXMLTag);
		if(lmdl.getSize()!=0) lmdl.addSelection(lmdl.get(0));
		this.setModel(lmdl);
		//this.setModel(new ListModelList(MappingWizardUtil.type2SensorProAnnotate.get(type).entrySet()));
        this.setRenderer(new SpecifiedSensorListItemRenderer());
        
	}
	
	private void backToPreviousStep(){
		SensorTypeWizardWindow w = (SensorTypeWizardWindow) this.getParent().getFellowIfAny("ChooseSensorTypeWizard");
		UserAnnotateWizard u = (UserAnnotateWizard) this.getParent();
		if(w.isNewSensorType())		
			u.moveToStep(this.getOrder()-1);
		else
			u.moveToStep(this.getOrder()-2);
	}
	
	private void moveToNextStep(){		
		SensorMappingXML2TableWizardWindow m = (SensorMappingXML2TableWizardWindow) this.getParent().getFellowIfAny("SensorMappingPropertiesWizard");
		m.setPropertiesList(this.getChosenDataList());
		m.addContent();
		
		SensorMetaPropertiesWizardWindow sensorMeta = (SensorMetaPropertiesWizardWindow) this.getParent().getFellowIfAny("SensorMetaPropertiesWizard");		
		sensorMeta.setSensorXMLTagList(lstXMLTag);
		sensorMeta.addContent();
		
		UserAnnotateWizard u = (UserAnnotateWizard) this.getParent();
		u.moveToStep(this.getOrder()+1);
	}
	
	private void initOrderListButton() {
		// TODO Auto-generated method stub
		Vbox vbox1 = new Vbox();
		vbox1.setSpacing("10px");
		vbox1.setParent(hbox);
		
		Image topBtn = new Image();
		topBtn.setSrc("/imgs/arrow/upuparrow_g.png");
		topBtn.setStyle("cursor:pointer");
		topBtn.setParent(vbox1);
		topBtn.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				top();
			}
		});
					
		Image  upBtn = new Image ();
		upBtn.setSrc("/imgs/arrow/uparrow_g.png");
		upBtn.setStyle("cursor:pointer");
		upBtn.setParent(vbox1);
		upBtn.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				up();
			}
		});
		
		Image  downBtn = new Image ();
		downBtn.setSrc("/imgs/arrow/downarrow_g.png");
		downBtn.setStyle("cursor:pointer");
		downBtn.setParent(vbox1);
		downBtn.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				down();
			}
		});
		
		Image  bottomBtn = new Image ();
		bottomBtn.setSrc("/imgs/arrow/downdownarrow_g.png");
		bottomBtn.setStyle("cursor:pointer");
		bottomBtn.setParent(vbox1);
		bottomBtn.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				bottom();
			}
		});
	}

	private void initChooseListImage () {
		// TODO Auto-generated method stub
		Vbox vbox1 = new Vbox();
		vbox1.setSpacing("10px");
		vbox1.setParent(hbox);
		
		Image  chooseAllbtn = new Image ();		
		chooseAllbtn.setSrc("/imgs/arrow/rightrightarrow_g.png");
		chooseAllbtn.setStyle("cursor:pointer");
		chooseAllbtn.setParent(vbox1);
		chooseAllbtn.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				chooseAll();
			}
		});
					
		Image  chooseBtn = new Image ();
		chooseBtn.setSrc("/imgs/arrow/rightarrow_g.png");
		chooseBtn.setStyle("cursor:pointer");
		chooseBtn.setParent(vbox1);
		chooseBtn.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				Set set = chooseOne();
		        getChooseEvent(set);
			}
		});
		
		Image  removeBtn = new Image ();
		removeBtn.setSrc("/imgs/arrow/leftarrow_g.png");
		removeBtn.setStyle("cursor:pointer");
		removeBtn.setParent(vbox1);
		removeBtn.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				Set set = unchooseOne();
				getChooseEvent(set);
			}
		});
		
		Image  removeAllBtn = new Image ();
		removeAllBtn.setSrc("/imgs/arrow/leftleftarrow_g.png");
		removeAllBtn.setStyle("cursor:pointer");
		removeAllBtn.setParent(vbox1);
		removeAllBtn.addEventListener(Events.ON_CLICK, new EventListener() {
			
			@Override
			public void onEvent(Event arg0) throws Exception {
				// TODO Auto-generated method stub
				unchooseAll();
			}
		});
	}

	private void initLstChooseSensorPro() {
		// TODO Auto-generated method stub
		chosenLb = new Listbox();
		chosenLb.setVflex(true);
		chosenLb.setWidth("300px");
		chosenLb.setMultiple(true);
		chosenLb.setMold("paging");
		chosenLb.setPageSize(6);		
		chosenLb.setParent(hbox);
		chosenLb.setHeight("80%");
					
	}
	
	private void initLstCandidateSensorPro(){
		candidateLb = new Listbox();
		candidateLb.setVflex(true);
		candidateLb.setWidth("300px");
		candidateLb.setMultiple(true);		
		candidateLb.setMold("paging");
		candidateLb.setPageSize(6);
		candidateLb.setParent(hbox);
		candidateLb.setHeight("80%");		   
		candidateLb.addEventListener(Events.ON_SELECT, new EventListener() {			
			@Override
			public void onEvent(Event event) throws Exception {
				// TODO Auto-generated method stub
				previewValue.setValue("Value:"+getValueFromXMLTag(xmlContent, candidateLb.getSelectedItem().getLabel()));
			}
		});
	}

	public void setModel(ListModelList candidate) {
		// TODO Auto-generated method stub
		candidateLb.setModel(this.candidateModel = new ListModelList(candidate));
        chosenDataModel.clear();
	}

	public void setRenderer(
			SpecifiedSensorListItemRenderer dataRenderer) {
		// TODO Auto-generated method stub
		candidateLb.setItemRenderer(dataRenderer);
        chosenLb.setItemRenderer(dataRenderer);
        if(candidateLb.getListhead()!=null) return;
        candidateLb.getChildren().add(dataRenderer.getListhead());
        chosenLb.getChildren().add(dataRenderer.getListhead());
	}
	
	public void getChooseEvent(Set set) {
		Events.postEvent(new ChooseEvent(this, set));
    }
 
 
    // Button Action - Choose All
    public void chooseAll() {
        for (int i = 0, j = candidateModel.getSize(); i < j; i++) {
            chosenDataModel.add(candidateModel.getElementAt(i));
        }
        candidateModel.clear();
    }
 
    // Button Action - Choose One
    public Set chooseOne() {
        Set set = candidateLb.getSelectedItems();
        for (Object obj : new ArrayList(set)) {
            Object data = ((Listitem) obj).getValue();
            chosenDataModel.add(data);
            candidateModel.remove(data);
        }
        return set;
    }
    
    public void chooseFromListbox() {
        String st = lsbSystemPro.getSelectedItem().getLabel();
        if(!chosenDataModel.contains(st))
        	chosenDataModel.add(st);        
    }
    // Button Action - Unchoose One
    public Set unchooseOne() {
        Set set = chosenLb.getSelectedItems();
        for (Object obj : new ArrayList(set)) {
            Object data = ((Listitem) obj).getValue();
            candidateModel.add(data);
            chosenDataModel.remove(data);
        }
        return set;
    }
     
    // Button Action - Unchoose All
    public void unchooseAll() {
        for (int i = 0, j = chosenDataModel.getSize(); i < j; i++) {
            candidateModel.add(chosenDataModel.getElementAt(i));
        }
        chosenDataModel.clear();
    }
     
    // Button Action - Move to Top
    public void top() {
        int i = 0;
        for (Object obj : new ArrayList(chosenLb.getSelectedItems())) {
            obj = ((Listitem) obj).getValue();
            chosenDataModel.remove(obj);
            chosenDataModel.add(i++, obj);
        }
    }
 
    // Button Action - Move up
    public void up() {
        int index = chosenLb.getSelectedIndex();
        if (index == 0 || index < 0) //first one or nothing selected
            return;
        Object obj = chosenLb.getSelectedItem().getValue();
        chosenDataModel.remove(obj);
        chosenDataModel.add(--index, obj);
        chosenLb.setSelectedIndex(index);
    }
 
    // Button Action - Move down
    public void down() {
        int index = chosenLb.getSelectedIndex();
        if (index == chosenDataModel.size() - 1 || index < 0)  //last one or nothing selected
            return;
        Object obj = chosenLb.getSelectedItem().getValue();
        chosenDataModel.remove(obj);
        chosenDataModel.add(++index, obj);
        chosenLb.setSelectedIndex(index);
    }
 
    // Button Action - Move to Bottom
    public void bottom() {
        for (Object obj : new ArrayList(chosenLb.getSelectedItems())) {
            obj = ((Listitem) obj).getValue();
            chosenDataModel.remove(obj);
            chosenDataModel.add(obj);
        }
    }
    

   public List getChosenDataList() {
       return new ArrayList(chosenDataModel);
   }

   @SuppressWarnings("unchecked")
   public HashMap getHmChosenDataList() {
       ArrayList ha = new ArrayList(chosenDataModel);
       HashMap hm = new HashMap();
       Iterator iter = ha.iterator();
       while(iter.hasNext()){
    	   Map.Entry me = (Map.Entry)iter.next();
    	   hm.put(me.getKey(),((List)me.getValue()).get(0));
       }
       return hm;
   }
   
   public String getValueFromXMLTag(String xml,String xmlTag){
	   String value = "";
	   try {
		Document document = DocumentHelper.parseText(xml);
		if(!xmlTag.contains("@")){
			Element elm = (Element)document.selectSingleNode(xmlTag);
			value = elm.getStringValue();
		}else if(xmlTag.contains("@")){
			String parentPath = xmlTag.substring(0, xmlTag.indexOf("@")-1);
			String attrName = xmlTag.substring(xmlTag.indexOf("@")+1);
			Element elm = (Element)document.selectSingleNode(parentPath);
			Attribute attribute = elm.attribute(attrName);
			value = attribute.getStringValue();
		}
	} catch (DocumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	   return value;
   }
   // Customized Event
   public class ChooseEvent extends Event {
       public ChooseEvent(Component target, Set data) {
           super("onChoose", target, data);
       }
   }
   
   
}
