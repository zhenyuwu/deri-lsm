package deri.sensor.components.windows;


import java.util.List;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;
import org.zkoss.zul.Tabbox;

import deri.sensor.components.search.RowViewTabPannel;
import deri.sensor.components.search.TripleViewTabPannel;
import deri.sensor.database.ServiceLocator;
import deri.sensor.javabeans.Observation;
import deri.sensor.javabeans.Sensor;
import deri.sensor.manager.SensorManager;
import deri.sensor.meta.SensorType;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class SensorDataDetail extends Window {
	
	private Tabbox tbbDetails;
	private String sensorType;
	private List<String> listDataResult;
	
	 public String getSensorType() {
		return sensorType;
	}

	public void setSensorType(String sensorType) {
		this.sensorType = sensorType;
	}

	public SensorDataDetail(){
		 super();
	 }
	 
	 public SensorDataDetail(String title,String type){
		 super();
		 this.sensorType = type;
	 }
	 
	public List<String> getListDataResult() {
		return listDataResult;
	}

	public void setListDataResult(List<String> listDataResult) {
		this.listDataResult = listDataResult;
	}
		
	 public void init(){
		 this.setId("SensorDataDetail");
		 this.setPosition("center,center");
		 this.setTitle("Data detail");
		 this.setWidth("70%");
		 this.setHeight("50%");
		 this.setBorder("normal");
		 this.setContentStyle("overflow:auto");		
		 this.setClosable(true);
		 this.setSizable(true);
		 
		 tbbDetails = new Tabbox();
		 tbbDetails.setWidth("100%");
		 tbbDetails.setMold("accordion");
		 tbbDetails.setParent(this);
		 initTab();
	 }
	 
	 public void initTab(){		
			Tab rowTab = new Tab("Row view");
			Tab tripleTab = new Tab("Triple view");
			//Tab chartTab = new Tab("Chart");
			
			tbbDetails.appendChild(new Tabs());			
			tbbDetails.appendChild(new Tabpanels());			
						
			tbbDetails.getTabs().appendChild(rowTab);
			tbbDetails.getTabs().appendChild(tripleTab);
			//tbbDetails.getTabs().appendChild(chartTab);
			tripleTab.setHeight("50%");
			if(sensorType.equals(SensorType.all)){
				SensorManager sensormanager = ServiceLocator.getSensorManager();
				Sensor sensor = sensormanager.getSpecifiedSensorWithObservationId(listDataResult.get(0));
				this.setSensorType(sensor.getSensorType());
			}
			RowViewTabPannel rowViewPanel = new RowViewTabPannel();
			tbbDetails.getTabpanels().appendChild(rowViewPanel);
			rowViewPanel.setType(this.sensorType);
			rowViewPanel.setSpatialResultList(listDataResult);
			rowViewPanel.init();
			rowViewPanel.addContent();
			
			TripleViewTabPannel tripleViewPanel = new TripleViewTabPannel();
			tripleViewPanel.setHeight("100%");
			tbbDetails.getTabpanels().appendChild(tripleViewPanel);
			tripleViewPanel.setType(sensorType);
			tripleViewPanel.setSpatialResultList(listDataResult);
			tripleViewPanel.init();
			tripleViewPanel.addContent();			
			
		}
}
