package deri.sensor.components.user;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Space;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;
import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.SensorTypeToProperties;
import deri.sensor.javabeans.AbstractProperty;
import deri.sensor.javabeans.Observation;
import deri.sensor.javabeans.Place;
import deri.sensor.javabeans.Sensor;
import deri.sensor.manager.SensorManager;
import deri.sensor.meta.SensorType;
import deri.sensor.utils.ConstantsUtil;
import deri.sensor.utils.DateUtil;
import deri.sensor.utils.NumberUtil;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class UserSearchLiveInforResultWindow extends Window {
	private static final long serialVersionUID = 448396014305387327L;
	private List<Sensor> sensors;
	private Grid grid = new Grid();
	private Rows rows = new Rows();
	private SensorManager sensorManager = ServiceLocator.getSensorManager();
	private Place place;
	
	public UserSearchLiveInforResultWindow() {
		super();
		init();
	}
	
	public UserSearchLiveInforResultWindow(String title) {
		this();
		this.setTitle(title);
	}

	public UserSearchLiveInforResultWindow(String title,Place place) {
		this(title);
		this.place = place;
	}

	
	public List<Sensor> getSensors() {
		return sensors;
	}

	public void setSensors(List<Sensor> sensors) {
		this.sensors = sensors;
	}

	public void init(){
		this.setId("LiveInforResultWindow");
		this.setBorder("none");
		this.setSizable(true);
		this.setClosable(true);
		this.setWidth("50%");
		this.setPosition("top,center");
		this.setClosable(true);
	}
	
	public void addContent(){		
		Vbox vbox = new Vbox();
		vbox.setParent(this);
		vbox.setWidth("100%");
		
		Label label = new Label(place.getSimpleContent());
		label.setParent(vbox);
		label.setWidth("100%");
		
		label = new Label("Update: "+DateUtil.date2StandardString(new Date()));
		label.setParent(vbox);
		label.setWidth("100%");
		
		
		if(grid.getRows()!=null)
			grid.getRows().detach();
		for(Sensor sensor:sensors){
			addSensorContent(sensor);							
		}
		
		Separator separator = new Separator();
		separator.setParent(this);
		
		grid.setParent(this);
		grid.setWidth("100%");
		grid.setVflex(true);
		
		rows.setParent(grid);
	
	}
	
	private void addSensorContent(Sensor sensor){		
		rows = new Rows();
		rows.setParent(grid);
		Observation observation = sensorManager.getNewestObservationForOneSensor(sensor.getId());
		if(observation == null){
			Label label = new Label("No Available Data For This Sensor Station");
			label.setParent(this);
			label.setWidth("100%");
		}else{			
			grid.setParent(this);
			grid.setWidth("100%");
			grid.setVflex(true);
			
			rows.setParent(grid);
			this.list2Rows(data2List(observation),sensor);
		}
	}

	private List<String> data2List(Observation observation){
		String source = observation.getSensorId();//the reference of more data of the specified weather
		
		List<String> list = new ArrayList<String>();
		List<ArrayList> readings = sensorManager.getReadingDataOfObservation(observation.getId());
		for(ArrayList reading : readings){
			String sign = reading.get(0).toString().substring(reading.get(0).toString().lastIndexOf("#")+1);
			list.add(sign);						
			String unit=null;
			String content=null;
			try{
				content = reading.get(1).toString();
				unit = reading.get(2).toString();				
			}catch(Exception e){
			}
			if(unit == null){
				unit = "no";
			}

			try {				
				if(content == null){
					list.add("not_supported" + ConstantsUtil.useful_data_sign);
				}else if(NumberUtil.isDouble(content)){
					double data = Double.parseDouble(content);
					if(data != ConstantsUtil.weather_defalut_value){
						list.add(String.valueOf(data) + " unit:(" + unit + ")" + ConstantsUtil.useful_data_sign + source + "," + sign.toString());
					}else{
						list.add("not_supported" + ConstantsUtil.useful_data_sign);
					}
				}else{
					String data = content;
					if(!data.trim().equals("") ){
						list.add(data + " unit:(" + unit + ")" + ConstantsUtil.useful_data_sign + source + "," + sign.toString());
					}else{
						list.add("not_supported" + ConstantsUtil.useful_data_sign);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}
	
	/***************************************************************************************************/
	private void list2Rows(List<String> list,final Sensor sensor){
		Row row = null;
		for(String row_content : list){
			row = new Row();
			row.setParent(rows);
			if(!row_content.contains(ConstantsUtil.useful_data_sign)){
				Hbox hbox = new Hbox();
				hbox.setParent(row);
				Label label = new Label(row_content);
				label.setStyle("color:#336EF6;font-style:bold");
				label.setParent(hbox);
			}else{
				int index = row_content.indexOf(ConstantsUtil.useful_data_sign);
				String content = row_content.substring(0,index).trim();
				
				Hbox hbox = new Hbox();
				hbox.setParent(row);
				Space space = new Space();
				space.setParent(hbox);
				Label label = new Label(content);
				label.setStyle("color:#F63347;font-style:oblique");
				label.setParent(hbox);
				
				
				String source_sign = row_content.substring(index + ConstantsUtil.useful_data_sign.length()).trim();
				if(!source_sign.trim().equals("")){
					int comma_after_source = source_sign.indexOf(",");
					final String source = source_sign.substring(0, comma_after_source);
					final String series = source_sign.substring(comma_after_source + 1);
					
//					space = new Space();
//					space.setParent(hbox);
//				
//					Toolbarbutton bar = new Toolbarbutton();
//					bar.setParent(hbox);
//					bar.setStyle("color:blue;text-decoration:underline");
//					bar.setTooltiptext("Data summarization");
//					bar.setLabel("chart");
//					bar.addEventListener(Events.ON_CLICK, new EventListener(){
//						@Override
//						public void onEvent(Event event) throws Exception {
//							//showSpecifiedSignInSensorChartWindow(source, series);
//
//						}
//					});
				}
			}
		}
	}
			
	private void addImageFrameContent(Sensor sensor){
			this.setWidth("55%");
			this.setHeight("80%");
			String src = sensor.getSource();
			Iframe iframe = new Iframe();
			iframe.setParent(this);
			iframe.setSrc(src);
			iframe.setAlign("center");
			iframe.setWidth("100%");
			iframe.setHeight("100%");
	}
	
	public void addImageContentFromFile(String path) throws IOException{
		try{
			Image image = new Image();
			image.setParent(this);	
			try{
				File sourceimage = new File(path.trim());
				BufferedImage imageBuff = ImageIO.read(sourceimage);
				image.setContent(imageBuff);
			}catch(Exception e){
				image.setSrc(path);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public void clearContent(){
		while(this.getFirstChild() != null){
			this.getFirstChild().detach();
		}
	}
	
	@Override
	public void onClose(){
		this.clearContent();
		this.detach();
	}
}
