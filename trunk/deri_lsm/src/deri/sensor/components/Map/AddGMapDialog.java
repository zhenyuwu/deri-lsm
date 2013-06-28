package deri.sensor.components.Map;

import org.zkoss.gmaps.event.MapMouseEvent;
import org.zkoss.gmaps.Ginfo;
import org.zkoss.gmaps.Gmaps;
import org.zkoss.gmaps.Gmarker;
import org.zkoss.gmaps.event.MapDropEvent;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class AddGMapDialog extends GenericForwardComposer {
	private Window addGmapDlg;// auto wired
	private Gmaps gmap;// auto wired
	private Gmarker marker;// auto wired
	// latitude and longitude
	private double lat;
	private double lng;

	/**
	 * Pan to marker position on the drop of a marker.
	 * @param event
	 * @throws InterruptedException
	 */
	public void onMapDrop$gmap(MapDropEvent event) throws InterruptedException {
		lat = event.getLat();
		lng = event.getLng();
		gmap.panTo(lat, lng);
	}

	public void onMapClick$gmap(MapMouseEvent event) throws InterruptedException {
		lat = event.getLat();
		lng = event.getLng();
		Ginfo ginfo = new Ginfo();
		ginfo.setOpen(true);
		ginfo.setContent("hello");
		//ginfo.setParent();
	}
	
	/**
	 * On click of the add map button, set the lat and long to the desktop,
	 * change the add map toolbarbutton (in AddListingDialog) to green, and
	 * detach this window.	
	 */
	public void onClick$addMapBtn(Event event) throws InterruptedException {
		// set latitude and longitude to the desktop
		desktop.setAttribute("lat", lat);
		desktop.setAttribute("lng", lng);
		System.out.println(gmap.getZoom());
		// set button in mainWindow to green to indicate map was
		// successfully set
		Toolbarbutton addMapTbb = (Toolbarbutton) Path
				.getComponent("/mainWindow/addMapTbb");
		addMapTbb.setLabel("Map added");
		addMapTbb.setStyle("color:green");
		addGmapDlg.detach();
		// this line demonstrates how the latitude and longitude values are
		// accessible on the desktop
		System.out.println("These variables have been set as ZK desktop attributes: lat: "
						+ desktop.getAttribute("lat")
						+ ", lng: "
						+ desktop.getAttribute("lng"));
	}

	/**
	 * On close of window, detach dialog.
	 *
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClose$addGmapDlg(Event event) throws InterruptedException {
		addGmapDlg.detach();
	}
}
