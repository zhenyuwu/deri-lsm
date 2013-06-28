package deri.sensor.components.windows;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;



import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Image;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Space;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.Label;

import deri.sensor.cosm.CosmSensorParser;
import deri.sensor.database.ServiceLocator;
import deri.sensor.hashmaps.util.SensorTypeToProperties;
import deri.sensor.javabeans.Observation;
import deri.sensor.javabeans.AbstractProperty;
import deri.sensor.javabeans.Sensor;
import deri.sensor.london.railwaytation.PredictionDetailedXMLParser;
import deri.sensor.manager.SensorManager;
import deri.sensor.meta.SensorType;
import deri.sensor.meta.SensorTypeEnum;
import deri.sensor.meta.SourceType;
import deri.sensor.utils.*;
import deri.sensor.wrapper.AuWeatherWrapper;
import deri.sensor.wrapper.WBugWeatherWrapper;
import deri.sensor.wrapper.WUnderGroundWrapper;
import deri.sensor.wrapper.WebServiceURLRetriever;
import deri.sensor.wrapper.YahooWeatherWrapper;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class SensorWindow extends Window {
	private static final long serialVersionUID = 448396014305387327L;
	private String type;
	private Grid grid = new Grid();
	private Rows rows = new Rows();
	private SensorManager sensorManager = ServiceLocator.getSensorManager();
	
	public SensorWindow() {
		super();
		init();
	}
	
	public SensorWindow(String title) {
		this();
		this.setTitle(title);
	}

	public SensorWindow(String title, String type) {
		this(title);
		this.type = type;
	}

	
	public void init(){
		this.setId("sensorWindow");
		this.setBorder("none");
		this.setSizable(true);
		this.setWidth("50%");
		this.setPosition("top,center");
		this.setClosable(true);
	}
	
	public void addContent(Sensor sensor){
		if(type.equals("satellite")||type.equals("webcam")||type.equals("radar")
				||type.equals("traffic"))
			addImageFrameContent(sensor);
		else if(type.equals("railwaystation")){
//			PredictionDetailedXMLParser preParser = new PredictionDetailedXMLParser();
//			preParser.getPredictionDetailedForOneStation(sensor);
			addRailwayStationContent(sensor); 
		}
		else
			addSensorContent(sensor);		
	}
	
	private void addSensorContent(Sensor sensor){		
		if(sensor.getSensorType().equals(SensorTypeEnum.weather.toString())){
			if(sensor.getSourceType().equals(SourceType.yahoo.toString())){
				YahooWeatherWrapper yahooW = new YahooWeatherWrapper();
				yahooW.postWeatherElement(sensor.getSource());
			}else if(sensor.getSourceType().equals(SourceType.wunderground.toString())){
				WUnderGroundWrapper wUnderWrapper = new WUnderGroundWrapper();
				wUnderWrapper.postWeatherElement(sensor.getSource());
			}else if(sensor.getSourceType().equals(SourceType.weatherbug.toString())){
				WBugWeatherWrapper wBugWrapper = new WBugWeatherWrapper();
				wBugWrapper.postWeatherElement(sensor.getSource());
			}else if(sensor.getSourceType().equals(SourceType.australia.toString())){
				AuWeatherWrapper auWrapper = new AuWeatherWrapper();
				auWrapper.postWeatherElements(sensor.getSource());
			}
		}else if(sensor.getSensorType().equals(SensorTypeEnum.cosm.toString())){
			String xml = WebServiceURLRetriever.RetrieveFromURLWithAuthentication(sensor.getSource(), "nmqhoan", "nmqhoan");
			CosmSensorParser.parseDataElementsFromCosmXML(xml, sensor.getUser());
		}
		Observation observation = sensorManager.getNewestObservationForOneSensor(sensor.getId());

		if(observation == null){
			Label label = new Label("No Available Data For This Sensor Station");
			label.setParent(this);
			label.setWidth("100%");
		}else{
			Vbox vbox = new Vbox();
			vbox.setParent(this);
			vbox.setWidth("100%");
			
			Label label = new Label(sensor.getPlace().getSimpleContent());
			label.setParent(vbox);
			label.setWidth("100%");
			
			label = new Label("Update: "+DateUtil.date2StandardString(observation.getTimes()));
			label.setParent(vbox);
			label.setWidth("100%");
			
			Hbox hbox = new Hbox();
			hbox.setParent(vbox);
			hbox.setWidth("100%");
			
			Toolbarbutton bar = new Toolbarbutton();
			bar.setParent(hbox);
			
			Separator separator = new Separator();
			separator.setParent(this);
			
			grid.setParent(this);
			grid.setWidth("100%");
			
			rows.setParent(grid);
			this.list2Rows(data2List(observation),sensor);			
		}
	}

	private List<String> data2List(Observation observation){
		String source = observation.getSensorId();//the reference of more data of the specified weather
		String sign;
		List<String> list = new ArrayList<String>();
		List<ArrayList> readings = sensorManager.getReadingDataOfObservation(observation.getId());
		for(ArrayList reading : readings){
			if(type.equals("cosm"))
				sign = reading.get(3).toString();
			else
				sign = reading.get(0).toString().substring(reading.get(0).toString().lastIndexOf("#")+1);
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
					final String series = source_sign.substring(comma_after_source + 1);
					
					space = new Space();
					space.setParent(hbox);
				
					Toolbarbutton bar = new Toolbarbutton();
					bar.setParent(hbox);
					bar.setStyle("color:blue;text-decoration:underline");
					bar.setTooltiptext("Data visualization");
					bar.setLabel("chart");
					bar.addEventListener(Events.ON_CLICK, new EventListener(){
						@Override
						public void onEvent(Event event) throws Exception {
							//showSpecifiedSignInSensorChartWindow(source, series);
							showSpecifiedSignInSensorChartWindow(sensor, series);
						}
					});
				}
			}
		}
	}
	
	private SensorChartWindow getSensorChartWindow(){
		SensorChartWindow sensorChartWindow = null;
		if(this.getPage().getFirstRoot().getFellowIfAny("sensorChartWindow") != null){
			sensorChartWindow = (SensorChartWindow)this.getPage().getFirstRoot().getFellowIfAny("sensorChartWindow");
		}else{
			sensorChartWindow = new SensorChartWindow("Charts Window");
			sensorChartWindow.setParent(this.getPage().getFirstRoot());
			sensorChartWindow.init();
		}
		
		return sensorChartWindow;
	}
	
	private void showSpecifiedSignInSensorChartWindow(Sensor sensor, String sign){				
		try {
			SensorChartWindow sensorChartWindow = getSensorChartWindow();
			sensorChartWindow.addSensorChart(sensor, sign);
			sensorChartWindow.doOverlapped();
		} catch (Exception e) {
			e.printStackTrace();
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
			this.setWidth("45%");
			this.setHeight("60%");			
			Iframe iframe = new Iframe();
			iframe.setParent(this);
			iframe.setSrc(path);
			iframe.setAlign("center");
			
			iframe.setWidth("100%");
			iframe.setHeight("100%");
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	
	private void addRailwayStationContent(Sensor sensor){
		Observation observation = sensorManager.getNewestObservationForOneSensor(sensor.getId());
		if(observation == null){
			Label label = new Label("No Available Data For This Sensor Station");
			label.setParent(this);
			label.setWidth("100%");
		}else{
			Vbox vbox = new Vbox();
			vbox.setParent(this);
			vbox.setWidth("100%");
			
			Label label = new Label(sensor.getPlace().getSimpleContent());
			label.setParent(vbox);
			label.setWidth("100%");
			
			label = new Label("Update: "+DateUtil.date2StandardString(observation.getTimes()));
			label.setParent(vbox);
			label.setWidth("100%");			
			
			Hbox hbox = new Hbox();
			hbox.setParent(vbox);
			hbox.setWidth("100%");
			
			Toolbarbutton bar = new Toolbarbutton();
			bar.setParent(hbox);
			
			Separator separator = new Separator();
			separator.setParent(this);
			
			grid.setParent(this);
			grid.setWidth("100%");
			grid.setMold("paging");
			grid.setPageSize(6);
			grid.setVflex(true);
			
			rows.setParent(grid);
			Columns columns = new Columns();
			columns.setSizable(true);
			columns.setParent(grid);
			Column column = new Column();
			column.setParent(columns);
			column.setLabel("Station platform");
			column.setWidth("13%");
			column = new Column();
			column.setParent(columns);
			column.setLabel("Train number");
			column = new Column();
			column.setParent(columns);
			column.setLabel("Arrive to station");			
			column = new Column();
			column.setParent(columns);
			column.setLabel("Arrive to final destination");			
			column = new Column();
			column.setParent(columns);
			column.setLabel("Current location");
			column = new Column();
			column.setParent(columns);
			column.setLabel("Final destination");
			
			List<String> observations = sensorManager.getObservationsWithTimeCriteria(sensor.getId(), "=", observation.getTimes(), null);
//			List<String> signs = SensorTypeToProperties.sensorTypeToTableProperties.get(type);
			//System.out.println(observations);
			for(String obsId:observations){
				//System.out.println(obs.getId());
				String content=null;
				Row newRow = new Row();
				newRow.setParent(rows);
				List<ArrayList> readings = sensorManager.getReadingDataOfObservation(obsId);
				for(ArrayList reading : readings){					
					String unit=null;
					try{
						content = reading.get(1).toString();
						unit = reading.get(2).toString();				
					}catch(Exception e){}
					if(unit == null){
						unit = "";
					}
					try {				
						if(content == null){
							newRow.appendChild(new Label("not_supported" + ConstantsUtil.useful_data_sign));
						}if(NumberUtil.isInteger(content)&&unit!=""){						
							int data = Integer.parseInt(content);
							newRow.appendChild(new Label(String.valueOf(data) + " (" + unit + ")"));
						}else{
							newRow.appendChild(new Label(content));
						}
						
					}catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
