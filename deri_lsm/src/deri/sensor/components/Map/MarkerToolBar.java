package deri.sensor.components.Map;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Space;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Toolbarbutton;

import deri.sensor.meta.FilterSensorLabels;
import deri.sensor.meta.SensorType;
import deri.sensor.meta.SensorTypeEnum;
import deri.sensor.utils.ConstantsUtil;
/**
 * @author Hoan Nguyen Mau Quoc
 */
public class MarkerToolBar extends Toolbar {
	private static final long serialVersionUID = 1044492392364159678L;
	private final HashMap<Toolbarbutton, Boolean> toolbarButtonHMap = new HashMap<Toolbarbutton, Boolean>();
	public void onCreate(){
		final GMaps map = (GMaps)this.getFellowIfAny("map");
		//for(final String type : SensorType.getTypeList()){
		for(final String type : SensorType.getSystemTypeList()){
			//if(!type.equals(SensorType.all)){
				Toolbarbutton button = new Toolbarbutton();				
				button.setParent(this);
				if(SensorType.getIcon(type)!=null)
					button.setImage(SensorType.getIcon(type));
				else button.setImage(ConstantsUtil.gmarker_icon_usersensor);
				button.setLabel(FilterSensorLabels.getLabelForSensorType(type.toString()));
				Space space = new Space();
				space.setParent(this);
				space.setWidth("3px");
				//if(!button.getLabel().equals("All"))
					toolbarButtonHMap.put(button, false);
				
				initializeTheFirstViewOfTheFilter(SensorTypeEnum.weather.toString());
				button.addEventListener(Events.ON_CLICK, new EventListener(){
					@Override
					public void onEvent(Event event) throws Exception {
						String label = ((Toolbarbutton)event.getTarget()).getLabel();
//						if(FilterSensorLabels.getSensorType(label).equals(SensorType.all.toString())){
//							selectAllItems(true);
//							map.reshowMarkersWith3Filters();
//						}else{
							//map.setFirstLevelSelected(label, true);
							setSelected_Label(label, true);
							map.reshowMarkersWith3Filters();
						//}
					}
				});		
		}
	}
	
	private void initializeTheFirstViewOfTheFilter(String type){
		String label = FilterSensorLabels.getLabelForSensorType(type);		
		Toolbarbutton tbt = getToolbarButton(label);
		setSelected_Item(tbt, true);		
		//map.reshowMarkersWith3Filters();
	}
	
	
	
	@SuppressWarnings("unchecked")
	private void setSelected_Item(Toolbarbutton tbt, boolean isChecked){
		toolbarButtonHMap.put(tbt, isChecked);
		if(isChecked)
			tbt.setStyle("color:#848484");
		else tbt.setStyle("color:#FFFFFF");
		if(tbt.getLabel().equals("All"))
			selectAllItems(isChecked);
		else
			updateSelectSensorTypes();
	}
	
	private Toolbarbutton getToolbarButton(String label){
		Set set = toolbarButtonHMap.entrySet();
		Iterator iter = set.iterator();
		while(iter.hasNext()){
			Map.Entry item = (Map.Entry)iter.next();
			if(((Toolbarbutton)item.getKey()).getLabel().trim()
					.toLowerCase().equals(label.trim().toLowerCase()))					
				return (Toolbarbutton) item.getKey();
		}		
		return null;
	}
	
	
	@SuppressWarnings("unchecked")
	private void selectAllItems(boolean isChecked){
		Set set = toolbarButtonHMap.entrySet();
		Iterator iter = set.iterator();
		while(iter.hasNext()){
			Map.Entry item = (Map.Entry)iter.next();
			item.setValue(isChecked);
		}
		updateSelectSensorTypes();	
	}
	
	private void updateSelectSensorTypes(){
		List<String> selectedSensorTypes = this.getSelectedSensorTypes();
		this.getDesktop().setAttribute("selectedSensorTypes", selectedSensorTypes);
	}
	
	public List<String> getSelectedSensorTypes(){
		List<String> sensorTypes = new ArrayList<String>();		
		List<String> labels = this.getSelectedItemLabels();		
		
		for(String label : labels){
			//String lb = FilterSensorLabels.toolbarLabel2SensorLabel.get(label);
			String type = FilterSensorLabels.getSensorType(label);
			if(!sensorTypes.contains(type)){
				sensorTypes.add(type);
			}
		}
		return sensorTypes;
	}
	
	public void setSelected_Label(String label, boolean isChecked){
		Toolbarbutton tbt = getToolbarButton(label);
		if(tbt != null){
			boolean isChk = toolbarButtonHMap.get(this.getToolbarButton(label));
			//boolean oppositeValue = getOppositeValue(isChecked);
			boolean oppositeValue = getOppositeValue(isChk);			
			//selectAllItems(oppositeValue);
			//setSelected_Item(tbt, isChecked);
			setSelected_Item(tbt, oppositeValue);
		}
	}
	
	public List<String> getSelectedItemLabels(){
		List<String> selectedItemsLabel = new ArrayList<String>(20);
		Set set = toolbarButtonHMap.entrySet();
		Iterator iter = set.iterator();
		while(iter.hasNext()){
			Map.Entry item = (Map.Entry)iter.next();
			if((Boolean)item.getValue()==true)			
			  selectedItemsLabel.add(((Toolbarbutton)item.getKey()).getLabel());			
		}
		return selectedItemsLabel;
	}
	
	public void setItemsSelected(List<String> labels, boolean isChecked){
		if(labels != null && labels.size() > 0){			
			boolean oppositeValue = getOppositeValue(isChecked);
			selectAllItems(oppositeValue);
			for(String lebel : labels){
				updateSelectSensorTypes();
			}
		}
	}
	
	private boolean getOppositeValue(boolean value){
		if(value){
			return false;
		}else{
			return true;
		}
	}
}
