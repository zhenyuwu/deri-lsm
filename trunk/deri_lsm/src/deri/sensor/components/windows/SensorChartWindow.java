package deri.sensor.components.windows;

import org.zkoss.zul.Window;
import deri.sensor.javabeans.Sensor;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class SensorChartWindow extends Window {
	private static final long serialVersionUID = -8175729638645727283L;
	
	public SensorChartWindow(){
		super();
	}
	
	public SensorChartWindow(String title) {
		this();
		this.setTitle(title);
	}

	public void init(){
		this.setId("sensorChartWindow");
		this.setBorder("normal");
		this.setWidth("530px");
		this.setHeight("500px");
		this.setPosition("top,right");
		this.setContentStyle("overflow:auto");
		this.setClosable(true);
	}
	
	private SensorChart getSensorChart(String sensorType, String sign){
		SensorChart sensorChart = null;
		if(this.getFellowIfAny(sensorType + sign) != null){
			sensorChart = (SensorChart)this.getFellowIfAny(sensorType + sign);
		}else{
			sensorChart = new SensorChart(sensorType, sign, sign+" chart");
			sensorChart.setParent(this);
			sensorChart.init();
		}
		
		return sensorChart;
	}

	public void addSensorChart(Sensor sensor, String sign) throws Exception{
		String sensorType = sensor.getSensorType();
		SensorChart sensorChart = getSensorChart(sensorType, sign);
		sensorChart.addContent(sensor);
	}
	
}
