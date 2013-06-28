package deri.sensor.components.user;

import org.zkoss.zul.Window;


/**
 * @author Hoan Nguyen Mau Quoc
 */
public class UserControlWindow extends Window {
	private static final long serialVersionUID = -8464369269117686267L;
	
	public UserControlWindow(){
		super();
		this.setId("filterWindow");
		this.setContentStyle("overflow:auto");
		//this.setVisible(false);
	}
	
	public void onCreate(){
		this.addRegionFilter();
		this.addSaveViewFilter();
	}
	
	
	public void addRegionFilter(){
		FilterRegion filterRegion = new FilterRegion(this);
		filterRegion.addRegionFilter();
	}
	
	public void addSaveViewFilter(){
		FilterSaveView filterSaveView = new FilterSaveView(this);
		filterSaveView.addSaveViewFilter();
	}
}
