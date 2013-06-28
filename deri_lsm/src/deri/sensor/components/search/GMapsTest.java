package deri.sensor.components.search;



import org.zkoss.gmaps.Gmaps;
import org.zkoss.gmaps.event.MapMouseEvent;

import deri.sensor.utils.ConstantsUtil;

public class GMapsTest extends Gmaps {
	private static final long serialVersionUID = -7261148124568614950L;
	private int zoom_before = ConstantsUtil.map_default_zoom;
	
	public GMapsTest() {
		super();
	}
	
	public void init(double lat, double lng){
		this.setShowTypeCtrl(true);
		this.setScrollWheelZoom(true);
		this.setShowSmallCtrl(true);
		this.setShowScaleCtrl(true);
		this.setMapType("hybrid");
		this.setZoom(ConstantsUtil.map_default_zoom);
		this.setCenter(lat, lng);
	}
	
	/**
	 * This is called when the sensor.zul is created.
	 * 
	 */
	public void onCreate(){
		this.init(ConstantsUtil.map_default_lat, ConstantsUtil.map_default_lng);
	}
	
	/* ----------------------------- map functions ----------------------------- */
	/**
	 * this event will be activated when user clicks the marker.
	 * @param event
	 */
	public void onMapClick(MapMouseEvent event){
		System.out.println("Map zoom:"+this.getZoom());
	}
	
	/**
	 * In order to upgrade the performance, I have done some process before reshowMarkersWith3Filters();
	 * 1. search is enabled and search only checkbox is checked
	 * 2. search is not enabled and no sensor type filter or region filter item selected
	 * 3. if just zoom, no action, just zoom_before = zoom_now;
	 * 4. reshowMarkersWith3Filters();
	 */
	public void onMapMove(){
		System.out.println("Map zoom:"+this.getZoom());
		
	}

}
