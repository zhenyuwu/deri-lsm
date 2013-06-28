package deri.sensor.components.search;

import org.zkoss.zul.*;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class SearchTabbox extends Tabbox{
	private MapSearchTabPanel MapSearchpnl;
	private DataSearchTabPannel DataSearchpnl;
	SparqlQueryTabPannel sparqlPnl;
	public SearchTabbox(){
		super();
		this.setId("searchTab");
		this.setHeight("100%");
		this.setStyle("overflow:auto");
		
	}
	
	public void onCreate(){		
		Tab tab = new Tab("Location");
		Tab dataTab = new Tab("Data");
		Tab sparqltab = new Tab("Sparql endpoint");
		
		this.appendChild(new Tabs());			
		this.appendChild(new Tabpanels());			
					
		this.getTabs().appendChild(tab);
		this.getTabs().appendChild(dataTab);
		this.getTabs().appendChild(sparqltab);
		
		MapSearchpnl = new MapSearchTabPanel(this.getTabpanels());				
		MapSearchpnl.addSearchFunction();
		
		DataSearchpnl = new DataSearchTabPannel(this.getTabpanels());
		this.getTabpanels().appendChild(DataSearchpnl);
		
		sparqlPnl = new SparqlQueryTabPannel(this.getTabpanels());
		
	}
	
	public boolean isShowOnMap(){
		return DataSearchpnl.isShowOnMap();
	}

	public MapSearchTabPanel getMapSearchpnl() {
		return MapSearchpnl;
	}

	public void setMapSearchpnl(MapSearchTabPanel mapSearchpnl) {
		MapSearchpnl = mapSearchpnl;
	}

	public DataSearchTabPannel getDataSearchpnl() {
		return DataSearchpnl;
	}

	public void setDataSearchpnl(DataSearchTabPannel dataSearchpnl) {
		DataSearchpnl = dataSearchpnl;
	}
	
}
