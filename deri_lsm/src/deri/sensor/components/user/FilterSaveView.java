package deri.sensor.components.user;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Popup;

import deri.sensor.components.Map.GMaps;
import deri.sensor.database.ServiceLocator;
import deri.sensor.javabeans.User;
import deri.sensor.javabeans.UserView;
import deri.sensor.javabeans.UserViewObject;
import deri.sensor.manager.UserActiveManager;
import deri.sensor.utils.XMLUtil;

/**
 * @author Hoan Nguyen Mau Quoc
 */
public class FilterSaveView extends Groupbox {
	private static final long serialVersionUID = -5744195571721010130L;
	private Combobox box = new Combobox();
	private GMaps map ;
	private UserActiveManager userActiveManager = ServiceLocator.getUserActiveManager();
	public FilterSaveView(Component parent){
		super();
		this.setId("filterSaveView");
		this.setParent(parent);
		
		this.setMold("3d");
		this.setOpen(false);
		Caption caption = new Caption();
		caption.setLabel("Save/Open View");//
		caption.setParent(this);
		
		Popup popup = new Popup();
		popup.setParent(this.getPage().getFirstRoot());
		Label label = new Label("Input/Select the name");
		label.setParent(popup);
		caption.setTooltip(popup);
		
		initMap();
	}
	
	private void initMap(){
		this.map = (GMaps)this.getParent().getParent().getFellowIfAny("map");
	}
	
	public void addSaveViewFilter(){
		box.setParent(this);
		box.setWidth("100px");
		box.addEventListener(Events.ON_SELECT, new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				initializeMapWithUserView();
			}
		});
		
		box.addEventListener(Events.ON_OK, new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				initializeMapWithUserView();
			}
		});
		
		Button save = new Button("save");
		save.setParent(this);
		save.addEventListener(Events.ON_CLICK, new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				String username = getUserName();
				if(username != null){
					if(box.getValue() == null || box.getValue().trim().equals("")){
						showMessage("Please Input Your View Name","Empty View Name",Messagebox.OK,Messagebox.INFORMATION);
					}else{
						String viewname = box.getValue();
						if(userActiveManager.getUserView(username, viewname) != null){// overwrite
							int answer = showMessage("You already have the name of view. Do you want to overwrite it?","Overwrite Viewname",Messagebox.OK | Messagebox.CANCEL,Messagebox.INFORMATION);
							if(answer == Messagebox.OK){
								UserView userView = userActiveManager.getUserView(username, viewname);
								String viewXMLContent = getViewXMLContent();
								userView.setViewXMLContent(viewXMLContent);
								userActiveManager.updateUserView(userView);
							}
						}else{// save a new userview
							if(showMessage("Do you want to Save Your View -- "+viewname+"?","Save Dialogue",Messagebox.OK | Messagebox.CANCEL,Messagebox.INFORMATION) == Messagebox.OK){
								UserView userView = new UserView();
								userView.setUsername(username);
								userView.setViewname(viewname);
								userView.setViewXMLContent(getViewXMLContent());
								userActiveManager.addUserView(userView);
							}
						}
					}
				}else{
					showMessage("Only Available for registered users","Register Request",Messagebox.OK,Messagebox.INFORMATION);
				}
			}
		});
		
		
		Button delete = new Button("delete");
		delete.setParent(this);
		delete.addEventListener(Events.ON_CLICK, new EventListener(){
			@Override
			public void onEvent(Event event) throws Exception {
				String username = getUserName();
				if(username != null){
					if(box.getValue() == null || box.getValue().trim().equals("")){
						showMessage("Please Input Your View Name","Empty View Name",Messagebox.OK,Messagebox.INFORMATION);
					}else{
						String viewname = box.getValue();
						if(showMessage("Do you want to Delete Your View -- "+viewname+"?","Delete Dialogue",Messagebox.OK | Messagebox.CANCEL,Messagebox.INFORMATION) == Messagebox.OK){
							UserView userView = userActiveManager.getUserView(username, viewname);
							userActiveManager.deleteUserView(userView);
						}
					}
				}else{
					showMessage("Only Available for registered users","Register Request",Messagebox.OK,Messagebox.INFORMATION);
				}
			}
		});
	}
	
	private void initializeMapWithUserView(){
		Comboitem item = box.getSelectedItem();
		if(item != null){
			String viewname = item.getLabel();
			String username = getUserName();
			UserView userView = userActiveManager.getUserView(username, viewname);
			String viewXMLContent = userView.getViewXMLContent();
			if(viewXMLContent != null && !viewXMLContent.trim().equals("")){
				UserViewObject userviewObj = UserView.getFilterType(viewXMLContent);
				double lat = userviewObj.getLat();
				double lng = userviewObj.getLng();
				int zoom = userviewObj.getZoom();
				String cities = userviewObj.getCity();
				boolean isSearchOnly = userviewObj.isSearch_only();
				List<String> signItems = userviewObj.getFilterTypes();
				List<String> countries = userviewObj.getFilterRegion();
				map.setLat(lat);
				map.setLng(lng);
				map.setZoom(zoom);
				map.setSensorTypeItemsSelected(signItems, true);
				map.setCountryItemsSelected(countries);
				
				if(cities != null && !cities.trim().equals("")){
					//map.openSearchFilter(true);
					map.setSearchedCities(cities);
					map.setSearchText(cities);
					map.setSearchOnly(isSearchOnly);
					map.showSearchedCities();
				}else{
					//map.openSearchFilter(false);
					map.setSearchedCities(new HashSet<String>());
					map.setSearchText("");
					map.setSearchOnly(false);
				}
				
				map.reshowMarkersWith3Filters();
			}
		}
	}
	
	public void onOpen(){
		if(this.isOpen()){
			String username = this.getUserName();
			if(username != null){
				List<UserView> views = userActiveManager.getUserView(username);
				for(UserView view : views){
					String viewname = view.getViewname();
					box.appendItem(viewname);
				}
			}else{
				removeComboboxItems();
			}
		}else{
			removeComboboxItems();
		}
	}
	
	private String getUserName(){
		User user = (User)this.getDesktop().getSession().getAttribute("user");
		if(user != null){
			String username = user.getUsername();
			return username;
		}
		return null;
	}
	
	private void removeComboboxItems(){
		while(box.getFirstChild() != null){
			box.getFirstChild().detach();
		}
		box.setValue("");
	}
	
	private int showMessage(String message, String title, int buttons, String icon){
		try {
			return Messagebox.show(message, title, buttons, icon);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return Messagebox.CANCEL;
	}
	
	private String getViewXMLContent(){
		List<String> countries = map.getSelectedRegionAfterProcess();
		List<String> typeItems = map.getSelectedItemLabels();
		boolean isSearchEnabled = map.isSearchEnabled();
		boolean isSearchedOnly = false;
		String cities = "";
		if(isSearchEnabled){
			isSearchedOnly = map.isSearchOnlyChecked();
			cities = map.getSearchedCitiesString();
		}
		double lat = map.getLat();
		double lng = map.getLng();
		int zoom = map.getZoom();
		Document document = XMLUtil.createDocument();
		Element root = XMLUtil.addRootToDocument(document, XMLUtil.userview_sign_VIEW, null);
		
		
		Element CENTER = XMLUtil.addElementToElement(root, XMLUtil.userview_sign_CENTER, null, null);
		XMLUtil.addElementToElement(CENTER, XMLUtil.userview_sign_LAT, null, String.valueOf(lat));
		XMLUtil.addElementToElement(CENTER, XMLUtil.userview_sign_LNG, null, String.valueOf(lng));
		
		
		XMLUtil.addElementToElement(root, XMLUtil.userview_sign_ZOOM, null, String.valueOf(zoom));

		
		Element search = XMLUtil.addElementToElement(root, XMLUtil.userview_sign_SEARCH, null, null);
		if(isSearchEnabled){
			Map<String,String> attributes = new HashMap<String,String>();
			String attribute = "only";
			String value = (isSearchedOnly ? "true" : "false");
			attributes.put(attribute, value);
			XMLUtil.addElementToElement(search, XMLUtil.userview_sign_CITY, attributes, cities);
		}else{
			XMLUtil.addElementToElement(search, XMLUtil.userview_sign_CITY, null, null);
		}
		
		
		Element FILTER = XMLUtil.addElementToElement(root, XMLUtil.userview_sign_FILTER, null, null);
		String countries_str = "";
		for(String country : countries){
			countries_str += (country + ",");
		}
		XMLUtil.addElementToElement(FILTER, XMLUtil.userview_sign_REGION, null, countries_str);
		
		String typeItems_str = "";
		for(String typeItem : typeItems){
			typeItems_str += (typeItem + ",");
		}
		XMLUtil.addElementToElement(FILTER, XMLUtil.userview_sign_TYPE, null, typeItems_str);
		
		return document.asXML();
	}
}
