package deri.sensor.components.Map;

import org.zkoss.gmaps.Gmarker;
import org.zkoss.zul.Label;
import org.zkoss.zul.Popup;

import deri.sensor.database.ServiceLocator;
import deri.sensor.javabeans.User;
import deri.sensor.meta.SensorTypeEnum;
import deri.sensor.utils.ConstantsUtil;
import deri.sensor.utils.NumberUtil;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class SensorMarker extends Gmarker {
	private static final long serialVersionUID = -5475516895886414085L;
	private String type;
	private long markersNearby = 0;
	private int generateZoom;
	private double neLat = 0;
	private double neLng = 0;
	private double swLat = 0;
	private double swLng = 0;
	private String flightCallSign="";
	private User user;
	public SensorMarker() {
		super();
	}
	
	public SensorMarker(String type, long markersNearby, double lat, double lng, int generateZoom, double neLat, double neLng, double swLat, double swLng){
		this();
		this.type = type;
		this.markersNearby = markersNearby;
		this._lat = lat;
		this._lng = lng;
		this.generateZoom = generateZoom;
		this.neLat = neLat;
		this.neLng = neLng;
		this.swLat = swLat;
		this.swLng = swLng;
		//this.user = user;		
//		if(markersNearby == 1 && type.equals("weather")){
//			this.setDraggable("true");
//		}
	}
	
	public void setCallSign(String callSign){
		this.flightCallSign = "flight " +callSign;
	}

	public void init(){
		this.setDraggingEnabled(false);
		this.AnchorXY();		
		this.setImageURL();
		this.setContentPopup();

	}
	
	
	/**
	 * set the content popup for each sensor marker.
	 */
	public void setContentPopup(){
		try{
			Popup popup = new Popup();
			popup.setStyle("background-color:white");
			popup.setParent(this.getPage().getFirstRoot());
			
			String label_content = this.getMarkersNearby() + this.getLabel(this.getType());
			if(this.getMarkersNearby() == 1){
				if(!type.equals(SensorTypeEnum.ADSBHub.toString()))
					label_content = ServiceLocator.getPlaceManager().getPlaceWithSpecifiedLatLng(_lat, _lng).getSimpleContent();
				else{
					label_content = flightCallSign;
					
				}
			}
			Label label = new Label(label_content);
			label.setParent(popup);
			this.setTooltip(popup);
		}catch(Exception e){
			
		}
	}
	
	/**
	 * different sensor type has a different label
	 * this is for the setContentPopup() method
	 * @param type
	 * @return
	 */
	private String getLabel(String type){
		String label = "";
		if(type.equals("weather")) 
			label = " Weather Stations Nearby";
		else if(type.equals("webcam"))		
			label = " Web Cameras Stations Nearby";
		else if(type.equals("cosm"))		
			label = " Cosm Sensors Stations Nearby";
		else if(type.equals("satellite")) 
			label = " Satellite Graphics Stations Nearby";
		else if(type.equals("sealevel"))
			label = " Sea Level Sensors Stations Nearby";
		else if(type.equals("snowfall"))
			label = " SnowFall Sensors Stations Nearby";
		else if(type.equals("snowdepth"))
			label = " SnowDepth Sensors Stations Nearby";
		else if(type.equals("radar"))
			label = " Radar Stations Stations Nearby";
		else if(type.equals("traffic"))
			label = " Traffic camera Stations Nearby";
		else if(type.equals("roadactivity"))
			label = " Road activity Stations Nearby";
		else if(type.equals("bikehire"))
			label = " Bike dock hire Nearby";
		else if(type.equals("railwaystation"))
			label = " Railway Stations Nearby";
		else if(type.equals("airport"))
			label = " Airports Nearby";
		else if(type.equals(SensorTypeEnum.ADSBHub.toString()))
			label = " Flights Nearby";
		else label = " "+ type +" Stations Nearby";
		return label;
	}
	
	/**
	 * different sensor has different image
	 */
	private void setImageURL(){
		String url = "";
		//if(user.getUsername())
		if(type.equals("weather")) 
			url = ConstantsUtil.gmarker_icon_weather;
		else if(type.equals("webcam"))		
			url = ConstantsUtil.gmarker_icon_webcam;
		else if(type.equals("cosm"))		
			url = ConstantsUtil.gmarker_icon_cosm;
		else if(type.equals("satellite")) 
			url = ConstantsUtil.gmarker_icon_satellite;
		else if(type.equals("sealevel"))
			url = ConstantsUtil.gmarker_icon_sealevel;
		else if(type.equals("snowfall"))
			url = ConstantsUtil.gmarker_icon_snowfall;
		else if(type.equals("snowdepth"))
			url = ConstantsUtil.gmarker_icon_snowdepth;
		else if(type.equals("radar"))
			url = ConstantsUtil.gmarker_icon_radar;
		else if(type.equals("traffic"))
			url = ConstantsUtil.gmarker_icon_trafficcam;
		else if(type.equals("roadactivity"))
			url = ConstantsUtil.gmarker_icon_roadactivity;
		else if(type.equals("bikehire"))
			url = ConstantsUtil.gmarker_icon_bikehire;
		else if(type.equals("railwaystation"))
			url = ConstantsUtil.gmarker_icon_railwaystation;
		else if(type.equals(SensorTypeEnum.airport.toString()))
			url = ConstantsUtil.gmarker_icon_airport;
		else if(type.equals(SensorTypeEnum.ADSBHub.toString()))
			url = ConstantsUtil.gmarker_icon_plane;
		else 
			url = ConstantsUtil.gmarker_icon_usersensor;
		if(!url.trim().equals("")){
			this.setIconImage(url);
		}
	}
	
	/**
	 * if there are more than one sensor marker with one position, then it will have to add an ahchorX and anchorY
	 * to let the marker located with different position, but with same latitude and longitude.
	 */
	private void AnchorXY(){
		GMaps map = ((GMaps)this.getParent());
		if(map.getMarkerCountWithSpecifiedLatLng(_lat, _lng) >= 2){ // for including self, should not be one, should be more than one.
			this.setIconAnchorX(NumberUtil.getRandomIntegerNotMoreThan(ConstantsUtil.anchor));
			this.setIconAnchorY(NumberUtil.getRandomIntegerNotMoreThan(ConstantsUtil.anchor));
		}
	}
	
	public int getGenerateZoom() {
		return generateZoom;
	}

	public void setGenerateZoom(int generateZoom) {
		this.generateZoom = generateZoom;
	}
	
	public double getNeLat() {
		return neLat;
	}

	public void setNeLat(double neLat) {
		this.neLat = neLat;
	}

	public double getNeLng() {
		return neLng;
	}

	public void setNeLng(double neLng) {
		this.neLng = neLng;
	}

	public double getSwLat() {
		return swLat;
	}

	public void setSwLat(double swLat) {
		this.swLat = swLat;
	}

	public double getSwLng() {
		return swLng;
	}

	public void setSwLng(double swLng) {
		this.swLng = swLng;
	}

	public long getMarkersNearby() {
		return markersNearby;
	}

	public void setMarkersNearby(long markersNearby) {
		this.markersNearby = markersNearby;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}	
	
}
