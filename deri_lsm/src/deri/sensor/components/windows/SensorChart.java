package deri.sensor.components.windows;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.MouseEvent;
import org.zkoss.zul.Area;
import org.zkoss.zul.CategoryModel;
import org.zkoss.zul.Chart;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.SimpleCategoryModel;
import org.zkoss.zul.SimpleXYModel;
import org.zkoss.zul.Window;
import org.zkoss.zul.XYModel;

import deri.sensor.database.ServiceLocator;
import deri.sensor.javabeans.*;
import deri.sensor.manager.SensorManager;
import deri.sensor.meta.SensorTypeEnum;
import deri.sensor.utils.*;

/**
 * id: type + sign. 
 * This means every type+sign will have one chart
 * @author nmqhoan
 *
 */
public class SensorChart extends Window {
	private static final long serialVersionUID = 6415747234220602753L;
	private XYModel xymodel = new SimpleXYModel();
	private CategoryModel categoryModel = new SimpleCategoryModel();
	private String type;
	private String sign;
	private SensorManager sensorManager = ServiceLocator.getSensorManager();
	private Chart chart = new Chart();
	
	public SensorChart(String type, String sign) {
		this.type = type;
		this.sign = sign;
		this.setId(type.toString() + sign);
	}
	
	public SensorChart(String type, String sign, String title) {
		this(type,sign);
		this.setTitle(title);
	}
	
	public void init(){
		this.setClosable(true);
		this.setWidth("500px");
		
		initChart();
	}
	
	private void initChart(){
		chart.setParent(this);
		chart.setType(Chart.TIME_SERIES);
		chart.setModel(xymodel);

		if(this.getSign().equals(WeatherSignShown.state.toString()) || this.getSign().equals(WeatherSignShown.wind_direction.toString())){
			chart.setType(Chart.BAR);
			chart.setModel(categoryModel);
		}else{
		}
		
		
		chart.addEventListener(Events.ON_CLICK, new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				MouseEvent mouseEvent = (MouseEvent)event;
				Area area = (Area)chart.getFellow(mouseEvent.getArea());
				String entity = area.getAttribute("entity").toString();
				if(entity.toLowerCase().equals("data")){
					if(chart.getType().equals(Chart.TIME_SERIES)){
						String series = area.getAttribute("series").toString();
						String x = area.getAttribute("x").toString();
						Date time = null;
						String y = area.getAttribute("y").toString();
						double value = 0d;
						if(NumberUtil.isLong(x)){
							time = new Date(Long.parseLong(x));
						}
						if(NumberUtil.isDouble(y)){
							value = Double.parseDouble(y);
						}
						String message = "";
						message += type.toString() + (sign.trim().equals("") ? " " :(": " +  sign)) + "\n";
						message += "time: " + DateUtil.date2StandardString(time) + "\n";
						message += "value: " + value;
						String title = series;
						Messagebox.show(message, title, Messagebox.OK, Messagebox.EXCLAMATION);
					}else if(chart.getType().equals(Chart.BAR)){
						String series = area.getAttribute("series").toString();
						String category = area.getAttribute("category").toString();
						String value = area.getAttribute("value").toString();
						String message = "";
						message += type.toString() + (sign.trim().equals("") ? " " :(": " +  sign)) + "\n";
						message += "category: " + category + "\n";
						message += "value: " + value;
						String title = series;
						Messagebox.show(message, title, Messagebox.OK, Messagebox.EXCLAMATION);
					}
				}
			}
		});
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}
	
	
	public void addContent(Sensor sensor) throws Exception{
		List<Observation> observations = sensorManager.getObservationsForOneSensor(sensor.getId());
		AbstractProperty absPro = null;
		if(sensor.getSensorType().equals(SensorTypeEnum.cosm.toString()))
			absPro = sensorManager.getSpecifiedCosmReadingForOneObservation(
					observations.get(0).getId(), sign);
		else
			absPro = sensorManager.getSpecifiedReadingForOneObservation(
				observations.get(0).getId(), sign);		
		if(!NumberUtil.isDouble(absPro.getValue())||(absPro.getUnit().equals(""))){			
			if(observations != null && observations.size() > 0){
				chart.setType(Chart.BAR);
				chart.setModel(categoryModel);
				Map<String,Integer> categoryMap = new HashMap<String,Integer>();				
				String city = sensor.getPlace().getCity();
				String sourceId = sensor.getId();
				String author = sensor.getAuthor();
				this.setTitle(type.toString() + " " + sign +  " Chart");
					for(Observation observation : observations){
						if(sensor.getSensorType().equals(SensorTypeEnum.cosm.toString()))
							absPro = sensorManager.getSpecifiedCosmReadingForOneObservation(
									observation.getId(), sign);
						else
							absPro = sensorManager.getSpecifiedReadingForOneObservation(
									observation.getId(), sign);	
						String state = absPro.getValue();
						if(categoryMap.containsKey(state)){
							int value = categoryMap.get(state);
							categoryMap.put(state, ++value);
						}else{
							categoryMap.put(state, 1);
						}
					}			
				double count = 0;
				for(Integer value : categoryMap.values()){
					count += value;
				}
				
				for(String state : categoryMap.keySet()){
					if(!state.equals("Not Available")){
						categoryModel.setValue(city + "_"+sourceId + ", by " + author, state, categoryMap.get(state) / count);
					}
				}
			}
		}else{
			chart.setType(Chart.TIME_SERIES);
			chart.setModel(xymodel);
			chart.setDateFormat("MM-dd HH:mm");
			if(observations != null && observations.size() > 0){
				this.setTitle(type.toString() + " " + sign +  " Chart");
				String city = sensor.getPlace().getCity();
				String sourceId = sensor.getName();
				String author = sensor.getAuthor();
				for(Observation observation : observations){
					long time = observation.getTimes().getTime();					
					double content = ConstantsUtil.weather_defalut_value;
					if(sensor.getSensorType().equals(SensorTypeEnum.cosm.toString()))
						absPro = sensorManager.getSpecifiedCosmReadingForOneObservation(
								observation.getId(), sign);
					else
						absPro = sensorManager.getSpecifiedReadingForOneObservation(
								observation.getId(), sign);					
					if(absPro != null){
						content = Double.parseDouble(absPro.getValue());
					}
					
					if(content != ConstantsUtil.weather_defalut_value){
						xymodel.addValue(city + "_"+sourceId + ", by " + author, time, content);
					}
				}
			}	
		}
	}
}
